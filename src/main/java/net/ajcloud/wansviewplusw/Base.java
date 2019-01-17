package net.ajcloud.wansviewplusw;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.ajcloud.wansviewplusw.support.http.HttpCommonListener;
import net.ajcloud.wansviewplusw.support.http.RequestApiUnit;
import net.ajcloud.wansviewplusw.support.http.bean.start.AppStartUpBean;

public class Base extends Application {

    private RequestApiUnit requestApiUnit;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));

        Scene scene = new Scene(root, 300, 275);
        primaryStage.setTitle("使用FXML");
        primaryStage.setScene(scene);
        primaryStage.show();

        startUp();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void startUp() {
        if (requestApiUnit == null) {
            requestApiUnit = new RequestApiUnit();
        }
        requestApiUnit.appStartup(new HttpCommonListener<AppStartUpBean>() {
            @Override
            public void onSuccess(AppStartUpBean bean) {

            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }
}
