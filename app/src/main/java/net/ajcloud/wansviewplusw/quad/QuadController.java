package net.ajcloud.wansviewplusw.quad;

import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import net.ajcloud.wansviewplusw.Base;
import net.ajcloud.wansviewplusw.support.customview.PlayItemController;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@ViewController(value = "/fxml/quad.fxml", title = "Quad")
public class QuadController {
    @FXMLViewFlowContext
    private ViewFlowContext context;
    @FXML
    private StackPane content_1;
    @FXML
    private StackPane content_2;
    @FXML
    private StackPane content_3;
    @FXML
    private StackPane content_4;
    @FXML
    private StackPane content_5;
    @FXML
    private StackPane content_6;
    @FXML
    private StackPane content_7;
    @FXML
    private StackPane content_8;
    @FXML
    private StackPane content_9;

    @PostConstruct
    public void init() {
        List<Camera> deviceList = new ArrayList<>(DeviceCache.getInstance().getAllDevices());
        for (int i = 0; i < deviceList.size(); i++) {
            Camera camera = deviceList.get(i);
            if (i == 0) {
//                addCamera(content_1, camera.deviceId);
            } else if (i == 1) {
                addCamera(content_1, camera.deviceId);
//                addCamera(content_2, camera.deviceId);
//                addCamera(content_3, camera.deviceId);
//                addCamera(content_4, camera.deviceId);
            } else if (i == 2) {
//                addCamera(content_3, camera.deviceId);
            } else if (i == 3) {
            }
        }
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
