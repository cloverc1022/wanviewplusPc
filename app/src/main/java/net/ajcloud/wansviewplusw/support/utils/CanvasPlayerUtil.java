package net.ajcloud.wansviewplusw.support.utils;

import javafx.beans.property.FloatProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.PixelWriter;
import net.ajcloud.wansviewplusw.support.customview.CanvasBufferFormatCallback;
import net.ajcloud.wansviewplusw.support.customview.CanvasPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;

public class CanvasPlayerUtil {

    private CanvasPlayerComponent mediaPlayerComponent;
    private CanvasBufferFormatCallback canvasBufferFormatCallback;
    private OnInitListener onInitListener;
    private boolean isInit = false;

    private CanvasPlayerUtil() {
    }

    public static CanvasPlayerUtil getInstance() {
        return CanvasPlayerHolder.instance;
    }

    private static class CanvasPlayerHolder {
        private static final CanvasPlayerUtil instance = new CanvasPlayerUtil();
    }

    public void init() {
        new Thread(() -> {
            isInit = false;
            canvasBufferFormatCallback = new CanvasBufferFormatCallback();
            mediaPlayerComponent = new CanvasPlayerComponent(canvasBufferFormatCallback);
            isInit = true;
            if (onInitListener != null) {
                onInitListener.onComplete();
                onInitListener = null;
            }
        }).start();
    }

    public void addOptions(PixelWriter pixelWriter, FloatProperty videoSourceRatioProperty, ChangeListener listener, MediaPlayerEventListener mediaPlayerEventListener) {
        if (mediaPlayerComponent != null) {
            mediaPlayerComponent.setPixelWriter(pixelWriter);
            if (mediaPlayerComponent.getMediaPlayer() != null) {
                mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(mediaPlayerEventListener);
            }
        }
        if (canvasBufferFormatCallback != null) {
            canvasBufferFormatCallback.setVideoSourceRatioProperty(videoSourceRatioProperty, listener);
        }
    }

    public void removeOptions() {
        if (mediaPlayerComponent != null) {
            mediaPlayerComponent.setPixelWriter(null);
        }
    }

    public CanvasPlayerComponent getMediaPlayerComponent() {
        return mediaPlayerComponent;
    }

    public interface OnInitListener {
        void onComplete();
    }

    public void setOnInitListener(OnInitListener onInitListener) {
        this.onInitListener = onInitListener;
    }

    public boolean isInit() {
        return isInit;
    }
}
