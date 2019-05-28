package net.ajcloud.wansviewplusw.quad.add;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.ajcloud.wansviewplusw.support.device.Camera;

public class AddGroupBean {
    public Camera camera;
    private boolean isSelected = false;
    private int index = 0;
    private StringProperty indexProperty = new SimpleStringProperty();
    private StringProperty selectedCss = new SimpleStringProperty("add_camera_unselected");

    public AddGroupBean(Camera camera) {
        this.camera = camera;
        setSelected(false);
        setIndex(0);
    }

    public AddGroupBean(Camera camera, boolean isSelected) {
        this.camera = camera;
        setSelected(isSelected);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        if (isSelected) {
            setSelectedCss("-fx-background-image: url('/image/ic_selected_camera.png');\n" +
                    "-fx-background-size: 24 24;\n" +
                    "-fx-background-position: center center;");
        } else {
            setSelectedCss("-fx-background-image: url('/image/ic_select_camera.png');\n" +
                    "-fx-background-size: 24 24;\n" +
                    "-fx-background-position: center center;");
        }
    }

    public String getSelectedCss() {
        return selectedCss.get();
    }

    public StringProperty selectedCssProperty() {
        return selectedCss;
    }

    public void setSelectedCss(String selectedCss) {
        this.selectedCss.set(selectedCss);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        setIndexProperty(index == 0 ? null : String.valueOf(index));
    }

    public String getIndexProperty() {
        return indexProperty.get();
    }

    public StringProperty indexPropertyProperty() {
        return indexProperty;
    }

    public void setIndexProperty(String indexProperty) {
        this.indexProperty.set(indexProperty);
    }
}
