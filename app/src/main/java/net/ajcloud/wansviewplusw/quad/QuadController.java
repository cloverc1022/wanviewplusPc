package net.ajcloud.wansviewplusw.quad;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListView;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.ajcloud.wansviewplusw.Base;
import net.ajcloud.wansviewplusw.quad.add.AddGroupController;
import net.ajcloud.wansviewplusw.support.customview.PlayItemController;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

@ViewController(value = "/fxml/quad.fxml", title = "Quad")
public class QuadController implements Initializable {
    @FXMLViewFlowContext
    private ViewFlowContext context;
    @FXML
    private HBox content_list;
    @FXML
    private StackPane content_list_empty;

    @FXML
    private VBox content_left;
    @FXML
    private JFXListView<QuadBean> lv_quads;
    @FXML
    private JFXButton btn_create;

    @FXML
    private GridPane content_play;
    @FXML
    private StackPane content_play_empty;

    @FXML
    private StackPane content_1;
    @FXML
    private StackPane content_2;
    @FXML
    private StackPane content_3;
    @FXML
    private StackPane content_4;

    private ResourceBundle resourceBundle;
    private Stage addGroupStage;

    private ObservableList<QuadBean> mInfos = FXCollections.observableArrayList();

    @PostConstruct
    public void init() {
        lv_quads.depthProperty().set(1);
        lv_quads.setExpanded(true);
        lv_quads.setCellFactory(param -> {
            QuadListCell quadListCell = new QuadListCell();
            quadListCell.setPaneClicked((v -> {
                if (v.getButton() == MouseButton.PRIMARY) {
                    if (!content_play.isVisible()) {
                        content_play.setVisible(true);
                        content_play.setManaged(true);
                        content_play_empty.setVisible(false);
                        content_play_empty.setManaged(false);
                    }
                    handleMouseClick(quadListCell.getItem());
                    v.consume();
                }
            }));
            quadListCell.setDeleteClicked((v -> {
                showDeleteGroupDialog(quadListCell.getItem());
                v.consume();
            }));
            return quadListCell;
        });

        initData();
        initListener();
    }

    private void initData() {
        mInfos.clear();
        lv_quads.setItems(null);
        //initData
        if (DeviceCache.getInstance().getAllDevices() == null || DeviceCache.getInstance().getAllDevices().size() == 0) {
            content_list_empty.setVisible(true);
            content_list_empty.setManaged(true);
            content_list.setVisible(false);
            content_list.setManaged(false);
        } else {
            content_list_empty.setVisible(false);
            content_list_empty.setManaged(false);
            content_list.setVisible(true);
            content_list.setManaged(true);
            List<QuadBean> quadBeanList = QuadListCache.getInstance().getGroupList(DeviceCache.getInstance().getSigninBean().mail);
            if (quadBeanList != null && quadBeanList.size() > 0) {
                mInfos.setAll(quadBeanList);
                lv_quads.setItems(mInfos);
            }
        }
    }

    private void initListener() {
        btn_create.setOnMouseClicked((v) -> {
            go2AddGroup(null);
            v.consume();
        });
    }

    private void go2AddGroup(String groupName) {
        try {
            FXMLLoader loader = new FXMLLoader();
            InputStream in = QuadController.class.getResourceAsStream("/fxml/add_group.fxml");
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(QuadController.class.getResource("/fxml/add_group.fxml"));
            ResourceBundle bundle = ResourceBundle.getBundle("strings");
            loader.setResources(bundle);
            Pane page = loader.load(in);
            AddGroupController addGroupController = loader.getController();
            addGroupController.init(groupName);
            addGroupController.setOnFinishListener((groupName1, isAdd) -> {
                if (addGroupStage != null) {
                    addGroupStage.close();
                }
                initData();
                if (!isAdd) {
                    handleMouseClick(QuadListCache.getInstance().getQuadData(groupName1));
                }
            });
            in.close();
            Scene scene = new Scene(page, 445, 360);
            final ObservableList<String> stylesheets = scene.getStylesheets();
            stylesheets.addAll(Base.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                    Base.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                    Base.class.getResource("/css/main.css").toExternalForm());
            if (addGroupStage == null) {
                addGroupStage = new Stage();
                addGroupStage.getIcons().add(new Image("/image/ic_launcher.png"));
                addGroupStage.setTitle(resourceBundle.getString("quadScreen_creatGroup"));
                addGroupStage.sizeToScene();
                addGroupStage.setResizable(false);
                addGroupStage.initStyle(StageStyle.DECORATED);
            }
            addGroupStage.setScene(scene);
            Platform.runLater(() -> addGroupStage.show());
        } catch (Exception ex) {
            Logger.getLogger(QuadController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.gc();
        }
    }

    private void handleMouseClick(QuadBean quadBean) {
        content_1.getChildren().clear();
        content_2.getChildren().clear();
        content_3.getChildren().clear();
        content_4.getChildren().clear();
        addCamera(content_1, quadBean.getGroupName(), quadBean.getCamera_one());
        addCamera(content_2, quadBean.getGroupName(), quadBean.getCamera_two());
        addCamera(content_3, quadBean.getGroupName(), quadBean.getCamera_three());
        addCamera(content_4, quadBean.getGroupName(), quadBean.getCamera_four());
    }

    private void addCamera(Pane parent, String groupName, String deviceId) {
        try {
            FXMLLoader loader = new FXMLLoader();
            InputStream in = Base.class.getResourceAsStream("/fxml/play_item.fxml");
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(Base.class.getResource("/fxml/play_item.fxml"));
            ResourceBundle bundle = ResourceBundle.getBundle("strings");
            loader.setResources(bundle);
            Pane page = loader.load(in);
            PlayItemController playItemController = loader.getController();
            playItemController.setAddListener(() -> {
                go2AddGroup(groupName);
            });
            playItemController.init(deviceId);
            in.close();
            parent.getChildren().add(page);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showDeleteGroupDialog(QuadBean quadBean) {
        JFXAlert alert = new JFXAlert((Stage) lv_quads.getScene().getWindow());
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
            initData();
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourceBundle = resources;
    }
}
