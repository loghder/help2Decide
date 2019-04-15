package com.hebut.help2decide.LoginUI;


import android.os.CountDownTimer;
import android.widget.Button;

/**
 * Created by cc on 2019/3/30 0030.
 */
public class TimeCounter extends CountDownTimer {
    Button butt;
    public TimeCounter(long millisInFuture, long countDownInterval, Button butt) {
        super(millisInFuture, countDownInterval);
        this.butt = butt;
    }
    @Override
    public void onFinish() {
        butt.setText("获取验证码");
        butt.setEnabled(true);
    }
    @Override
    public void onTick(long millisUntilFinished) {
        butt.setText("请" + millisUntilFinished / 1000 + "秒后重新发送");
        butt.setEnabled(false);
    }
}