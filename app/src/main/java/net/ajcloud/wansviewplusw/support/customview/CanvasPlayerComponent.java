package net.ajcloud.wansviewplusw.support.customview;

import com.sun.jna.Memory;
import javafx.application.Platform;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;

import java.nio.ByteBuffer;

public class CanvasPlayerComponent extends DirectMediaPlayerComponent {

    private PixelWriter pixelWriter = null;
    private WritablePixelFormat<ByteBuffer> pixelFormat;

    public CanvasPlayerComponent(CanvasBufferFormatCallback callback) {
        super(callback);
        pixelFormat = PixelFormat.getByteBgraPreInstance();
    }

    @Override
    public void display(DirectMediaPlayer mediaPlayer, Memory[] nativeBuffers, BufferFormat bufferFormat) {
        if (pixelWriter == null || mediaPlayer == null) {
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
                    pixelWriter.setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
                } finally {
                    mediaPlayer.unlock();
                }
            }
        });
    }

    public PixelWriter getPixelWriter() {
        return pixelWriter;
    }

    public void setPixelWriter(PixelWriter pixelWriter) {
        this.pixelWriter = pixelWriter;
    }
}
