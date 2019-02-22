package net.ajcloud.wansviewplusw.camera;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import com.sun.jna.Memory;
import io.datafx.controller.ViewController;
import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.util.Callback;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.http.HttpCommonListener;
import net.ajcloud.wansviewplusw.support.http.RequestApiUnit;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;
import net.ajcloud.wansviewplusw.support.utils.WLog;
import net.ajcloud.wansviewplusw.support.utils.play.PoliceHelper;
import org.tcprelay.Tcprelay;
import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import javax.annotation.PostConstruct;
import java.nio.ByteBuffer;
import java.util.List;

@ViewController(value = "/fxml/camera.fxml", title = "Camera")
public class CameraController implements PoliceHelper.PoliceControlListener {

    private static final String TAG = "CameraController";
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
    private JFXSpinner loading;
    @FXML
    private JFXListView<Camera> lv_devices;
    private ImageView imageView;
    /**
     * Set this to <code>true</code> to resize the display to the dimensions of the
     * video, otherwise it will use {@link #WIDTH} and {@link #HEIGHT}.
     */
    private static final boolean useSourceSize = false;

    /**
     * Target width, unless {@link #useSourceSize} is set.
     */
    private static final int WIDTH = 668;

    /**
     * Target height, unless {@link #useSourceSize} is set.
     */
    private static final int HEIGHT = 376;
    private DirectMediaPlayerComponent mediaPlayerComponent;
    private WritableImage writableImage;
    private WritablePixelFormat<ByteBuffer> pixelFormat;
    private FloatProperty videoSourceRatioProperty;
    private RequestApiUnit requestApiUnit;
    private ObservableList<Camera> mInfos = FXCollections.observableArrayList();
    private PoliceHelper policeHelper;
    private String deviceId;
    private String localUrl;
    private String relay_server_ip;
    private boolean isP2p = false;
    private int p2pNum;
    private int port = 10001;
    private Tcprelay tcprelay;

    /**
     * 初始化
     */
    @PostConstruct
    public void init() throws Exception {
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
                return new DeviceListCell();
            }
        });
        lv_devices.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                handleMouseClick(event);
            }
        });
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

    public void handleMouseClick(MouseEvent mouseEvent) {
        if (mediaPlayerComponent.getMediaPlayer().isPlaying()) {
            if (!StringUtil.isNullOrEmpty(deviceId)&&StringUtil.equals(deviceId,lv_devices.getSelectionModel().getSelectedItem().deviceId)){
                return;
            }
            if (isP2p) {
                tcprelay.relaydisconnect(p2pNum);
            }
            mediaPlayerComponent.getMediaPlayer().stop();
        }
        this.deviceId = lv_devices.getSelectionModel().getSelectedItem().deviceId;
        Camera camera = DeviceCache.getInstance().get(deviceId);
        if (camera != null) {
            loading.setVisible(true);
            label_name.setText(camera.aliasName);
            policeHelper.setCamera(camera);
            policeHelper.getUrlAndPlay();
        }
    }

    @Override
    public void onCannotPlay() {

    }

    @Override
    public void onPlay(int playMethod, String url, int mVideoHeight, int mVideoWidth) {
        isP2p = false;
        mediaPlayerComponent.getMediaPlayer().playMedia(url);
    }

    @Override
    public void onP2pPlay(String relayServer, String url) {
        this.localUrl = url;
        p2pNum = 0;
        isP2p = true;
        this.relay_server_ip = relayServer;
        new P2pTask().start();
    }

    public void stop() {
        if (isP2p) {
            tcprelay.relaydisconnect(p2pNum);
        }
        mediaPlayerComponent.getMediaPlayer().stop();
        mediaPlayerComponent.getMediaPlayer().release();
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
            if (writableImage == null) {
                return;
            }
            Platform.runLater(() -> {
                Memory nativeBuffer = mediaPlayer.lock()[0];
                try {
                    ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
                    getPW().setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
                } finally {
                    mediaPlayer.unlock();
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
//            WLog.w(TAG, "p2p----- status:" + p2pNum);
//
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
                        mediaPlayerComponent.getMediaPlayer().playMedia(url.toString());
                    }
                });
            }
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
//        public void onEvent(MediaPlayer.Event event) {
//
//            switch (event.type) {
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
//                        NewVideoView.this.mOnChangeListener.onTimeChange(event.getTimeChanged());
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
//                                NewVideoView.this.mOnChangeListener.onFirstBufferChanged(event.getBufferingChanged());
//                            } else {
//                                NewVideoView.this.mOnChangeListener.onBufferChanged(event.getBufferingChanged());
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
}
