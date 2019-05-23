package net.ajcloud.wansviewplusw.quad;

import net.ajcloud.wansviewplusw.support.utils.IPreferences;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class QuadListCache {

    private Preferences preferences = Preferences.userNodeForPackage(QuadListCache.class);

    private String currentAccount;

    private List<QuadData> groupList = new ArrayList<>();


    public static QuadListCache getInstance() {
        return QuadListDataHolder.instance;
    }

    private static class QuadListDataHolder {
        private static final QuadListCache instance = new QuadListCache();
    }

    public List<QuadData> getGroupList(String account) {
        if (StringUtil.isNullOrEmpty(account))
            return null;
        if (StringUtil.isNullOrEmpty(currentAccount) || !StringUtil.equals(currentAccount, account)) {
            //重新获取
            groupList.clear();
            //获取组信息
            String group = preferences.get(String.format(IPreferences.P_QUAD_LIST_, account), null);
            if (StringUtil.isNullOrEmpty(group))
                return null;
            String[] groupNames = group.split("_");
            if (groupNames.length == 0)
                return null;
            for (int i = 0; i < groupNames.length; i++) {
                QuadData data = new QuadData();

                String name = groupNames[i];
                data.groupName = name;

                String groupDetail = preferences.get(String.format(IPreferences.P_QUAD_DETAIL_, account, name), null);
                if (StringUtil.isNullOrEmpty(group)) {
                    groupList.add(data);
                    continue;
                }
                String[] groupDetails = groupDetail.split("-");
                if (groupDetails.length == 0) {
                    groupList.add(data);
                    continue;
                }
                for (int j = 0; j < groupDetails.length; j++) {
                    String detail = groupDetails[i];
                    int num = Integer.parseInt(detail.split("_")[0]);
                    String deviceId = detail.split("_")[1];
                    switch (num) {
                        case 1:
                            data.camera_one = deviceId;
                            break;
                        case 2:
                            data.camera_two = deviceId;
                            break;
                        case 3:
                            data.camera_three = deviceId;
                            break;
                        case 4:
                            data.camera_four = deviceId;
                            break;
                    }
                }

                groupList.add(data);
            }
        }
        currentAccount = account;
        return groupList;
    }

    public void addQuadData(QuadData quadData) {
        groupList.add(quadData);
        save();
    }

    public void deleteQuadData(String groupName) {
        int index = 0;
        for (int i = 0; i < groupList.size(); i++) {
            if (StringUtil.equals(groupList.get(i).groupName, groupName)) {
                index = i;
                break;
            }
        }
        groupList.remove(index);
        save();
    }

    public void editQuadData(QuadData quadData, String newGroupName) {
        for (int i = 0; i < groupList.size(); i++) {
            if (StringUtil.equals(groupList.get(i).groupName, quadData.groupName)) {
                groupList.get(i).groupName = newGroupName;
                groupList.get(i).camera_one = quadData.camera_one;
                groupList.get(i).camera_two = quadData.camera_two;
                groupList.get(i).camera_three = quadData.camera_three;
                groupList.get(i).camera_four = quadData.camera_four;
                break;
            }
        }
        save();
    }

    public QuadData getQuadData(String groupName) {
        for (int i = 0; i < groupList.size(); i++) {
            if (StringUtil.equals(groupList.get(i).groupName, groupName)) {
                return groupList.get(i);
            }
        }
        return null;
    }

    private void save() {
        StringBuilder quadList = new StringBuilder();
        for (int i = 0; i < groupList.size(); i++) {
            QuadData item = groupList.get(i);

            quadList.append(item.groupName);
            if (i != groupList.size() - 1)
                quadList.append("_");

            StringBuilder quadDetail = new StringBuilder();
            if (!StringUtil.isNullOrEmpty(item.camera_one)) {
                quadDetail.append("1_");
                quadDetail.append(item.camera_one);
                quadDetail.append("-");
            }
            if (!StringUtil.isNullOrEmpty(item.camera_two)) {
                quadDetail.append("2_");
                quadDetail.append(item.camera_two);
                quadDetail.append("-");
            }
            if (!StringUtil.isNullOrEmpty(item.camera_three)) {
                quadDetail.append("3_");
                quadDetail.append(item.camera_three);
                quadDetail.append("-");
            }
            if (!StringUtil.isNullOrEmpty(item.camera_four)) {
                quadDetail.append("4_");
                quadDetail.append(item.camera_four);
            }
            if (quadDetail.length() > 0 && quadDetail.toString().endsWith("-")) {
                quadDetail.deleteCharAt(quadDetail.length() - 1);
            }
            preferences.put(String.format(IPreferences.P_QUAD_DETAIL_, currentAccount, item.groupName), quadDetail.toString());
        }
        preferences.put(String.format(IPreferences.P_QUAD_LIST_, currentAccount), quadList.toString());
    }
}
