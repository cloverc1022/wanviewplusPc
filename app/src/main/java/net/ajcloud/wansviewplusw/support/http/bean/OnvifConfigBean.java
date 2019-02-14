package net.ajcloud.wansviewplusw.support.http.bean;

import java.io.Serializable;

/**
 * Created by mamengchao on 2019/01/14.
 * Function:
 */
public class OnvifConfigBean implements Serializable {
    public int enable;
    public int port;
    public int verfiy;

    public boolean enable() {
        return enable == 1;
    }
}
