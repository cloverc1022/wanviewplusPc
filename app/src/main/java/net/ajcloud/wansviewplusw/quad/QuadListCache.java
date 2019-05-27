package net.ajcloud.wansviewplusw.quad;

import net.ajcloud.wansviewplusw.support.utils.IPreferences;
import net.ajcloud.wansviewplusw.support.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class QuadListCache {

    private Preferences preferences = Preferences.userNodeForPackage(QuadListCache.class);

    private String currentAccount;

    private List<QuadBean> groupList = new ArrayList<>();


    public static QuadListCache getInstance() {
        return QuadListDataHolder.instance;
    }

    private static class QuadListDataHolder {
        private static final QuadListCache instance = new QuadListCache();
    }

    public List<QuadBean> getGroupList(String account) {
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
                QuadBean data = new QuadBean();

                String name = groupNames[i];
                data.setGroupName(name);

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
                            data.setCamera_one(deviceId);
                            break;
                        case 2:
                            data.setCamera_two(deviceId);
                            break;
                        case 3:
                            data.setCamera_three(deviceId);
                            break;
                        case 4:
                            data.setCamera_four(deviceId);
                            break;
                    }
                }

                groupList.add(data);
            }
        }
        currentAccount = account;
        return groupList;
    }

    public void addQuadData(QuadBean quadBean) {
        groupList.add(quadBean);
        save();
    }

    public void deleteQuadData(String groupName) {
        int index = 0;
        for (int i = 0; i < groupList.size(); i++) {
            if (StringUtil.equals(groupList.get(i).getGroupName(), groupName)) {
                index = i;
                break;
            }
        }
        groupList.remove(index);
        save();
    }

    public void editQuadData(QuadBean quadBean, String newGroupName) {
        for (int i = 0; i < groupList.size(); i++) {
            if (StringUtil.equals(groupList.get(i).getGroupName(), quadBean.getGroupName())) {
                groupList.get(i).setGroupName(newGroupName);
                groupList.get(i).setCamera_one(quadBean.getCamera_one());
                groupList.get(i).setCamera_two(quadBean.getCamera_two());
                groupList.get(i).setCamera_three(quadBean.getCamera_three());
                groupList.get(i).setCamera_four(quadBean.getCamera_four());
                break;
            }
        }
        save();
    }

    public QuadBean getQuadData(String groupName) {
        for (int i = 0; i < groupList.size(); i++) {
            if (StringUtil.equals(groupList.get(i).getGroupName(), groupName)) {
                return groupList.get(i);
            }
        }
        return null;
    }

    private void save() {
        StringBuilder quadList = new StringBuilder();
        for (int i = 0; i < groupList.size(); i++) {
            QuadBean item = groupList.get(i);

            quadList.append(item.getGroupName());
            if (i != groupList.size() - 1)
                quadList.append("_");

            StringBuilder quadDetail = new StringBuilder();
            if (!StringUtil.isNullOrEmpty(item.getCamera_one())) {
                quadDetail.append("1_");
                quadDetail.append(item.getCamera_one());
                quadDetail.append("-");
            }
            if (!StringUtil.isNullOrEmpty(item.getCamera_two())) {
                quadDetail.append("2_");
                quadDetail.append(item.getCamera_two());
                quadDetail.append("-");
            }
            if (!StringUtil.isNullOrEmpty(item.getCamera_three())) {
                quadDetail.append("3_");
                quadDetail.append(item.getCamera_three());
                quadDetail.append("-");
            }
            if (!StringUtil.isNullOrEmpty(item.getCamera_four())) {
                quadDetail.append("4_");
                quadDetail.append(item.getCamera_four());
            }
            if (quadDetail.length() > 0 && quadDetail.toString().endsWith("-")) {
                quadDetail.deleteCharAt(quadDetail.length() - 1);
            }
            preferences.put(String.format(IPreferences.P_QUAD_DETAIL_, currentAccount, item.getGroupName()), quadDetail.toString());
        }
        preferences.put(String.format(IPreferences.P_QUAD_LIST_, currentAccount), quadList.toString());
    }
}
