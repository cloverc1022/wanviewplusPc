package net.ajcloud.wansviewplusw.support.device;

import java.util.HashMap;

/**
 * Created by mamengchao on 2018/06/04.
 * Function: 设备信息字典
 */
public class DeviceInfoDictionary {

    private static final HashMap<String, DeviceInfoDataBean> deviceDataMap = new HashMap<>();

    /**
     * type         设备的type
     * typeID       自增
     */
    private static final Object[][] deviceInfoData = new Object[][]{
            // type		typeId     iconRes
//            {"K3C", 1, R.mipmap.ic_model_k3},
//            {"K3S", 2, R.mipmap.ic_model_k3},
//            // q3
//            {"Q3C", 3, R.mipmap.ic_model_q3},
//            // q3
//            {"Q3S", 4, R.mipmap.ic_model_q3},
            // w2
//            {"W02", 3, R.string.device_default_name_w2, R.mipmap.ic_model_w2}
    };

    static {
        for (int i = 0; i < deviceInfoData.length; i++) {
            deviceDataMap.put((String) deviceInfoData[i][0], new DeviceInfoDataBean(deviceInfoData[i]));
        }
    }

    /**
     * 设备type到图标资源映射
     *
     * @param type
     * @return
     */
    public static int getIconByType(String type) {
//        if (type == null) {
//            return R.mipmap.ic_launcher;
//        }
//
//        DeviceInfoDataBean bean = deviceDataMap.get(type);
//        if (bean == null) {
//            return R.mipmap.ic_launcher;
//        }
//        return bean.iconRes;
        return 0;
    }

    public static String getNameByDevice(Camera camera) {
//        if (camera == null) {
//            return MainApplication.getApplication().getResources().getString(R.string.device_unknow);
//        } else {
//            String deviceName = camera.aliasName;
//            if (deviceName == null) {
//                deviceName = camera.deviceId;
//            }
//            return deviceName;
//        }
        return null;
    }

    public static String getNameById(String deviceId) {
//        if (StringUtil.isNullOrEmpty(deviceId)) {
//            return MainApplication.getApplication().getResources().getString(R.string.device_unknow);
//        } else {
//            Camera camera = MainApplication.getApplication().getDeviceCache().get(deviceId);
//            return getNameByDevice(camera);
//        }
        return null;
    }

    public static HashMap<String, DeviceInfoDataBean> getDeviceDataMap() {
        return deviceDataMap;
    }

    /**
     * 是否支持该设备
     */
    public static boolean supportMe(String type) {
        return deviceDataMap.containsKey(type);
    }

    public static class DeviceInfoDataBean {
        public String type;
        public int typeId;
        public int iconRes;

        public DeviceInfoDataBean(Object[] data) {
            this.type = (String) data[0];
            this.typeId = (int) data[1];
            this.iconRes = (int) data[2];
        }
    }
}
