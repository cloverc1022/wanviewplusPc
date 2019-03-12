package net.ajcloud.wansviewplusw.support.timer;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class CountDownTimer {

    private Timer timer;
    //走时
    int seconds = 0;

    public void CountDown(long milliseconds, OnTimerListener onTimerListener) {
        //开始时间
        long start = System.currentTimeMillis();
        //结束时间
        final long end = start + milliseconds;
        //延迟0毫秒（即立即执行）开始，每隔1000毫秒执行一次
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                onTimerListener.onTick(++seconds);
            }
        }, 0, 1000);
        //计时结束时候，停止全部timer计时计划任务
        timer.schedule(new TimerTask() {
            public void run() {
                seconds = 0;
                onTimerListener.onFinish();
                timer.cancel();
            }

        }, new Date(end));
    }

    public void cancel() {
        seconds = 0;
        if (timer != null) {
            timer.cancel();
        }
    }

    public interface OnTimerListener {
        void onTick(int second);

        void onFinish();
    }
}
