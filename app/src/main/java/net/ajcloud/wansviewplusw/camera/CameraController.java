package net.ajcloud.wansviewplusw.camera;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXSpinner;
import com.sun.jna.Memory;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.reactivex.functions.Consumer;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.eventbus.Event;
import net.ajcloud.wansviewplusw.support.eventbus.EventBus;
import net.ajcloud.wansviewplusw.support.eventbus.EventType;
import net.ajcloud.wansviewplusw.support.eventbus.event.DeviceRefreshEvent;
import net.ajcloud.wansviewplusw.support.eventbus.event.SnapshotEvent;
import net.ajcloud.wansviewplusw.support.http.HttpCommonListener;
import net.ajcloud.wansviewplusw.support.http.RequestApiUnit;
import net.ajcloud.wansviewplusw.support.timer.CountDownTimer;
import net.ajcloud.wansviewplusw.support.utils.FileUtil;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;
import net.ajcloud.wansviewplusw.support.utils.WLog;
import net.ajcloud.wansviewplusw.support.utils.play.PoliceHelper;
import org.tcprelay.Tcprelay;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_stats_t;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static uk.co.caprica.vlcj.binding.internal.libvlc_state_t.libvlc_Playing;

@ViewController(value = "/fxml/camera.fxml", title = "Camera")
public class CameraController implements PoliceHelper.PoliceControlListener {

    private static final String TAG = "CameraController";
    private SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
    @FXMLViewFlowContext
    private ViewFlowContext context;
    @FXML
    private AnchorPane root;
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
    private JFXButton btn_voice_full;
    @FXML
    private ImageView image_voice_full;
    @FXML
    private JFXButton btn_screenshot;
    @FXML
    private JFXButton btn_screenshot_full;
    @FXML
    private ImageView image_screenshot_full;
    @FXML
    private JFXButton btn_play;
    @FXML
    private JFXButton btn_play_full;
    @FXML
    private ImageView image_play_full;
    @FXML
    private JFXButton btn_record;
    @FXML
    private JFXButton btn_record_full;
    @FXML
    private ImageView image_record_full;
    @FXML
    private JFXSpinner loading;
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
    private HBox control_play_control;
    @FXML
    private HBox control_play_control_full;
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

    private JFXPopup qualityPop;

    private ImageView imageView;

    private DirectMediaPlayerComponent mediaPlayerComponent;
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
    private String deviceId;
    private String localUrl;
    private String relay_server_ip;
    private boolean isP2p = false;
    private int p2pNum;
    private int port = 10001;
    private Tcprelay tcprelay;
    //control
    private boolean isMute = false;

    private TimerService timerService = new TimerService();

    /**
     * 定时，限制播放时长
     */
    private static final long PLAY_TIME = 10 * 60 * 1000;
    private CountDownTimer playTimer = new CountDownTimer();

    /**
     * 初始化
     */
    @PostConstruct
    public void init() throws Exception {
        Objects.requireNonNull(context, "context");
        fullscreenListener = (FullscreenListener) context.getRegisteredObject("FullscreenListener");
        //init play
        loading.setVisible(false);
        tcprelay = new Tcprelay();
        policeHelper = new PoliceHelper(this);
        mediaPlayerComponent = new CanvasPlayerComponent();
        mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(mMediaPlayerListener);
        videoSourceRatioProperty = new SimpleFloatProperty(0.4f);
        pixelFormat = PixelFormat.getByteBgraPreInstance();
        initializeImageView();
        //init device list
        lv_devices.depthProperty().set(1);
        lv_devices.setExpanded(true);
        lv_devices.setCellFactory(new Callback<ListView<Camera>, ListCell<Camera>>() {

            @Override
            public ListCell<Camera> call(ListView<Camera> param) {
                DeviceListCell deviceListCell = new DeviceListCell();
                deviceListCell.setOnMouseClicked((v) -> {
                    handleMouseClick(deviceListCell.getItem());
                });
                return deviceListCell;
            }
        });

        AtomicInteger count = new AtomicInteger(0);
        timerService.setCount(count.get());
        timerService.setPeriod(Duration.seconds(1));
        timerService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                count.set((int) t.getSource().getValue());
                if (!canDo()) {
                    return;
                }
                libvlc_media_stats_t p_stats = mediaPlayerComponent.getMediaPlayer().getMediaStatistics();
                double bitrate = p_stats.f_demux_bitrate * 1000;/*KBps*/
                if (bitrate < 0)
                    bitrate = 0.3;
                label_speed.setText((bitrate < 1024 ? ((int) bitrate + "K/s") : String.format("%.1fM/s", bitrate / 1024)));
            }
        });
        initListener();
        initData();

        btn_record.setDisable(true);
        btn_record_full.setDisable(true);
        image_record_full.setDisable(true);
    }

    private void initListener() {
        btn_play_full.onMouseClickedProperty().bindBidirectional(image_play_full.onMouseClickedProperty());
        btn_screenshot_full.onMouseClickedProperty().bindBidirectional(image_screenshot_full.onMouseClickedProperty());
        btn_record_full.onMouseClickedProperty().bindBidirectional(image_record_full.onMouseClickedProperty());
        btn_voice_full.onMouseClickedProperty().bindBidirectional(image_voice_full.onMouseClickedProperty());
        //function
        btn_voice.setOnMouseClicked((v) -> {
            setMute();
        });
        btn_voice_full.setOnMouseClicked((v) -> {
            setMute();
        });
        btn_screenshot.setOnMouseClicked((v) -> {
            takeSnapshot();
        });
        btn_screenshot_full.setOnMouseClicked((v) -> {
            takeSnapshot();
        });
        btn_play.setOnMouseClicked((v) -> {
            startOrPause();
        });
        btn_play_full.setOnMouseClicked((v) -> {
            startOrPause();
        });
        btn_record.setOnMouseClicked((v) -> {
            if (!canDo()) {
                return;
            }
        });
        btn_record_full.setOnMouseClicked((v) -> {
            if (!canDo()) {
                return;
            }
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
                start();
            }
            content_tips.setVisible(false);
            label_stop.setVisible(true);
            label_stop.setManaged(true);
        });
        //direction
        btn_top.addEventFilter(MouseEvent.ANY, event -> {
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
        });
        btn_right.addEventFilter(MouseEvent.ANY, event -> {
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
        });
        btn_bottom.addEventFilter(MouseEvent.ANY, event -> {
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
        });
        btn_left.addEventFilter(MouseEvent.ANY, event -> {
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
        });
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
        EventBus.getInstance().register(new Consumer<Event>() {
            @Override
            public void accept(Event event) throws Exception {
                if (event.getType() == EventType.DEVICE_REFRESH) {
                    DeviceRefreshEvent deviceRefreshEvent = (DeviceRefreshEvent) event;
                    doSnapShot(deviceRefreshEvent.getDeviceId());
                }
            }
        });
    }

    private void initData() {
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
                        }
                    }
                });
            }

            @Override
            public void onFail(int code, String msg) {

            }
        });
    }

    private void initView(String deviceId) {
        //播放按钮
        btn_play.getStyleClass().remove("jfx_button_pause");
        btn_play.getStyleClass().remove("jfx_button_play");
        btn_play.getStyleClass().add("jfx_button_pause");
        image_play_full.getStyleClass().remove("image_play_full");
        image_play_full.getStyleClass().remove("image_pause_full");
        image_play_full.getStyleClass().add("image_pause_full");

        //播放背景
        setPlayBg(deviceId);
        //底部操作栏
        control_play_control.setVisible(true);
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
        if (mediaPlayerComponent.getMediaPlayer().isPlaying()) {
            if (!StringUtil.isNullOrEmpty(deviceId) && StringUtil.equals(deviceId, camera.deviceId)) {
                return;
            }
            if (isP2p) {
                new Thread(() -> tcprelay.relaydisconnect(p2pNum)).start();
            }
            mediaPlayerComponent.getMediaPlayer().stop();
        }
        this.deviceId = camera.deviceId;
        play();
    }

    private void doSnapShot(String deviceId) {
        Camera camera = DeviceCache.getInstance().get(deviceId);
        if (camera == null ||
                !camera.isOnline() ||
                (camera.isShare() && !camera.isVaild())) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
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
            }
        }).start();
    }

    @Override
    public void onCannotPlay() {

    }

    @Override
    public void onPlay(int playMethod, String url, int mVideoHeight, int mVideoWidth) {
        isP2p = false;
        onVideoPlay(url);
    }

    @Override
    public void onP2pPlay(String relayServer, String url) {
        this.localUrl = url;
        p2pNum = 0;
        isP2p = true;
        this.relay_server_ip = relayServer;
        new P2pTask().start();
    }

    private void onVideoPlay(String url) {
        if (!StringUtil.isNullOrEmpty(url)) {
            btn_play.getStyleClass().remove("jfx_button_pause");
            btn_play.getStyleClass().remove("jfx_button_play");
            btn_play.getStyleClass().add("jfx_button_pause");
            image_play_full.getStyleClass().remove("image_play_full");
            image_play_full.getStyleClass().remove("image_pause_full");
            image_play_full.getStyleClass().add("image_pause_full");
            startOrCancelTimer(true);
            mediaPlayerComponent.getMediaPlayer().playMedia(url);
        }
    }

    public void play() {
        Camera camera = DeviceCache.getInstance().get(deviceId);
        if (camera != null && camera.isOnline()) {
            btn_play.getStyleClass().remove("jfx_button_pause");
            btn_play.getStyleClass().remove("jfx_button_play");
            btn_play.getStyleClass().add("jfx_button_pause");
            image_play_full.getStyleClass().remove("image_play_full");
            image_play_full.getStyleClass().remove("image_pause_full");
            image_play_full.getStyleClass().add("image_pause_full");
            loading.setVisible(true);
            label_name.setText(camera.aliasName);
            policeHelper.setCamera(camera);
            policeHelper.getUrlAndPlay();
        }
    }

    public void stop() {
        if (isP2p) {
            new Thread(() -> tcprelay.relaydisconnect(p2pNum)).start();
        }
        if (mediaPlayerComponent != null && mediaPlayerComponent.getMediaPlayer() != null) {
            btn_play.getStyleClass().remove("jfx_button_pause");
            btn_play.getStyleClass().remove("jfx_button_play");
            btn_play.getStyleClass().add("jfx_button_play");
            image_play_full.getStyleClass().remove("image_play_full");
            image_play_full.getStyleClass().remove("image_pause_full");
            image_play_full.getStyleClass().add("image_play_full");
            mediaPlayerComponent.getMediaPlayer().stop();
            startOrCancelTimer(false);
        }
    }

    public void destory() {
        if (isP2p) {
            new Thread(() -> tcprelay.relaydisconnect(p2pNum)).start();
        }
        if (mediaPlayerComponent != null && mediaPlayerComponent.getMediaPlayer() != null) {
            btn_play.getStyleClass().remove("jfx_button_pause");
            btn_play.getStyleClass().remove("jfx_button_play");
            btn_play.getStyleClass().add("jfx_button_play");
            image_play_full.getStyleClass().remove("image_play_full");
            image_play_full.getStyleClass().remove("image_pause_full");
            image_play_full.getStyleClass().add("image_play_full");
            mediaPlayerComponent.getMediaPlayer().stop();
            mediaPlayerComponent.getMediaPlayer().release();
            startOrCancelTimer(false);
        }
    }

    private void initializeImageView() {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        writableImage = new WritableImage((int) visualBounds.getWidth(), (int) visualBounds.getHeight());

        imageView = new ImageView(writableImage);
        playPane.getChildren().add(imageView);

        playPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            fitImageViewSize(newValue.floatValue(), (float) playPane.getHeight());
        });

        playPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            fitImageViewSize((float) playPane.getWidth(), newValue.floatValue());
        });

        videoSourceRatioProperty.addListener((observable, oldValue, newValue) -> {
            fitImageViewSize((float) playPane.getWidth(), (float) playPane.getHeight());
        });
    }

    private void fitImageViewSize(float width, float height) {
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                requestApiUnit.setPtz(deviceId, action, new HttpCommonListener<Object>() {
                    @Override
                    public void onSuccess(Object bean) {

                    }

                    @Override
                    public void onFail(int code, String msg) {

                    }
                });
            }
        }).start();
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

    private class CanvasBufferFormatCallback implements BufferFormatCallback {
        @Override
        public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            Platform.runLater(() -> videoSourceRatioProperty.set((float) sourceHeight / (float) sourceWidth));
            return new RV32BufferFormat((int) visualBounds.getWidth(), (int) visualBounds.getHeight());
        }
    }

    class P2pTask extends Thread {
        @Override
        public void run() {
            WLog.w(TAG, "p2p-----process:relayconnect");
            p2pNum = tcprelay.relayconnect(deviceId, DeviceCache.getInstance().get(deviceId).getStunServers(), relay_server_ip, port);
            WLog.w(TAG, "p2p----- status:" + p2pNum);
            if (p2pNum > 0) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //更新JavaFX的主线程的代码放在此处
                        StringBuilder url = new StringBuilder();
                        String token = localUrl.split("live")[1];

                        url.append("rtsp://");
                        url.append("127.0.0.1:");
                        url.append(port);
                        url.append("/live");
                        url.append(token);

                        WLog.w("p2p_debug", "------p2p-camera-----" + url.toString());
                        onVideoPlay(url.toString());
                    }
                });
            }
        }
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
            Image image = new Image(file.toURI().toString(), 0, 0, false, true, false);
            playBg.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, true, true, true, true))));
            playBg.setEffect(new GaussianBlur(60));
        }
    }

    /**
     * 截图
     */
    private void takeSnapshot() {
        if (!canDo()) {
            return;
        }
        mediaPlayerComponent.getMediaPlayer().saveSnapshot(new File(FileUtil.getImagePath(DeviceCache.getInstance().getSigninBean().mail) + File.separator + sDateFormat.format(System.currentTimeMillis()) + ".jpg"));
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
        image_voice_full.getStyleClass().remove("image_voice_full");
        image_voice_full.getStyleClass().remove("image_voice_full_mute");
        if (isMute) {
            btn_voice.getStyleClass().add("jfx_button_voice_mute");
            image_voice_full.getStyleClass().add("image_voice_full_mute");
        } else {
            btn_voice.getStyleClass().add("jfx_button_voice");
            image_voice_full.getStyleClass().add("image_voice_full");
        }
        mediaPlayerComponent.getMediaPlayer().mute(isMute);
    }

    /**
     * 开始/暂停
     */
    private void startOrPause() {
        if (StringUtil.isNullOrEmpty(deviceId)) {
            return;
        }
        btn_play.getStyleClass().remove("jfx_button_pause");
        btn_play.getStyleClass().remove("jfx_button_play");
        image_play_full.getStyleClass().remove("image_play_full");
        image_play_full.getStyleClass().remove("image_pause_full");
        if (mediaPlayerComponent.getMediaPlayer().isPlaying()) {
            btn_play.getStyleClass().add("jfx_button_play");
            image_play_full.getStyleClass().add("image_play_full");
            mediaPlayerComponent.getMediaPlayer().pause();
            startOrCancelTimer(false);
        } else {
            btn_play.getStyleClass().add("jfx_button_pause");
            image_play_full.getStyleClass().add("image_pause_full");
            mediaPlayerComponent.getMediaPlayer().start();
            startOrCancelTimer(true);
        }
    }

    /**
     * 开始
     */
    private void start() {
        if (StringUtil.isNullOrEmpty(deviceId)) {
            return;
        }
        if (!mediaPlayerComponent.getMediaPlayer().isPlaying()) {
            btn_play.getStyleClass().remove("jfx_button_pause");
            btn_play.getStyleClass().remove("jfx_button_play");
            btn_play.getStyleClass().add("jfx_button_pause");
            image_play_full.getStyleClass().remove("image_pause_full");
            image_play_full.getStyleClass().remove("image_play_full");
            image_play_full.getStyleClass().add("image_pause_full");
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
        if (mediaPlayerComponent.getMediaPlayer().isPlaying()) {
            btn_play.getStyleClass().remove("jfx_button_pause");
            btn_play.getStyleClass().remove("jfx_button_play");
            btn_play.getStyleClass().add("jfx_button_play");
            image_play_full.getStyleClass().remove("image_pause_full");
            image_play_full.getStyleClass().remove("image_play_full");
            image_play_full.getStyleClass().add("image_play_full");
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
        File directory = new File(FileUtil.getVideoPath(DeviceCache.getInstance().getSigninBean().mail));
//        if (mediaPlayerComponent.getMediaPlayer().)
    }

    /**
     * 重新播放
     */
    private void restart() {
        if (!canDo()) {
            return;
        }
        stop();
        play();
    }

    private void startOrCancelTimer(boolean isStart) {
        playTimer.cancel();
        if (isStart) {
            playTimer.CountDown(PLAY_TIME, new CountDownTimer.OnTimerListener() {
                @Override
                public void onTick(int second) {
                    if (second > 10) {
                        content_tips.setVisible(true);
                        label_time.setText((PLAY_TIME / 1000 - second) + "s");
                    }
                }

                @Override
                public void onFinish() {
                    WLog.w("playTimer", "onFinish");
                    stop();
                    label_time.setText("Have stopped");
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

    private final MediaPlayerEventListener mMediaPlayerListener = new MediaPlayerEventListener() {
        @Override
        public void mediaChanged(MediaPlayer mediaPlayer, libvlc_media_t libvlc_media_t, String s) {

        }

        @Override
        public void opening(MediaPlayer mediaPlayer) {

        }

        @Override
        public void buffering(MediaPlayer mediaPlayer, float v) {

        }

        @Override
        public void playing(MediaPlayer mediaPlayer) {

        }

        @Override
        public void paused(MediaPlayer mediaPlayer) {

        }

        @Override
        public void stopped(MediaPlayer mediaPlayer) {

        }

        @Override
        public void forward(MediaPlayer mediaPlayer) {

        }

        @Override
        public void backward(MediaPlayer mediaPlayer) {

        }

        @Override
        public void finished(MediaPlayer mediaPlayer) {

        }

        @Override
        public void timeChanged(MediaPlayer mediaPlayer, long l) {

        }

        @Override
        public void positionChanged(MediaPlayer mediaPlayer, float v) {

        }

        @Override
        public void seekableChanged(MediaPlayer mediaPlayer, int i) {

        }

        @Override
        public void pausableChanged(MediaPlayer mediaPlayer, int i) {

        }

        @Override
        public void titleChanged(MediaPlayer mediaPlayer, int i) {

        }

        @Override
        public void snapshotTaken(MediaPlayer mediaPlayer, String s) {

        }

        @Override
        public void lengthChanged(MediaPlayer mediaPlayer, long l) {

        }

        @Override
        public void videoOutput(MediaPlayer mediaPlayer, int i) {
            loading.setVisible(false);
            mediaPlayer.saveSnapshot(new File(FileUtil.getRealtimeImagePath(deviceId) + File.separator + "realtime_picture.jpg"));
            setPlayBg(deviceId);
        }

        @Override
        public void scrambledChanged(MediaPlayer mediaPlayer, int i) {

        }

        @Override
        public void elementaryStreamAdded(MediaPlayer mediaPlayer, int i, int i1) {

        }

        @Override
        public void elementaryStreamDeleted(MediaPlayer mediaPlayer, int i, int i1) {

        }

        @Override
        public void elementaryStreamSelected(MediaPlayer mediaPlayer, int i, int i1) {

        }

        @Override
        public void corked(MediaPlayer mediaPlayer, boolean b) {

        }

        @Override
        public void muted(MediaPlayer mediaPlayer, boolean b) {

        }

        @Override
        public void volumeChanged(MediaPlayer mediaPlayer, float v) {

        }

        @Override
        public void audioDeviceChanged(MediaPlayer mediaPlayer, String s) {

        }

        @Override
        public void chapterChanged(MediaPlayer mediaPlayer, int i) {

        }

        @Override
        public void error(MediaPlayer mediaPlayer) {

        }

        @Override
        public void mediaMetaChanged(MediaPlayer mediaPlayer, int i) {

        }

        @Override
        public void mediaSubItemAdded(MediaPlayer mediaPlayer, libvlc_media_t libvlc_media_t) {

        }

        @Override
        public void mediaDurationChanged(MediaPlayer mediaPlayer, long l) {

        }

        @Override
        public void mediaParsedChanged(MediaPlayer mediaPlayer, int i) {

        }

        @Override
        public void mediaFreed(MediaPlayer mediaPlayer) {

        }

        @Override
        public void mediaStateChanged(MediaPlayer mediaPlayer, int i) {

        }

        @Override
        public void mediaSubItemTreeAdded(MediaPlayer mediaPlayer, libvlc_media_t libvlc_media_t) {

        }

        @Override
        public void newMedia(MediaPlayer mediaPlayer) {

        }

        @Override
        public void subItemPlayed(MediaPlayer mediaPlayer, int i) {

        }

        @Override
        public void subItemFinished(MediaPlayer mediaPlayer, int i) {

        }

        @Override
        public void endOfSubItems(MediaPlayer mediaPlayer) {

        }

//        @Override
//        public void onEvent(MediaPlayer.Event eventbus) {
//
//            switch (eventbus.event) {
//                case MediaPlayer.Event.Stopped:
//                    WLog.i(TAG, "Stopped");
//                    if (NewVideoView.this.mOnChangeListener != null) {
//                        isVideoOut = false;
//                        NewVideoView.this.mOnChangeListener.onVideoStop();
//                    }
//                    break;
//                case MediaPlayer.Event.EndReached:
//                    WLog.d(TAG, "EndReached");
//                    if (NewVideoView.this.mOnChangeListener != null) {
//                        if (OldEvent == Media.State.Buffering) {
//                            NewVideoView.this.mOnChangeListener.onError();
//                        } else {
//                            NewVideoView.this.mOnChangeListener.onEnd();
//                        }
//                    }
//                    break;
//                case MediaPlayer.Event.EncounteredError:
//                    WLog.d(TAG, "EncounteredError");
//                    if (NewVideoView.this.mOnChangeListener != null) {
//                        NewVideoView.this.mOnChangeListener.onError();
//                    }
//                    break;
//                case MediaPlayer.Event.TimeChanged:
////                    WLog.d(TAG, "TimeChanged");
//                    if (NewVideoView.this.mOnChangeListener != null) {
//                        NewVideoView.this.mOnChangeListener.onTimeChange(eventbus.getTimeChanged());
//                    }
//                    break;
//                case MediaPlayer.Event.PositionChanged:
////                    WLog.d(TAG, "PositionChanged");
//                    if (!NewVideoView.this.mCanSeek) {
//                        NewVideoView.this.mCanSeek = true;
//                    }
//                    break;
//                case MediaPlayer.Event.Buffering:
//                    WLog.d(TAG, "Buffering");
//                    if (NewVideoView.this.mOnChangeListener != null) {
//                        if (isVideoOut) {
//                            if (OldEvent == Media.State.Playing) {
//                                NewVideoView.this.mOnChangeListener.onFirstBufferChanged(eventbus.getBufferingChanged());
//                            } else {
//                                NewVideoView.this.mOnChangeListener.onBufferChanged(eventbus.getBufferingChanged());
//                            }
//                        }
//                    }
//                    break;
//                case MediaPlayer.Event.Vout:
//                    WLog.d(TAG, "Vout:" + OldEvent);
//                    if (NewVideoView.this.mOnChangeListener != null) {
//                        if (OldEvent == Media.State.Playing) {
//                            isVideoOut = true;
//                            NewVideoView.this.mOnChangeListener.onLoadComplet();
//                        }
//                    }
//                    break;
//                case MediaPlayer.Event.ESAdded:
//                    WLog.d(TAG, "ESAdded");
//                    break;
//                case MediaPlayer.Event.ESDeleted:
//                    WLog.d(TAG, "ESDeleted");
//                    break;
//                case MediaPlayer.Event.ESSelected:
//                    WLog.d(TAG, "ESSelected");
//                    break;
//                case MediaPlayer.Event.PausableChanged:
//                    WLog.d(TAG, "PausableChanged");
//                    break;
//                case MediaPlayer.Event.SeekableChanged:
//                    WLog.d(TAG, "SeekableChanged");
//                    break;
//                case MediaPlayer.Event.ESDelay:
//                    WLog.d(TAG, "ESDelay");
//                    break;
//                default:
//                    WLog.d(TAG, "default");
//                    break;
//            }
//            OldEvent = mMediaPlayer.getPlayerState();
//            WLog.d(TAG, "PlayerState:" + OldEvent);
//        }
    };

    private static class TimerService extends ScheduledService<Integer> {
        private IntegerProperty count = new SimpleIntegerProperty();

        public final void setCount(Integer value) {
            count.set(value);
        }

        public final Integer getCount() {
            return count.get();
        }

        public final IntegerProperty countProperty() {
            return count;
        }

        protected Task<Integer> createTask() {
            return new Task<Integer>() {
                protected Integer call() {
                    //Adds 1 to the count
                    count.set(getCount() + 1);
                    return getCount();
                }
            };
        }
    }

    private Timeline controlTimeline = new Timeline(new KeyFrame(Duration.seconds(3), ae -> {
        gp_control.setVisible(false);
    }));

    private void showControlPane(boolean isShow) {
        if (!StringUtil.isNullOrEmpty(deviceId)) {
            Camera camera = DeviceCache.getInstance().get(deviceId);
            if (camera != null && camera.hasPtz()) {
                if (isShow) {
                    if (!gp_control.isVisible()) {
                        gp_control.setVisible(true);
                    }
                    controlTimeline.stop();
                } else {
                    if (gp_control.isVisible()) {
                        controlTimeline.setCycleCount(1);
                        controlTimeline.play();
                    }
                }
            }
        }
    }


    /**
     * 全屏
     */
    public void fullscreen(boolean isFullscreen) {
        control_play_control_full.setVisible(isFullscreen);
        control_play_control_full.setManaged(isFullscreen);
        content_left.setVisible(!isFullscreen);
        content_left.setManaged(!isFullscreen);
        content_bottom.setVisible(!isFullscreen);
        content_bottom.setManaged(!isFullscreen);
        label_name.setVisible(!isFullscreen);
        label_name.setManaged(!isFullscreen);

        btn_fullscreen.getStyleClass().remove("btn_smallscreen");
        btn_fullscreen.getStyleClass().remove("btn_fullscreen");
        if (isFullscreen) {
            btn_fullscreen.getStyleClass().add("btn_smallscreen");
        } else {
            btn_fullscreen.getStyleClass().add("btn_fullscreen");
        }
    }

    public interface FullscreenListener {
        void fullscreen(boolean isFullScreen);
    }
}
