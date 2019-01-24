package net.ajcloud.wansviewplusw;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.ajcloud.wansviewplusw.login.LoginController;
import net.ajcloud.wansviewplusw.main.MainController;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.http.HttpCommonListener;
import net.ajcloud.wansviewplusw.support.http.RequestApiUnit;
import net.ajcloud.wansviewplusw.support.http.bean.LiveSrcBean;
import net.ajcloud.wansviewplusw.support.http.bean.start.AppStartUpBean;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Base extends Application implements LoginController.OnLoginListener, MainController.OnItemClickListener {

    private final double MINIMUM_WINDOW_WIDTH = 900.0;
    private final double MINIMUM_WINDOW_HEIGHT = 600.0;
    private Stage stage;
    private RequestApiUnit requestApiUnit;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle("WansviewPlus");
        stage.setMinWidth(MINIMUM_WINDOW_WIDTH);
        stage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
        go2Login();
        stage.show();

        startUp();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        requestApiUnit = new RequestApiUnit();
    }

    private void startUp() {
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
            MainController main = (MainController) replaceSceneContent("/fxml/main.fxml");
            main.init();
            main.setOnItemClickListener(this);
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
        Scene scene = new Scene(page, 800, 600);
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

    @Override
    public void onItemClick(String deviceId) {
        Camera camera = DeviceCache.getInstance().get(deviceId);
        if (camera != null) {
            requestApiUnit.getLiveSrcToken(deviceId, 1, 5, new HttpCommonListener<LiveSrcBean>() {
                @Override
                public void onSuccess(LiveSrcBean bean) {

                }

                @Override
                public void onFail(int code, String msg) {

                }
            });
        }
    }
}
