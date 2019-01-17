package net.ajcloud.wansviewplusw.support.http.bean.cloudStoragePlan;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mamengchao on 2018/07/20.
 * Function:云存储套餐(PLAN)
 */
public class Plan implements Serializable {
    public String name;
    public String description;
    public List<RefBean> refs;
}
