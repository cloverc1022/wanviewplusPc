package net.ajcloud.wansviewplusw.quad.add;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import net.ajcloud.wansviewplusw.BaseController;
import net.ajcloud.wansviewplusw.quad.QuadBean;
import net.ajcloud.wansviewplusw.quad.QuadListCache;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;
import org.controlsfx.control.GridView;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddGroupController implements BaseController, Initializable {

    @FXML
    private Label label_tittle;
    @FXML
    private Label label_num;
    @FXML
    private GridView<AddGroupBean> device_list;
    @FXML
    private JFXTextField text_name;
    @FXML
    private JFXButton btn_done;

    private ResourceBundle resourceBundle;
    private ObservableList<AddGroupBean> mInfos = FXCollections.observableArrayList();

    private void init(String groupName) {
        device_list.setCellFactory(param -> {
            AddGroupCell addGroupCell = new AddGroupCell();
            addGroupCell.setOnMouseClicked((v) -> {
                if (v.getButton() == MouseButton.PRIMARY) {
                    AddGroupBean addGroupBean = addGroupCell.getItem();
                    addGroupBean.setIsSelected(!addGroupBean.isIsSelected());
                }
            });
            return addGroupCell;
        });
        initData(groupName);
    }

    private void initData(String groupName) {
        List<Camera> cameras = new ArrayList<>(DeviceCache.getInstance().getAllDevices());

        if (StringUtil.isNullOrEmpty(groupName)) {
            label_tittle.setText("Create a group");
            label_num.setText("0/4");
            for (Camera camera : cameras) {
                AddGroupBean addGroupBean = new AddGroupBean(camera);
                mInfos.add(addGroupBean);
            }
        } else {
            QuadBean quadBean = QuadListCache.getInstance().getQuadData(groupName);
            label_tittle.setText("Manage group");
            if (quadBean != null) {
                int num = 0;
                if (!StringUtil.isNullOrEmpty(quadBean.getCamera_one())) {
                    num++;
                }
                if (!StringUtil.isNullOrEmpty(quadBean.getCamera_two())) {
                    num++;
                }
                if (!StringUtil.isNullOrEmpty(quadBean.getCamera_three())) {
                    num++;
                }
                if (!StringUtil.isNullOrEmpty(quadBean.getCamera_four())) {
                    num++;
                }
                label_num.setText(num + "/4");
                text_name.setText(groupName);
                for (Camera camera : cameras) {
                    AddGroupBean addGroupBean = new AddGroupBean(camera);
                    if (StringUtil.equals(camera.deviceId, quadBean.getCamera_one()) ||
                            StringUtil.equals(camera.deviceId, quadBean.getCamera_two()) ||
                            StringUtil.equals(camera.deviceId, quadBean.getCamera_two()) ||
                            StringUtil.equals(camera.deviceId, quadBean.getCamera_two())) {
                        addGroupBean.setIsSelected(true);
                    }
                    mInfos.add(addGroupBean);
                }
            }
        }
        device_list.setItems(mInfos);
    }

    private void initListener(){
        btn_done.setOnMouseClicked((v) -> {
            //TODO
        });
        text_name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!StringUtil.isNullOrEmpty(text_name.getText())&&text_name.getText().length()>0) {
                btn_done.setDisable(false);
            }else {
                btn_done.setDisable(true);
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourceBundle = resources;
    }
}
