package net.ajcloud.wansviewplusw.support.http.bean;

import java.io.Serializable;

/**
 * Created by mamengchao on 2018/08/07.
 * Function:
 */
public class NewVersionBean implements Serializable {
    public String version;
    public int priority;
    public String downloadUrl;
    public String releaseNoteUrl;
}
