package net.ajcloud.wansviewplusw.quad;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
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

    private ResourceBundle resourceBundle;
    private Stage addGroupStage;

    private ObservableList<QuadBean> mInfos = FXCollections.observableArrayList();

    @PostConstruct
    public void init() {
        lv_quads.depthProperty().set(1);
        lv_quads.setExpanded(true);
        lv_quads.setCellFactory(param -> {
            QuadListCell quadListCell = new QuadListCell();
            quadListCell.setOnMouseClicked((v) -> {
                if (v.getButton() == MouseButton.PRIMARY) {
                    if (!content_play.isVisible()) {
                        content_play.setVisible(true);
                        content_play.setManaged(true);
                        content_play_empty.setVisible(false);
                        content_play_empty.setManaged(false);
                    }
//                    handleMouseClick(quadListCell.getItem());
                }
            });
            return quadListCell;
        });

        initData();
        initListener();
    }

    private void initData() {
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
            go2AddGroup();
        });
    }

    private void go2AddGroup() {
        try {
            FXMLLoader loader = new FXMLLoader();
            InputStream in = QuadController.class.getResourceAsStream("/fxml/add_group.fxml");
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(QuadController.class.getResource("/fxml/add_group.fxml"));
            ResourceBundle bundle = ResourceBundle.getBundle("strings");
            loader.setResources(bundle);
            Pane page = loader.load(in);
            AddGroupController addGroupController = loader.getController();
            addGroupController.init(null);
            addGroupController.setOnFinishListener(() -> {
                addGroupStage.close();
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
            addGroupStage.show();
        } catch (Exception ex) {
            Logger.getLogger(QuadController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.gc();
        }
    }

    private void addCamera(Pane parent, String deviceId) {
        try {
            FXMLLoader loader = new FXMLLoader();
            InputStream in = Base.class.getResourceAsStream("/fxml/play_item.fxml");
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(Base.class.getResource("/fxml/play_item.fxml"));
            loader.setResources(resourceBundle);
            Pane page = loader.load(in);
            PlayItemController playItemController = loader.getController();
            playItemController.init(deviceId);
            in.close();
            parent.getChildren().add(page);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourceBundle = resources;
    }
}
