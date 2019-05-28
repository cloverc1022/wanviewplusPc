package net.ajcloud.wansviewplusw.quad.add;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import net.ajcloud.wansviewplusw.support.utils.FileUtil;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;

import java.io.File;
import java.io.IOException;

public class AddGroupData {

    @FXML
    private AnchorPane pane;
    @FXML
    private ImageView iv_thumbnail;
    @FXML
    private Label label_deviceId;
    @FXML
    private Label label_selected;

    public AddGroupData(AddGroupBean item) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/item_add_group.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        initData(item);
    }

    private void initData(AddGroupBean bean) {
        File thumbnail = new File(FileUtil.getRealtimeImagePath(bean.camera.deviceId) + File.separator + "realtime_picture.jpg");
        if (thumbnail.exists()) {
            Image image = new Image(thumbnail.toURI().toString(), 196, 88, false, true, false);
            iv_thumbnail.setImage(image);
        } else {
            if (StringUtil.isNullOrEmpty(bean.camera.snapshotUrl)) {
                Image image = new Image("/image/ic_device_default.png", 196, 88, false, true, false);
                iv_thumbnail.setImage(image);
            } else {
                Image image = new Image(bean.camera.snapshotUrl, 196, 88, false, true, false);
                iv_thumbnail.setImage(image);
            }
        }
        label_deviceId.setText(bean.camera.deviceId);
        label_selected.styleProperty().bind(bean.selectedCssProperty());
    }

    public AnchorPane getPane() {
        return pane;
    }
}
