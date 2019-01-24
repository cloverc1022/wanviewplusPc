package net.ajcloud.wansviewplusw.main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import net.ajcloud.wansviewplusw.BaseController;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.http.HttpCommonListener;
import net.ajcloud.wansviewplusw.support.http.RequestApiUnit;

import java.util.List;

public class MainController implements BaseController {

    @FXML
    private ListView<Camera> deviceList;
    private RequestApiUnit requestApiUnit;
    private ObservableList<Camera> mInfos = FXCollections.observableArrayList();
    private OnItemClickListener onItemClickListener;

    @FXML
    public void handleMouseClick(MouseEvent mouseEvent) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(deviceList.getSelectionModel().getSelectedItem().deviceId);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String deviceId);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void init() {
        if (requestApiUnit == null) {
            requestApiUnit = new RequestApiUnit();
        }
        requestApiUnit.getDeviceList(new HttpCommonListener<List<Camera>>() {
            @Override
            public void onSuccess(List<Camera> bean) {
                if (bean != null) {
                    mInfos.setAll(bean);
                    deviceList.setItems(mInfos);
                    deviceList.setCellFactory(new Callback<ListView<Camera>, ListCell<Camera>>() {
                        @Override
                        public ListCell<Camera> call(ListView<Camera> param) {
                            return new DeviceListCell();
                        }
                    });
                }
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    @FXML
    private void initialize() {
    }
}
