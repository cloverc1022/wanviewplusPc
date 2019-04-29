package net.ajcloud.wansviewplusw.support.customview;

import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

public class CanvasBufferFormatCallback implements BufferFormatCallback {
    private FloatProperty videoSourceRatioProperty;

    @Override
    public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        Platform.runLater(() -> {
            if (videoSourceRatioProperty != null)
                videoSourceRatioProperty.set((float) sourceHeight / (float) sourceWidth);
        });
        return new RV32BufferFormat((int) visualBounds.getWidth(), (int) visualBounds.getHeight());
    }

    public void setVideoSourceRatioProperty(FloatProperty videoSourceRatioProperty, ChangeListener listener) {
        this.videoSourceRatioProperty = videoSourceRatioProperty;
        this.videoSourceRatioProperty.addListener(listener);
    }
}
