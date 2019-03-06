package net.ajcloud.wansviewplusw.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import net.ajcloud.wansviewplusw.BaseController;
import net.ajcloud.wansviewplusw.support.customview.LoadingManager;
import net.ajcloud.wansviewplusw.support.http.HttpCommonListener;
import net.ajcloud.wansviewplusw.support.http.RequestApiUnit;
import net.ajcloud.wansviewplusw.support.http.bean.SigninBean;
import net.ajcloud.wansviewplusw.support.http.bean.start.AppStartUpBean;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;

public class LoginController implements BaseController {

    public JFXTextField tf_name;
    public JFXPasswordField tf_password;
    private RequestApiUnit requestApiUnit;
    private OnLoginListener onLoginListener;
    private RequiredFieldValidator email_validator;
    private RequiredFieldValidator password_validator;

    public interface OnLoginListener {
        void onLoginSuccess();
    }

    public void setOnLoginListener(OnLoginListener onLoginListener) {
        this.onLoginListener = onLoginListener;
    }

    public void init() {
        email_validator = new RequiredFieldValidator();
        tf_name.getValidators().add(email_validator);
        tf_name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (StringUtil.equals(email_validator.getMessage(), "Can not be empty") &&
                    email_validator.isVisible() &&
                    tf_name.getText() != null && tf_name.getText().length() > 0) {
                tf_name.resetValidation();
            }
        });
        password_validator = new RequiredFieldValidator();
        tf_password.getValidators().add(password_validator);
        tf_password.textProperty().addListener((observable, oldValue, newValue) -> {
            if (StringUtil.equals(password_validator.getMessage(), "Can not be empty") &&
                    password_validator.isVisible() &&
                    tf_password.getText() != null && tf_password.getText().length() > 0) {
                tf_password.resetValidation();
            }
        });
    }

    @FXML
    private void handleSubmitButtonAction(ActionEvent event) {
        if (requestApiUnit == null) {
            requestApiUnit = new RequestApiUnit();
        }
        if (StringUtil.isNullOrEmpty(tf_name.getText())) {
            email_validator.setMessage("Can not be empty");
            tf_name.validate();
            return;
        }
        if (StringUtil.isNullOrEmpty(tf_password.getText())) {
            password_validator.setMessage("Can not be empty");
            tf_password.validate();
            return;
        }
        LoadingManager.getLoadingManager().showDefaultLoading((Stage) tf_name.getScene().getWindow());
        new Thread(new Runnable() {
            @Override
            public void run() {
                requestApiUnit.appStartup(new HttpCommonListener<AppStartUpBean>() {
                    @Override
                    public void onSuccess(AppStartUpBean bean) {
                        requestApiUnit.signin(tf_name.getText(), tf_password.getText(), new HttpCommonListener<SigninBean>() {
                            @Override
                            public void onSuccess(SigninBean bean) {
                                Platform.runLater(() -> {
                                    if (onLoginListener != null) {
                                        LoadingManager.getLoadingManager().hideDefaultLoading();
                                        onLoginListener.onLoginSuccess();
                                    }
                                });
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                Platform.runLater(() -> {
                                    LoadingManager.getLoadingManager().hideDefaultLoading();
                                    tf_name.resetValidation();
                                    password_validator.setMessage(msg);
                                    tf_password.validate();
                                });
                            }
                        });
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        Platform.runLater(() -> {
                            LoadingManager.getLoadingManager().hideDefaultLoading();
                        });
                    }
                });
            }
        }).start();
    }

    @FXML
    private void initialize() {
    }
}
