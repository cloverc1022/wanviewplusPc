package net.ajcloud.wansviewplusw.quad;

import com.jfoenix.controls.JFXListCell;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class QuadListCell extends JFXListCell<QuadBean> {

    private EventHandler<? super MouseEvent> paneClicked;
    private EventHandler<? super MouseEvent> deleteClicked;

    @Override
    protected void updateItem(QuadBean item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            QuadData quadData = new QuadData(item);
            quadData.setListenner(paneClicked, deleteClicked);
            setGraphic(quadData.getPane());
        }
    }

    public void setPaneClicked(EventHandler<? super MouseEvent> paneClicked) {
        this.paneClicked = paneClicked;
    }

    public void setDeleteClicked(EventHandler<? super MouseEvent> deleteClicked) {
        this.deleteClicked = deleteClicked;
    }
}
