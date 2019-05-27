package net.ajcloud.wansviewplusw.quad;

import com.jfoenix.controls.JFXListCell;

public class QuadListCell extends JFXListCell<QuadBean> {

    @Override
    protected void updateItem(QuadBean item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            QuadData quadData = new QuadData(item);
            setGraphic(quadData.getPane());
        }
    }
}
