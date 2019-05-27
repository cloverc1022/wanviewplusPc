package net.ajcloud.wansviewplusw.quad;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.IOException;

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

    public QuadData(QuadBean quadBean) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/item_quad_list.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        initData(quadBean);
    }

    private void initData(QuadBean quadBean) {
        groupName.textProperty().bind(quadBean.groupNameProperty());
        iv_one.imageProperty().bind(quadBean.camera_one_imageProperty());
        iv_two.imageProperty().bind(quadBean.camera_two_imageProperty());
        iv_three.imageProperty().bind(quadBean.camera_three_imageProperty());
        iv_four.imageProperty().bind(quadBean.camera_four_imageProperty());
    }

    public StackPane getPane() {
        return pane;
    }
}
