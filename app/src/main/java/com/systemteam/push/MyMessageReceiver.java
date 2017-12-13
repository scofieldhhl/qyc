package com.systemteam.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.systemteam.util.LogTool;

import cn.bmob.push.PushConstants;

public class MyMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            LogTool.d("bmob push："+intent.getStringExtra("msg"));
        }
    }
}
