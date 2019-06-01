package net.ajcloud.wansviewplusw.support.customview;


import com.jfoenix.controls.JFXButton;
import com.sun.jna.Memory;
import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import net.ajcloud.wansviewplusw.BaseController;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.timer.CountDownTimer;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;
import net.ajcloud.wansviewplusw.support.utils.WLog;
import net.ajcloud.wansviewplusw.support.utils.play.PlayMethod;
import net.ajcloud.wansviewplusw.support.utils.play.PoliceHelper;
import net.ajcloud.wansviewplusw.support.utils.play.TcprelayHelper;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.binding.internal.libvlc_state_t;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ResourceBundle;

import static uk.co.caprica.vlcj.binding.internal.libvlc_state_t.libvlc_Playing;

public class PlayItemController implements BaseController, PoliceHelper.PoliceControlListener, Initializable {
    private static final String TAG = "PlayItemController";
    @FXML
    private Label label_name;
    @FXML
    private Label label_status;
    @FXML
    private BorderPane playPane;
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
    private HBox content_tips;
    @FXML
    private Label label_stop;
    @FXML
    private Label label_time;
    @FXML
    private Label label_continue;
    @FXML
    private JFXButton btn_play;
    @FXML
    private JFXButton btn_delete;
    @FXML
    private VBox vb_play;
    @FXML
    private VBox vb_add;
    @FXML
    private StackPane sp_add;
    @FXML
    private Label label_add;

    private ResourceBundle resourceBundle;
    private CanvasPlayerComponent mediaPlayerComponent;
    private ImageView imageView;
    private WritableImage writableImage;
    private WritablePixelFormat<ByteBuffer> pixelFormat;
    private FloatProperty videoSourceRatioProperty;
    private PoliceHelper policeHelper;
    private String deviceId;
    private int play_method;
    private Camera camera;
    private boolean isReady = false;

    /**
     * 定时，限制播放时长
     */
    private static final long PLAY_TIME = 10 * 60 * 1000;
    private CountDownTimer playTimer = new CountDownTimer();

    public void init() {
        sp_add.setOnMouseClicked((v) -> {
            playItemListener.onAdd();
            v.consume();
        });
        label_add.setOnMouseClicked((v) -> {
            playItemListener.onAdd();
            v.consume();
        });
        if (mediaPlayerComponent == null) {
            new Thread(() -> {
                mediaPlayerComponent = new CanvasPlayerComponent();
                mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(mMediaPlayerListener);
                isReady = true;
            }).start();
        }
        policeHelper = new PoliceHelper(this);
        videoSourceRatioProperty = new SimpleFloatProperty(0.4f);
        pixelFormat = PixelFormat.getByteBgraPreInstance();
        initializeImageView();

        initListener();
    }

    public void prepare(String deviceId) {
        if (mediaPlayerComponent != null && mediaPlayerComponent.getMediaPlayer() != null && mediaPlayerComponent.getMediaPlayer().isPlaying()) {
            mediaPlayerComponent.getMediaPlayer().stop();
            startOrCancelTimer(false);
        }

        this.deviceId = deviceId;
        if (StringUtil.isNullOrEmpty(deviceId)) {
            vb_add.setVisible(true);
            vb_add.setManaged(true);
            vb_play.setVisible(false);
            vb_play.setManaged(false);
        } else {
            vb_add.setVisible(false);
            vb_add.setManaged(false);
            vb_play.setVisible(true);
            vb_play.setManaged(true);

            camera = DeviceCache.getInstance().get(deviceId);
            if (camera == null || !camera.isOnline())
                return;
            //init
            showLoading(false, "");
            if (mediaPlayerComponent == null) {
                new Thread(() -> {
                    mediaPlayerComponent = new CanvasPlayerComponent();
                    mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(mMediaPlayerListener);
                    isReady = true;
                }).start();
            }
            label_status.textProperty().bind(camera.deviceStatusProperty());
            label_status.styleProperty().bind(camera.deviceStatusCssProperty());
            label_name.textFillProperty().bind(camera.deviceNameBgProperty());
            label_name.setText(camera.aliasName);
            play();
        }
    }

    private void initListener() {
        reconnect.onMouseClickedProperty().bindBidirectional(iv_reconnect.onMouseClickedProperty());
        reconnect.onMouseClickedProperty().bindBidirectional(label_reconnect.onMouseClickedProperty());
        btn_play.setOnMouseClicked((v) -> {
            startOrStop();
        });
        reconnect.setOnMouseClicked(event -> {
            reconnect.setVisible(false);
            reconnect.setManaged(false);
            if (play_method == PlayMethod.RELAY || play_method == PlayMethod.P2P)
                TcprelayHelper.getInstance().reConnect(deviceId);
            play();
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
        btn_delete.setOnMouseClicked((v) -> {
            if (playItemListener != null)
                playItemListener.onDelete(deviceId);
        });
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

    public void play() {
        if (camera != null) {
            btn_play.getStyleClass().remove("jfx_button_pause");
            btn_play.getStyleClass().remove("jfx_button_play");
            btn_play.getStyleClass().add("jfx_button_pause");
            loading.setVisible(true);
            loading.setManaged(true);
            label_tips.setText(resourceBundle.getString("play_establishing_channel"));
            reconnect.setVisible(false);
            reconnect.setManaged(false);
            camera.setCurrentQuality(1);
            policeHelper.setCamera(camera);
            if (camera.isOnline()) {
                policeHelper.getUrlAndPlay();
            } else {
                cannotPlayDo();
            }
        }
    }

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
        if (policeHelper != null)
            policeHelper.reset();
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
                                mediaPlayerComponent.getMediaPlayer().mute(true);
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

    private void doVideoStop() {
        //init UI
        btn_play.getStyleClass().remove("jfx_button_pause");
        btn_play.getStyleClass().remove("jfx_button_play");
        btn_play.getStyleClass().add("jfx_button_play");
        //结束定时器
        startOrCancelTimer(false);
        //停止播放
        if (mediaPlayerComponent != null && mediaPlayerComponent.getMediaPlayer() != null && mediaPlayerComponent.getMediaPlayer().isPlaying()) {
            WLog.w("onStop--------------", deviceId);
            mediaPlayerComponent.getMediaPlayer().stop();
        }
        //重新建链
        WLog.w("onStop--------------", play_method);
        if ((play_method == PlayMethod.RELAY || play_method == PlayMethod.P2P) && !StringUtil.isNullOrEmpty(deviceId)) {
            TcprelayHelper.getInstance().reConnect(deviceId);
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

    public void stop() {
        if (mediaPlayerComponent != null && mediaPlayerComponent.getMediaPlayer() != null && mediaPlayerComponent.getMediaPlayer().isPlaying()) {
            mediaPlayerComponent.getMediaPlayer().stop();
            startOrCancelTimer(false);
        }
    }

    public void destroy() {
        imageView.setImage(null);
        writableImage = null;
        if (mediaPlayerComponent != null && mediaPlayerComponent.getMediaPlayer() != null && mediaPlayerComponent.getMediaPlayer().isPlaying()) {
            mediaPlayerComponent.getMediaPlayer().stop();
            mediaPlayerComponent.getMediaPlayer().release();
            startOrCancelTimer(false);
            if ((play_method == PlayMethod.RELAY || play_method == PlayMethod.P2P) && !StringUtil.isNullOrEmpty(deviceId)) {
                TcprelayHelper.getInstance().reConnect(deviceId);
            }
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
                fitImageViewSize((float) playPane.getWidth(), (float) playPane.getHeight());
                showLoading(false, "");
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

    @Override
    public void onCannotPlay(String deviceId) {
        cannotPlayDo();
    }

    @Override
    public void onPlay(String deviceId, int playMethod, String url, int mVideoHeight, int mVideoWidth) {
        play_method = playMethod;
        if (playPane != null)
            fitImageViewSize((float) playPane.getWidth(), (float) playPane.getHeight());
        onVideoPlay(url);
    }

    @Override
    public void onP2pPlay(String deviceId) {
        TcprelayHelper.getInstance().getPlayUrl(deviceId, camera.getCurrentQuality(), new TcprelayHelper.ConnectCallback() {
            @Override
            public void onSuccess(String url) {
                play_method = TcprelayHelper.getInstance().getConnectType(deviceId) == 0 ? PlayMethod.P2P : PlayMethod.RELAY;
                Platform.runLater(() -> {
                    if (playPane != null)
                        fitImageViewSize((float) playPane.getWidth(), (float) playPane.getHeight());
                    onVideoPlay(url);
                });
            }

            @Override
            public void onFail() {
                Platform.runLater(() -> {
                    cannotPlayDo();
                });
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourceBundle = resources;
    }

    @Override
    public void Destroy() {

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

    private PlayItemListener playItemListener;

    public interface PlayItemListener {
        void onAdd();

        void onDelete(String deviceId);
    }

    public void setPlayItemListener(PlayItemListener playItemListener) {
        this.playItemListener = playItemListener;
    }
}
