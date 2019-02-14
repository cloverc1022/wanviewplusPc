package net.ajcloud.wansviewplusw.support.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mamengchao on 2018/05/10.
 */

public class WLog {
    /**
     * isPrint: print switch, true will print. false not print
     */
    private static boolean isPrint = true;
    private static final String defaultTag = "WansView";

    private WLog() {
    }

    public static void i(Object o) {
        if (isPrint)
            Logger.getLogger(defaultTag).log(Level.INFO, o.toString());
    }

    public static void i(String m) {
        if (isPrint)
            Logger.getLogger(defaultTag).log(Level.INFO, m);
    }

    public static void w(Object o) {
        if (isPrint)
            Logger.getLogger(defaultTag).log(Level.WARNING, o.toString());
    }

    public static void w(String m) {
        if (isPrint)
            Logger.getLogger(defaultTag).log(Level.WARNING, m);
    }

    public static void s(Object o) {
        if (isPrint)
            Logger.getLogger(defaultTag).log(Level.SEVERE, o.toString());
    }

    public static void s(String m) {
        if (isPrint)
            Logger.getLogger(defaultTag).log(Level.SEVERE, m);
    }

    /**
     * ******************** WLog json **************************
     */
    public static void json(String tag, String msg) {
        if (isPrint)
            Logger.getLogger(tag).log(Level.INFO, format(msg));
    }

    public static String format(String jsonStr) {
        int level = 0;
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < jsonStr.length(); i++) {
            char c = jsonStr.charAt(i);
            if (level > 0 && '\n' == buffer.charAt(buffer.length() - 1)) {
                buffer.append(getLevelStr(level));
            }
            switch (c) {
                case '{':
                case '[':
                    buffer.append(c).append("\n");
                    level++;
                    break;
                case ',':
                    buffer.append(c).append("\n");
                    break;
                case '}':
                case ']':
                    buffer.append("\n");
                    level--;
                    buffer.append(getLevelStr(level));
                    buffer.append(c);
                    break;
                default:
                    buffer.append(c);
                    break;
            }
        }

        return buffer.toString();

    }

    private static String getLevelStr(int level) {
        StringBuilder buffer = new StringBuilder();
        for (int levelI = 0; levelI < level; levelI++) {
            buffer.append("\t");
        }
        return buffer.toString();
    }

    /**
     * ******************** WLog **************************
     */

    public static void i(String tag, String msg) {
        if (isPrint)
            Logger.getLogger(tag).log(Level.INFO, format(msg));
    }

    public static void w(String tag, String msg) {
        if (isPrint)
            Logger.getLogger(tag).log(Level.WARNING, format(msg));
    }

    public static void s(String tag, String msg) {
        if (isPrint)
            Logger.getLogger(tag).log(Level.SEVERE, format(msg));
    }

    /**
     * ******************** WLog with object list **************************
     */

    public static void i(String tag, Object... msg) {
        if (isPrint)
            Logger.getLogger(tag).log(Level.INFO, getLogMessage(msg));
    }

    public static void w(String tag, Object... msg) {
        if (isPrint)
            Logger.getLogger(tag).log(Level.WARNING, getLogMessage(msg));
    }

    public static void s(String tag, Object... msg) {
        if (isPrint)
            Logger.getLogger(tag).log(Level.SEVERE, getLogMessage(msg));
    }

    private static String getLogMessage(Object... msg) {
        if (msg != null && msg.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (Object s : msg) {
                if (s != null) {
                    sb.append(s.toString());
                }
            }
            return sb.toString();
        }
        return "";
    }


    /**
     * ******************** TAG use Object Tag **************************
     */
    public static void i(Object tag, String msg) {
        if (isPrint)
            Logger.getLogger(tag.getClass().getSimpleName()).log(Level.INFO, getLogMessage(msg));
    }


    public static void w(Object tag, String msg) {
        if (isPrint)
            Logger.getLogger(tag.getClass().getSimpleName()).log(Level.WARNING, getLogMessage(msg));
    }

    public static void s(Object tag, String msg) {
        if (isPrint)
            Logger.getLogger(tag.getClass().getSimpleName()).log(Level.SEVERE, getLogMessage(msg));
    }

    public static String byte2hex(byte[] data) {
        if (data == null) {
            return null;
        }
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            String temp = Integer.toHexString(0xFF & data[i]);
            if (temp.length() == 1) {
                hexString.append("0");
                hexString.append(temp);
            } else if (temp.length() == 2) {
                hexString.append(temp);
            }
        }
        return hexString.toString();
    }
}
