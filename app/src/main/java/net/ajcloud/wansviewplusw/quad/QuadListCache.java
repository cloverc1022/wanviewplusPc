package net.ajcloud.wansviewplusw.quad;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.ajcloud.wansviewplusw.support.device.DeviceCache;
import net.ajcloud.wansviewplusw.support.http.bean.group.GroupDetail;
import net.ajcloud.wansviewplusw.support.http.bean.group.GroupList;
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
            currentAccount = account;
            //重新获取
            groupList.clear();

            GroupList groupListBean = JSONObject.parseObject(preferences.get(String.format(IPreferences.P_QUAD, account), null), GroupList.class);
            if (groupListBean == null || groupListBean.groupList == null || groupListBean.groupList.size() == 0) {
                return null;
            }
            for (GroupDetail groupDetail : groupListBean.groupList) {
                QuadBean quadBean = new QuadBean();
                quadBean.setGroupName(groupDetail.groupName);
                if (StringUtil.isNullOrEmpty(groupDetail.cameraOne)) {
                    quadBean.setCamera_one(groupDetail.cameraOne);
                } else {
                    if (DeviceCache.getInstance().get(groupDetail.cameraOne) == null) {
                        quadBean.setCamera_one(null);
                    } else {
                        quadBean.setCamera_one(groupDetail.cameraOne);
                    }
                }

                if (StringUtil.isNullOrEmpty(groupDetail.cameraTwo)) {
                    quadBean.setCamera_two(groupDetail.cameraTwo);
                } else {
                    if (DeviceCache.getInstance().get(groupDetail.cameraTwo) == null) {
                        quadBean.setCamera_two(null);
                    } else {
                        quadBean.setCamera_two(groupDetail.cameraTwo);
                    }
                }

                if (StringUtil.isNullOrEmpty(groupDetail.cameraThree)) {
                    quadBean.setCamera_three(groupDetail.cameraThree);
                } else {
                    if (DeviceCache.getInstance().get(groupDetail.cameraThree) == null) {
                        quadBean.setCamera_three(null);
                    } else {
                        quadBean.setCamera_three(groupDetail.cameraThree);
                    }
                }

                if (StringUtil.isNullOrEmpty(groupDetail.cameraFour)) {
                    quadBean.setCamera_four(groupDetail.cameraFour);
                } else {
                    if (DeviceCache.getInstance().get(groupDetail.cameraFour) == null) {
                        quadBean.setCamera_four(null);
                    } else {
                        quadBean.setCamera_four(groupDetail.cameraFour);
                    }
                }
                groupList.add(quadBean);
            }
        }
        save();
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
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (QuadBean quadBean : groupList) {
            JSONObject itemJson = new JSONObject();
            itemJson.put("groupName", quadBean.getGroupName());
            itemJson.put("cameraOne", quadBean.getCamera_one());
            itemJson.put("cameraTwo", quadBean.getCamera_two());
            itemJson.put("cameraThree", quadBean.getCamera_three());
            itemJson.put("cameraFour", quadBean.getCamera_four());
            jsonArray.add(itemJson);
        }
        jsonObject.put("groupList", jsonArray);
        preferences.put(String.format(IPreferences.P_QUAD, currentAccount), jsonObject.toString());
    }
}
