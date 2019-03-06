package net.ajcloud.wansviewplusw.support.customview;

import com.jfoenix.controls.JFXAlert;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.ajcloud.wansviewplusw.Base;

import java.io.IOException;
import java.io.InputStream;

public class LoadingManager {

    private static LoadingManager instance;
    private JFXAlert defaultLoading;

    public static LoadingManager getLoadingManager() {
        if (instance == null) instance = new LoadingManager();
        return instance;
    }

    public void showDefaultLoading(Stage stage) {
        if (defaultLoading != null && defaultLoading.isShowing()) {
            return;
        }
        defaultLoading = new JFXAlert(stage);
        defaultLoading.initModality(Modality.APPLICATION_MODAL);
        defaultLoading.setOverlayClose(false);
        try {
            FXMLLoader loader = new FXMLLoader();
            InputStream in = Base.class.getResourceAsStream("/fxml/Loading.fxml");
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(Base.class.getResource("/fxml/Loading.fxml"));
            Pane page = loader.load(in);
            defaultLoading.setContent(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
        defaultLoading.show();
    }

    public void hideDefaultLoading() {
        if (defaultLoading != null) {
            if (!defaultLoading.isShowing()) {
                return;
            }
            defaultLoading.hideWithAnimation();
        }
    }
}
