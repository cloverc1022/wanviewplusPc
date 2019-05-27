package net.ajcloud.wansviewplusw.quad.add;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import net.ajcloud.wansviewplusw.support.device.Camera;

public class AddGroupBean {
    public Camera camera;
    private BooleanProperty isSelected = new SimpleBooleanProperty(false);

    public AddGroupBean(Camera camera) {
        this.camera = camera;
        setIsSelected(false);
    }

    public AddGroupBean(Camera camera, boolean isSelected) {
        this.camera = camera;
        setIsSelected(isSelected);
    }

    public boolean isIsSelected() {
        return isSelected.get();
    }

    public BooleanProperty isSelectedProperty() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected.set(isSelected);
    }
}
