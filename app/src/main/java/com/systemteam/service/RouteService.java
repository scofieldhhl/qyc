package com.systemteam.service;

/**
 * Created by gaolei on 17/2/4.
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.gson.Gson;
import com.systemteam.BikeApplication;
import com.systemteam.Main2Activity;
import com.systemteam.R;
import com.systemteam.activity.RouteDetailActivity;
import com.systemteam.bean.Car;
import com.systemteam.bean.EventMessage;
import com.systemteam.bean.MyUser;
import com.systemteam.bean.RoutePoint;
import com.systemteam.bean.UseRecord;
import com.systemteam.util.Constant;
import com.systemteam.util.LogTool;
import com.systemteam.util.Utils;
import com.systemteam.welcome.WelcomeActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Locale;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.systemteam.util.Constant.ACTION_BROADCAST_ACTIVE;
import static com.systemteam.util.Constant.BUNDLE_CAR;
import static com.systemteam.util.Constant.BUNDLE_KEY_CODE;
import static com.systemteam.util.Constant.COST_BASE_DEFAULT;
import static com.systemteam.util.Constant.EARN_RATE_DEFAULT;
import static com.systemteam.util.Constant.FORMAT_TIME;
import static com.systemteam.util.Constant.TIME_ONCE_ACTIVE;


//        当前位置:我的异常网» Android » Android使用百度LBS SDK（4）记录和显示行走轨迹
//        Android使用百度LBS SDK（4）记录和显示行走轨迹
//        www.MyException.Cn 网友分享于：2015-04-01浏览：0次
//        Android使用百度LBS SDK（四）记录和显示行走轨迹
//        记录轨迹思路
//        用Service获取经纬度，onCreate中开始采集经纬度点，保存到ArrayList
//        每隔5秒取样一次，若经纬度未发生变化，丢弃该次取样
//        在onDestroy中，将ArrayList转成JSON格式，然后存储到SDCard中
//        显示轨迹思路
//        读取目录下所有轨迹文件，并生成ListView
//        在OnItemClick中将文件名称通过intent.putExtra传递给显示轨迹的Activity
//        根据文件名将对应的JSON内容转成ArrayList
//        然后将以上ArrayList的点集依次连线，并绘制到百度地图上
//        设置起始点Marker，Zoom级别,中心点为起始点
//        轨迹点小于2个无法绘制轨迹，给出提示
//        初步Demo效果图，获取的经纬度有偏移，明天看看哪里的问题：
//        LBS
//        先贴一个保存经纬度点的Service的核心代码：
// TODO 将扣费顺序改为可配置
public class RouteService extends Service {

    private double currentLatitude, currentLongitude;

    private String rt_time, rt_distance, rt_price;
    //定位图层显示方式
    public ArrayList<RoutePoint> routPointList = new ArrayList<>();
    public  int totalDistance = 0;
    private float totalPrice = COST_BASE_DEFAULT;
    public  long beginTime = 0, totalTime = 0;
    Notification notification;
    RemoteViews contentView;
    private boolean isBikeUsing = false;
    private String mCarNo = "";
    private Car mCar;
    private String mTime;
    private MyUser mUser;
    private boolean isUseFree = false;

    public void onCreate() {
        super.onCreate();
        beginTime = System.currentTimeMillis();
        totalTime = 0;
        totalDistance = 0;
        totalPrice = COST_BASE_DEFAULT;
        routPointList.clear();

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        LogTool.d("RouteService--------onStartCommand---------------");
        if(intent != null){
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                mCar = (Car) bundle.get(BUNDLE_CAR);
                if(mCar != null){
                    mCarNo = mCar.getCarNo();
                }else {
                    Toast.makeText(this, R.string.error_car_no, Toast.LENGTH_SHORT).show();
                    return super.onStartCommand(intent, flags, startId);
                }
            }else {
                Toast.makeText(this, R.string.error_car_no, Toast.LENGTH_SHORT).show();
                return super.onStartCommand(intent, flags, startId);
            }
        }
        mUser = BmobUser.getCurrentUser(MyUser.class);
        if(mUser == null){
            Toast.makeText(this, R.string.user_no, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, WelcomeActivity.class));
            return super.onStartCommand(intent, flags, startId);
        }
//        initLocation();//初始化LocationgClient
        initNotification();
        //发送开锁指令，开锁成功后开始计时，扫码开锁时即发送开锁指令
        initCountDownTimer();
        Utils.acquireWakeLock(this);
        // 开启轨迹记录线程
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void initNotification() {
        int icon = R.mipmap.bike_icon2;
        contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
        notification = new NotificationCompat.Builder(this).setContent(contentView).setSmallIcon(icon).build();
        Intent notificationIntent = new Intent(this, Main2Activity.class);
        notificationIntent.putExtra("flag", "notification");
        notification.contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
    }

    private void startNotifi(String time, String distance, String price) {
        isBikeUsing = true;
        startForeground(1, notification);
        contentView.setTextViewText(R.id.bike_time, time);
        contentView.setTextViewText(R.id.bike_distance, distance);
        contentView.setTextViewText(R.id.bike_price, price);
        rt_time=time;
        rt_distance=distance;
        rt_price=price;
    }


    public IBinder onBind(Intent intent) {
        LogTool.d("onBind-------------");
        return null;
    }

    public boolean onUnBind(Intent intent) {
        LogTool.d("onBind-------------");
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        releaseCountDownTimer();
//        mlocationClient.stop();
//        myOrientationListener.stop();
        LogTool.d("RouteService----0nDestroy---------------");
        Utils.releaseWakeLock();
        mTime = "";
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    // 发送设备停止指令,设备停止运作由硬件控制
    private void doGameOver(boolean isFree){
        LogTool.d("doGameOver");
        Gson gson = new Gson();
        String routeListStr = gson.toJson(routPointList);
        LogTool.d("RouteService----routeListStr-------------" + routeListStr);
        stopForeground(true);
        isBikeUsing = false;
        if(!isFree){
//            requestBalance();
            if(mTime.trim().equalsIgnoreCase(Utils.formatTime(Constant.TIME_ONCE_ACTIVE - 1000)) ||
                    mTime.trim().equalsIgnoreCase(Utils.formatTime(Constant.TIME_ONCE_ACTIVE - 2000))){
                mTime = Utils.formatTime(Constant.TIME_ONCE_ACTIVE);
            }
            Bundle bundle = new Bundle();
            bundle.putString("totalTime", mTime);
            bundle.putString("totalDistance", mCarNo + "");
            bundle.putString("totalPrice", totalPrice + "");
            bundle.putString("routePoints", routeListStr);
            bundle.putString(BUNDLE_KEY_CODE, mCarNo);
            Intent intent = new Intent(this, RouteDetailActivity.class);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            insertData(routeListStr);

        }else {

        }
        stopSelf();
    }


    public static class NetWorkReceiver extends BroadcastReceiver{
        public NetWorkReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo.State wifiState = null;
            NetworkInfo.State mobileState = null;
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            if (wifiState != null && mobileState != null
                    && NetworkInfo.State.CONNECTED != wifiState
                    && NetworkInfo.State.CONNECTED == mobileState) {
//                Toast.makeText(context, context.getString(R.string.net_mobile), Toast.LENGTH_SHORT).show();
                // 手机网络连接成功
            } else if (wifiState != null && mobileState != null
                    && NetworkInfo.State.CONNECTED != wifiState
                    && NetworkInfo.State.CONNECTED != mobileState) {
//                Toast.makeText(context, context.getString(R.string.net_none), Toast.LENGTH_SHORT).show();

                // 手机没有任何的网络
            } else if (wifiState != null && NetworkInfo.State.CONNECTED == wifiState) {
                // 无线网络连接成功
//                Toast.makeText(context, context.getString(R.string.net_wifi), Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void insertData(String routeListStr) {
        LogTool.d("insertData");
        newUseRecord(mTime);
    }

    private void initCountDownTimer(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isBikeUsing){
                    countDownTimer.start();
                    requestBalance();
                }
            }
        }, 0);
    }

    private CountDownTimer countDownTimer = new CountDownTimer(TIME_ONCE_ACTIVE, 1000) {
        String timeLeft;
        private Handler handler = new Handler();
        Runnable taskBroad = new Runnable() {
            @Override
            public void run() {
                sendBroadcast(timeLeft);
            }
        };
        @Override
        public void onTick(long millisUntilFinished) {
            long min = millisUntilFinished / 60000;
            long secode = (millisUntilFinished / 1000) % 60;
            timeLeft = String.format(Locale.US, FORMAT_TIME, min,
                    secode < 10 ? "0" + secode : String.valueOf(secode));
            startNotifi(timeLeft, mCarNo,
                    getString(R.string.cost_num, String.valueOf(totalPrice)));
            handler.postDelayed(taskBroad, 1000);

            long millUse = TIME_ONCE_ACTIVE - millisUntilFinished;
            secode = (millUse / 1000) % 60;
            mTime = String.format(Locale.US, FORMAT_TIME, millUse / 60000,
                    secode < 10 ? "0" + secode : String.valueOf(secode));
        }

        @Override
        public void onFinish() {
            LogTool.d("onFinish");
            handler.removeCallbacks(taskBroad);
            sendBroadcast(getString(R.string.time_end));
            doGameOver(isUseFree);
        }
    };

    private void sendBroadcast(String time){
        Intent intent = new Intent(ACTION_BROADCAST_ACTIVE);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        Bundle bundle = new Bundle();
        bundle.putString("totalTime", time);
        bundle.putString("totalDistance", mCarNo);
        bundle.putString("totalPrice", getString(R.string.cost_num, String.valueOf(totalPrice)));
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    private void releaseCountDownTimer(){
        if(countDownTimer != null){
            countDownTimer.onFinish();
            countDownTimer.cancel();
        }
    }

    private CompositeSubscription mCompositeSubscription;

    /**
     * 解决Subscription内存泄露问题
     * @param s
     */
    protected void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    private void requestBalance(){
        //1.扣费
        mUser = BmobUser.getCurrentUser(MyUser.class);
        if(mUser == null){
            LogTool.e("mUser == null");
//            stopSelf();
            return;
        }
        MyUser newUser = new MyUser();
        // 增加优惠券一天最多使用3张
        if(mUser.getCoupon() != null && mUser.getCoupon().intValue() > 0){
            totalPrice = 0;
            newUser.setCoupon(mUser.getCoupon().intValue() - 1);
        }else {
            totalPrice = COST_BASE_DEFAULT;
            newUser.setBalance(mUser.getBalance().floatValue() - totalPrice);
        }
        addSubscription(newUser.update(mUser.getObjectId(),new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                }else{
                    // 错误码：206， 502
                    LogTool.e("错误码："+(e).getErrorCode()+",错误描述："+(e).getMessage());
                }
                //2.修改car收益
                if(mCar == null){
                    // 360
                    LogTool.e("mCar == null");
//                    stopSelf();
                    return;
                }
                mEarn = totalPrice * EARN_RATE_DEFAULT;
                Car newCar = new Car();
                newCar.setIncome((mCar.getIncome() == null ? 0f : mCar.getIncome()) + totalPrice);
                newCar.setEarn((mCar.getEarn() == null ? 0f : mCar.getEarn()) + mEarn);
                newCar.setPosition(BikeApplication.mCurrentPosition);// 同步更新设备定位信息
                addSubscription(newCar.update(mCar.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
//                        stopSelf();
                        if(e==null){
                        }else{
                            if(e instanceof BmobException){
                                LogTool.e("错误码："+((BmobException)e).getErrorCode()+",错误描述："+((BmobException)e).getMessage());
                            }else{
                                LogTool.e("错误描述："+e.getMessage());
                            }
                        }
                    }
                }));
            }
        }));
    }

    float mEarn = COST_BASE_DEFAULT * EARN_RATE_DEFAULT;
    private void newUseRecord(String timeUse){
        //2.增加使用记录
        UseRecord record = new UseRecord();
        record.setAuthor(mUser);
        record.setCar(mCar);
        record.setCarNo(mCarNo);
        record.setCost(totalPrice);
        record.setEarn(mEarn);
        record.setEarnRate(EARN_RATE_DEFAULT);
        record.setTimeUse(timeUse);
        addSubscription(record.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {

            }
        }));
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void ActionMessage(EventMessage msg){
        switch (msg.getAction()){
            case EventMessage.ACTION_GAMEOVER:
                LogTool.d("EventMessage.ACTION_GAMEOVER");
                isUseFree = msg.getIsFree();
                releaseCountDownTimer();
                break;
        }
    }
}
