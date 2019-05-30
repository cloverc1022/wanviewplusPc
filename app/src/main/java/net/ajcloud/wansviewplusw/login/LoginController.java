package net.ajcloud.wansviewplusw.login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import net.ajcloud.wansviewplusw.BaseController;
import net.ajcloud.wansviewplusw.support.customview.LoadingManager;
import net.ajcloud.wansviewplusw.support.http.HttpCommonListener;
import net.ajcloud.wansviewplusw.support.http.RequestApiUnit;
import net.ajcloud.wansviewplusw.support.http.bean.SigninBean;
import net.ajcloud.wansviewplusw.support.http.bean.start.AppStartUpBean;
import net.ajcloud.wansviewplusw.support.utils.CipherUtil;
import net.ajcloud.wansviewplusw.support.utils.IPreferences;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;
import net.ajcloud.wansviewplusw.support.utils.WLog;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class LoginController implements BaseController , Initializable {

    @FXML
    public JFXTextField tf_name;
    @FXML
    public JFXPasswordField tf_password;
    @FXML
    public JFXButton btn_login;
    @FXML
    public JFXCheckBox cb_remember;
    private RequestApiUnit requestApiUnit;
    private OnLoginListener onLoginListener;
    private RequiredFieldValidator email_validator;
    private RequiredFieldValidator password_validator;
    private Preferences preferences;
    private ResourceBundle resourceBundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourceBundle = resources;
    }

    @Override
    public void Destroy() {

    }

    public interface OnLoginListener {
        void onLoginSuccess();
    }

    public void setOnLoginListener(OnLoginListener onLoginListener) {
        this.onLoginListener = onLoginListener;
    }

    public void init() {
        preferences = Preferences.userNodeForPackage(LoginController.class);
        if (StringUtil.isNullOrEmpty(preferences.get(IPreferences.P_SALT, ""))) {
            String salt = CipherUtil.getRandomSalt();
            preferences.put(IPreferences.P_SALT, salt);
        }
        if (preferences.getBoolean(IPreferences.P_REMEMBER_ACCOUNT, false)) {
            cb_remember.setSelected(true);
            tf_name.setText(preferences.get(IPreferences.P_LAST_ACCOUNT, null));
            tf_password.setText(CipherUtil.naclDecodeLocal(preferences.get(IPreferences.P_LAST_ACCOUNT_PASSWORD, null), preferences.get(IPreferences.P_SALT, null)));
        } else {
            cb_remember.setSelected(false);
        }
        email_validator = new RequiredFieldValidator();
        tf_name.getValidators().add(email_validator);
        tf_name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (StringUtil.equals(email_validator.getMessage(), resourceBundle.getString("login_empty")) &&
                    email_validator.isVisible() &&
                    tf_name.getText() != null && tf_name.getText().length() > 0) {
                tf_name.resetValidation();
            }
        });
        password_validator = new RequiredFieldValidator();
        tf_password.getValidators().add(password_validator);
        tf_password.textProperty().addListener((observable, oldValue, newValue) -> {
            if (StringUtil.equals(password_validator.getMessage(), resourceBundle.getString("login_empty")) &&
                    password_validator.isVisible() &&
                    tf_password.getText() != null && tf_password.getText().length() > 0) {
                tf_password.resetValidation();
            }
        });
        tf_name.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER))
                handleSubmitButtonAction();
        });
        tf_password.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER))
                handleSubmitButtonAction();
        });
        btn_login.setOnMouseClicked(event -> handleSubmitButtonAction());
    }

    private void handleSubmitButtonAction() {
        if (requestApiUnit == null) {
            requestApiUnit = new RequestApiUnit();
        }
        if (StringUtil.isNullOrEmpty(tf_name.getText())) {
            email_validator.setMessage(resourceBundle.getString("login_empty"));
            tf_name.validate();
            return;
        }
        if (StringUtil.isNullOrEmpty(tf_password.getText())) {
            password_validator.setMessage(resourceBundle.getString("login_empty"));
            tf_password.validate();
            return;
        }
        LoadingManager.getLoadingManager().showDefaultLoading((Stage) tf_name.getScene().getWindow());
        WLog.s("LoginTest","-----------------------step--1-----------------------------");
        new Thread(() -> requestApiUnit.appStartup(new HttpCommonListener<AppStartUpBean>() {
            @Override
            public void onSuccess(AppStartUpBean bean) {
                requestApiUnit.signin(tf_name.getText(), tf_password.getText(), new HttpCommonListener<SigninBean>() {
                    @Override
                    public void onSuccess(SigninBean bean) {
                        Platform.runLater(() -> {
                            WLog.s("LoginTest","-----------------------step--2-----------------------------");
                            if (cb_remember.isSelected()) {
                                preferences.putBoolean(IPreferences.P_REMEMBER_ACCOUNT, true);
                                preferences.put(IPreferences.P_LAST_ACCOUNT, bean.mail);
//                                    preferences.put(IPreferences.P_LAST_ACCOUNT_PASSWORD, CipherUtil.naclEncodeLocal(tf_password.getText(), preferences.get(IPreferences.P_SALT, "")));
                            } else {
                                preferences.putBoolean(IPreferences.P_REMEMBER_ACCOUNT, false);
                                preferences.put(IPreferences.P_LAST_ACCOUNT, "");
//                                    preferences.put(IPreferences.P_LAST_ACCOUNT_PASSWORD, "");
                            }
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
                            tf_password.clear();
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
        })).start();
    }
}
