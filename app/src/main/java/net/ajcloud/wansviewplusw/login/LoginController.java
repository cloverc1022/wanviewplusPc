package net.ajcloud.wansviewplusw.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import net.ajcloud.wansviewplusw.BaseController;
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
//        email_validator = new RequiredFieldValidator();
//        email_validator.setMessage("Can not be empty");
//        email_validator.setIcon(GlyphsBuilder.create(FontAwesomeIconView.class)
//                .glyph(FontAwesomeIcon.WARNING)
//                .style("-fx-text-fill:#FF5252;-fx-font-size:10;")
//                .styleClass("error")
//                .build());
//        tf_name.getValidators().add(email_validator);
//
//        password_validator = new RequiredFieldValidator();
//        password_validator.setMessage("Can not be empty");
//        password_validator.setIcon(GlyphsBuilder.create(FontAwesomeIconView.class)
//                .glyph(FontAwesomeIcon.WARNING)
//                .style("-fx-text-fill:#FF5252;-fx-font-size:10;")
//                .styleClass("error")
//                .build());
//        tf_password.getValidators().add(password_validator);
    }

    @FXML
    private void handleSubmitButtonAction(ActionEvent event) {
        if (requestApiUnit == null) {
            requestApiUnit = new RequestApiUnit();
        }
        if (StringUtil.isNullOrEmpty(tf_name.getText())) {
//            tf_name.validate();
            return;
        }
        if (StringUtil.isNullOrEmpty(tf_password.getText())) {
//            tf_password.validate();
            return;
        }
        requestApiUnit.appStartup(new HttpCommonListener<AppStartUpBean>() {
            @Override
            public void onSuccess(AppStartUpBean bean) {
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
                        showErrorAlert(msg);
                    }
                });
            }

            @Override
            public void onFail(int code, String msg) {
                showErrorAlert(msg);
            }
        });
    }

    @FXML
    private void initialize() {
    }

    private void showErrorAlert(String string) {
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                JFXAlert alert = new JFXAlert((Stage) tf_name.getScene().getWindow());
//                alert.initModality(Modality.WINDOW_MODAL);
//                alert.setOverlayClose(false);
//                JFXDialogLayout layout = new JFXDialogLayout();
//                layout.setBody(new Label(string));
//                JFXButton closeButton = new JFXButton("OK");
//                closeButton.getStyleClass().add("dialog-accept");
//                closeButton.setOnAction(event -> alert.hideWithAnimation());
//                layout.setActions(closeButton);
//                alert.setContent(layout);
//                alert.show();
//            }
//        });
    }
}
