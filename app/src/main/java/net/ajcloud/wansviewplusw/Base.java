package net.ajcloud.wansviewplusw;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import net.ajcloud.wansviewplusw.login.LoginController;
import net.ajcloud.wansviewplusw.main.MainController;
import net.ajcloud.wansviewplusw.support.http.HttpCommonListener;
import net.ajcloud.wansviewplusw.support.http.RequestApiUnit;
import net.ajcloud.wansviewplusw.support.http.bean.start.AppStartUpBean;
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
    private static final String NATIVE_LIBRARY_SEARCH_PATH = "app/libs/dll";
    private Stage stage;
    private RequestApiUnit requestApiUnit;
    MainController main;


    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
//        stage.setTitle("WansviewPlus");
//        stage.getIcons().add(new Image("image/ic_launcher.png"));
        stage.initStyle(StageStyle.UTILITY);
        go2Login();
        stage.show();

        startUp();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (main != null)
            main.stop();
        new Tcprelay().relaydeinit();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        requestApiUnit = new RequestApiUnit();
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), NATIVE_LIBRARY_SEARCH_PATH);
        Native.load(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
    }

    private void startUp() {
        new Tcprelay().relayinit();
        requestApiUnit.appStartup(new HttpCommonListener<AppStartUpBean>() {
            @Override
            public void onSuccess(AppStartUpBean bean) {

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
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
            stage.setScene(scene);
            stage.sizeToScene();
            stage.setResizable(false);
        } catch (Exception ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void go2Main() {
        try {
            FXMLLoader loader = new FXMLLoader();
            InputStream in = Base.class.getResourceAsStream("/fxml/main.fxml");
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(Base.class.getResource("/fxml/main.fxml"));
            Pane page = loader.load(in);
            main = loader.getController();
            main.init();
            in.close();
            Scene scene = new Scene(page, MAIN_WIDTH, MAIN_HEIGHT);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.setResizable(true);
        } catch (Exception ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 切换界面
     */
    private BaseController replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = Base.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(Base.class.getResource(fxml));
        Pane page;
        try {
            page = loader.load(in);
        } finally {
            in.close();
        }
        Scene scene = new Scene(page, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        stage.setScene(scene);
        stage.sizeToScene();
        return loader.getController();
    }

    @Override
    public void onLoginSuccess() {
        go2Main();
    }

    @Override
    public void onLoginError() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "账号或密码错误", ButtonType.OK);
        alert.showAndWait();
    }

}
