package net.ajcloud.wansviewplusw.camera;

import com.jfoenix.controls.*;
import com.sun.jna.Memory;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.animation.AnimationTimer;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.ajcloud.wansviewplusw.BaseController;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.eventbus.EventBus;
import net.ajcloud.wansviewplusw.support.eventbus.EventType;
import net.ajcloud.wansviewplusw.support.eventbus.event.DeviceRefreshEvent;
import net.ajcloud.wansviewplusw.support.eventbus.event.SnapshotEvent;
import net.ajcloud.wansviewplusw.support.http.HttpCommonListener;
import net.ajcloud.wansviewplusw.support.http.RequestApiUnit;
import net.ajcloud.wansviewplusw.support.timer.CountDownTimer;
import net.ajcloud.wansviewplusw.support.utils.*;
import net.ajcloud.wansviewplusw.support.utils.play.PlayMethod;
import net.ajcloud.wansviewplusw.support.utils.play.PoliceHelper;
import net.ajcloud.wansviewplusw.support.utils.play.TcprelayHelper;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_stats_t;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.binding.internal.libvlc_state_t;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static uk.co.caprica.vlcj.binding.internal.libvlc_state_t.libvlc_Playing;

@ViewController(value = "/fxml/camera.fxml", title = "Camera")
public class CameraController implements BaseController, PoliceHelper.PoliceControlListener, Initializable {

    private static final String TAG = "CameraController";
    private SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
    @FXMLViewFlowContext
    private ViewFlowContext context;
    @FXML
    private AnchorPane root;
    @FXML
    private HBox content_list;
    @FXML
    private StackPane content_list_empty;
    @FXML
    private VBox content_play;
    @FXML
    private StackPane content_play_empty;
    @FXML
    private Label label_num;
    @FXML
    private Label label_name;
    @FXML
    private StackPane play_content;
    @FXML
    private StackPane playBg;
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
    private VBox loading;
    @FXML
    private Label label_tips;
    @FXML
    private VBox reconnect;
    @FXML
    private ImageView iv_reconnect;
    @FXML
    private Label label_reconnect;
    @FXML
    private JFXListView<Camera> lv_devices;
    @FXML
    private GridPane gp_control;
    @FXML
    private Button btn_top;
    @FXML
    private Button btn_right;
    @FXML
    private Button btn_bottom;
    @FXML
    private Button btn_left;
    @FXML
    private JFXButton btn_quality;
    @FXML
    private Label label_speed;
    @FXML
    private JFXButton btn_fullscreen;
    @FXML
    private VBox content_left;
    @FXML
    private AnchorPane content_bottom;
    @FXML
    private HBox content_tips;
    @FXML
    private Label label_stop;
    @FXML
    private Label label_time;
    @FXML
    private Label label_continue;
    @FXML
    private ImageView iv_tips;
    @FXML
    private HBox content_record_tips;
    @FXML
    private Circle circle_record_tips;
    @FXML
    private Label label_record_tips;

    private JFXPopup qualityPop;

    private ResourceBundle resourceBundle;
    private CanvasPlayerComponent mediaPlayerComponent;
    private ImageView imageView;
    private WritableImage writableImage;
    private WritablePixelFormat<ByteBuffer> pixelFormat;
    private FloatProperty videoSourceRatioProperty;
    private RequestApiUnit requestApiUnit;
    private ObservableList<Camera> mInfos = FXCollections.observableArrayList();
    private PoliceHelper policeHelper;
    private ExecuteTimer executeTimer_top;
    private ExecuteTimer executeTimer_right;
    private ExecuteTimer executeTimer_bottom;
    private ExecuteTimer executeTimer_left;
    private FullscreenListener fullscreenListener;
    private Timer recordTimer;
    private String deviceId;
    private StringProperty speed;
    private int play_method;
    //control
    private boolean isMute = false;
    private boolean isReady = false;

    private TimeService timerService = new TimeService();

    /**
     * 定时，限制播放时长
     */
    private static final long PLAY_TIME = 10 * 60 * 1000;
    private CountDownTimer playTimer = new CountDownTimer();

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        try {
            WLog.w("============4================");
            Objects.requireNonNull(context, "context");
            fullscreenListener = (FullscreenListener) context.getRegisteredObject("FullscreenListener");
            showLoading(false, "");
            if (mediaPlayerComponent == null) {
                new Thread(() -> {
                    mediaPlayerComponent = new CanvasPlayerComponent();
                    mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(mMediaPlayerListener);
                    isReady = true;
                }).start();
            }
            WLog.w("============5================");
            policeHelper = new PoliceHelper(this);
            videoSourceRatioProperty = new SimpleFloatProperty(0.4f);
            pixelFormat = PixelFormat.getByteBgraPreInstance();
            initializeImageView();
            //init device list
            lv_devices.depthProperty().set(1);
            lv_devices.setExpanded(true);
            lv_devices.setCellFactory(param -> {
                DeviceListCell deviceListCell = new DeviceListCell();
                deviceListCell.setOnMouseClicked((v) -> {
                    if (v.getButton() == MouseButton.PRIMARY) {
                        if (!content_play.isVisible()) {
                            content_play.setVisible(true);
                            content_play.setManaged(true);
                            content_play_empty.setVisible(false);
                            content_play_empty.setManaged(false);
                        }
                        handleMouseClick(deviceListCell.getItem());
                    }
                });
                return deviceListCell;
            });

            AtomicInteger count = new AtomicInteger(0);
            timerService.setCount(count.get());
            timerService.setPeriod(Duration.seconds(1));
            timerService.setOnSucceeded(t -> {
                        try {
                            count.set((int) t.getSource().getValue());
                            if (!canDo()) {
                                return;
                            }
                            if (mediaPlayerComponent != null && mediaPlayerComponent.getMediaPlayer() != null) {
                                libvlc_media_stats_t p_stats = mediaPlayerComponent.getMediaPlayer().getMediaStatistics();
                                double bitrate = p_stats.f_demux_bitrate * 1000;/*KBps*/
                                if (bitrate < 0)
                                    bitrate = 0.3;
                                if (bitrate < 1024) {
                                    speed.set((int) bitrate + "K/s");
                                } else {
                                    speed.set(String.format("%.1fM/s", bitrate / 1024));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );

            Tooltip tooltip = new Tooltip(resourceBundle.getString("countDown_msg"));
            tooltip.getStyleClass().add("tips");
            Tooltip.install(iv_tips, tooltip);
            WLog.w("============6================");
            initListener();
            WLog.w("============7================");
            initData();
            WLog.w("============8================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initListener() {
        reconnect.onMouseClickedProperty().bindBidirectional(iv_reconnect.onMouseClickedProperty());
        reconnect.onMouseClickedProperty().bindBidirectional(label_reconnect.onMouseClickedProperty());
        //function
        btn_voice.setOnMouseClicked((v) -> {
            setMute();
        });
        btn_screenshot.setOnMouseClicked((v) -> {
            takeSnapshot(FileUtil.getImagePath(DeviceCache.getInstance().getSigninBean().mail), true);
        });
        btn_play.setOnMouseClicked((v) -> {
            startOrStop();
        });
        btn_record.setOnMouseClicked((v) -> {
            recording();
        });
        btn_quality.setOnMouseClicked((v) -> {
            if (qualityPop != null && qualityPop.isShowing()) {
                qualityPop.hide();
            } else {
                showQualityPop();
            }
        });
        btn_fullscreen.setOnMouseClicked((v) -> {
            fullscreenListener.fullscreen(!((Stage) btn_fullscreen.getScene().getWindow()).isFullScreen());
        });
        label_continue.setOnMouseClicked((v) -> {
            if (playTimer.isCounting()) {
                startOrCancelTimer(true);
            } else {
                play();
            }
            content_tips.setVisible(false);
            label_stop.setVisible(true);
            label_stop.setManaged(true);
        });
        //direction
        btn_top.addEventFilter(MouseEvent.ANY, topEventHandler);
        btn_right.addEventFilter(MouseEvent.ANY, rightEventHandler);
        btn_bottom.addEventFilter(MouseEvent.ANY, bottomEventHandler);
        btn_left.addEventFilter(MouseEvent.ANY, leftEventHandler);
        btn_top.setOnAction(event -> {
            if (executeTimer_top != null && executeTimer_top.isActive)
                setPtz(10);
        });
        btn_right.setOnAction(event -> {
            if (executeTimer_right != null && executeTimer_right.isActive)
                setPtz(9);
        });
        btn_bottom.setOnAction(event -> {
            if (executeTimer_bottom != null && executeTimer_bottom.isActive)
                setPtz(11);
        });
        btn_left.setOnAction(event -> {
            if (executeTimer_left != null && executeTimer_left.isActive)
                setPtz(8);
        });
        play_content.setOnMouseEntered(event -> {
            showControlPane(true);
        });
        play_content.setOnMouseExited(event -> {
            showControlPane(false);
        });
        reconnect.setOnMouseClicked(event -> {
            reconnect.setVisible(false);
            reconnect.setManaged(false);
            if (play_method == PlayMethod.RELAY || play_method == PlayMethod.P2P)
                TcprelayHelper.getInstance().reConnect(deviceId);
            play();
        });
        EventBus.getInstance().register(event -> {
            if (event.getType() == EventType.DEVICE_REFRESH) {
                DeviceRefreshEvent deviceRefreshEvent = (DeviceRefreshEvent) event;
                doSnapShot(deviceRefreshEvent.getDeviceId());
            }
        });
    }

    private void initData() {
        speed = new SimpleStringProperty("0K/s");
        label_speed.textProperty().bind(speed);
        if (DeviceCache.getInstance().getAllDevices().size() > 0) {
            content_list.setVisible(true);
            content_list.setManaged(true);
            content_list_empty.setVisible(false);
            content_list_empty.setManaged(false);

            label_num.setText(new MessageFormat(resourceBundle.getString("home_device_num")).format(new Object[]{DeviceCache.getInstance().getAllDevices().size()}));
            mInfos.setAll(DeviceCache.getInstance().getAllDevices());
            lv_devices.setItems(mInfos);
        } else {
            if (requestApiUnit == null) {
                requestApiUnit = new RequestApiUnit();
            }
            requestApiUnit.getDeviceList(new HttpCommonListener<java.util.List<Camera>>() {
                @Override
                public void onSuccess(List<Camera> bean) {
                    Platform.runLater(() -> {
                        if (bean != null && bean.size() > 0) {
                            content_list.setVisible(true);
                            content_list.setManaged(true);
                            content_list_empty.setVisible(false);
                            content_list_empty.setManaged(false);

                            label_num.setText(new MessageFormat(resourceBundle.getString("home_device_num")).format(new Object[]{bean.size()}));
                            mInfos.setAll(bean);
                            lv_devices.setItems(mInfos);
                        } else {
                            content_list_empty.setVisible(true);
                            content_list_empty.setManaged(true);
                            content_list.setVisible(false);
                            content_list.setManaged(false);
                        }
                    });
                }

                @Override
                public void onFail(int code, String msg) {

                }
            });
        }

    }

    private void initView(String deviceId) {
        //播放按钮
        btn_play.getStyleClass().remove("jfx_button_pause");
        btn_play.getStyleClass().remove("jfx_button_play");
        btn_play.getStyleClass().add("jfx_button_pause");

        //播放背景
        setPlayBg(deviceId);
        //清晰度
        btn_quality.setText("FHD");
        Camera camera = DeviceCache.getInstance().get(deviceId);
        if (camera != null && camera.capability != null && camera.capability.getVideoQualities() != null) {
            for (Map.Entry<String, Integer> entry : camera.capability.getVideoQualities().entrySet()) {
                String key = entry.getKey();
                int value = entry.getValue();
                if (camera.getCurrentQuality() == value) {
                    btn_quality.setText(key);
                    break;
                }
            }
        }
        //频繁刷新操作
        if (timerService.isRunning()) {
            timerService.restart();
        } else {
            timerService.start();
        }
    }

    private void handleMouseClick(Camera camera) {
        if (camera == null) {
            return;
        }
        for (Camera c :
                DeviceCache.getInstance().getAllDevices()) {
            if (StringUtil.equals(c.deviceId, camera.deviceId)) {
                c.setSelected(true);
            } else {
                c.setSelected(false);
            }
        }
        initView(camera.deviceId);

        if (mediaPlayerComponent != null && mediaPlayerComponent.getMediaPlayer() != null && mediaPlayerComponent.getMediaPlayer().isPlaying()) {
            if (!StringUtil.isNullOrEmpty(deviceId) && StringUtil.equals(deviceId, camera.deviceId)) {
                return;
            }
            try {
                doVideoStop();
                deviceId = camera.deviceId;
                play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if ((play_method == PlayMethod.RELAY || play_method == PlayMethod.P2P) && !StringUtil.isNullOrEmpty(deviceId)) {
                TcprelayHelper.getInstance().reConnect(deviceId);
            }
            deviceId = camera.deviceId;
            play();
        }
    }

    private void doSnapShot(String deviceId) {
        Camera camera = DeviceCache.getInstance().get(deviceId);
        if (camera == null ||
                !camera.isOnline() ||
                (camera.isShare() && !camera.isVaild())) {
            return;
        }
        new Thread(() -> {
            if (FileUtil.getRealImageNum(camera.deviceId) > 2) {
                FileUtil.resetRealTimeImage(camera.deviceId);
            }
            requestApiUnit.doSnapshot(camera.deviceId, new HttpCommonListener<Object>() {
                @Override
                public void onSuccess(Object bean) {
                    EventBus.getInstance().post(new SnapshotEvent());
                }

                @Override
                public void onFail(int code, String msg) {

                }
            });
        }).start();
    }

    @Override
    public void onCannotPlay(String deviceId) {
        if (StringUtil.equals(this.deviceId, deviceId)) {
            cannotPlayDo();
        }
    }

    @Override
    public void onPlay(String deviceId, int playMethod, String url, int mVideoHeight, int mVideoWidth) {
        if (StringUtil.equals(this.deviceId, deviceId)) {
            play_method = playMethod;
            onVideoPlay(url);
        }
    }

    @Override
    public void onP2pPlay(String deviceId) {
        if (StringUtil.equals(this.deviceId, deviceId)) {
            play_method = TcprelayHelper.getInstance().getConnectType(deviceId) == 0 ? PlayMethod.P2P : PlayMethod.RELAY;
            Camera camera = DeviceCache.getInstance().get(deviceId);
            TcprelayHelper.getInstance().getPlayUrl(deviceId, camera.getCurrentQuality(), new TcprelayHelper.ConnectCallback() {
                @Override
                public void onSuccess(String url) {
                    WLog.w("TcprelayHelper", "play-----------onSuccess:" + url);
                    Platform.runLater(() -> {
                        onVideoPlay(url);
                    });
                }

                @Override
                public void onFail() {
                    WLog.w("TcprelayHelper", "play-----------onFail");
                    Platform.runLater(() -> {
                        cannotPlayDo();
                    });
                }
            });
        }
    }

    private void onVideoPlay(String url) {
        try {
            if (!StringUtil.isNullOrEmpty(url)) {
                btn_play.getStyleClass().remove("jfx_button_pause");
                btn_play.getStyleClass().remove("jfx_button_play");
                btn_play.getStyleClass().add("jfx_button_pause");
                showLoading(true, resourceBundle.getString("play_preraring"));
                startOrCancelTimer(true);
                new Thread(() -> {
                    boolean finish = false;
                    while (!finish) {
                        if (isReady) {
                            finish = true;
                            Platform.runLater(() -> {
                                mediaPlayerComponent.getMediaPlayer().playMedia(url);
                            });
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            cannotPlayDo();
        }
    }

    private void doVideoStop() {
        //init UI
        btn_play.getStyleClass().remove("jfx_button_pause");
        btn_play.getStyleClass().remove("jfx_button_play");
        btn_play.getStyleClass().add("jfx_button_play");
        isMute = false;
        btn_voice.getStyleClass().remove("jfx_button_voice");
        btn_voice.getStyleClass().remove("jfx_button_voice_mute");
        btn_voice.getStyleClass().add("jfx_button_voice");
        //结束定时器
        startOrCancelTimer(false);
        //结束录制
        stopRecord(false);
        //停止播放
        if (mediaPlayerComponent != null && mediaPlayerComponent.getMediaPlayer() != null && mediaPlayerComponent.getMediaPlayer().isPlaying()) {
            WLog.w("onStop--------------", deviceId);
            mediaPlayerComponent.getMediaPlayer().stop();
//            mediaPlayerComponent.getMediaPlayer().release();
//            mediaPlayerComponent.release(true);
//            mediaPlayerComponent = new CanvasPlayerComponent();
//            mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(mMediaPlayerListener);
        }
        //重新建链
        WLog.w("onStop--------------", play_method);
        if ((play_method == PlayMethod.RELAY || play_method == PlayMethod.P2P) && !StringUtil.isNullOrEmpty(deviceId)) {
            TcprelayHelper.getInstance().reConnect(deviceId);
        }
    }

    public void play() {
        Camera camera = DeviceCache.getInstance().get(deviceId);
        if (camera != null) {
            btn_play.getStyleClass().remove("jfx_button_pause");
            btn_play.getStyleClass().remove("jfx_button_play");
            btn_play.getStyleClass().add("jfx_button_pause");
            loading.setVisible(true);
            loading.setManaged(true);
            label_tips.setText(resourceBundle.getString("play_establishing_channel"));
            reconnect.setVisible(false);
            reconnect.setManaged(false);
            label_name.setText(camera.aliasName);
            policeHelper.setCamera(camera);
            if (camera.isOnline()) {
                policeHelper.getUrlAndPlay();
            } else {
                cannotPlayDo();
            }
        }

    }

    public void destroy() {
        imageView.setImage(null);
        writableImage = null;
        if (mediaPlayerComponent != null && mediaPlayerComponent.getMediaPlayer() != null && mediaPlayerComponent.getMediaPlayer().isPlaying()) {
            stopRecord(false);
            mediaPlayerComponent.getMediaPlayer().stop();
            mediaPlayerComponent.getMediaPlayer().release();
            startOrCancelTimer(false);
            timerService.cancel();
            if ((play_method == PlayMethod.RELAY || play_method == PlayMethod.P2P) && !StringUtil.isNullOrEmpty(deviceId)) {
                TcprelayHelper.getInstance().reConnect(deviceId);
            }
        }
    }

    private void initializeImageView() {
        if (writableImage == null) {
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            writableImage = new WritableImage((int) visualBounds.getWidth(), (int) visualBounds.getHeight());
        }

        if (imageView == null)
            imageView = new ImageView(writableImage);

        playPane.getChildren().removeAll();
        playPane.getChildren().add(imageView);

        fitImageViewSize((float) playPane.getWidth(), (float) playPane.getHeight());

        playPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            fitImageViewSize(newValue.floatValue(), (float) playPane.getHeight());
        });

        playPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            fitImageViewSize((float) playPane.getWidth(), newValue.floatValue());
        });
    }

    private void fitImageViewSize(float width, float height) {
        if (imageView != null)
            Platform.runLater(() -> {
                float fitHeight = videoSourceRatioProperty.get() * width;
                if (fitHeight > height) {
                    imageView.setFitHeight(height);
                    double fitWidth = height / videoSourceRatioProperty.get();
                    imageView.setFitWidth(fitWidth);
                    imageView.setX((width - fitWidth) / 2);
                    imageView.setY(0);
                } else {
                    imageView.setFitWidth(width);
                    imageView.setFitHeight(fitHeight);
                    imageView.setY((height - fitHeight) / 2);
                    imageView.setX(0);
                }
            });
    }

    private void setPtz(int action) {
        new Thread(() -> requestApiUnit.setPtz(deviceId, action, new HttpCommonListener<Object>() {
            @Override
            public void onSuccess(Object bean) {

            }

            @Override
            public void onFail(int code, String msg) {

            }
        })).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourceBundle = resources;
    }

    @PreDestroy
    public void Destroy() {
        try {
            for (Camera c :
                    DeviceCache.getInstance().getAllDevices()) {
                c.setSelected(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        destroy();
        System.gc();
    }

    class ExecuteTimer extends AnimationTimer {
        private long lastUpdate = 0L;
        private Button mbtn;
        private boolean isActive;

        public ExecuteTimer(Button button) {
            this.mbtn = button;
        }

        @Override
        public void handle(long now) {
            if ((now - this.lastUpdate) > 500 * 1000000) {
                if (mbtn.isPressed()) {
                    mbtn.fire();
                    this.lastUpdate = now;
                }
            }
        }

        @Override
        public void start() {
            isActive = true;
            super.start();
        }

        @Override
        public void stop() {
            isActive = false;
            super.stop();
        }
    }

    private boolean canDo() {
        if (StringUtil.isNullOrEmpty(deviceId)) {
            return false;
        }
        Camera camera = DeviceCache.getInstance().get(deviceId);
        if (camera == null || mediaPlayerComponent == null || mediaPlayerComponent.getMediaPlayer() == null ||
                mediaPlayerComponent.getMediaPlayer().getMediaPlayerState() != libvlc_Playing ||
                !camera.isOnline()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 设置直播界面背景
     */
    private void setPlayBg(String deviceId) {
        File file = new File(FileUtil.getRealtimeImagePath(deviceId) + File.separator + "realtime_picture.jpg");
        if (file.exists()) {
            Image image = new Image(file.toURI().toString(), 192, 108, true, false, false);
            playBg.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, true))));
            playBg.setEffect(new GaussianBlur(50));
        }
    }

    /**
     * 截图
     */
    private void takeSnapshot(String path, boolean showAnim) {
        if (!canDo()) {
            return;
        }
        String fileName = path + File.separator + sDateFormat.format(System.currentTimeMillis()) + ".jpg";
        File file = new File(fileName);
        mediaPlayerComponent.getMediaPlayer().saveSnapshot(file);

        ImageView imageView = new ImageView();
        imageView.setImage(new Image(file.toURI().toString(), 66, 46, true, false));
        imageView.setFitWidth(playPane.getWidth());
        imageView.setFitHeight(playPane.getHeight());
        play_content.getChildren().add(imageView);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(800), imageView);
        translateTransition.setFromX(0);
        translateTransition.setToX(-(imageView.getFitWidth() / 2 + 250));
        translateTransition.setFromY(0);
        translateTransition.setToY(imageView.getFitHeight() / 2);
        translateTransition.setCycleCount(1);
        translateTransition.setAutoReverse(false);
        WLog.w("anim_test" + imageView.getFitHeight() + "\t" + imageView.getFitWidth());

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(800), imageView);
        scaleTransition.setToX(0);
        scaleTransition.setToY(0);
        scaleTransition.setCycleCount(1);
        scaleTransition.setAutoReverse(false);

        ParallelTransition parallelTransition = new ParallelTransition(translateTransition, scaleTransition);
        parallelTransition.setCycleCount(1);
        parallelTransition.setOnFinished(event -> play_content.getChildren().remove(imageView));
        parallelTransition.play();
    }

    /**
     * 静音
     */
    private void setMute() {
        if (!canDo()) {
            return;
        }
        isMute = !isMute;
        btn_voice.getStyleClass().remove("jfx_button_voice");
        btn_voice.getStyleClass().remove("jfx_button_voice_mute");
        if (isMute) {
            btn_voice.getStyleClass().add("jfx_button_voice_mute");
        } else {
            btn_voice.getStyleClass().add("jfx_button_voice");
        }
        mediaPlayerComponent.getMediaPlayer().mute(isMute);
    }

    /**
     * 开始/暂停
     */
    private void startOrStop() {
        if (StringUtil.isNullOrEmpty(deviceId)) {
            return;
        }
        btn_play.getStyleClass().remove("jfx_button_pause");
        btn_play.getStyleClass().remove("jfx_button_play");
        if (mediaPlayerComponent != null && mediaPlayerComponent.getMediaPlayer() != null && mediaPlayerComponent.getMediaPlayer().isPlaying()) {
            btn_play.getStyleClass().add("jfx_button_play");
            doVideoStop();
        } else {
            btn_play.getStyleClass().add("jfx_button_pause");
            play();
        }
    }

    /**
     * 开始
     */
    private void start() {
        if (StringUtil.isNullOrEmpty(deviceId)) {
            return;
        }
        if (mediaPlayerComponent != null && mediaPlayerComponent.getMediaPlayer() != null && !mediaPlayerComponent.getMediaPlayer().isPlaying()) {
            btn_play.getStyleClass().remove("jfx_button_pause");
            btn_play.getStyleClass().remove("jfx_button_play");
            btn_play.getStyleClass().add("jfx_button_pause");
            mediaPlayerComponent.getMediaPlayer().start();
            startOrCancelTimer(true);
        }
    }

    /**
     * 暂停
     */
    private void pause() {
        if (StringUtil.isNullOrEmpty(deviceId)) {
            return;
        }
        if (mediaPlayerComponent != null && mediaPlayerComponent.getMediaPlayer() != null && mediaPlayerComponent.getMediaPlayer().isPlaying()) {
            btn_play.getStyleClass().remove("jfx_button_pause");
            btn_play.getStyleClass().remove("jfx_button_play");
            btn_play.getStyleClass().add("jfx_button_play");
            mediaPlayerComponent.getMediaPlayer().pause();
            startOrCancelTimer(false);
        }
    }

    /**
     * 录像
     */
    private void recording() {
        if (!canDo()) {
            return;
        }
        if (mediaPlayerComponent != null && mediaPlayerComponent.getMediaPlayer() != null) {
            if (mediaPlayerComponent.getMediaPlayer().isRecording()) {
                WLog.w(TAG, "stopRecord");
                recordingAnim(false);
                mediaPlayerComponent.getMediaPlayer().stopRecord();
                takeSnapshot(FileUtil.getTmpPath(), true);
            } else {
                WLog.w(TAG, "startRecord");
                recordingAnim(true);
                mediaPlayerComponent.getMediaPlayer().startRecord(FileUtil.getVideoPath(DeviceCache.getInstance().getSigninBean().mail));
            }
        }
    }

    /**
     * 录像
     */
    private void stopRecord(boolean showAnim) {
        if (mediaPlayerComponent != null && mediaPlayerComponent.getMediaPlayer() != null && mediaPlayerComponent.getMediaPlayer().isRecording()) {
            WLog.w(TAG, "stopRecord");
            recordingAnim(false);
            mediaPlayerComponent.getMediaPlayer().stopRecord();
            if (showAnim) {
                takeSnapshot(FileUtil.getTmpPath(), true);
            }
        }
    }

    private long time = 0;

    /**
     * 录像动画
     */
    private void recordingAnim(boolean isRecord) {
        if (isRecord) {
            content_record_tips.setVisible(true);
            content_record_tips.setManaged(true);
            label_record_tips.setText("00:00:00");
            if (recordTimer == null) {
                recordTimer = new Timer();
            } else {
                recordTimer.cancel();
            }
            time = 0;
            recordTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        label_record_tips.setText(DateUtils.timeFormat(time++));
                    });
                }
            }, 0, 1000);
        } else {
            content_record_tips.setVisible(false);
            content_record_tips.setManaged(false);
            label_record_tips.setText("00:00:00");
            if (recordTimer != null) {
                recordTimer.cancel();
                recordTimer = null;
            }
        }
    }

    /**
     * 重新播放
     */
    private void restart() {
        doVideoStop();
        play();
    }

    private void startOrCancelTimer(boolean isStart) {
        playTimer.cancel();
        content_tips.setVisible(false);
        content_tips.setManaged(false);
        label_stop.setVisible(true);
        label_stop.setManaged(true);
        if (play_method != PlayMethod.RELAY) {
            return;
        }
        if (isStart) {
            playTimer.CountDown(PLAY_TIME, new CountDownTimer.OnTimerListener() {
                @Override
                public void onTick(int second) {
                    if ((PLAY_TIME / 1000 - second) <= 20) {
                        content_tips.setVisible(true);
                        label_time.setText((PLAY_TIME / 1000 - second) + "s");
                    }
                }

                @Override
                public void onFinish() {
                    WLog.w("playTimer", "onFinish");
                    btn_play.getStyleClass().remove("jfx_button_pause");
                    btn_play.getStyleClass().remove("jfx_button_play");
                    btn_play.getStyleClass().add("jfx_button_play");
                    stopRecord(true);
                    if (mediaPlayerComponent != null && mediaPlayerComponent.getMediaPlayer() != null)
                        mediaPlayerComponent.getMediaPlayer().stop();
                    TcprelayHelper.getInstance().reConnect(deviceId);

                    content_tips.setVisible(true);
                    content_tips.setManaged(true);
                    label_time.setText(resourceBundle.getString("countDown_haveStopped"));
                    label_stop.setVisible(false);
                    label_stop.setManaged(false);
                }
            });
        }
    }

    private void showQualityPop() {
        try {
            qualityPop = new JFXPopup();
            VBox vBox = new VBox();
            vBox.setSpacing(8.0);
            vBox.setAlignment(Pos.CENTER);

            if (StringUtil.isNullOrEmpty(deviceId)) {
                return;
            }
            Camera camera = DeviceCache.getInstance().get(deviceId);
            if (camera == null || camera.capability == null) {
                return;
            }

            LinkedHashMap<String, Integer> qualities = camera.capability.getVideoQualities();
            if (qualities == null) {
                return;
            }
            List<String> tmp = new ArrayList<>(qualities.keySet());
            for (int i = 0; i < tmp.size(); i++) {
                if (camera.getCurrentQuality() == qualities.get(tmp.get(i))) {
                } else {
                    int finalI = i;
                    Label quality = new Label();
                    quality.setText(tmp.get(i));
                    quality.getStyleClass().setAll("pop_btn_quality");
                    quality.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            qualityPop.hide();
                            btn_quality.setText(tmp.get(finalI));
                            Camera camera = DeviceCache.getInstance().get(deviceId);
                            camera.setCurrentQuality(qualities.get(tmp.get(finalI)));
                            restart();
                        }
                    });
                    vBox.getChildren().add(quality);
                }
            }
            qualityPop.setPopupContent(vBox);
            qualityPop.show(btn_quality, JFXPopup.PopupVPosition.BOTTOM, JFXPopup.PopupHPosition.LEFT, 0, -28);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean isVideoOut = false;
    public static libvlc_state_t OldEvent;
    private final MediaPlayerEventListener mMediaPlayerListener = new MediaPlayerEventListener() {

        @Override
        public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t libvlc_media_t, String s) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void opening(MediaPlayer mediaPlayer) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void buffering(MediaPlayer mediaPlayer, float v) {
            if (isVideoOut) {
                //Buffer
                WLog.w(v);
                showLoading(v < 100, "");
            }
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void playing(MediaPlayer mediaPlayer) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void paused(MediaPlayer mediaPlayer) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void stopped(MediaPlayer mediaPlayer) {
            isVideoOut = false;
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void forward(MediaPlayer mediaPlayer) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void backward(MediaPlayer mediaPlayer) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void finished(MediaPlayer mediaPlayer) {
            OldEvent = mediaPlayer.getMediaPlayerState();
            stopRecord(false);
        }

        @Override
        public void timeChanged(MediaPlayer mediaPlayer, long l) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void positionChanged(MediaPlayer mediaPlayer, float v) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void seekableChanged(MediaPlayer mediaPlayer, int i) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void pausableChanged(MediaPlayer mediaPlayer, int i) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void titleChanged(MediaPlayer mediaPlayer, int i) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void snapshotTaken(MediaPlayer mediaPlayer, String s) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void lengthChanged(MediaPlayer mediaPlayer, long l) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void videoOutput(MediaPlayer mediaPlayer, int i) {
            if (OldEvent == libvlc_Playing) {
                isVideoOut = true;
                showLoading(false, "");
                fitImageViewSize((float) playPane.getWidth(), (float) playPane.getHeight());
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        if (mediaPlayerComponent != null && mediaPlayerComponent.getMediaPlayer() != null) {
                            mediaPlayerComponent.getMediaPlayer().saveSnapshot(new File(FileUtil.getRealtimeImagePath(deviceId) + File.separator + "realtime_picture.jpg"));
                            EventBus.getInstance().post(new SnapshotEvent());
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void scrambledChanged(MediaPlayer mediaPlayer, int i) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void elementaryStreamAdded(MediaPlayer mediaPlayer, int i, int i1) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void elementaryStreamDeleted(MediaPlayer mediaPlayer, int i, int i1) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void elementaryStreamSelected(MediaPlayer mediaPlayer, int i, int i1) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void corked(MediaPlayer mediaPlayer, boolean b) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void muted(MediaPlayer mediaPlayer, boolean b) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void volumeChanged(MediaPlayer mediaPlayer, float v) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void audioDeviceChanged(MediaPlayer mediaPlayer, String s) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void chapterChanged(MediaPlayer mediaPlayer, int i) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void error(MediaPlayer mediaPlayer) {
            OldEvent = mediaPlayer.getMediaPlayerState();
            cannotPlayDo();
        }

        @Override
        public void mediaPlayerReady(MediaPlayer mediaPlayer) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void mediaMetaChanged(MediaPlayer mediaPlayer, int i) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t libvlc_media_t) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void mediaDurationChanged(MediaPlayer mediaPlayer, long l) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void mediaParsedChanged(MediaPlayer mediaPlayer, int i) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void mediaParsedStatus(MediaPlayer mediaPlayer, int newStatus) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void mediaFreed(MediaPlayer mediaPlayer) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void mediaStateChanged(MediaPlayer mediaPlayer, int i) {
            WLog.w("logtest", OldEvent);
            if (OldEvent == libvlc_Playing &&
                    mediaPlayer.getMediaPlayerState() == libvlc_state_t.libvlc_Ended) {
                cannotPlayDo();
            }
            OldEvent = mediaPlayer.getMediaPlayerState();
            WLog.w("logtest", OldEvent);
        }

        @Override
        public void mediaSubItemTreeAdded(MediaPlayer mediaPlayer, libvlc_media_t libvlc_media_t) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void newMedia(MediaPlayer mediaPlayer) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void subItemPlayed(MediaPlayer mediaPlayer, int i) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void subItemFinished(MediaPlayer mediaPlayer, int i) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }

        @Override
        public void endOfSubItems(MediaPlayer mediaPlayer) {
            OldEvent = mediaPlayer.getMediaPlayerState();
        }
    };

    private void showControlPane(boolean isShow) {
        if (!StringUtil.isNullOrEmpty(deviceId)) {
            Camera camera = DeviceCache.getInstance().get(deviceId);
            if (camera != null && camera.hasPtz()) {
                if (isShow) {
                    if (!gp_control.isVisible()) {
                        gp_control.setVisible(true);
                        gp_control.setManaged(true);
                    }
                } else {
                    if (gp_control.isVisible()) {
                        gp_control.setVisible(false);
                        gp_control.setManaged(false);
                    }
                }
            }
        }
    }


    /**
     * 全屏
     */
    public void fullscreen(boolean isFullscreen) {
        content_list.setVisible(true);
        content_list.setManaged(true);
        content_list_empty.setVisible(false);
        content_list_empty.setManaged(false);
        content_play.setVisible(true);
        content_play.setManaged(true);
        content_play_empty.setVisible(false);
        content_play_empty.setManaged(false);
        content_left.setVisible(!isFullscreen);
        content_left.setManaged(!isFullscreen);
        content_bottom.setVisible(true);
        content_bottom.setManaged(true);
        label_name.setVisible(true);
        label_name.setManaged(true);

        btn_fullscreen.getStyleClass().remove("btn_smallscreen");
        btn_fullscreen.getStyleClass().remove("btn_fullscreen");
        if (isFullscreen) {
            btn_fullscreen.getStyleClass().add("btn_smallscreen");
        } else {
            btn_fullscreen.getStyleClass().add("btn_fullscreen");
        }
    }

    private class CanvasPlayerComponent extends DirectMediaPlayerComponent {

        public CanvasPlayerComponent() {
            super(new CanvasBufferFormatCallback());
        }

        PixelWriter pixelWriter = null;

        private PixelWriter getPW() {
            if (pixelWriter == null) {
                pixelWriter = writableImage.getPixelWriter();
            }
            return pixelWriter;
        }

        @Override
        public void display(DirectMediaPlayer mediaPlayer, Memory[] nativeBuffers, BufferFormat bufferFormat) {
            if (writableImage == null || mediaPlayer == null) {
                return;
            }
            Platform.runLater(() -> {
                Memory[] nativeBufferArray = mediaPlayer.lock();
                if (nativeBufferArray == null || nativeBufferArray.length == 0) {
                    mediaPlayer.unlock();
                } else {
                    Memory nativeBuffer = nativeBufferArray[0];
                    try {
                        ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
                        getPW().setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
                    } finally {
                        mediaPlayer.unlock();
                    }
                }
            });
        }
    }

    public class CanvasBufferFormatCallback implements BufferFormatCallback {

        @Override
        public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            Platform.runLater(() -> {
                if (videoSourceRatioProperty != null)
                    videoSourceRatioProperty.set((float) sourceHeight / (float) sourceWidth);
            });
            return new RV32BufferFormat((int) visualBounds.getWidth(), (int) visualBounds.getHeight());
        }

    }

    public interface FullscreenListener {
        void fullscreen(boolean isFullScreen);
    }

    private EventHandler<MouseEvent> topEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (!canDo()) {
                return;
            }
            if (executeTimer_top == null) {
                executeTimer_top = new ExecuteTimer(btn_top);
            }
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                gp_control.getStyleClass().setAll("direction_pane_top");
                executeTimer_top.start();
            } else {
                gp_control.getStyleClass().setAll("direction_pane");
                executeTimer_top.stop();
            }
        }
    };

    private EventHandler<MouseEvent> bottomEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (!canDo()) {
                return;
            }
            if (executeTimer_bottom == null) {
                executeTimer_bottom = new ExecuteTimer(btn_bottom);
            }
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                gp_control.getStyleClass().setAll("direction_pane_bottom");
                executeTimer_bottom.start();
            } else {
                gp_control.getStyleClass().setAll("direction_pane");
                executeTimer_bottom.stop();
            }
        }
    };

    private EventHandler<MouseEvent> leftEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (!canDo()) {
                return;
            }
            if (executeTimer_left == null) {
                executeTimer_left = new ExecuteTimer(btn_left);
            }
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                gp_control.getStyleClass().setAll("direction_pane_left");
                executeTimer_left.start();
            } else {
                gp_control.getStyleClass().setAll("direction_pane");
                executeTimer_left.stop();
            }
        }
    };

    private EventHandler<MouseEvent> rightEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (!canDo()) {
                return;
            }
            if (executeTimer_right == null) {
                executeTimer_right = new ExecuteTimer(btn_right);
            }
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                gp_control.getStyleClass().setAll("direction_pane_right");
                executeTimer_right.start();
            } else {
                gp_control.getStyleClass().setAll("direction_pane");
                executeTimer_right.stop();
            }
        }
    };

    private void showLoading(boolean isShow, String msg) {
        Platform.runLater(() -> {
            loading.setVisible(isShow);
            loading.setManaged(isShow);
            label_tips.setText(msg);
            if (isShow) {
                reconnect.setVisible(false);
                reconnect.setManaged(false);
            }
        });
    }

    private void cannotPlayDo() {
        btn_play.getStyleClass().remove("jfx_button_pause");
        btn_play.getStyleClass().remove("jfx_button_play");
        btn_play.getStyleClass().add("jfx_button_play");
        showLoading(false, "");
        startOrCancelTimer(false);
        reconnect.setVisible(true);
        reconnect.setManaged(true);
        stopRecord(false);
        if (policeHelper != null)
            policeHelper.reset();
    }

    private void showErrorAlert(String string) {
        Platform.runLater(() -> {
            JFXAlert alert = new JFXAlert((Stage) root.getScene().getWindow());
            alert.initModality(Modality.WINDOW_MODAL);
            alert.setOverlayClose(false);
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setBody(new Label(string));
            JFXButton closeButton = new JFXButton(resourceBundle.getString("common_ok"));
            closeButton.getStyleClass().add("dialog-accept");
            closeButton.setOnAction(event -> alert.hideWithAnimation());
            layout.setActions(closeButton);
            alert.setContent(layout);
            alert.show();
        });
    }


}
