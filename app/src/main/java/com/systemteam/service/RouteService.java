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
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.Gson;
import com.systemteam.MainActivity;
import com.systemteam.R;
import com.systemteam.activity.RouteDetailActivity;
import com.systemteam.bean.Car;
import com.systemteam.bean.MyUser;
import com.systemteam.bean.RoutePoint;
import com.systemteam.bean.RouteRecord;
import com.systemteam.bean.UseRecord;
import com.systemteam.callback.AllInterface;
import com.systemteam.database.db.DBManager;
import com.systemteam.map.MyOrientationListener;
import com.systemteam.util.LogTool;
import com.systemteam.util.Utils;
import com.systemteam.welcome.WelcomeActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.systemteam.BaseActivity.loge;
import static com.systemteam.util.Constant.ACTION_BROADCAST_ACTIVE;
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


public class RouteService extends Service {

    private double currentLatitude, currentLongitude;

    private LocationClient mlocationClient = null;
    private MylocationListener mlistener;
    private BitmapDescriptor mIconLocation;
    private MyOrientationListener myOrientationListener;
    private String rt_time, rt_distance, rt_price;
    //定位图层显示方式
    private MyLocationConfiguration.LocationMode locationMode;
    AllInterface.IUpdateLocation iUpdateLocation;
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

    public void setiUpdateLocation(AllInterface.IUpdateLocation iUpdateLocation) {
        this.iUpdateLocation = iUpdateLocation;
    }

    public void onCreate() {
        super.onCreate();
        beginTime = System.currentTimeMillis();
//        RouteDBHelper dbHelper = new RouteDBHelper(this);
//        // 只有调用了DatabaseHelper的getWritableDatabase()方法或者getReadableDatabase()方法之后，才会创建或打开一个连接
//        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        totalTime = 0;
        totalDistance = 0;
        totalPrice = COST_BASE_DEFAULT;
        routPointList.clear();

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        LogTool.d("RouteService--------onStartCommand---------------");
        if(intent != null){
            String key = intent.getStringExtra(BUNDLE_KEY_CODE);
            if(key != null && !TextUtils.isEmpty(key)){
                mCarNo = key;
                checkCarExist(key);
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
        initCountDownTimer();
        Utils.acquireWakeLock(this);
        // 开启轨迹记录线程
        return super.onStartCommand(intent, flags, startId);
    }

    private void initNotification() {
        int icon = R.mipmap.bike_icon2;
        contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
        notification = new NotificationCompat.Builder(this).setContent(contentView).setSmallIcon(icon).build();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("flag", "notification");
        notification.contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
    }

    private void initLocation() {
        mIconLocation = BitmapDescriptorFactory
                .fromResource(R.mipmap.location_marker);
        locationMode = MyLocationConfiguration.LocationMode.NORMAL;

        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        mlocationClient = new LocationClient(this);
        mlistener = new MylocationListener();
//        initMarkerClickEvent();
        //注册监听器
        mlocationClient.registerLocationListener(mlistener);
        //配置定位SDK各配置参数，比如定位模式、定位时间间隔、坐标系类型等
        LocationClientOption mOption = new LocationClientOption();
        //设置坐标类型
        mOption.setCoorType("bd09ll");
        //设置是否需要地址信息，默认为无地址
        mOption.setIsNeedAddress(true);
        //设置是否打开gps进行定位
        mOption.setOpenGps(true);
        //设置扫描间隔，单位是毫秒 当<1000(1s)时，定时定位无效
        int span = 10000;
        mOption.setScanSpan(span);
        //设置 LocationClientOption
        mlocationClient.setLocOption(mOption);

        //初始化图标,BitmapDescriptorFactory是bitmap 描述信息工厂类.
        mIconLocation = BitmapDescriptorFactory
                .fromResource(R.mipmap.location_marker);

        myOrientationListener = new MyOrientationListener(this);
        //通过接口回调来实现实时方向的改变
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
            }
        });
//        mSearch = RoutePlanSearch.newInstance();
//        mSearch.setOnGetRoutePlanResultListener(this);
//        //开启定位
//        mBaiduMap.setMyLocationEnabled(true);
        if (!mlocationClient.isStarted()) {
            mlocationClient.start();
        }
        myOrientationListener.start();
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
        releaseCountDownTimer();
//        mlocationClient.stop();
//        myOrientationListener.stop();
        LogTool.d("RouteService----0nDestroy---------------");
        Gson gson = new Gson();
        String routeListStr = gson.toJson(routPointList);
        LogTool.d("RouteService----routeListStr-------------" + routeListStr);
        if(mTime.equalsIgnoreCase("02 : 58") || mTime.equalsIgnoreCase("02 : 59")){
            mTime = getString(R.string.time_start);
        }
        Bundle bundle = new Bundle();
//        bundle.putString("totalTime", totalTime + "");
        bundle.putString("totalTime", mTime);
        bundle.putString("totalDistance", mCarNo + "");
        bundle.putString("totalPrice", totalPrice + "");
        bundle.putString("routePoints", routeListStr);
        Intent intent = new Intent(this, RouteDetailActivity.class);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        /*if (routPointList.size() > 2)
            insertData(routeListStr);*/
        insertData(routeListStr);
        Utils.releaseWakeLock();
        stopForeground(true);
        isBikeUsing = false;
        mTime = "";
    }


    //所有的定位信息都通过接口回调来实现
    public class MylocationListener implements BDLocationListener {
        //定位请求回调接口
        private boolean isFirstIn = true;

        //定位请求回调函数,这里面会得到定位信息
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (null == bdLocation) return;
            //"4.9E-324"表示目前所处的环境（室内或者是网络状况不佳）造成无法获取到经纬度
            if ("4.9E-324".equals(String.valueOf(bdLocation.getLatitude())) ||
                    "4.9E-324".equals(String.valueOf(bdLocation.getLongitude()))) {
                return;
            }//过滤百度定位失败

            LogTool.d("RouteService---------getAddrStr()-------------" + bdLocation.getAddrStr());
            double routeLat = bdLocation.getLatitude();
            double routeLng = bdLocation.getLongitude();
            RoutePoint routePoint = new RoutePoint();
            routePoint.setRouteLat(routeLat);
            routePoint.setRouteLng(routeLng);
            if (routPointList.size() == 0)
                routPointList.add(routePoint);
            else {
                RoutePoint lastPoint = routPointList.get(routPointList.size() - 1);

                if (routeLat == lastPoint.getRouteLat() && routeLng == lastPoint.getRouteLng()) {

                } else {

                    LatLng lastLatLng = new LatLng(lastPoint.getRouteLat(),
                            lastPoint.getRouteLng());
                    LatLng currentLatLng = new LatLng(routeLat, routeLng);
                    if (routeLat > 0 && routeLng > 0) {
                        double distantce = DistanceUtil.getDistance(lastLatLng, currentLatLng);
//                        LogTool.d("distantce--------------" + distantce);
                        if (distantce > 5) {
                            routPointList.add(routePoint);
                            totalDistance += distantce;
                        }
                    }
                }
            }

            totalTime = (int) (System.currentTimeMillis() - beginTime) / 1000 / 60;
//            totalPrice = (float) (Math.floor(totalTime / 30) * COST_BASE_DEFAULT + COST_BASE_DEFAULT);
            startNotifi(getString(R.string.cost_time, String.valueOf(totalTime)),
                    getString(R.string.cost_distance, String.valueOf(totalDistance)),
                    getString(R.string.cost_num, String.valueOf(totalPrice)));
            Intent intent = new Intent("com.locationreceiver");
            Bundle bundle = new Bundle();
            bundle.putString("totalTime", getString(R.string.cost_time, String.valueOf(totalTime)));
            bundle.putString("totalDistance", getString(R.string.cost_distance, String.valueOf(totalDistance)));
            bundle.putString("totalPrice", getString(R.string.cost_num, String.valueOf(totalPrice)));
            intent.putExtras(bundle);
            sendBroadcast(intent);
        }
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
        newUseRecord(mTime);
        /*ContentValues values = new ContentValues();
        // 向该对象中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据当中的数据类型一致
        values.put("cycle_date", Utils.getDateFromMillisecond(beginTime));
        values.put("cycle_time", totalTime);
        values.put("cycle_distance", totalDistance);
        values.put("cycle_price", totalPrice);
        values.put("cycle_points", routeListStr);
        // 创建DatabaseHelper对象
        RouteDBHelper dbHelper = new RouteDBHelper(this);
        // 得到一个可写的SQLiteDatabase对象
        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
        // 调用insert方法，就可以将数据插入到数据库当中
        // 第一个参数:表名称
        // 第二个参数：SQl不允许一个空列，如果ContentValues是空的，那么这一列被明确的指明为NULL值
        // 第三个参数：ContentValues对象
        sqliteDatabase.insert("cycle_route", null, values);
        sqliteDatabase.close();*/
        RouteRecord record = new RouteRecord();
        record.setCycle_date(Utils.getDateFromMillisecond(beginTime));
        record.setCycle_time(String.valueOf(mTime));
        record.setCycle_distance(String.valueOf(totalDistance));
        record.setCycle_price(String.valueOf(totalPrice));
        record.setCycle_points(routeListStr);
        record.setUserId(BmobUser.getCurrentUser().getObjectId());
        record.setCarNo(mCarNo);
        record.setCost(totalPrice);
        record.setTime(new Date());
        record.setTimeUse(String.valueOf(mTime));
        record.setEarnRate(EARN_RATE_DEFAULT);
        record.setEarn(totalPrice * EARN_RATE_DEFAULT);
        new DBManager().save(record);
    }

    private void initCountDownTimer(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isBikeUsing){
                    countDownTimer.start();
                    balance();
                }
            }
        }, 0);
    }

    private CountDownTimer countDownTimer = new CountDownTimer(TIME_ONCE_ACTIVE, 1000) {
        private Handler handler = new Handler();
        @Override
        public void onTick(long millisUntilFinished) {
            long min = millisUntilFinished / 60000;
            long secode = (millisUntilFinished / 1000) % 60;
//            totalTime = (int) (System.currentTimeMillis() - beginTime) / 1000 / 60;
//            totalPrice = (float) (Math.floor(totalTime / 30) * COST_BASE_DEFAULT + COST_BASE_DEFAULT);
            final String timeLeft = String.format(Locale.US, FORMAT_TIME, min,
                    secode < 10 ? "0" + secode : String.valueOf(secode));
//            mCarNo = getString(R.string.cost_distance, String.valueOf(totalDistance));
            startNotifi(timeLeft, mCarNo,
                    getString(R.string.cost_num, String.valueOf(totalPrice)));
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendBroadcast(timeLeft);
                }
            }, 1000);

            long millUse = TIME_ONCE_ACTIVE - millisUntilFinished;
            secode = (millUse / 1000) % 60;
            mTime = String.format(Locale.US, FORMAT_TIME, millUse / 60000,
                    secode < 10 ? "0" + secode : String.valueOf(secode));
        }

        @Override
        public void onFinish() {
            sendBroadcast(getString(R.string.time_end));
            stopSelf();
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

    private void balance(){
        //1.扣费
        if(mUser == null){
            return;
        }
        MyUser newUser = new MyUser();
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
                    loge(e);
                }
            }
        }));
        //2.修改car收益
        if(mCar == null){
            return;
        }
        mEarn = totalPrice * EARN_RATE_DEFAULT;
        Car newCar = new Car();
        newCar.setIncome(newCar.getIncome() + COST_BASE_DEFAULT);
        newCar.setEarn(newCar.getEarn() + mEarn);
        addSubscription(newCar.update(mCar.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {

            }
        }));
    }

    private void checkCarExist(String carNo) {
        BmobQuery<Car> query = new BmobQuery<>();
        query.addWhereEqualTo("carNo", carNo);
        addSubscription(query.findObjects(new FindListener<Car>() {

            @Override
            public void done(List<Car> object, BmobException e) {
                if(e==null){
                    if(object != null && object.size() > 0){
                        mCar = object.get(0);
                    }
                }else{
                    loge(e);
                }
            }
        }));
    }
    float mEarn = COST_BASE_DEFAULT * EARN_RATE_DEFAULT;
    private void newUseRecord(String timeUse){
        //2.增加使用记录
        UseRecord record = new UseRecord();
        record.setAuthor(mUser);
        record.setCarNo(mCarNo);
        record.setCost(COST_BASE_DEFAULT);
        record.setEarn(mEarn);
        record.setEarnRate(EARN_RATE_DEFAULT);
        record.setTimeUse(timeUse);
        addSubscription(record.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {

            }
        }));
    }
}
