package net.ajcloud.wansviewplusw.camera;

import com.jfoenix.controls.JFXListCell;
import javafx.geometry.Insets;
import net.ajcloud.wansviewplusw.support.device.Camera;

public class DeviceListCell extends JFXListCell<Camera> {

    @Override
    protected void updateItem(Camera item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            CameraData cameraData = new CameraData();
            cameraData.setInfo(item);
            setGraphic(cameraData.getPane());
        }
    }
}
