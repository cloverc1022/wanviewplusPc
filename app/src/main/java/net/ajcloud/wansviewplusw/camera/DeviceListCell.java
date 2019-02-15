package net.ajcloud.wansviewplusw.camera;

import javafx.scene.control.ListCell;
import net.ajcloud.wansviewplusw.support.device.Camera;

public class DeviceListCell extends ListCell<Camera> {

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
