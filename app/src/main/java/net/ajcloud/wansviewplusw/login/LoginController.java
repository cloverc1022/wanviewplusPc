package net.ajcloud.wansviewplusw.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import net.ajcloud.wansviewplusw.BaseController;
import net.ajcloud.wansviewplusw.support.http.HttpCommonListener;
import net.ajcloud.wansviewplusw.support.http.RequestApiUnit;
import net.ajcloud.wansviewplusw.support.http.bean.SigninBean;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;

public class LoginController implements BaseController {

    public JFXTextField tf_name;
    public JFXPasswordField tf_password;
    private RequestApiUnit requestApiUnit;
    private OnLoginListener onLoginListener;

    public interface OnLoginListener {
        void onLoginSuccess();

        void onLoginError();
    }

    public void setOnLoginListener(OnLoginListener onLoginListener) {
        this.onLoginListener = onLoginListener;
    }

    public void handleSubmitButtonAction(ActionEvent event) {
        if (requestApiUnit == null) {
            requestApiUnit = new RequestApiUnit();
        }
        if (StringUtil.isNullOrEmpty(tf_name.getText()) ||
                StringUtil.isNullOrEmpty(tf_password.getText())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "账号或密码不能为空", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        requestApiUnit.signin(tf_name.getText(), tf_password.getText(), new HttpCommonListener<SigninBean>() {
            @Override
            public void onSuccess(SigninBean bean) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (onLoginListener != null) {
                            onLoginListener.onLoginSuccess();
                        }
                    }
                });
            }

            @Override
            public void onFail(int code, String msg) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (onLoginListener != null) {
                            onLoginListener.onLoginError();
                        }
                    }
                });
            }
        });
    }

    @FXML
    private void initialize() {
    }
}
