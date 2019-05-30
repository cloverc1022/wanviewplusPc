package net.ajcloud.wansviewplusw.about;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import net.ajcloud.wansviewplusw.BaseController;
import net.ajcloud.wansviewplusw.support.http.ApiConstant;

import java.awt.*;
import java.net.URI;

public class AboutController implements BaseController {

    @FXML
    public AnchorPane content_terms;
    @FXML
    public AnchorPane content_policy;

    public void onContentTerms() {
        try {
            Desktop.getDesktop().browse(new URI(ApiConstant.URL_AGREEMENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onContentPolicy() {
        try {
            Desktop.getDesktop().browse(new URI(ApiConstant.URL_PRIVACY));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void Destroy() {

    }
}
