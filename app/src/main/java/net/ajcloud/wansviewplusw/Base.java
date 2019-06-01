package net.ajcloud.wansviewplusw;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import io.datafx.controller.ViewConfiguration;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import net.ajcloud.wansviewplusw.camera.CameraController;
import net.ajcloud.wansviewplusw.login.LoginController;
import net.ajcloud.wansviewplusw.main.MainController;
import net.ajcloud.wansviewplusw.support.customview.LoadingManager;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.entity.LocalInfo;
import net.ajcloud.wansviewplusw.support.http.HttpCommonListener;
import net.ajcloud.wansviewplusw.support.http.RequestApiUnit;
import net.ajcloud.wansviewplusw.support.http.bean.RefreshTokenBean;
import net.ajcloud.wansviewplusw.support.utils.CanvasPlayerUtil;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;
import net.ajcloud.wansviewplusw.support.utils.TimeService;
import net.ajcloud.wansviewplusw.support.utils.play.TcprelayHelper;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Formatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Base extends Application implements LoginController.OnLoginListener, Initializable {

    private final double LOGIN_WIDTH = 320;
    private final double LOGIN_HEIGHT = 360;
    private final double MAIN_WIDTH = 960;
    private final double MAIN_HEIGHT = 540;
    private final double DEFAULT_WIDTH = 960;
    private final double DEFAULT_HEIGHT = 540;
    private static final String NATIVE_LIBRARY_SEARCH_PATH = "vlc-3.0.6";
    private Stage mainStage;
    private Stage loginStage;
    private Scene mainScene;
    private FlowHandler mainFlowHandler;
    @FXMLViewFlowContext
    private ViewFlowContext flowContext;
    private ResourceBundle resourceBundle;
    private boolean isFullscreen = false;
    private RequestApiUnit requestApiUnit;
    private TimeService timerService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        loginStage = primaryStage;
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
        go2Login();
//        go2Main();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        requestApiUnit = new RequestApiUnit();
        flowContext = new ViewFlowContext();
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), NATIVE_LIBRARY_SEARCH_PATH);
        Native.load(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        getLocalInfo();
        TcprelayHelper.getInstance().getTcprelay().relayinit();
        CanvasPlayerUtil.getInstance().init();
//        new CheckPortUnit().check(port -> TcprelayHelper.getInstance().addPorts(port));
    }

    private void go2Login() {
        try {
            FXMLLoader loader = new FXMLLoader();
            InputStream in = Base.class.getResourceAsStream("/fxml/login.fxml");
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(Base.class.getResource("/fxml/login.fxml"));
            ResourceBundle bundle = ResourceBundle.getBundle("strings");
            loader.setResources(bundle);
            Pane page = loader.load(in);
            LoginController login = loader.getController();
            login.init();
            login.setOnLoginListener(this);
            in.close();
            Scene scene = new Scene(page, LOGIN_WIDTH, LOGIN_HEIGHT);
            final ObservableList<String> stylesheets = scene.getStylesheets();
            stylesheets.addAll(Base.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                    Base.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                    Base.class.getResource("/css/main.css").toExternalForm());
            loginStage.setScene(scene);
            loginStage.getIcons().add(new Image("/image/ic_launcher.png", 48, 48, true, true));
            loginStage.setTitle("WansviewCloud");
            loginStage.sizeToScene();
            loginStage.setResizable(false);
            loginStage.initStyle(StageStyle.DECORATED);
            loginStage.setOnCloseRequest(e -> {
                e.consume();
                close();
            });
            loginStage.show();
        } catch (Exception ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.gc();
        }
    }

    private void go2Main() {
        try {
            if (mainStage == null) {
                mainStage = new Stage(StageStyle.DECORATED);
                mainStage.getIcons().add(new Image("/image/ic_launcher.png",48, 48, true, true));
                mainStage.setTitle("WansviewCloud");
                mainStage.setMinWidth(980.0);
                mainStage.setMinHeight(580.0);
                flowContext.register("Stage", mainStage);
                flowContext.register("MainListener", mainListener);
                flowContext.register("FullscreenListener", fullscreenListener);

                Flow flow = new Flow(MainController.class);
                DefaultFlowContainer container = new DefaultFlowContainer();
                ViewConfiguration viewConfiguration = new ViewConfiguration();
                ResourceBundle bundle = ResourceBundle.getBundle("strings");
                viewConfiguration.setResources(bundle);
                mainFlowHandler = new FlowHandler(flow, flowContext, viewConfiguration);
                mainFlowHandler.start(container);
                mainScene = new Scene(container.getView(), MAIN_WIDTH, MAIN_HEIGHT);
                final ObservableList<String> stylesheets = mainScene.getStylesheets();
                stylesheets.addAll(Base.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                        Base.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                        Base.class.getResource("/css/main.css").toExternalForm());
                mainStage.setScene(mainScene);
                mainStage.setOnCloseRequest(e -> {
                    e.consume();
                    close();
                });
                mainStage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.ESCAPE && isFullscreen) {
                        fullScreen(false);
                    }
                });
            } else {
                Flow flow = new Flow(MainController.class);
                DefaultFlowContainer container = new DefaultFlowContainer();
                ViewConfiguration viewConfiguration = new ViewConfiguration();
                ResourceBundle bundle = ResourceBundle.getBundle("strings");
                viewConfiguration.setResources(bundle);
                mainFlowHandler = new FlowHandler(flow, flowContext, viewConfiguration);
                mainFlowHandler.start(container);
                mainScene.setRoot(container.getView());
            }
            mainStage.show();
            loginStage.hide();
            //开始刷新token

            if (timerService != null) {
                timerService.cancel();
                timerService = null;
            }
            timerService = new TimeService();
            AtomicInteger count = new AtomicInteger(0);
            timerService.setCount(count.get());
            timerService.setPeriod(Duration.minutes(5));
            timerService.setOnSucceeded(t -> doRefreshToken());
            timerService.start();
        } catch (Exception ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.gc();
        }
    }

    @Override
    public void onLoginSuccess() {
        go2Main();
    }

    private void close() {
        if (mainStage != null)
            mainStage.close();
        if (loginStage != null)
            loginStage.close();
        Platform.exit();
        System.exit(0);
    }

    private void getLocalInfo() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            //ni.getInetAddresses().nextElement().getAddress();
            byte[] mac = ni.getHardwareAddress();
            String sIP = address.getHostAddress();
            String sMAC = "";
            Formatter formatter = new Formatter();
            for (int i = 0; i < mac.length; i++) {
                sMAC = formatter.format(Locale.getDefault(), "%02X%s", mac[i],
                        (i < mac.length - 1) ? "-" : "").toString();

            }
            LocalInfo.getInstance().deviceName = address.getHostName(); //获取本机计算机名称
            LocalInfo.getInstance().deviceId = address.getHostName() + sIP.toLowerCase() + sMAC.toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doLogout(boolean isForce, String msg) {
        Platform.runLater(() -> LoadingManager.getLoadingManager().showDefaultLoading(mainStage));
        new RequestApiUnit().signout(new HttpCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {
                Platform.runLater(() -> {
                    //清理camera view
                    FlowHandler flowHandler = (FlowHandler) flowContext.getRegisteredObject("ContentFlowHandler");
                    BaseController controller = (BaseController) flowHandler.getCurrentView().getViewContext().getController();
                    if (controller != null) {
                        try {
                            controller.Destroy();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //清理main view
                    MainController mainController = (MainController) mainFlowHandler.getCurrentView().getViewContext().getController();
                    if (mainController != null) {
                        mainController.destroy();
                    }
                    //清理base
                    mainFlowHandler = null;
                    //清理数据
                    DeviceCache.getInstance().logout();
                    //停止刷新token
                    timerService.cancel();
                    LoadingManager.getLoadingManager().hideDefaultLoading();
                    mainStage.close();
                    loginStage.show();
                    if (isForce) {
                        showForceLogoutTips(msg);
                    }
                });
            }

            @Override
            public void onFail(int code, String msg) {
                Platform.runLater(() -> LoadingManager.getLoadingManager().hideDefaultLoading());
            }
        });
    }

    private void showForceLogoutTips(String msg) {
        JFXAlert alert = new JFXAlert(loginStage);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setPrefWidth(320);
        layout.setMinWidth(320);
        layout.setMaxWidth(320);
        layout.setBody(new Label(StringUtil.isNullOrEmpty(msg) ? resourceBundle.getString("common_error") : msg));
        JFXButton closeButton = new JFXButton(resourceBundle.getString("common_ok"));
        closeButton.setMinWidth(80);
        closeButton.setMaxWidth(80);
        closeButton.setPrefWidth(80);
        closeButton.getStyleClass().add("dialog-accept");
        closeButton.setOnAction(event -> {
            alert.hideWithAnimation();
        });
        layout.setActions(closeButton);
        alert.setContent(layout);
        alert.show();
    }

    private MainController.MainListener mainListener = () -> doLogout(false, null);

    private CameraController.FullscreenListener fullscreenListener = this::fullScreen;

    private void fullScreen(boolean isFullscreen) {
        try {
            this.isFullscreen = isFullscreen;
            mainStage.setFullScreen(isFullscreen);
            FlowHandler flowHandler = (FlowHandler) flowContext.getRegisteredObject("ContentFlowHandler");
            CameraController cameraController = (CameraController) flowHandler.getCurrentView().getViewContext().getController();
            MainController mainController = (MainController) mainFlowHandler.getCurrentView().getViewContext().getController();
            if (cameraController != null) {
                cameraController.fullscreen(isFullscreen);
            }
            if (mainController != null) {
                mainController.fullscreen(isFullscreen);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doRefreshToken() {
        requestApiUnit.refreshToken(new HttpCommonListener<RefreshTokenBean>() {
            @Override
            public void onSuccess(RefreshTokenBean bean) {

            }

            @Override
            public void onFail(int code, String msg) {
                Platform.runLater(() -> doLogout(true, msg));
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourceBundle = resources;
    }
}
