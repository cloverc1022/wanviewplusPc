package net.ajcloud.wansviewplusw.main;

import com.sun.jna.Memory;
import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.util.Callback;
import net.ajcloud.wansviewplusw.BaseController;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.http.HttpCommonListener;
import net.ajcloud.wansviewplusw.support.http.RequestApiUnit;
import net.ajcloud.wansviewplusw.support.utils.P2pInterface;
import net.ajcloud.wansviewplusw.support.utils.WLog;
import net.ajcloud.wansviewplusw.support.utils.play.PoliceHelper;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import java.nio.ByteBuffer;
import java.util.List;

public class MainController implements BaseController, PoliceHelper.PoliceControlListener {

    /**
     * Set this to <code>true</code> to resize the display to the dimensions of the
     * video, otherwise it will use {@link #WIDTH} and {@link #HEIGHT}.
     */
    private static final boolean useSourceSize = false;

    /**
     * Target width, unless {@link #useSourceSize} is set.
     */
    private static final int WIDTH = 1080;

    /**
     * Target height, unless {@link #useSourceSize} is set.
     */
    private static final int HEIGHT = 607;

    private ImageView imageView;

    private DirectMediaPlayerComponent mediaPlayerComponent;

    private WritableImage writableImage;

    private WritablePixelFormat<ByteBuffer> pixelFormat;

    private FloatProperty videoSourceRatioProperty;


    @FXML
    private ListView<Camera> deviceList;
    @FXML
    private BorderPane playPane;
    @FXML
    private SplitPane listSplit;
    private RequestApiUnit requestApiUnit;
    private ObservableList<Camera> mInfos = FXCollections.observableArrayList();
    private PoliceHelper policeHelper;
    private String deviceId;
    private String localUrl;
    private String relay_server_ip;
    private int port = 10001;
    private static final String TAG = "MainController";

    class P2pTask extends Thread {
        @Override
        public void run() {
            WLog.w(TAG, "p2p-----process:relayconnect");
            int p2pNum = P2pInterface.instanceDll.SE_SetServInfo(deviceId, DeviceCache.getInstance().get(deviceId).getStunServers(), relay_server_ip, port);
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

                        WLog.w("p2p_debug", "------p2p-play-----" + url.toString());
                        mediaPlayerComponent.getMediaPlayer().playMedia(url.toString());
                    }
                });
            }
        }
    }

    @FXML
    public void handleMouseClick(MouseEvent mouseEvent) {
        this.deviceId = deviceList.getSelectionModel().getSelectedItem().deviceId;
        Camera camera = DeviceCache.getInstance().get(deviceId);
        if (camera != null) {
            policeHelper.setCamera(camera);
            policeHelper.getUrlAndPlay();
        }
    }

    /**
     * 初始化
     */
    public void init() {
        policeHelper = new PoliceHelper(this);
        mediaPlayerComponent = new CanvasPlayerComponent();
        videoSourceRatioProperty = new SimpleFloatProperty(0.4f);
        pixelFormat = PixelFormat.getByteBgraPreInstance();
        initializeImageView();

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

    @FXML
    private void initialize() {
    }

    public void stop() {
        mediaPlayerComponent.getMediaPlayer().stop();
        mediaPlayerComponent.getMediaPlayer().release();
    }

    @Override
    public void onCannotPlay() {

    }

    @Override
    public void onPlay(int playMethod, String url, int mVideoHeight, int mVideoWidth) {
        mediaPlayerComponent.getMediaPlayer().playMedia(url);
    }

    @Override
    public void onP2pPlay(String relayServer, String url) {
        this.localUrl = url;
        this.relay_server_ip = relayServer;
        new P2pTask().start();
    }
}
