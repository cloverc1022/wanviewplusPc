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
import net.ajcloud.wansviewplusw.support.utils.WLog;
import org.controlsfx.control.GridView;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class AddGroupController implements BaseController, Initializable {

    private static final String TAG = "AddGroupController";
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

    private QuadBean quadBean;
    private ResourceBundle resourceBundle;
    private ObservableList<AddGroupBean> mInfos = FXCollections.observableArrayList();

    private OnFinishListener onFinishListener;

    public void init(String groupName) {
        device_list.setCellFactory(param -> {
            AddGroupCell addGroupCell = new AddGroupCell();
            addGroupCell.setOnMouseClicked((v) -> {
                if (v.getButton() == MouseButton.PRIMARY) {
                    AddGroupBean addGroupBean = addGroupCell.getItem();
                    if (addGroupBean.isSelected()) {
                        addGroupBean.setSelected(false);
                        addGroupBean.setIndex(0);
                    } else {
                        int index = judgeIndex();
                        if (index > 0) {
                            addGroupBean.setSelected(true);
                            addGroupBean.setIndex(index);
                        }
                    }
                    int index = 0;
                    List<AddGroupBean> beans = device_list.getItems();
                    for (AddGroupBean bean : beans) {
                        if (bean.getIndex() == 1)
                            index++;
                        if (bean.getIndex() == 2)
                            index++;
                        if (bean.getIndex() == 3)
                            index++;
                        if (bean.getIndex() == 4)
                            index++;
                    }
                    if (index >= 0) {
                        label_num.setText(index + "/4");
                    }
                }
            });
            return addGroupCell;
        });
        initData(groupName);
        initListener();
    }

    private void initData(String groupName) {
        List<Camera> cameras = new ArrayList<>(DeviceCache.getInstance().getAllDevices());

        if (StringUtil.isNullOrEmpty(groupName)) {
            quadBean = new QuadBean();
            label_tittle.setText(resourceBundle.getString("quadScreen_creatGroup"));
            label_num.setText("0/4");
            for (Camera camera : cameras) {
                AddGroupBean addGroupBean = new AddGroupBean(camera);
                mInfos.add(addGroupBean);
            }
        } else {
            quadBean = QuadListCache.getInstance().getQuadData(groupName);
            label_tittle.setText(resourceBundle.getString("quadScreen_manageGroup"));
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
                    if (StringUtil.equals(camera.deviceId, quadBean.getCamera_one())) {
                        addGroupBean.setSelected(true);
                        addGroupBean.setIndex(1);
                    }
                    if (StringUtil.equals(camera.deviceId, quadBean.getCamera_two())) {
                        addGroupBean.setSelected(true);
                        addGroupBean.setIndex(2);
                    }
                    if (StringUtil.equals(camera.deviceId, quadBean.getCamera_three())) {
                        addGroupBean.setSelected(true);
                        addGroupBean.setIndex(3);
                    }
                    if (StringUtil.equals(camera.deviceId, quadBean.getCamera_four())) {
                        addGroupBean.setSelected(true);
                        addGroupBean.setIndex(4);
                    }
                    mInfos.add(addGroupBean);
                }
            }
        }
        device_list.setItems(mInfos);
    }

    private void initListener() {
        btn_done.setOnMouseClicked((v) -> {
            String groupName = text_name.getText();

            List<AddGroupBean> beans = device_list.getItems();
            for (AddGroupBean bean : beans) {
                if (bean.getIndex() == 1)
                    quadBean.setCamera_one(bean.camera.deviceId);
                if (bean.getIndex() == 2)
                    quadBean.setCamera_two(bean.camera.deviceId);
                if (bean.getIndex() == 3)
                    quadBean.setCamera_three(bean.camera.deviceId);
                if (bean.getIndex() == 4)
                    quadBean.setCamera_four(bean.camera.deviceId);
            }

            if (QuadListCache.getInstance().getQuadData(groupName) == null) {
                quadBean.setGroupName(groupName);
                WLog.w(TAG, "Quad_add:" + quadBean.getGroupName() + "\t" + quadBean.getCamera_one() + "\t" + quadBean.getCamera_two() + "\t" + quadBean.getCamera_three() + "\t" + quadBean.getCamera_four());
                QuadListCache.getInstance().addQuadData(quadBean);
            } else {
                WLog.w(TAG, "Quad_edit:" + quadBean.getGroupName() + "->" + groupName + "\t" + quadBean.getCamera_one() + "\t" + quadBean.getCamera_two() + "\t" + quadBean.getCamera_three() + "\t" + quadBean.getCamera_four());
                QuadListCache.getInstance().editQuadData(quadBean, groupName);
            }

            if (onFinishListener != null) {
                onFinishListener.onFinish();
            }
        });
        text_name.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!StringUtil.isNullOrEmpty(text_name.getText()) && text_name.getText().length() > 0) {
                btn_done.setDisable(false);
            } else {
                btn_done.setDisable(true);
            }
        });
    }

    private int judgeIndex() {
        List<AddGroupBean> beans = device_list.getItems();
        List<String> indexes = new ArrayList<>(Arrays.asList("1", "2", "3", "4"));
        for (AddGroupBean bean : beans) {
            if (bean.getIndex() == 1) {
                indexes.remove("1");
            }
            if (bean.getIndex() == 2) {
                indexes.remove("2");
            }
            if (bean.getIndex() == 3) {
                indexes.remove("3");
            }
            if (bean.getIndex() == 4) {
                indexes.remove("4");
            }
        }
        if (indexes.size() == 0) {
            return 0;
        } else {
            return Integer.parseInt(indexes.get(0));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourceBundle = resources;
    }

    public interface OnFinishListener {
        void onFinish();
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }
}
