package net.ajcloud.wansviewplusw.support.vlc;

public class MediaEvent extends VLCEvent {
    public static final int MediaChanged = 0x100;
    //public static final int NothingSpecial      = 0x101;
    public static final int Opening = 0x102;
    public static final int Buffering = 0x103;
    public static final int Playing = 0x104;
    public static final int Paused = 0x105;
    public static final int Stopped = 0x106;
    //public static final int Forward             = 0x107;
    //public static final int Backward            = 0x108;
    public static final int EndReached = 0x109;
    public static final int EncounteredError = 0x10a;
    public static final int TimeChanged = 0x10b;
    public static final int PositionChanged = 0x10c;
    public static final int SeekableChanged = 0x10d;
    public static final int PausableChanged = 0x10e;
    //public static final int TitleChanged        = 0x10f;
    //public static final int SnapshotTaken       = 0x110;
    //public static final int LengthChanged       = 0x111;
    public static final int Vout = 0x112;
    //public static final int ScrambledChanged    = 0x113;
    public static final int ESAdded = 0x114;
    public static final int ESDeleted = 0x115;
    public static final int ESSelected = 0x116;
    public static final int ESDelay = 0x120;


    protected MediaEvent(int type) {
        super(type);
    }

    protected MediaEvent(int type, long arg1) {
        super(type, arg1);
    }

    protected MediaEvent(int type, long arg1, long arg2) {
        super(type, arg1, arg2);
    }

    protected MediaEvent(int type, float argf) {
        super(type, argf);
    }

    public long getTimeChanged() {
        return arg1;
    }

    public float getPositionChanged() {
        return argf1;
    }

    public int getVoutCount() {
        return (int) arg1;
    }

    public int getEsChangedType() {
        return (int) arg1;
    }

    public int getEsChangedID() {
        return (int) arg2;
    }

    public boolean getPausable() {
        return arg1 != 0;
    }

    public boolean getSeekable() {
        return arg1 != 0;
    }

    public float getBuffering() {
        return argf1;
    }

    public float getBufferingChanged() {
        return argf1;
    }
}
