package net.ajcloud.wansviewplusw.quad.add;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.ajcloud.wansviewplusw.BaseController;
import net.ajcloud.wansviewplusw.quad.QuadBean;
import net.ajcloud.wansviewplusw.quad.QuadListCache;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;
import net.ajcloud.wansviewplusw.support.utils.WLog;
import org.controlsfx.control.GridView;

import java.net.URL;
import java.text.MessageFormat;
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

    private QuadBean oldQuadBean;
    private QuadBean quadBean;
    private ResourceBundle resourceBundle;
    private ObservableList<AddGroupBean> mInfos = FXCollections.observableArrayList();

    private OnFinishListener onFinishListener;
    private boolean isAdd = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourceBundle = resources;
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
    }

    public void init(String groupName) {
        initData(groupName);
        initListener();
    }

    private void initData(String groupName) {
        List<Camera> cameras = new ArrayList<>(DeviceCache.getInstance().getAllDevices());
        mInfos.clear();
        if (StringUtil.isNullOrEmpty(groupName)) {
            btn_done.setDisable(true);
            isAdd = true;
            quadBean = new QuadBean();
            oldQuadBean = new QuadBean();
            label_tittle.setText(resourceBundle.getString("quadScreen_creatGroup"));
            label_num.setText("0/4");
            for (Camera camera : cameras) {
                AddGroupBean addGroupBean = new AddGroupBean(camera);
                mInfos.add(addGroupBean);
            }
        } else {
            btn_done.setDisable(false);
            isAdd = false;
            quadBean = QuadListCache.getInstance().getQuadData(groupName);

            oldQuadBean = new QuadBean();
            oldQuadBean.setGroupName(quadBean.getGroupName());
            oldQuadBean.setCamera_one(quadBean.getCamera_one());
            oldQuadBean.setCamera_two(quadBean.getCamera_two());
            oldQuadBean.setCamera_three(quadBean.getCamera_three());
            oldQuadBean.setCamera_four(quadBean.getCamera_four());

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
            if (isAdd) {
                for (int i = 0; i < QuadListCache.getInstance().getGroupList(DeviceCache.getInstance().getSigninBean().mail).size(); i++) {
                    if (StringUtil.equals(QuadListCache.getInstance().getGroupList(DeviceCache.getInstance().getSigninBean().mail).get(i).getGroupName(),
                            groupName)) {
                        showExistDialog(groupName);
                        return;
                    }
                }
            }

            quadBean.setCamera_one(null);
            quadBean.setCamera_two(null);
            quadBean.setCamera_three(null);
            quadBean.setCamera_four(null);
            List<AddGroupBean> beans = device_list.getItems();
            for (AddGroupBean bean : beans) {
                if (bean.getIndex() == 1 && bean.isSelected())
                    quadBean.setCamera_one(bean.camera.deviceId);
                if (bean.getIndex() == 2 && bean.isSelected())
                    quadBean.setCamera_two(bean.camera.deviceId);
                if (bean.getIndex() == 3 && bean.isSelected())
                    quadBean.setCamera_three(bean.camera.deviceId);
                if (bean.getIndex() == 4 && bean.isSelected())
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
                onFinishListener.onFinish(quadBean, oldQuadBean, isAdd);
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

    private void showExistDialog(String groupName) {
        JFXAlert alert = new JFXAlert((Stage) btn_done.getScene().getWindow());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new javafx.scene.control.Label(new MessageFormat(resourceBundle.getString("quadScreen_exists")).format(new Object[]{groupName})));
        JFXButton closeButton = new JFXButton(resourceBundle.getString("common_ok"));
        closeButton.setMinWidth(100);
        closeButton.setMaxWidth(100);
        closeButton.setPrefWidth(100);
        closeButton.getStyleClass().add("dialog-accept");
        closeButton.setOnAction(event -> {
            alert.hideWithAnimation();
        });
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().add(closeButton);
        layout.setActions(hBox);
        alert.setContent(layout);
        alert.show();
    }

    @Override
    public void Destroy() {

    }

    public interface OnFinishListener {
        void onFinish(QuadBean newQuad, QuadBean oldQuad, boolean isAdd);
    }

    public void setOnFinishListener(OnFinishListener onFinishListener) {
        this.onFinishListener = onFinishListener;
    }
}
