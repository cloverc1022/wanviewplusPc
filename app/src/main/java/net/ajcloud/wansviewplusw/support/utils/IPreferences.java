package net.ajcloud.wansviewplusw.support.utils;

public interface IPreferences {
    String P_SALT = "P_SALT";
    String P_REMEMBER_ACCOUNT = "P_REMEMBER_ACCOUNT";
    String P_LAST_ACCOUNT = "P_LAST_ACCOUNT";
    String P_LAST_ACCOUNT_PASSWORD = "P_LAST_ACCOUNT_PASSWORD";
    String P_FILE_LOCATION = "P_FILE_LOCATION";

    /**
     * account:  {groupList:[
     * {groupName:value,
     * camera_one:value,
     * camera_two:value,
     * camera_three:value,
     * camera_four:value},
     * {groupName:value,
     * *  camera_one:value,
     * *  camera_two:value,
     * *  camera_three:value,
     * *  camera_four:value}
     * ]}
     */
    String P_QUAD = "P_QUAD_DETAIL_%1$s";
}
