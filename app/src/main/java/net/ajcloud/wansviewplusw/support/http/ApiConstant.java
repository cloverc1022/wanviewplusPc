package net.ajcloud.wansviewplusw.support.http;

import com.alibaba.fastjson.JSONException;
import com.google.gson.JsonObject;
import net.ajcloud.wansviewplusw.support.http.bean.start.AppConfigBean;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;

import java.io.File;

/**
 * Created by mamengchao on 2018/05/21.
 */
public class ApiConstant {
    public static boolean isApply = false;
    public static String appVendorCode = "WVC";
    public static String wanIp = "";
    public static String TOPIC_NOTICE = appVendorCode + "-notice";
    public static String TOPIC_ADS = appVendorCode + "-ads";
    public static String UAC_URL = "https://uac.ajyun.com.cn/api";
    public static String CAP_URL = "https://cap.ajyun.com.cn/api";
    public static String EMC_PORTAL_URL = "https://emc.ajyun.com.cn/api";
    public static String STOR_PORTAL_URL = "https://cloud-stor.ajyun.com.cn/api";

    //v1
    public static String BASE_UAC_URL = UAC_URL + "/v1/";
    public static String BASE_CAP_URL = CAP_URL + "/v1";
    public static String BASE_EMC_PORTAL_URL = EMC_PORTAL_URL + "/v1";
    public static String BASE_STOR_PORTAL_URL = STOR_PORTAL_URL + "/v1";
    //v2
    public static String BASE_UAC_V2_URL = UAC_URL + "/v2/";

    //服务条款与免责声明
    public static String URL_AGREEMENT = "https://www.ajcloud.net/docs/wansview-agreement.html";
    public static String URL_PRIVACY = "https://www.ajcloud.net/docs/wansview-privacy.html";
    public static String URL_CLOUD_AGREEMENT = "https://www.ajcloud.net/docs/ajcloud-cloudstor-agreement.html";

    //Base
    private static String URL_BASE = "https://sdc.ajcloud.net";
    // App启动参数接口(合并app-config、app-version、app-activity)
    public static String URL_START_UP = URL_BASE + "/api/v1/app-startup";
    //设备接入地址信息
    public static String URL_GET_DEVICE_URL_INFO = URL_BASE + "/api/v1/cam-config";
    //解绑清除摄像头注册配置
    public static String URL_DEVICE_WIPE_DATA = URL_BASE + "/api/v1/wipe-data";

    // uac账号相关
    public static String URL_UAC_CHALLENGE = BASE_UAC_URL + "challenge";
    public static String URL_UAC_SIGNIN = BASE_UAC_URL + "signin";
    public static String URL_UAC_RESET_PASSWORD = BASE_UAC_URL + "reset-password";
    public static String URL_UAC_SIGNUP = BASE_UAC_URL + "signup";
    public static String URL_UAC_SIGNOUT = BASE_UAC_URL + "signout";
    public static String URL_UAC_USER_ACTIVE = BASE_UAC_URL + "user-active";
    public static String URL_UAC_CHANGE_PASSWORD = BASE_UAC_URL + "change-password";
    public static String URL_UAC_REFRESH_TOKEN = BASE_UAC_URL + "refresh-token";
    public static String URL_UAC_DELETE_ACCOUNT = BASE_UAC_URL + "logout";
    //uac设备相关
    public static String URL_UAC_PREBIND = BASE_UAC_URL + "req-bind";
    public static String URL_UAC_GET_BIND_STATUS = BASE_UAC_URL + "bind-status";
    public static String URL_UAC_GET_BIND_STATUS_BY_TOKEN = BASE_UAC_URL + "check-bind";
    public static String URL_UAC_GET_DEVICE_LIST = BASE_UAC_URL + "device-list";
    public static String URL_UAC_SET_DEVICE_NAME_UAC = BASE_UAC_URL + "change-alias-name";
    public static String URL_UAC_UNBIND = BASE_UAC_URL + "unbind";
    public static String URL_UAC_SHARE = BASE_UAC_URL + "share-camera";
    public static String URL_UAC_SHARE_DELETE = BASE_UAC_URL + "delete-share-entry";
    public static String URL_UAC_SHARE_DELETE_ALL = BASE_UAC_URL + "delete-share-all";
    public static String URL_UAC_GUEST_BIND = BASE_UAC_URL + "bind-invite-camera";
    public static String URL_UAC_GUEST_UNBIND = BASE_UAC_URL + "unbind-invite-camera";
    //emc
    public static String URL_EMC_PUSH_SETTING = "/v1/push-setting";
    public static String URL_EMC_ALARMS_LIST = "/v1/alarms-list";
    //emc_portal
    public static String URL_EMC_PORTAL_ALARMS_SURVEY = BASE_EMC_PORTAL_URL + "/alarms-survey";
    public static String URL_SYSTEM_MSG_LIST = BASE_EMC_PORTAL_URL + "/sysmsg-list";
    public static String URL_SYSTEM_MSG_STATUS = BASE_EMC_PORTAL_URL + "/sysmsg-status";
    //cam-config
    public static String URL_CAM_GW_GET_DEVICE_INFO = "/v1/fetch-infos";
    public static String URL_CAM_GW_SET_DEVICE_NAME = "/v1/alias-name";
    public static String URL_CAM_GW_GET_FIRST_FRAME = "/v1/snapshot";
    public static String URL_CAM_GW_MOVE_DETECTION = "/v1/move-monitor-config";
    public static String URL_CAM_GW_PLACEMENT = "/v1/orientation-config";
    public static String URL_CAM_GW_NIGHT_VERSION = "/v1/night-vision-config";
    public static String URL_CAM_GW_AUDIO_CONFIG = "/v1/audio-config";
    public static String URL_CAM_GW_LOCAL_STOR = "/v1/local-stor-config";
    public static String URL_CAM_GW_CLOUD_STOR = "/v1/cloud-stor-config";
    public static String URL_CAM_GW_TIME_ZONE = "/v1/time-config";
    public static String URL_CAM_GW_RESTART = "/v1/restart";
    public static String URL_CAM_GW_RESTORE = "/v1/reset";
    public static String URL_CAM_GW_REMOVE = "/v1/remove";
    public static String URL_CAM_GW_GET_UPLOAD_INFO = "/v1/upload-token";
    public static String URL_CAM_GW_UPLOAD_NOTIFY = "/v1/upload-notify";
    public static String URL_CAM_GW_GET_LIVE_SRC_TOKEN = "/v1/live-sec-token";
    public static String URL_CAM_GW_DELETE_ANGLE = "/v1/view-angle-removal";
    public static String URL_CAM_GW_TURN_TO_ANGLE = "/v1/view-angle-turn";
    public static String URL_CAM_GW_PTZ_CONTROL = "/v1/ptz-control";
    public static String URL_CAM_GW_UPDATE_CONFIRM = "/v1/upgrade-confirm";
    public static String URL_CAM_GW_CLOUD_PLAN = "/v1/cloud-stor-plan";
    public static String URL_CAM_GW_ALARMS_CALENDAR = "/v1/alarms-calendar";
    public static String URL_CAM_GW_STORAGE_FORMAT = "/v1/tfcard-format";
    public static String URL_CAM_GW_ONVIF_CONFIG = "/v1/time-config";
    public static String URL_CAM_GW_LOCAL_ACCOUNT = "/v1/local-account-config";

    //cap
    public static String URL_GET_DEVICE_CAPABILITY = BASE_CAP_URL + "/capability";
    //cloud storage
    public static String URL_CLOUD_GROUP_LIST = "/v1/group-list";
    public static String URL_CLOUD_CLOUD_CALENDAR = "/v1/group-calendar";
    public static String URL_CLOUD_REMOVE_GROUPS = "/v1/remove-groups";
    //cloud storage portal
    public static String URL_CLOUD_PLAN_LIST_PORTAL = BASE_STOR_PORTAL_URL + "/plan-list";
    public static String URL_CLOUD_ORDER_LIST_PORTAL = BASE_STOR_PORTAL_URL + "/order-list";
    public static String URL_CLOUD_ORDER_REL_DEVICE_PORTAL = BASE_STOR_PORTAL_URL + "/order-rel-device";
    public static String URL_CLOUD_ORDER_UNREL_DEVICE_PORTAL = BASE_STOR_PORTAL_URL + "/order-unrel-device";
    public static String URL_CLOUD_REQUEST_BUY_PORTAL = BASE_STOR_PORTAL_URL + "/order-reqnew";
    public static String URL_CLOUD_ORDER_CANCEL_PORTAL = BASE_STOR_PORTAL_URL + "/order-cancel";
    public static String URL_CLOUD_ORDER_REMOVE_PORTAL = BASE_STOR_PORTAL_URL + "/order-remove";
    public static String URL_CLOUD_TRANSACTION_LIST = BASE_STOR_PORTAL_URL + "/transaction-list";
    //v2
    public static String URL_UAC_GET_DEVICE_LIST_V2 = BASE_UAC_V2_URL + "/device-list";

    public static void setUrl(AppConfigBean bean) {
        isApply = true;
        ApiConstant.wanIp = bean.wanIp;
        ApiConstant.UAC_URL = StringUtil.isNullOrEmpty(bean.uacUrl) ? ApiConstant.UAC_URL : bean.uacUrl;
        ApiConstant.CAP_URL = StringUtil.isNullOrEmpty(bean.capUrl) ? ApiConstant.CAP_URL : bean.capUrl;
        ApiConstant.URL_AGREEMENT = StringUtil.isNullOrEmpty(bean.agreementUrl) ? ApiConstant.URL_AGREEMENT : bean.agreementUrl;
        ApiConstant.URL_PRIVACY = StringUtil.isNullOrEmpty(bean.privacyUrl) ? ApiConstant.URL_PRIVACY : bean.privacyUrl;
        ApiConstant.URL_CLOUD_AGREEMENT = StringUtil.isNullOrEmpty(bean.cloudStorAgreementUrl) ? ApiConstant.URL_CLOUD_AGREEMENT : bean.cloudStorAgreementUrl;
        ApiConstant.EMC_PORTAL_URL = StringUtil.isNullOrEmpty(bean.emcPortalUrl) ? ApiConstant.EMC_PORTAL_URL : bean.emcPortalUrl;
        ApiConstant.STOR_PORTAL_URL = StringUtil.isNullOrEmpty(bean.storePortalUrl) ? ApiConstant.STOR_PORTAL_URL : bean.storePortalUrl;
        applyUrls();
    }

    private static void applyUrls() {
        //v1
        BASE_UAC_URL = UAC_URL + "/v1/";
        BASE_CAP_URL = CAP_URL + "/v1";
        BASE_EMC_PORTAL_URL = EMC_PORTAL_URL + "/v1";
        BASE_STOR_PORTAL_URL = STOR_PORTAL_URL + "/v1";
        //v2
        BASE_UAC_V2_URL = UAC_URL + "/v2/";
        // uac账号相关
        URL_UAC_CHALLENGE = BASE_UAC_URL + "challenge";
        URL_UAC_SIGNIN = BASE_UAC_URL + "signin";
        URL_UAC_RESET_PASSWORD = BASE_UAC_URL + "reset-password";
        URL_UAC_SIGNUP = BASE_UAC_URL + "signup";
        URL_UAC_SIGNOUT = BASE_UAC_URL + "signout";
        URL_UAC_USER_ACTIVE = BASE_UAC_URL + "user-active";
        URL_UAC_CHANGE_PASSWORD = BASE_UAC_URL + "change-password";
        URL_UAC_REFRESH_TOKEN = BASE_UAC_URL + "refresh-token";
        URL_UAC_DELETE_ACCOUNT = BASE_UAC_URL + "logout";
        //uac设备相关
        URL_UAC_PREBIND = BASE_UAC_URL + "req-bind";
        URL_UAC_GET_BIND_STATUS = BASE_UAC_URL + "bind-status";
        URL_UAC_GET_BIND_STATUS_BY_TOKEN = BASE_UAC_URL + "check-bind";
        URL_UAC_GET_DEVICE_LIST = BASE_UAC_URL + "device-list";
        URL_UAC_SET_DEVICE_NAME_UAC = BASE_UAC_URL + "change-alias-name";
        URL_UAC_UNBIND = BASE_UAC_URL + "unbind";
        URL_UAC_SHARE = BASE_UAC_URL + "share-camera";
        URL_UAC_SHARE_DELETE = BASE_UAC_URL + "delete-share-entry";
        URL_UAC_SHARE_DELETE_ALL = BASE_UAC_URL + "delete-share-all";
        URL_UAC_GUEST_BIND = BASE_UAC_URL + "bind-invite-camera";
        URL_UAC_GUEST_UNBIND = BASE_UAC_URL + "unbind-invite-camera";
        //emc_portal
        URL_EMC_PORTAL_ALARMS_SURVEY = BASE_EMC_PORTAL_URL + "/alarms-survey";
        URL_SYSTEM_MSG_LIST = BASE_EMC_PORTAL_URL + "/sysmsg-list";
        URL_SYSTEM_MSG_STATUS = BASE_EMC_PORTAL_URL + "/sysmsg-status";
        //cap
        URL_GET_DEVICE_CAPABILITY = BASE_CAP_URL + "/capability";
        //cloud portal
        URL_CLOUD_PLAN_LIST_PORTAL = BASE_STOR_PORTAL_URL + "/plan-list";
        URL_CLOUD_ORDER_LIST_PORTAL = BASE_STOR_PORTAL_URL + "/order-list";
        URL_CLOUD_ORDER_REL_DEVICE_PORTAL = BASE_STOR_PORTAL_URL + "/order-rel-device";
        URL_CLOUD_ORDER_UNREL_DEVICE_PORTAL = BASE_STOR_PORTAL_URL + "/order-unrel-device";
        URL_CLOUD_REQUEST_BUY_PORTAL = BASE_STOR_PORTAL_URL + "/order-reqnew";
        URL_CLOUD_ORDER_CANCEL_PORTAL = BASE_STOR_PORTAL_URL + "/order-cancel";
        URL_CLOUD_ORDER_REMOVE_PORTAL = BASE_STOR_PORTAL_URL + "/order-remove";
        URL_CLOUD_TRANSACTION_LIST = BASE_STOR_PORTAL_URL + "/transaction-list";

        //v2接口
        URL_UAC_GET_DEVICE_LIST_V2 = BASE_UAC_V2_URL + "/device-list";
    }

    /**
     * 获取http请求body，统一修改入口
     */
    public static JsonObject getReqBody(JsonObject data, String deviceId) {
        try {
            //meta
            JsonObject metaJson = new JsonObject();
            metaJson.addProperty("locale", "en");
            metaJson.addProperty("localtz", "480");
            metaJson.addProperty("appVendorCode", appVendorCode);
            if (!StringUtil.isNullOrEmpty(deviceId)) {
                metaJson.addProperty("deviceId", deviceId);
            }

            //body
            JsonObject body = new JsonObject();
            body.add("meta", metaJson);
            body.add("data", data);
            return body;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
