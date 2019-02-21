package net.ajcloud.wansviewplusw.camera;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import io.datafx.controller.ViewController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.http.HttpCommonListener;
import net.ajcloud.wansviewplusw.support.http.RequestApiUnit;

import javax.annotation.PostConstruct;
import java.util.List;

@ViewController(value = "/fxml/camera.fxml", title = "Camera")
public class CameraController {

    @FXML
    private Label label_num;
    @FXML
    private Label label_name;
    @FXML
    private ImageView iv_bg;
    @FXML
    private BorderPane playPane;
    @FXML
    private JFXButton btn_voice;
    @FXML
    private JFXButton btn_screenshot;
    @FXML
    private JFXButton btn_play;
    @FXML
    private JFXButton btn_record;
    @FXML
    private JFXButton btn_refresh;
    @FXML
    private JFXListView<Camera> lv_devices;

    private RequestApiUnit requestApiUnit;
    private ObservableList<Camera> mInfos = FXCollections.observableArrayList();

    /**
     * 初始化
     */
    @PostConstruct
    public void init() throws Exception {
        lv_devices.depthProperty().set(1);
        lv_devices.setExpanded(true);
        if (requestApiUnit == null) {
            requestApiUnit = new RequestApiUnit();
        }
        requestApiUnit.getDeviceList(new HttpCommonListener<java.util.List<Camera>>() {
            @Override
            public void onSuccess(List<Camera> bean) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (bean != null) {
                            label_num.setText(bean.size() + " devices");
                            mInfos.setAll(bean);
                            lv_devices.setItems(mInfos);
                            lv_devices.setCellFactory(new Callback<ListView<Camera>, ListCell<Camera>>() {
                                @Override
                                public ListCell<Camera> call(ListView<Camera> param) {
                                    return new DeviceListCell();
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }
}
