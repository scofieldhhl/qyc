package com.systemteam.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.systemteam.activity.PlayActivity;
import com.systemteam.util.LogTool;

import java.util.ArrayList;

import cn.bmob.push.PushConstants;

import static com.systemteam.activity.PlayActivity.KEY_PLAY_PATH_LIST;

public class MyMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            LogTool.d("bmob push："+intent.getStringExtra("msg"));
            //   0.  定义好视频的路径
//            Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/keepvid/M30B.mp4");
//
//            //  1.  先设定好Intent
//            Intent intentVideo = new Intent(Intent.ACTION_VIEW);
//
//            //  2.  设置好 Data：播放源，是一个URI
//            //      设置好 Data的Type：类型是 “video/mp4"
//            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
//            intent.setDataAndType(uri,"video/*");
//
//            //  3.  跳转：
//            context.startActivity(intent);
            Intent intentPlayer = new Intent(context, PlayActivity.class);
            intentPlayer.putExtra(PlayActivity.KEY_PATH_VIDEO, "");
            intentPlayer.putStringArrayListExtra(KEY_PLAY_PATH_LIST, new ArrayList<String>());
            context.startActivity(intentPlayer);

        }
    }
}
