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
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import net.ajcloud.wansviewplusw.Base;
import net.ajcloud.wansviewplusw.support.customview.PlayItemController;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;

@ViewController(value = "/fxml/quad.fxml", title = "Quad")
public class QuadController {
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
        });
    }

    private void addCamera(Pane parent, String deviceId) {
        try {
            FXMLLoader loader = new FXMLLoader();
            InputStream in = Base.class.getResourceAsStream("/fxml/play_item.fxml");
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(Base.class.getResource("/fxml/play_item.fxml"));
            Pane page = loader.load(in);
            PlayItemController playItemController = loader.getController();
            playItemController.init(deviceId);
            in.close();
            parent.getChildren().add(page);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
