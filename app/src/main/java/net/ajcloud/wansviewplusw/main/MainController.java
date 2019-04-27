package net.ajcloud.wansviewplusw.main;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPopup;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.ajcloud.wansviewplusw.Base;
import net.ajcloud.wansviewplusw.BaseController;
import net.ajcloud.wansviewplusw.camera.CameraController;
import net.ajcloud.wansviewplusw.setting.SettingController;
import net.ajcloud.wansviewplusw.support.customview.popup.AccountPopController;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;
import net.ajcloud.wansviewplusw.support.utils.WLog;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@ViewController(value = "/fxml/main.fxml", title = "Main")
public class MainController implements BaseController {

    private static final String CONTENT_DEVICE = "CONTENT_DEVICE";
    private static final String CONTENT_QUAD = "CONTENT_QUAD";
    private static final String CONTENT_NINE = "CONTENT_NINE";
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

    private Stage aboutStage;

    private Stage settingStage;

    private FlowHandler flowHandler;

    private JFXPopup accountPop;

    private MainListener listener;

    private String currentItem;

    /**
     * 初始化
     */
    @PostConstruct
    public void init() throws Exception {
        WLog.s("LoginTest","-----------------------step--4-----------------------------");
        Objects.requireNonNull(context, "context");
        listener = (MainListener) context.getRegisteredObject("MainListener");
        //init menu
        DropShadow dropShadow = new DropShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.26), 10, 0.12, -1, 2);
        content_left.setEffect(dropShadow);
        content_left.setStyle("-fx-background-color: white");
        //init listener
        vb_user.setOnMouseClicked(e -> {
            showAccountPop();
        });
        vb_device.setOnMouseClicked(e -> replace(CONTENT_DEVICE));
        vb_quad.setOnMouseClicked(e -> replace(CONTENT_QUAD));
        vb_nine.setOnMouseClicked(e -> replace(CONTENT_NINE));
        vb_local.setOnMouseClicked(e -> go2Setting());

        currentItem = CONTENT_DEVICE;
        Flow innerFlow = new Flow(CameraController.class);
        flowHandler = innerFlow.createHandler(context);
        bindNodeToController(CONTENT_DEVICE, CameraController.class, innerFlow, flowHandler);
//        bindNodeToController(CONTENT_QUAD, QuadController.class, innerFlow, flowHandler);
//        bindNodeToController(CONTENT_NINE, NineController.class, innerFlow, flowHandler);

        context.register("ContentFlowHandler", flowHandler);
        context.register("ContentFlow", innerFlow);
        content.getChildren().add(flowHandler.start(new DefaultFlowContainer()));
        context.register("ContentPane", content);
        WLog.s("LoginTest","-----------------------step--5-----------------------------");
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

    private void go2About() {
        try {
            if (aboutStage == null) {
                FXMLLoader loader = new FXMLLoader();
                InputStream in = Base.class.getResourceAsStream("/fxml/about.fxml");
                loader.setBuilderFactory(new JavaFXBuilderFactory());
                loader.setLocation(Base.class.getResource("/fxml/about.fxml"));
                Pane page = loader.load(in);
                in.close();
                Scene scene = new Scene(page, 530, 240);
                final ObservableList<String> stylesheets = scene.getStylesheets();
                stylesheets.addAll(Base.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                        Base.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                        Base.class.getResource("/css/main.css").toExternalForm());
                aboutStage = new Stage();
                aboutStage.setScene(scene);
                aboutStage.getIcons().add(new Image("/image/ic_launcher.png"));
                aboutStage.setTitle("WansviewCloud");
                aboutStage.sizeToScene();
                aboutStage.setResizable(false);
                aboutStage.initStyle(StageStyle.DECORATED);
            }
            aboutStage.show();
        } catch (Exception ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.gc();
        }
    }

    private void go2Setting() {
        try {
            if (settingStage == null) {
                FXMLLoader loader = new FXMLLoader();
                InputStream in = Base.class.getResourceAsStream("/fxml/setting.fxml");
                loader.setBuilderFactory(new JavaFXBuilderFactory());
                loader.setLocation(Base.class.getResource("/fxml/setting.fxml"));
                Pane page = loader.load(in);
                SettingController settingController = loader.getController();
                settingController.init();
                in.close();
                Scene scene = new Scene(page, 432, 320);
                final ObservableList<String> stylesheets = scene.getStylesheets();
                stylesheets.addAll(Base.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                        Base.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                        Base.class.getResource("/css/main.css").toExternalForm());
                settingStage = new Stage();
                settingStage.setScene(scene);
                settingStage.getIcons().add(new Image("/image/ic_launcher.png"));
                settingStage.setTitle("Setting");
                settingStage.sizeToScene();
                settingStage.setResizable(false);
                settingStage.initStyle(StageStyle.DECORATED);
            }
            settingStage.show();
        } catch (Exception ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.gc();
        }
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
                accountPopController.setOnLogoutListener(event -> {
                    accountPop.hide();
                    showLogoutDialog();
                });
                accountPopController.setOnAboutListener(event -> go2About());
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

    private void showLogoutDialog() {
        JFXAlert alert = new JFXAlert((Stage) vb_user.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new javafx.scene.control.Label("Sign out?"));
        JFXButton closeButton = new JFXButton("Ok");
        closeButton.setMinWidth(100);
        closeButton.setMaxWidth(100);
        closeButton.setPrefWidth(100);
        closeButton.getStyleClass().add("dialog-accept");
        closeButton.setOnAction(event -> {
            alert.hideWithAnimation();
            if (listener != null) {
                listener.onLogout();
            }
        });
        JFXButton cancelButton = new JFXButton("Cancel");
        cancelButton.setMinWidth(100);
        cancelButton.setMaxWidth(100);
        cancelButton.setPrefWidth(100);
        cancelButton.getStyleClass().add("dialog-cancel");
        cancelButton.setOnAction(event -> {
            alert.hideWithAnimation();
        });
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().add(closeButton);
        hBox.getChildren().add(cancelButton);
        layout.setActions(hBox);
        alert.setContent(layout);
        alert.show();
    }

    public void destroy() {
        vb_user.setOnMouseClicked(null);
        vb_device.setOnMouseClicked(null);
        vb_quad.setOnMouseClicked(null);
        vb_nine.setOnMouseClicked(null);
        vb_local.setOnMouseClicked(null);
        content = null;
        flowHandler = null;
    }
}
