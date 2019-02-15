package net.ajcloud.wansviewplusw.main;

import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import net.ajcloud.wansviewplusw.BaseController;

@ViewController(value = "/fxml/main.fxml", title = "Main")
public class MainController implements BaseController {
    @FXMLViewFlowContext
    private ViewFlowContext context;
    @FXML
    private ImageView iv_user;
    @FXML
    private VBox vb_device;
    @FXML
    private VBox vb_quad;
    @FXML
    private VBox vb_nine;
}
