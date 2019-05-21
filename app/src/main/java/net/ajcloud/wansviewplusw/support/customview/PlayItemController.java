package net.ajcloud.wansviewplusw.support.customview;


import com.sun.jna.Memory;
import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import net.ajcloud.wansviewplusw.BaseController;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
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

import java.nio.ByteBuffer;

import static uk.co.caprica.vlcj.binding.internal.libvlc_state_t.libvlc_Playing;

public class PlayItemController implements BaseController, PoliceHelper.PoliceControlListener {
    private static final String TAG = "PlayItemController";
    @FXML
    private Label label_name;
    @FXML
    private Label label_status;
    @FXML
    private BorderPane playPane;

    private CanvasPlayerComponent mediaPlayerComponent;
    private ImageView imageView;
    private WritableImage writableImage;
    private WritablePixelFormat<ByteBuffer> pixelFormat;
    private FloatProperty videoSourceRatioProperty;
    private PoliceHelper policeHelper;
    private String deviceId;
    private int play_method;
    private Camera camera;

    public void init(String deviceId) {
        if (StringUtil.isNullOrEmpty(deviceId))
            return;
        this.deviceId = deviceId;
        camera = DeviceCache.getInstance().get(deviceId);
        if (camera == null||!camera.isOnline())
            return;
        //init
        new Thread(() -> {
            mediaPlayerComponent = new CanvasPlayerComponent();
            mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(mMediaPlayerListener);
        }).start();
        policeHelper = new PoliceHelper(this);
        videoSourceRatioProperty = new SimpleFloatProperty(0.4f);
        pixelFormat = PixelFormat.getByteBgraPreInstance();
        initializeImageView();

        label_status.textProperty().bind(camera.deviceStatusProperty());
        label_status.styleProperty().bind(camera.deviceStatusCssProperty());
        label_name.textFillProperty().bind(camera.deviceNameBgProperty());
        label_name.setText(camera.aliasName);
        play();
    }

    public void play() {
        if (camera != null) {
            camera.setCurrentQuality(1);
            policeHelper.setCamera(camera);
            if (camera.isOnline()) {
                policeHelper.getUrlAndPlay();
            } else {
                cannotPlayDo();
            }
        }
    }

    private void cannotPlayDo() {
        if (policeHelper != null)
            policeHelper.reset();
    }

    private void onVideoPlay(String url) {
        try {
            if (!StringUtil.isNullOrEmpty(url)) {
                mediaPlayerComponent.getMediaPlayer().playMedia(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
            cannotPlayDo();
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
//            if (isVideoOut) {
//                //Buffer
//                WLog.w(v);
//                showLoading(v < 100, "");
//            }
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
//            stopRecord(false);
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
//            if (OldEvent == libvlc_Playing) {
//                isVideoOut = true;
//                showLoading(false, "");
//                new Thread(() -> {
//                    try {
//                        Thread.sleep(1500);
//                        if (CanvasPlayerUtil.getInstance().getMediaPlayerComponent() != null && CanvasPlayerUtil.getInstance().getMediaPlayerComponent().getMediaPlayer() != null) {
//                            CanvasPlayerUtil.getInstance().getMediaPlayerComponent().getMediaPlayer().saveSnapshot(new File(FileUtil.getRealtimeImagePath(deviceId) + File.separator + "realtime_picture.jpg"));
//                            EventBus.getInstance().post(new SnapshotEvent());
//                        }
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }).start();
//            }
//            OldEvent = mediaPlayer.getMediaPlayerState();
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
        play_method = TcprelayHelper.getInstance().getConnectType(deviceId) == 0 ? PlayMethod.P2P : PlayMethod.RELAY;
        TcprelayHelper.getInstance().getPlayUrl(deviceId, camera.getCurrentQuality(), new TcprelayHelper.ConnectCallback() {
            @Override
            public void onSuccess(String url) {
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
}
