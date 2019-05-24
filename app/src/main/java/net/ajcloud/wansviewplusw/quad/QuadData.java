package net.ajcloud.wansviewplusw.quad;

import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class QuadData {

    public QuadData() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/item_quad_list.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
