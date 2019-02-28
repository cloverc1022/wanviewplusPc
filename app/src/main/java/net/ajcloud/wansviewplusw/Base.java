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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import net.ajcloud.wansviewplusw.camera.CameraController;
import net.ajcloud.wansviewplusw.login.LoginController;
import net.ajcloud.wansviewplusw.main.MainController;
import org.tcprelay.Tcprelay;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Base extends Application implements LoginController.OnLoginListener {

    private final double LOGIN_WIDTH = 320;
    private final double LOGIN_HEIGHT = 360;
    private final double MAIN_WIDTH = 960;
    private final double MAIN_HEIGHT = 540;
    private final double DEFAULT_WIDTH = 960;
    private final double DEFAULT_HEIGHT = 540;
    private static final String NATIVE_LIBRARY_SEARCH_PATH = "app/lib/dll";
    private Stage mainStage;
    private Stage loginStage;

    @FXMLViewFlowContext
    private ViewFlowContext flowContext;

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
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), NATIVE_LIBRARY_SEARCH_PATH);
        Native.load(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
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
        }
    }

    private void go2Main() {
        try {
            mainStage = new Stage(StageStyle.DECORATED);
            mainStage.setMinWidth(980.0);
            mainStage.setMinHeight(580.0);
            Flow flow = new Flow(MainController.class);
            DefaultFlowContainer container = new DefaultFlowContainer();
            flowContext = new ViewFlowContext();
            flowContext.register("Stage", mainStage);
            flowContext.register("MainListener", listener);
            flow.createHandler(flowContext).start(container);


            Scene scene = new Scene(container.getView(), MAIN_WIDTH, MAIN_HEIGHT);
            final ObservableList<String> stylesheets = scene.getStylesheets();
            stylesheets.addAll(Base.class.getResource("/css/jfoenix-fonts.css").toExternalForm(),
                    Base.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                    Base.class.getResource("/css/main.css").toExternalForm());
            mainStage.setScene(scene);
            mainStage.setOnCloseRequest(e -> {
                e.consume();
                close();
            });
            mainStage.show();
            loginStage.hide();
        } catch (Exception ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onLoginSuccess() {
        go2Main();
    }

    private void close() {
        if (mainStage != null) {
            FlowHandler flowHandler = (FlowHandler) flowContext.getRegisteredObject("ContentFlowHandler");
            Class<?> controller = flowHandler.getCurrentViewControllerClass();
            if (controller != null) {
                try {
                    CameraController cameraController = (CameraController) controller.newInstance();
                    cameraController.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mainStage.close();
        }
        if (loginStage != null)
            loginStage.close();

        Platform.exit();
        System.exit(0);
    }


    private MainController.MainListener listener = new MainController.MainListener() {
        @Override
        public void onLogout() {
            mainStage.close();
            loginStage.show();
        }
    };
}
