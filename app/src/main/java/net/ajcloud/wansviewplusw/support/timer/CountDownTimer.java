package net.ajcloud.wansviewplusw.support.timer;

import javafx.application.Platform;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class CountDownTimer {

    private Timer timer;
    //走时
    private int seconds = 0;
    private boolean isCounting = false;

    public void CountDown(long milliseconds, OnTimerListener onTimerListener) {
        isCounting = true;
        //开始时间
        long start = System.currentTimeMillis();
        //结束时间
        final long end = start + milliseconds;
        //延迟0毫秒（即立即执行）开始，每隔1000毫秒执行一次
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(() -> onTimerListener.onTick(++seconds));
            }
        }, 0, 1000);
        //计时结束时候，停止全部timer计时计划任务
        timer.schedule(new TimerTask() {
            public void run() {
                seconds = 0;
                timer.cancel();
                isCounting = false;
                Platform.runLater(onTimerListener::onFinish);
            }
        }, new Date(end));
    }

    public void cancel() {
        seconds = 0;
        if (timer != null) {
            isCounting = false;
            timer.cancel();
        }
    }

    public boolean isCounting() {
        return isCounting;
    }

    public interface OnTimerListener {
        void onTick(int second);

        void onFinish();
    }
}
