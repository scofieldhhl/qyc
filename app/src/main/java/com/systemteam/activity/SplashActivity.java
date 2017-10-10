package com.systemteam.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.systemteam.BaseActivity;
import com.systemteam.BikeApplication;
import com.systemteam.Main2Activity;
import com.systemteam.R;
import com.systemteam.util.LogTool;
import com.systemteam.welcome.WelcomeActivity;

public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogTool.d("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        checkSDK();
        mUser = ((BikeApplication) this.getApplication()).getmUser();
        if(mUser == null){
//            startActivity(new Intent(SplashActivity.this, MyWelcomeActivity.class));
            startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            finish();
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, Main2Activity.class));
                }
            }, 4000);
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        LogTool.d("Permissions --> " + "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        LogTool.d("Permissions --> " + "Permission Denied: " + permissions[i]);
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    public void onClick(View v) {

    }
}
