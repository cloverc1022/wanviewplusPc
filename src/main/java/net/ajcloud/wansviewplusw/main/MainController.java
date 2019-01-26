package net.ajcloud.wansviewplusw.main;

import com.sun.jna.Memory;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import net.ajcloud.wansviewplusw.BaseController;
import net.ajcloud.wansviewplusw.support.device.Camera;
import net.ajcloud.wansviewplusw.support.http.HttpCommonListener;
import net.ajcloud.wansviewplusw.support.http.RequestApiUnit;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DefaultDirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import java.nio.ByteBuffer;
import java.util.List;

public class MainController implements BaseController {

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

    /**
     * Lightweight JavaFX canvas, the video is rendered here.
     */
    private Canvas canvas;

    /**
     * Pixel writer to update the canvas.
     */
    private PixelWriter pixelWriter;

    /**
     * Pixel format.
     */
    private WritablePixelFormat<ByteBuffer> pixelFormat;

    /**
     * The vlcj direct rendering media player component.
     */
    private DirectMediaPlayerComponent mediaPlayerComponent;

    private AnimationTimer timer;

    @FXML
    private ListView<Camera> deviceList;
    @FXML
    private BorderPane playPane;
    @FXML
    private SplitPane listSplit;
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

    /**
     * 初始化
     */
    public void init() {
        canvas = new Canvas();
        pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
        pixelFormat = PixelFormat.getByteBgraInstance();
        playPane.setCenter(canvas);
        mediaPlayerComponent = new TestMediaPlayerComponent();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                renderFrame();
            }
        };

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

    /**
     * Implementation of a direct rendering media player component that renders
     * the video to a JavaFX canvas.
     */
    private class TestMediaPlayerComponent extends DirectMediaPlayerComponent {

        public TestMediaPlayerComponent() {
            super(new TestBufferFormatCallback());
        }
    }

    /**
     * Callback to get the buffer format to use for video playback.
     */
    private class TestBufferFormatCallback implements BufferFormatCallback {

        @Override
        public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
            final int width;
            final int height;
            if (useSourceSize) {
                width = sourceWidth;
                height = sourceHeight;
            } else {
                width = WIDTH;
                height = HEIGHT;
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    canvas.setWidth(width);
                    canvas.setHeight(height);
                }
            });
            return new RV32BufferFormat(width, height);
        }
    }

    protected final void renderFrame() {
        Memory[] nativeBuffers = mediaPlayerComponent.getMediaPlayer().lock();
        if (nativeBuffers != null) {
            // FIXME there may be more efficient ways to do this...
            // Since this is now being called by a specific rendering time, independent of the native video callbacks being
            // invoked, some more defensive conditional checks are needed
            Memory nativeBuffer = nativeBuffers[0];
            if (nativeBuffer != null) {
                ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
                BufferFormat bufferFormat = ((DefaultDirectMediaPlayer) mediaPlayerComponent.getMediaPlayer()).getBufferFormat();
                if (bufferFormat.getWidth() > 0 && bufferFormat.getHeight() > 0) {
                    pixelWriter.setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
                }
            }
        }
        mediaPlayerComponent.getMediaPlayer().unlock();
    }

    public void play(String url) {
        mediaPlayerComponent.getMediaPlayer().playMedia(url);
        timer.start();
    }

    public void stop() {
        timer.stop();
        mediaPlayerComponent.getMediaPlayer().stop();
        mediaPlayerComponent.getMediaPlayer().release();
    }
}
