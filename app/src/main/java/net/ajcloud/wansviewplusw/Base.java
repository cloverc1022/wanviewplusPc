package net.ajcloud.wansviewplusw;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
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
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import net.ajcloud.wansviewplusw.camera.CameraController;
import net.ajcloud.wansviewplusw.login.LoginController;
import net.ajcloud.wansviewplusw.main.MainController;
import net.ajcloud.wansviewplusw.support.customview.LoadingManager;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.entity.LocalInfo;
import net.ajcloud.wansviewplusw.support.http.HttpCommonListener;
import net.ajcloud.wansviewplusw.support.http.RequestApiUnit;
import org.tcprelay.Tcprelay;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Formatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Base extends Application implements LoginController.OnLoginListener {

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
    private boolean isFullscreen = false;

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

    @Override
    public void stop() throws Exception {
        super.stop();
        new Tcprelay().relaydeinit();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        new Tcprelay().relayinit();
        flowContext = new ViewFlowContext();
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), NATIVE_LIBRARY_SEARCH_PATH);
        Native.load(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        getLocalInfo();
    }

    private void go2Login() {
        try {
            FXMLLoader loader = new FXMLLoader();
            InputStream in = Base.class.getResourceAsStream("/fxml/login.fxml");
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(Base.class.getResource("/fxml/login.fxml"));
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
            loginStage.sizeToScene();
            loginStage.setResizable(false);
            loginStage.initStyle(StageStyle.UTILITY);
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
                mainStage.getIcons().add(new Image("/image/ic_launcher.png"));
                mainStage.setTitle("WansviewCloud");
                mainStage.setMinWidth(980.0);
                mainStage.setMinHeight(580.0);
                flowContext.register("Stage", mainStage);
                flowContext.register("MainListener", mainListener);
                flowContext.register("FullscreenListener", fullscreenListener);

                Flow flow = new Flow(MainController.class);
                DefaultFlowContainer container = new DefaultFlowContainer();
                mainFlowHandler = flow.createHandler(flowContext);
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
                mainFlowHandler = flow.createHandler(flowContext);
                mainFlowHandler.start(container);
                mainScene.setRoot(container.getView());
            }
            mainStage.show();
            loginStage.hide();
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

    private MainController.MainListener mainListener = new MainController.MainListener() {
        @Override
        public void onLogout() {
            LoadingManager.getLoadingManager().showDefaultLoading(mainStage);
            new RequestApiUnit().signout(new HttpCommonListener<Object>() {
                @Override
                public void onSuccess(Object bean) {
                    Platform.runLater(() -> {
                        //清理camera view
                        FlowHandler flowHandler = (FlowHandler) flowContext.getRegisteredObject("ContentFlowHandler");
                        CameraController cameraController = (CameraController) flowHandler.getCurrentView().getViewContext().getController();
                        if (cameraController != null) {
                            try {
                                cameraController.destroy();
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
                        LoadingManager.getLoadingManager().hideDefaultLoading();
                        mainStage.close();
                        loginStage.show();
                    });
                }

                @Override
                public void onFail(int code, String msg) {
                    LoadingManager.getLoadingManager().hideDefaultLoading();
                }
            });
        }
    };

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
}
