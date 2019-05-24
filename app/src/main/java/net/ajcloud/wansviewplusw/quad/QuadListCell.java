package net.ajcloud.wansviewplusw.quad;

import com.jfoenix.controls.JFXListCell;
import net.ajcloud.wansviewplusw.camera.CameraData;
import net.ajcloud.wansviewplusw.support.device.Camera;

public class QuadListCell extends JFXListCell<Camera> {

    @Override
    protected void updateItem(Camera item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            CameraData cameraData = new CameraData(item.deviceId);
            cameraData.setInfo(item);
            setGraphic(cameraData.getPane());
        }
    }
}
