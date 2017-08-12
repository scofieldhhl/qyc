package com.systemteam.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.service.RouteService;
import com.systemteam.util.Utils;

import java.lang.ref.WeakReference;

import static com.systemteam.util.Constant.ACTION_BROADCAST_ACTIVE;
import static com.systemteam.util.Constant.BUNDLE_KEY_CODE;
import static com.systemteam.util.Constant.MSG_UPDATE_UI;
import static com.systemteam.util.Constant.TIME_ONCE_ACTIVE_STR;

public class ActiveActivity extends BaseActivity {
    private String mTime = TIME_ONCE_ACTIVE_STR;
    private LocationReceiver mReceiver;
    private TextView mTvTick;
    private boolean isFinished = false;
    private static class MyHandler extends Handler {
        private WeakReference<ActiveActivity> mActivity;

        public MyHandler(ActiveActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final ActiveActivity theActivity = mActivity.get();
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_UPDATE_UI:
                    if(theActivity.isFinished){
                        theActivity.mTvTick.setText(theActivity.getString(R.string.play_onemore));
                    }else {
                        theActivity.mTvTick.setText((String)msg.obj);
                    }
                    break;
            }
        }
    }

    private MyHandler mHandler = new MyHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active);
        mContext = this;
        initToolBar(this, R.string.bybike);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTvTick = (TextView) findViewById(R.id.view_ticktock_countdown);
    }

    @Override
    protected void initData() {
        mReceiver = new LocationReceiver();
        registerBroadcast(ACTION_BROADCAST_ACTIVE, mReceiver);
        Intent serviceIntent = new Intent(this, RouteService.class);
        serviceIntent.putExtra(BUNDLE_KEY_CODE, getIntent().getStringExtra(BUNDLE_KEY_CODE));
        startService(serviceIntent);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast(mReceiver);
    }

    public class LocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Utils.isTopActivity(context)) {
                mTime = intent.getStringExtra("totalTime");
                Message msg = mHandler.obtainMessage(MSG_UPDATE_UI);
                msg.obj = mTime;
                msg.sendToTarget();
                if(getString(R.string.time_end).equalsIgnoreCase(mTime)){
                    isFinished = true;
                    mHandler.sendEmptyMessageDelayed(MSG_UPDATE_UI, 3*1000);
                }
            }
        }
    }

    public void doDone(View view){
        if(!isFinished){
            toastDialog();
        }else {
            startService(new Intent(this, RouteService.class));
            isFinished = false;
        }
    }

    protected void toastDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.exist);
        builder.setTitle(R.string.tip);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(ActiveActivity.this, RouteService.class);
                stopService(intent);
                isFinished = true;
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_UI, 3*1000);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
