package net.ajcloud.wansviewplusw.main;

import com.jfoenix.controls.JFXPopup;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import net.ajcloud.wansviewplusw.Base;
import net.ajcloud.wansviewplusw.BaseController;
import net.ajcloud.wansviewplusw.camera.CameraController;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.utils.FileUtil;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.util.Objects;

@ViewController(value = "/fxml/main.fxml", title = "Main")
public class MainController implements BaseController {

    @FXMLViewFlowContext
    private ViewFlowContext context;
    @FXML
    private VBox content_left;
    @FXML
    private VBox vb_user;
    @FXML
    private VBox vb_device;
    @FXML
    private VBox vb_quad;
    @FXML
    private VBox vb_nine;
    @FXML
    private VBox vb_local;
    @FXML
    private StackPane content;

    private FlowHandler flowHandler;

    private JFXPopup accountPop;

    private MainListener listener;

    private String currentItem;

    /**
     * 初始化
     */
    @PostConstruct
    public void init() throws Exception {
        Objects.requireNonNull(context, "context");
        listener = (MainListener) context.getRegisteredObject("MainListener");
        vb_user.setOnMouseClicked(e -> {
            showAccountPop();
        });
        vb_device.setOnMouseClicked(e -> replace(vb_device.getId()));
        vb_quad.setOnMouseClicked(e -> replace(vb_quad.getId()));
        vb_nine.setOnMouseClicked(e -> replace(vb_nine.getId()));
        vb_local.setOnMouseClicked(e -> new Thread(() -> {
            try {
                if (Desktop.isDesktopSupported())
                    Desktop.getDesktop().open(new File(FileUtil.getRootPath()));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }).start());

        currentItem = vb_device.getId();
        Flow innerFlow = new Flow(CameraController.class);
        flowHandler = innerFlow.createHandler(context);
        bindNodeToController(vb_device.getId(), CameraController.class, innerFlow, flowHandler);
//        bindNodeToController(vb_quad.getId(), QuadController.class, innerFlow, flowHandler);
//        bindNodeToController(vb_nine.getId(), NineController.class, innerFlow, flowHandler);

        context.register("ContentFlowHandler", flowHandler);
        context.register("ContentFlow", innerFlow);
        content.getChildren().add(flowHandler.start(new DefaultFlowContainer()));
        context.register("ContentPane", content);
    }

    private void replace(final String id) {
        if (!StringUtil.equals(currentItem, id)) {
            new Thread(() -> {
                Platform.runLater(() -> {
                    try {
                        flowHandler.handle(id);
                        currentItem = id;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }).start();
        }
    }

    private void bindNodeToController(String tag, Class<?> controllerClass, Flow flow, FlowHandler flowHandler) {
        flow.withGlobalLink(tag, controllerClass);
    }

    private void showAccountPop() {
        if (accountPop == null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                InputStream in = Base.class.getResourceAsStream("/fxml/pop_account.fxml");
                loader.setBuilderFactory(new JavaFXBuilderFactory());
                loader.setLocation(Base.class.getResource("/fxml/pop_account.fxml"));
                accountPop = new JFXPopup(loader.load(in));

                AccountPopController accountPopController = loader.getController();
                accountPopController.initView(DeviceCache.getInstance().getSigninBean().mail);
                accountPopController.setOnLogoutListener(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (listener != null) {
                            listener.onLogout();
                        }
                    }
                });
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        accountPop.show(vb_user, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, 72, 0);
    }

    /**
     * 全屏
     */
    public void fullscreen(boolean isFullscreen) {
        content_left.setVisible(!isFullscreen);
        content_left.setManaged(!isFullscreen);
    }

    public interface MainListener {
        void onLogout();
    }
}
