package net.ajcloud.wansviewplusw.quad.add;

import org.controlsfx.control.GridCell;

public class AddGroupCell extends GridCell<AddGroupBean> {

    @Override
    protected void updateItem(AddGroupBean item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            AddGroupData quadData = new AddGroupData(item);
            setGraphic(quadData.getPane());
        }
    }
}
