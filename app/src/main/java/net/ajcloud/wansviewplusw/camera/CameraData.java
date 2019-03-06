package net.ajcloud.wansviewplusw.camera;

import io.reactivex.functions.Consumer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.eventbus.Event;
import net.ajcloud.wansviewplusw.support.eventbus.EventBus;
import net.ajcloud.wansviewplusw.support.eventbus.EventType;
import net.ajcloud.wansviewplusw.support.utils.FileUtil;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;

import java.io.File;
import java.io.IOException;

public class CameraData {
    @FXML
    private Pane pane;
    @FXML
    private ImageView iv_thumbnail;
    @FXML
    private Label deviceName;
    @FXML
    private Label status;

    private String deviceId;

    private boolean isSelect = false;

    public CameraData(String deviceId) {
        this.deviceId = deviceId;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/item_device_list.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Camera camera = DeviceCache.getInstance().get(deviceId);
        status.textProperty().bind(camera.deviceStatusProperty());
        status.backgroundProperty().bind(camera.deviceStatusBgProperty());
        deviceName.textFillProperty().bind(camera.deviceNameBgProperty());
        EventBus.getInstance().register(new Consumer<Event>() {
            @Override
            public void accept(Event event) throws Exception {
                if (event.getType() == EventType.SNAPSHOT) {
                    setInfo(camera);
                }
            }
        });
    }

    public void setInfo(Camera camera) {
        deviceName.setText(camera.aliasName);
        File thumbnail = new File(FileUtil.getRealtimeImagePath(deviceId) + File.separator + "realtime_picture.jpg");
        if (thumbnail.exists()) {
            Image image = new Image(thumbnail.toURI().toString(), 196, 88, false, true, false);
            iv_thumbnail.setImage(image);
        } else {
            if (StringUtil.isNullOrEmpty(camera.snapshotUrl)) {
                Image image = new Image("/image/ic_device_default.png", 196, 88, false, true, false);
                iv_thumbnail.setImage(image);
            } else {
                Image image = new Image(camera.snapshotUrl, 196, 88, false, true, false);
                iv_thumbnail.setImage(image);
            }
        }
    }

    public Pane getPane() {
        return pane;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
        if (select) {
            deviceName.setTextFill(Color.rgb(41, 121, 255));
        } else {
            deviceName.setTextFill(Color.rgb(38, 50, 56));
        }
    }
}
