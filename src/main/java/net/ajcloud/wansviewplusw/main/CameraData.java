package net.ajcloud.wansviewplusw.main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;

import java.io.IOException;

public class CameraData {
    @FXML
    private Pane pane;
    @FXML
    private ImageView icon;
    @FXML
    private Label deviceName;
    @FXML
    private Label deviceId;

    public CameraData() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/item_device_list.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setInfo(Camera camera) {
        String imagePath = "image/ic_launcher.png";
        if (StringUtil.equals(camera.deviceMode.toUpperCase(), "K3C")) {
            imagePath = "image/ic_model_k3.png";
        } else if (StringUtil.equals(camera.deviceMode.toUpperCase(), "Q3S")) {
            imagePath = "image/ic_model_q3.png";
        }
        Image image = new Image(imagePath);
        icon.setImage(image);
        deviceName.setText(camera.aliasName);
        deviceId.setText(camera.deviceId);
    }

    public Pane getPane() {
        return pane;
    }
}
