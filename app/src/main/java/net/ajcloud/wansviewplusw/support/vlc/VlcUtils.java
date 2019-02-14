package net.ajcloud.wansviewplusw.support.vlc;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

public class VlcUtils {

    private static final String NATIVE_LIBRARY_SEARCH_PATH = "dll";
    static {
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), NATIVE_LIBRARY_SEARCH_PATH);
    }

    public VlcUtils() {
    }
}
