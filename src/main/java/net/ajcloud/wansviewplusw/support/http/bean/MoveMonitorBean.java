package net.ajcloud.wansviewplusw.support.http.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * Created by mamengchao on 2018/06/06.
 * Function:
 */
public class MoveMonitorBean implements Serializable {
    public int enable;
    public String susceptiveness;
    public List<Policy> policies;

    public static class Policy implements Serializable {
        public String no;
        public int enable;
        public int respondMode;
        public String startTime;
        public String endTime;
        public List<Integer> weekDays;
    }

    public Object deepClone() {
        try {
            // 序列化
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeObject(this);

            // 反序列化
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);

            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
