package net.ajcloud.wansviewplusw;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.ajcloud.wansviewplusw.login.LoginController;
import net.ajcloud.wansviewplusw.main.MainController;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.http.HttpCommonListener;
import net.ajcloud.wansviewplusw.support.http.RequestApiUnit;
import net.ajcloud.wansviewplusw.support.http.bean.LiveSrcBean;
import net.ajcloud.wansviewplusw.support.http.bean.start.AppStartUpBean;
import net.ajcloud.wansviewplusw.support.utils.P2pInterface;
import net.ajcloud.wansviewplusw.support.utils.RelayUtils;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Base extends Application implements LoginController.OnLoginListener {

    private final double MINIMUM_WINDOW_WIDTH = 1280;
    private final double MINIMUM_WINDOW_HEIGHT = 800;
    private static final String NATIVE_LIBRARY_SEARCH_PATH = "lib/dll";
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
        stage.setTitle("WansviewPlus");
        stage.getIcons().add(new Image("image/ic_launcher.png"));
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        go2Login();
        stage.show();

        startUp();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (main != null)
            main.stop();
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
//        RelayUtils relayUtils = new RelayUtils();
//        relayUtils.relayInit();
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
            LoginController login = (LoginController) replaceSceneContent("/fxml/login.fxml");
            login.setOnLoginListener(this);
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
            Pane page;
            try {
                page = loader.load(in);
                main = loader.getController();
                main.init();
            } finally {
                in.close();
            }
            Scene scene = new Scene(page, MINIMUM_WINDOW_WIDTH, MINIMUM_WINDOW_HEIGHT);
            stage.setScene(scene);
            stage.sizeToScene();
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
        Scene scene = new Scene(page, MINIMUM_WINDOW_WIDTH, MINIMUM_WINDOW_HEIGHT);
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
