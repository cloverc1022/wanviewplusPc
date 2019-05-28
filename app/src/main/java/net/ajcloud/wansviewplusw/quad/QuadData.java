package net.ajcloud.wansviewplusw.quad;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

        iv_delete.setOnMouseClicked((v) -> showDeleteGroupDialog());
    }

    private void initData() {
        resourceBundle = ResourceBundle.getBundle("strings");
        groupName.textProperty().bind(quadBean.groupNameProperty());
        iv_one.imageProperty().bind(quadBean.camera_one_imageProperty());
        iv_two.imageProperty().bind(quadBean.camera_two_imageProperty());
        iv_three.imageProperty().bind(quadBean.camera_three_imageProperty());
        iv_four.imageProperty().bind(quadBean.camera_four_imageProperty());
    }

    public StackPane getPane() {
        return pane;
    }

    private void showDeleteGroupDialog() {
        JFXAlert alert = new JFXAlert((Stage) pane.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new javafx.scene.control.Label(resourceBundle.getString("quadScreen_delete_group")));
        JFXButton deleteButton = new JFXButton(resourceBundle.getString("common_ok"));
        deleteButton.setMinWidth(100);
        deleteButton.setMaxWidth(100);
        deleteButton.setPrefWidth(100);
        deleteButton.getStyleClass().add("dialog-accept");
        deleteButton.setOnAction(event -> {
            QuadListCache.getInstance().deleteQuadData(quadBean.getGroupName());
            alert.hideWithAnimation();
        });
        JFXButton cancelButton = new JFXButton(resourceBundle.getString("common_cancel"));
        cancelButton.setMinWidth(100);
        cancelButton.setMaxWidth(100);
        cancelButton.setPrefWidth(100);
        cancelButton.getStyleClass().add("dialog-cancel");
        cancelButton.setOnAction(event -> {
            alert.hideWithAnimation();
        });
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().add(deleteButton);
        hBox.getChildren().add(cancelButton);
        layout.setActions(hBox);
        alert.setContent(layout);
        alert.show();
    }
}
