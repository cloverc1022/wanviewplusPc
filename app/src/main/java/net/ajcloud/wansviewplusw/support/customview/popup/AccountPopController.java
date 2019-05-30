package net.ajcloud.wansviewplusw.support.customview.popup;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import net.ajcloud.wansviewplusw.BaseController;

public class AccountPopController implements BaseController {

    @FXML
    private Label label_name;
    @FXML
    private HBox account;
    @FXML
    private HBox about;
    @FXML
    private HBox logout;

    public void initView(String name) {
        label_name.setText(name);
    }

    public void setOnLogoutListener(EventHandler<? super MouseEvent> value) {
        logout.setOnMouseClicked(value);
    }

    public void setOnAboutListener(EventHandler<? super MouseEvent> value) {
        about.setOnMouseClicked(value);
    }

    @Override
    public void Destroy() {

    }
}
