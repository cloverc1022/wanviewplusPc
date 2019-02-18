package net.ajcloud.wansviewplusw.main;

import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.datafx.controller.util.VetoException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.ajcloud.wansviewplusw.BaseController;
import net.ajcloud.wansviewplusw.nine.NineController;
import net.ajcloud.wansviewplusw.camera.CameraController;
import net.ajcloud.wansviewplusw.quad.QuadController;

import javax.annotation.PostConstruct;

@ViewController(value = "/fxml/main.fxml", title = "Main")
public class MainController implements BaseController {
    @FXMLViewFlowContext
    private ViewFlowContext context;
    @FXML
    private ImageView iv_user;
    @FXML
    private VBox vb_device;
    @FXML
    private VBox vb_quad;
    @FXML
    private VBox vb_nine;
    @FXML
    private StackPane content;

    private FlowHandler flowHandler;

    /**
     * 初始化
     */
    @PostConstruct
    public void init() throws Exception {
        iv_user.setOnMouseClicked(e -> {
        });
        vb_device.setOnMouseClicked(e -> replace(vb_device.getId()));
        vb_quad.setOnMouseClicked(e -> replace(vb_quad.getId()));
        vb_nine.setOnMouseClicked(e -> replace(vb_nine.getId()));

        Flow innerFlow = new Flow(CameraController.class);
        flowHandler = innerFlow.createHandler(context);
        bindNodeToController(vb_device, CameraController.class, innerFlow, flowHandler);
        bindNodeToController(vb_quad, QuadController.class, innerFlow, flowHandler);
        bindNodeToController(vb_nine, NineController.class, innerFlow, flowHandler);

        context.register("ContentFlowHandler", flowHandler);
        context.register("ContentFlow", innerFlow);
        content.getChildren().add(flowHandler.start(new DefaultFlowContainer()));
        context.register("ContentPane", content);
    }

    private void replace(final String id) {
        new Thread(() -> {
            Platform.runLater(() -> {
                try {
                    flowHandler.handle(id);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }).start();
    }

    private void bindNodeToController(Node node, Class<?> controllerClass, Flow flow, FlowHandler flowHandler) {
        flow.withGlobalLink(node.getId(), controllerClass);
    }
}
