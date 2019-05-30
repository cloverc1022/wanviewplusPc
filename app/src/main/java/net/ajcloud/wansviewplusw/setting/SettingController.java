package net.ajcloud.wansviewplusw.setting;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import net.ajcloud.wansviewplusw.BaseController;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.utils.FileUtil;

import java.awt.*;
import java.io.File;

public class SettingController implements BaseController {

    @FXML
    private Label label_location;

    public void init() {
        label_location.setText(FileUtil.getRootPath());
    }

    public void onLocationChange() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(label_location.getScene().getWindow());
        if (file != null) {
            FileUtil.setRootPath(file.getAbsolutePath());
            label_location.setText(FileUtil.getRootPath());
        }
    }

    public void onLocalPhoto() {
        new Thread(() -> {
            try {
                if (Desktop.isDesktopSupported())
                    Desktop.getDesktop().open(new File(FileUtil.getImagePath(DeviceCache.getInstance().getSigninBean().mail)));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }).start();
    }

    public void onLocalVideo() {
        new Thread(() -> {
            try {
                if (Desktop.isDesktopSupported())
                    Desktop.getDesktop().open(new File(FileUtil.getVideoPath(DeviceCache.getInstance().getSigninBean().mail)));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }).start();
    }

    @Override
    public void Destroy() {

    }
}
