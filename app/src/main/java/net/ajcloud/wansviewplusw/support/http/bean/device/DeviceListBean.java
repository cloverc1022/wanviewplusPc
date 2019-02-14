package net.ajcloud.wansviewplusw.support.http.bean.device;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mamengchao on 2018/12/17.
 * Function:
 */
public class DeviceListBean {
    public List<ConDeviceBean> conDevices;                      //设备列表
    public List<DeviceGeneralsBean> devGenerals;             //设备基础信息
    public List<InvitesBean> invites;                                    //设备分享信息
    public List<GroupsBean> groups;                                  //四分屏信息
}
