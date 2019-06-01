package net.ajcloud.wansviewplusw.quad;

import javafx.beans.property.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.utils.FileUtil;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;

import java.io.File;

public class QuadBean {
    private StringProperty groupName = new SimpleStringProperty();
    private String camera_one;
    private String camera_two;
    private String camera_three;
    private String camera_four;
    private boolean isSelected;
    private BooleanProperty isAnimShow = new SimpleBooleanProperty(false);
    private ObjectProperty<Paint> groupNameBg = new SimpleObjectProperty<>(Color.rgb(38, 50, 56, 1));

    private ObjectProperty<Image> camera_one_image = new SimpleObjectProperty<>(null);
    private ObjectProperty<Image> camera_two_image = new SimpleObjectProperty<>(null);
    private ObjectProperty<Image> camera_three_image = new SimpleObjectProperty<>(null);
    private ObjectProperty<Image> camera_four_image = new SimpleObjectProperty<>(null);

    public void setGroupName(String groupName) {
        this.groupName.set(groupName);
    }

    public String getGroupName() {
        return groupName.get();
    }

    public StringProperty groupNameProperty() {
        return groupName;
    }

    public String getCamera_one() {
        return camera_one;
    }

    public void setCamera_one(String camera_one) {
        if (!StringUtil.equals(this.camera_one, camera_one)) {
            this.camera_one = camera_one;
            if (StringUtil.isNullOrEmpty(camera_one)) {
                Image image = new Image("/image/ic_device_default.png", 98, 44, false, true, false);
                setCamera_one_image(image);
            } else {
                File thumbnail = new File(FileUtil.getRealtimeImagePath(camera_one) + File.separator + "realtime_picture.jpg");
                if (thumbnail.exists()) {
                    Image image = new Image(thumbnail.toURI().toString(), 98, 44, false, true, false);
                    setCamera_one_image(image);
                } else {
                    Camera camera = DeviceCache.getInstance().get(camera_one);
                    if (camera == null || StringUtil.isNullOrEmpty(camera.snapshotUrl)) {
                        Image image = new Image("/image/ic_device_default.png", 98, 44, false, true, false);
                        setCamera_one_image(image);
                    } else {
                        Image image = new Image(camera.snapshotUrl, 98, 44, false, true, false);
                        setCamera_one_image(image);
                    }
                }
            }
        }
    }

    public String getCamera_two() {
        return camera_two;
    }

    public void setCamera_two(String camera_two) {
        if (!StringUtil.equals(this.camera_two, camera_two)) {
            this.camera_two = camera_two;
            if (StringUtil.isNullOrEmpty(camera_two)) {
                Image image = new Image("/image/ic_device_default.png", 98, 44, false, true, false);
                setCamera_two_image(image);
            } else {
                File thumbnail = new File(FileUtil.getRealtimeImagePath(camera_two) + File.separator + "realtime_picture.jpg");
                if (thumbnail.exists()) {
                    Image image = new Image(thumbnail.toURI().toString(), 98, 44, false, true, false);
                    setCamera_two_image(image);
                } else {
                    Camera camera = DeviceCache.getInstance().get(camera_two);
                    if (camera == null || StringUtil.isNullOrEmpty(camera.snapshotUrl)) {
                        Image image = new Image("/image/ic_device_default.png", 98, 44, false, true, false);
                        setCamera_two_image(image);
                    } else {
                        Image image = new Image(camera.snapshotUrl, 98, 44, false, true, false);
                        setCamera_two_image(image);
                    }
                }
            }
        }
    }

    public String getCamera_three() {
        return camera_three;
    }

    public void setCamera_three(String camera_three) {
        if (!StringUtil.equals(this.camera_three, camera_three)) {
            this.camera_three = camera_three;
            if (StringUtil.isNullOrEmpty(camera_three)) {
                Image image = new Image("/image/ic_device_default.png", 98, 44, false, true, false);
                setCamera_three_image(image);
            } else {
                File thumbnail = new File(FileUtil.getRealtimeImagePath(camera_three) + File.separator + "realtime_picture.jpg");
                if (thumbnail.exists()) {
                    Image image = new Image(thumbnail.toURI().toString(), 98, 44, false, true, false);
                    setCamera_three_image(image);
                } else {
                    Camera camera = DeviceCache.getInstance().get(camera_three);
                    if (camera == null || StringUtil.isNullOrEmpty(camera.snapshotUrl)) {
                        Image image = new Image("/image/ic_device_default.png", 98, 44, false, true, false);
                        setCamera_three_image(image);
                    } else {
                        Image image = new Image(camera.snapshotUrl, 98, 44, false, true, false);
                        setCamera_three_image(image);
                    }
                }
            }
        }
    }

    public String getCamera_four() {
        return camera_four;
    }

    public void setCamera_four(String camera_four) {
        if (!StringUtil.equals(this.camera_four, camera_four)) {
            this.camera_four = camera_four;
            if (StringUtil.isNullOrEmpty(camera_four)) {
                Image image = new Image("/image/ic_device_default.png", 98, 44, false, true, false);
                setCamera_four_image(image);
            } else {
                File thumbnail = new File(FileUtil.getRealtimeImagePath(camera_four) + File.separator + "realtime_picture.jpg");
                if (thumbnail.exists()) {
                    Image image = new Image(thumbnail.toURI().toString(), 98, 44, false, true, false);
                    setCamera_four_image(image);
                } else {
                    Camera camera = DeviceCache.getInstance().get(camera_four);
                    if (camera == null || StringUtil.isNullOrEmpty(camera.snapshotUrl)) {
                        Image image = new Image("/image/ic_device_default.png", 98, 44, false, true, false);
                        setCamera_four_image(image);
                    } else {
                        Image image = new Image(camera.snapshotUrl, 98, 44, false, true, false);
                        setCamera_four_image(image);
                    }
                }
            }
        }
    }

    public Image getCamera_one_image() {
        return camera_one_image.get();
    }

    public ObjectProperty<Image> camera_one_imageProperty() {
        return camera_one_image;
    }

    public void setCamera_one_image(Image camera_one_image) {
        this.camera_one_image.set(camera_one_image);
    }

    public Image getCamera_two_image() {
        return camera_two_image.get();
    }

    public ObjectProperty<Image> camera_two_imageProperty() {
        return camera_two_image;
    }

    public void setCamera_two_image(Image camera_two_image) {
        this.camera_two_image.set(camera_two_image);
    }

    public Image getCamera_three_image() {
        return camera_three_image.get();
    }

    public ObjectProperty<Image> camera_three_imageProperty() {
        return camera_three_image;
    }

    public void setCamera_three_image(Image camera_three_image) {
        this.camera_three_image.set(camera_three_image);
    }

    public Image getCamera_four_image() {
        return camera_four_image.get();
    }

    public ObjectProperty<Image> camera_four_imageProperty() {
        return camera_four_image;
    }

    public void setCamera_four_image(Image camera_four_image) {
        this.camera_four_image.set(camera_four_image);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        setIsAnimShow(selected);
        if (selected) {
            setGroupNameBg(Color.rgb(41, 121, 255, 1));
        } else {
            setGroupNameBg(Color.rgb(38, 50, 56, 1));
        }
    }

    public boolean isIsAnimShow() {
        return isAnimShow.get();
    }

    public BooleanProperty isAnimShowProperty() {
        return isAnimShow;
    }

    public void setIsAnimShow(boolean isAnimShow) {
        this.isAnimShow.set(isAnimShow);
    }

    public Paint getGroupNameBg() {
        return groupNameBg.get();
    }

    public ObjectProperty<Paint> groupNameBgProperty() {
        return groupNameBg;
    }

    public void setGroupNameBg(Paint groupNameBg) {
        this.groupNameBg.set(groupNameBg);
    }
}
