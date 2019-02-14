package net.ajcloud.wansviewplusw.support.http.bean.device;

import java.util.List;

/**
 * Created by mamengchao on 2018/12/17.
 * Function:
 */
public class GroupsBean {
    public String name;                 //分组名称
    public List<String> dids;         //当前仅支持四分屏，按照左上、右上、左下、右下的顺序记录摄像头id。若当前序位不存在，则显示为空字符串
}
