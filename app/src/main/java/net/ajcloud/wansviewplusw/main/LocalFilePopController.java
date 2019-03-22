package net.ajcloud.wansviewplusw.main;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class LocalFilePopController {

    @FXML
    private HBox h_video;
    @FXML
    private HBox h_photo;

    public void setOnClick(EventHandler<? super MouseEvent> video_value, EventHandler<? super MouseEvent> photo_value) {
        h_video.setOnMouseClicked(video_value);
        h_photo.setOnMouseClicked(photo_value);
    }
}
