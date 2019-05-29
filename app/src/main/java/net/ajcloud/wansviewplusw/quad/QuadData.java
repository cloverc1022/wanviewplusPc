package net.ajcloud.wansviewplusw.quad;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.ResourceBundle;

public class QuadData {

    @FXML
    private StackPane pane;
    @FXML
    private ImageView iv_one;
    @FXML
    private ImageView iv_two;
    @FXML
    private ImageView iv_three;
    @FXML
    private ImageView iv_four;
    @FXML
    private StackPane sp_play_anim;
    @FXML
    private Label groupName;
    @FXML
    private ImageView iv_delete;

    private ResourceBundle resourceBundle;

    private QuadBean quadBean;

    public QuadData(QuadBean quadBean) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/item_quad_list.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.quadBean = quadBean;
        initData();
    }

    private void initData() {
        resourceBundle = ResourceBundle.getBundle("strings");
        groupName.textProperty().bind(quadBean.groupNameProperty());
        iv_one.imageProperty().bind(quadBean.camera_one_imageProperty());
        iv_two.imageProperty().bind(quadBean.camera_two_imageProperty());
        iv_three.imageProperty().bind(quadBean.camera_three_imageProperty());
        iv_four.imageProperty().bind(quadBean.camera_four_imageProperty());
        sp_play_anim.visibleProperty().bind(quadBean.isAnimShowProperty());
        groupName.textFillProperty().bind(quadBean.groupNameBgProperty());
    }

    public void setListenner(EventHandler<? super MouseEvent> paneClick, EventHandler<? super MouseEvent> deleteClick) {
        pane.setOnMouseClicked(paneClick);
        iv_delete.setOnMouseClicked(deleteClick);
    }

    public StackPane getPane() {
        return pane;
    }


}
