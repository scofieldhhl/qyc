package com.systemteam.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.bcgdv.asia.lib.ticktock.TickTockView;
import com.systemteam.BaseActivity;
import com.systemteam.R;

import java.util.Calendar;

public class ActiveActivity extends BaseActivity {
    TickTockView mCountDown, mCountUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active);
        initToolBar(this, R.string.bybike);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mCountDown = (TickTockView) findViewById(R.id.view_ticktock_countdown);
        if (mCountDown != null) {
            mCountDown.setOnTickListener(new TickTockView.OnTickListener() {
                @Override
                public String getText(long timeRemaining) {
                    int seconds = (int) (timeRemaining / 1000) % 60;
                    int minutes = (int) ((timeRemaining / (1000 * 60)) % 60);
                    int hours = (int) ((timeRemaining / (1000 * 60 * 60)) % 24);
                    int days = (int) (timeRemaining / (1000 * 60 * 60 * 24));
                    boolean hasDays = days > 0;
                    /*return String.format("%1$02d%4$s %2$02d%5$s %3$02d%6$s",
                            hasDays ? days : hours,
                            hasDays ? hours : minutes,
                            hasDays ? minutes : seconds,
                            hasDays ? "d" : "h",
                            hasDays ? "h" : "m",
                            hasDays ? "m" : "s");*/
                    return String.format("%1$02d%3$s %2$02d%4$s",
                            hasDays ? hours : minutes,
                            hasDays ? minutes : seconds,
                            hasDays ? " :" : " :",
                            hasDays ? "" : "");
                }
            });
        }

        /*if (mCountUp != null) {
            mCountUp.setOnTickListener(new TickTockView.OnTickListener() {
                SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
                Date date = new Date();
                @Override
                public String getText(long timeRemaining) {
                    date.setTime(System.currentTimeMillis());
                    return format.format(date);
                }
            });
        }*/
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Calendar end = Calendar.getInstance();
                end.add(Calendar.MINUTE, 3);
                end.add(Calendar.SECOND, 0);

                Calendar start = Calendar.getInstance();
                start.add(Calendar.MINUTE, -1);
                if (mCountDown != null) {
                    mCountDown.start(start, end);
                }
            }
        }, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCountDown.stop();
//        mCountUp.stop();
    }
}
