package com.systemteam;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.getui.gis.sdk.GInsightManager;
import com.systemteam.bean.MyUser;
import com.systemteam.database.db.DbCore;
import com.systemteam.service.GInsightEventListener;
import com.systemteam.util.LogTool;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

import static com.systemteam.util.Constant.WX_APP_ID;

public class BikeApplication extends Application {

    public static String mSDCardPath;
    public static String APP_FOLDER_NAME;
    public static String mCurrentAddress;//当前定位地址
    public static BmobGeoPoint mCurrentPosition; //当前定位经纬度信息
    public static boolean isHaveUpdate;
    private MyUser mUser;
    private static BikeApplication myApplication;
    public static BikeApplication getInstance() {
        return myApplication;
    }
    public MyUser getmUser() {
        if(mUser == null){
            mUser = BmobUser.getCurrentUser(MyUser.class);
        }
        return mUser;
    }
    public static void setPosition(double longitude, double latitude){
        if(mCurrentPosition == null){
            mCurrentPosition = new BmobGeoPoint(longitude, latitude);
        }else {
            mCurrentPosition.setLongitude(longitude);
            mCurrentPosition.setLatitude(latitude);
        }
    }

    public void setmUser(MyUser mUser) {
        this.mUser = mUser;
    }
    private List<GInsightEventListener> gInsightListeners;
    public void onCreate() {
        super.onCreate();
        myApplication = this;

        initDirs();
        initBmob();
        regToWx();
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        DbCore.init(this);

        if (handler == null) {
            handler = new DemoHandler();
        }

        //个像
        GInsightManager.getInstance().init (getApplicationContext(), "nwGatlIRCj5lxvecl181u7");
        gInsightListeners = new ArrayList<>();
    }

    private boolean initDirs() {
        mSDCardPath = Environment.getExternalStorageDirectory().toString();
        if (mSDCardPath == null) {
            return false;
        }
        APP_FOLDER_NAME = getString(R.string.app_name);
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void initBmob(){
        //		//第一：设置BmobConfig，允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)
		BmobConfig config = new BmobConfig.Builder(this)
		//设置appkey
		.setApplicationId("fe66bafa3d7118e88693e01a7ef41b60")
		//请求超时时间（单位为秒）：默认15s
		.setConnectTimeout(30)
		//文件分片上传时每片的大小（单位字节），默认512*1024
		.setUploadBlockSize(1024*1024)
		//文件的过期时间(单位为秒)：默认1800s
		.setFileExpiration(5500)
		.build();
		Bmob.initialize(config);
    }

    public static IWXAPI mWxApi;
    private void regToWx(){
        mWxApi = WXAPIFactory.createWXAPI(this, WX_APP_ID, true);
        mWxApi.registerApp(WX_APP_ID);
    }

    //个推
    private static DemoHandler handler;

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder payloadData = new StringBuilder();
    public static void sendMessage(Message msg) {
        handler.sendMessage(msg);
    }

    public static class DemoHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    /*if (demoActivity != null) {
                        payloadData.append((String) msg.obj);
                        payloadData.append("\n");
                        if (GetuiSdkDemoActivity.tLogView != null) {
                            GetuiSdkDemoActivity.tLogView.append(msg.obj + "\n");
                        }
                    }*/
                    LogTool.e("000:" + ((String) msg.obj));
                    break;

                case 1:
                    /*if (demoActivity != null) {
                        if (GetuiSdkDemoActivity.tLogView != null) {
                            GetuiSdkDemoActivity.tView.setText((String) msg.obj);
                        }
                    }*/
                    LogTool.e("1111:" + ((String) msg.obj));
                    break;
            }
        }
    }

    public void registerGInsightListener(GInsightEventListener listener) {
        gInsightListeners.add(listener);
    }

    public void unregisterGInsightListener(GInsightEventListener listener) {
        gInsightListeners.remove(listener);
    }

    public String getGiuid() {
        SharedPreferences sp = getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
        return sp.getString("giuid", null);
    }

    public void setGiuid(String giuid) {
        SharedPreferences sp = getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
        sp.edit().putString("giuid", giuid).apply();

        for (GInsightEventListener l : gInsightListeners) {
            l.onGiuid(giuid);
        }
    }
}
