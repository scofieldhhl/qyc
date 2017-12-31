package com.systemteam;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.igexin.sdk.PushManager;
import com.systemteam.activity.BaseActiveActivity;
import com.systemteam.activity.MyRouteActivity;
import com.systemteam.activity.QRCodeScanActivity;
import com.systemteam.activity.SettingActivity;
import com.systemteam.activity.WalletActivity;
import com.systemteam.activity.ZxingActivity;
import com.systemteam.bean.BikeInfo;
import com.systemteam.bean.Car;
import com.systemteam.bean.MyUser;
import com.systemteam.callback.AllInterface;
import com.systemteam.car.MyCarActivity;
import com.systemteam.custom.LeftDrawerLayout;
import com.systemteam.fragment.LeftMenuFragment;
import com.systemteam.gdmap.AMapUtil;
import com.systemteam.gdmap.lib.LocationTask;
import com.systemteam.gdmap.lib.OnLocationGetListener;
import com.systemteam.gdmap.lib.PositionEntity;
import com.systemteam.gdmap.lib.RegeocodeTask;
import com.systemteam.gdmap.lib.RouteTask;
import com.systemteam.gdmap.overlay.WalkRouteOverlay;
import com.systemteam.service.DemoIntentService;
import com.systemteam.service.DemoPushService;
import com.systemteam.service.GInsightEventListener;
import com.systemteam.user.UserInfoActivity;
import com.systemteam.util.Constant;
import com.systemteam.util.LogTool;
import com.systemteam.util.Utils;
import com.systemteam.view.CatLoadingView;
import com.systemteam.welcome.WelcomeActivity;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.systemteam.util.Constant.ACTION_BROADCAST_ACTIVE;
import static com.systemteam.util.Constant.BUNDLE_KEY_ADDEVICE_ID;
import static com.systemteam.util.Constant.BUNDLE_KEY_CODE;
import static com.systemteam.util.Constant.DISMISS_SPLASH;
import static com.systemteam.util.Constant.DISTANCE_RELOADCAR_DEFAULT;
import static com.systemteam.util.Constant.MAP_SCALING;
import static com.systemteam.util.Constant.MODEL_DEVICE_ZXINGQR;
import static com.systemteam.util.Constant.MSG_RESPONSE_SUCCESS;
import static com.systemteam.util.Constant.MSG_UPDATE_UI;

// 首次打开app都是北京界面换成上次定位结果
// 定位不准，5D定位成 A8 (移动网络定位不准确，通过WI-FI定位准确率高)
// 首次定位绘制marker，中心点和定位位置不一致。
// 移动地图中心位置，加载周边车辆信息
// 首次20个测试数据太过分散（部分设备标记到海里去了）
//63:EA:F2:A9:F8:38:29:90:CB:E5:07:2E:D3:71:37:DC:4B:A3:A3:E2
public class Main2Activity extends BaseActiveActivity implements AMap.OnCameraChangeListener,
        AMap.OnMapLoadedListener, OnLocationGetListener, View.OnClickListener,RouteTask.OnRouteCalculateListener,
        AMap.OnMapTouchListener,RouteSearch.OnRouteSearchListener,AMap.OnMapClickListener,AMap.InfoWindowAdapter,
        AllInterface.OnMenuSlideListener, LocationSource, AMapLocationListener, GInsightEventListener {
    //地图view
    MapView mMapView = null;
    //初始化地图控制器对象
    AMap aMap;

    //定位
    private LocationTask mLocationTask;
    //逆地理编码功能
    private RegeocodeTask mRegeocodeTask;
    //绘制点标记
    private Marker mPositionMark, mInitialMark,tempMark;//可移动、圆点、点击
    //初始坐标、移动记录坐标
    private LatLng mStartPosition,mRecordPositon,mPrePositon;//记录上次加载设备的位置
    private LatLng initLocation;
    //默认添加一次
    private boolean mIsFirst = true;
    //就第一次显示位置
    private boolean mIsFirstShow = true;

    private ValueAnimator animator = null;//坐标动画
    private BitmapDescriptor initBitmap,moveBitmap,smallIdentificationBitmap,bigIdentificationBitmap;//定位圆点、可移动、所有标识（车）
    private RouteSearch mRouteSearch;

    private WalkRouteResult mWalkRouteResult;
    private LatLonPoint mStartPoint = null;//起点，116.335891,39.942295
    private LatLonPoint mEndPoint = null;//终点，116.481288,39.995576
    private final int ROUTE_TYPE_WALK = 3;
    private boolean isClickIdentification = false;
    WalkRouteOverlay walkRouteOverlay;//路线
    private String [] time;
    private String distance;
    View shadowView;
    private boolean isShowingUsing = false;

    private LocationReceiver mReciver;
    private double currentLatitude, currentLongitude;
    private ImageView  btn_locale, mIvScan, mIvMenu;
    private TextView mTvUsingStatus;
    private long exitTime = 0;
    CatLoadingView mView;
    LeftDrawerLayout mLeftDrawerLayout;
    LeftMenuFragment mMenuFragment;
    //定位功能
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    @Override
    public void onMenuSlide(float offset) {
        shadowView.setVisibility(offset == 0 ? View.INVISIBLE : View.VISIBLE);
        int alpha = (int) Math.round(offset * 255 * 0.4);
        shadowView.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
    }

    @Override
    public void onGiuid(String giuid) {
        if (!TextUtils.isEmpty(giuid)) {
            LogTool.e("giuid :" +giuid);
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<Main2Activity> mActivity;

        public MyHandler(Main2Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final Main2Activity theActivity = mActivity.get();
            super.handleMessage(msg);
            switch (msg.what){
                case DISMISS_SPLASH:
                    theActivity.findViewById(R.id.v_splash).setVisibility(View.GONE);
//                    theActivity.cancelFullScreen();
                    theActivity.requestInitConfig();
                    break;
                case MSG_RESPONSE_SUCCESS:
                    List<Car> list = (List<Car>) msg.obj;//网络加载数据的26设备没有显示到地图
                    List<BikeInfo> newList = new ArrayList<>();
                    if(list != null && list.size() > 0){
                        LogTool.e("result : " + list.size() );
                        for(Car car : list){// 两层for循环效率低
                            if(car != null){
                                newList.add(new BikeInfo(car));
                            }
                        }
                    }
                    /*newList.add(new BikeInfo(theActivity.currentLatitude - new Random().nextInt(5) * 0.0005,
                            theActivity.currentLongitude - new Random().nextInt(5) * 0.0005, R.mipmap.bike_icon, "001", "100米", "1分钟"));
                    newList.add(new BikeInfo(theActivity.currentLatitude - new Random().nextInt(5) * 0.0005,
                            theActivity.currentLongitude - new Random().nextInt(5) * 0.0005, R.mipmap.bike_icon, "002", "200米", "2分钟"));
                    newList.add(new BikeInfo(theActivity.currentLatitude - new Random().nextInt(5) * 0.0005,
                            theActivity.currentLongitude - new Random().nextInt(5) * 0.0005, R.mipmap.bike_icon, "003", "300米", "3分钟"));
                    newList.add(new BikeInfo(theActivity.currentLatitude - new Random().nextInt(5) * 0.0005,
                            theActivity.currentLongitude - new Random().nextInt(5) * 0.0005, R.mipmap.bike_icon, "004", "400米", "4分钟"));
                    BikeInfo bikeInfo = new BikeInfo(theActivity.currentLatitude - 0.0005,
                            theActivity.currentLongitude - 0.0005, R.mipmap.bike_icon, "005", "50米", "0.5分钟");
                    newList.add(bikeInfo);*/
                    theActivity.addInfosOverlay(newList);
//                  initNearestBike(bikeInfo, new LatLng(_latitude - 0.0005, _longitude - 0.0005));
                    break;
                case MSG_UPDATE_UI:
                    if(theActivity.isGaming){
                        if(theActivity.mView == null && theActivity.isShowingUsing){
                            theActivity.mView = new CatLoadingView();
                            theActivity.mView.show(theActivity.getSupportFragmentManager(), "");
                        }
                        theActivity.bikeOnUsing();
                        theActivity.btn_locale.setEnabled(false);
                        theActivity.mIvScan.setEnabled(false);
                        theActivity.mIvMenu.setEnabled(false);
//                        theActivity.mIvScan.setImageResource(R.drawable.middle_using);
                        theActivity.mTvUsingStatus.setText(R.string.main_using);
                    }else {
                        if(theActivity.mView != null){
                            theActivity.mView.dismiss();
                            theActivity.mView = null;
                        }
                        theActivity.btn_locale.setEnabled(true);
                        theActivity.mIvScan.setEnabled(true);
                        theActivity.mIvMenu.setEnabled(true);
//                        theActivity.mIvScan.setImageResource(R.drawable.middle);
                        theActivity.mTvUsingStatus.setText(R.string.main_scan);
                    }
                    break;
            }
        }
    }
    private MyHandler mHandler = new MyHandler(this);
/**
 * //地图默认显示北京地区，通过采用重载的 MapView 构造方法更改默认地图显示区域：
 * // 定义北京市经纬度坐标（此处以北京坐标为例）
 LatLng centerBJPoint= new LatLng(39.904989,116.405285);
 // 定义了一个配置 AMap 对象的参数类
 AMapOptions mapOptions = new AMapOptions();
 // 设置了一个可视范围的初始化位置
 // CameraPosition 第一个参数： 目标位置的屏幕中心点经纬度坐标。
 // CameraPosition 第二个参数： 目标可视区域的缩放级别
 // CameraPosition 第三个参数： 目标可视区域的倾斜度，以角度为单位。
 // CameraPosition 第四个参数： 可视区域指向的方向，以角度为单位，从正北向顺时针方向计算，从0度到360度
 mapOptions.camera(new CameraPosition(centerBJPoint, 10f, 0, 0));
 // 定义一个 MapView 对象，构造方法中传入 mapOptions 参数类
 MapView mapView = new MapView(this, mapOptions);
 // 调用 onCreate方法 对 MapView LayoutParams 设置
 mapView.onCreate(savedInstanceState);
 * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setFullScreen();
        setContentView(R.layout.activity_main);
        findViewById(R.id.v_splash).setVisibility(View.VISIBLE);
        checkSDK();
        LogTool.i("Main2Activity---------onCreate---------------");
        mContext = this;
        checkLogin();
        initToolBar(this, R.string.bybike);
        initView();
        initData();
        initGeTui();
        initGeX();

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        initBitmap();
        initAMap();
        initLocation();
        RouteTask.getInstance(getApplicationContext()).addRouteCalculateListener(this);
//        LogTool.e("sha1" + Sha1.sHA1(this));
        LogTool.d("oncreate end");
        mHandler.sendEmptyMessageDelayed(DISMISS_SPLASH, 4*1000);

        String tag = BikeApplication.getInstance().getTag();
        if(tag != null && !TextUtils.isEmpty(tag)){
            LogTool.e("tag:" + tag);
        }else {
            LogTool.e("tag null");
            requestToken();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
                return super.onOptionsItemSelected(item);
    }

    private void initBitmap(){
        initBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker);
        moveBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.location_center);
        smallIdentificationBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.bike_icon);
        bigIdentificationBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.bike_icon_focus);
    }
    /**
     * 初始化地图控制器对象
     */
    private void initAMap() {
        if (aMap == null) {
            aMap = mMapView.getMap();
            mRouteSearch = new RouteSearch(this);
            mRouteSearch.setRouteSearchListener(this);
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.getUiSettings().setGestureScaleByMapCenter(true);
//            aMap.getUiSettings().setScrollGesturesEnabled(false);
            aMap.getUiSettings().setLogoBottomMargin(-50);//隐藏logo
            aMap.setOnMapTouchListener(this);
            aMap.setOnMapLoadedListener(this);
            aMap.setOnCameraChangeListener(this);
            aMap.setOnMapClickListener(this);
            aMap.setLocationSource(this);
            // 绑定 Marker 被点击事件
            aMap.setOnMarkerClickListener(markerClickListener);
            aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
            //设置为true时，地图每次移动都会自动跳转到定位位置
            aMap.setMyLocationEnabled(false);// 设置为true表示系统定位按钮显示并响应点击，false表示隐藏，默认是false
        }
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        mLocationTask = LocationTask.getInstance(getApplicationContext());
        mLocationTask.setOnLocationGetListener(this);
        mRegeocodeTask = new RegeocodeTask(getApplicationContext());
    }

    // 定义 Marker 点击事件监听
    AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {

        // marker 对象被点击时回调的接口
        // 返回 true 则表示接口已响应事件，否则返回false
        @Override
        public boolean onMarkerClick(final Marker marker) {
            LogTool.e("点击的Marker");
            LogTool.e(marker.getPosition() + "");
            isClickIdentification = true;
            if(tempMark!=null)
            {
                tempMark.setIcon(smallIdentificationBitmap);
                walkRouteOverlay.removeFromMap();
                tempMark = null;
            }
            startAnim(marker);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(300);
                        tempMark = marker;
                        LogTool.e(mPositionMark.getPosition().latitude+"==="+mPositionMark.getPosition().longitude);
                        mStartPoint = new LatLonPoint(mRecordPositon.latitude, mRecordPositon.longitude);
                        mPositionMark.setPosition(mRecordPositon);
                        mEndPoint =new LatLonPoint(marker.getPosition().latitude,marker.getPosition().longitude);
                        marker.setIcon(bigIdentificationBitmap);
                        marker.setPosition(marker.getPosition());
                        searchRouteResult(ROUTE_TYPE_WALK, RouteSearch.WalkDefault);
//                        Intent intent = new Intent(Main2Activity.this, RouteActivity.class);
//                        intent.putExtra("start_lat", mPositionMark.getPosition().latitude);
//                        intent.putExtra("start_lng", mPositionMark.getPosition().longitude);
//                        intent.putExtra("end_lat", marker.getPosition().latitude);
//                        intent.putExtra("end_lng", marker.getPosition().longitude);
//                        startActivity(intent);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            return true;
        }
    };

    private void startAnim(Marker marker) {
        ScaleAnimation anim = new ScaleAnimation(1.0f, 1.3f, 1.0f, 1.3f);
        anim.setDuration(300);
        marker.setAnimation(anim);
        marker.startAnimation();
    }

    @Override
    protected void initView() {
        shadowView = (View) findViewById(R.id.shadow);
        shadowView.setOnClickListener(this);
        mIvScan = (ImageView) findViewById(R.id.iv_scan);
        mIvMenu = (ImageView) findViewById(R.id.iv_menu);
        mTvUsingStatus = (TextView) findViewById(R.id.tv_status);

        //侧滑栏
        mLeftDrawerLayout = (LeftDrawerLayout) findViewById(R.id.id_drawerlayout);
        FragmentManager fm = getSupportFragmentManager();
        mMenuFragment = (LeftMenuFragment) fm.findFragmentById(R.id.id_container_menu);
        mLeftDrawerLayout.setOnMenuSlideListener(this);

        if (mMenuFragment == null) {
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment =
                    LeftMenuFragment.newInstance(mUser)).commit();
        }

        btn_locale = (ImageView) findViewById(R.id.btn_locale);
        btn_locale.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mReciver = new Main2Activity.LocationReceiver();
        registerBroadcast(ACTION_BROADCAST_ACTIVE, mReciver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
        if (mInitialMark != null) {
            mInitialMark.setToTop();
        }
        if (mPositionMark != null) {
            mPositionMark.setToTop();
        }
        LogTool.d("onResume");
        MobclickAgent.onPageStart("MainScreen");
        MobclickAgent.onResume(this);
        boolean isServiceLive = Utils.isServiceWork(this, getPackageName() + ".service.RouteService");
        LogTool.i("MainActivity------------onRestart------------------");
        if (!isServiceLive && mView != null) {
                mView.dismiss();
                mView = null;
        }
        isShowingUsing = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
        MobclickAgent.onPageEnd("MainScreen");
        MobclickAgent.onPause(this);
        closeMenu();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public void activate(OnLocationChangedListener listener) {
        LogTool.d("activate");
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getApplicationContext());
            mLocationOption = new AMapLocationClientOption();
            mlocationClient.setLocationListener(this);
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setLocationCacheEnable(true);
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }
    }


    @Override
    public void deactivate() {
        LogTool.d("deactivate");
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
        mLocationOption = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        LogTool.d("onLocationChanged");
    }
    /**
     * 对正在移动地图事件回调
     */
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
    }
    /**
     * 对移动地图结束事件回调
     */
    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
//        LogTool.e("onCameraChangeFinish" + cameraPosition.target);
        if(!isClickIdentification) {
            mRecordPositon = cameraPosition.target;
        }
        currentLatitude = cameraPosition.target.latitude;
        currentLongitude = cameraPosition.target.longitude;
//        mStartPosition = cameraPosition.target;
        //加载
        mRegeocodeTask.setOnLocationGetListener(this);
        mRegeocodeTask.search(currentLatitude, currentLongitude);
//        Utils.removeMarkers();
        if(mIsFirst) {
            createInitialPosition(cameraPosition.target.latitude, cameraPosition.target.longitude);
            createMovingPosition();
            mIsFirst = false;
        }else {
            loadCarlistNear(currentLatitude, currentLongitude, false);
        }
        if (mInitialMark != null) {
            mInitialMark.setToTop();
        }
        if (mPositionMark != null) {
            mPositionMark.setToTop();
            if(!isClickIdentification) {
                animMarker();
            }
        }
    }


    /**
     * 地图加载完成
     */
    @Override
    public void onMapLoaded() {
        LogTool.d("onMapLoaded");
        mLocationTask.startLocate();
    }

    public void getMyLocation() {
        if(mStartPosition != null){
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(
                    new LatLng(mStartPosition.latitude, mStartPosition.longitude)));
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(initLocation, MAP_SCALING));
        }
    }

    /**
     * 创建初始位置图标
     */
    private void createInitialPosition(double lat, double lng) {
        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.setFlat(true);
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.position(new LatLng(lat, lng));
        markerOptions.icon(initBitmap);
        mInitialMark = aMap.addMarker(markerOptions);
        mInitialMark.setClickable(false);
    }

    /**
     * 创建移动位置图标
     */
    private void createMovingPosition() {
        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.setFlat(true);
//        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.position(new LatLng(0, 0));
        markerOptions.icon(moveBitmap);
        mPositionMark = aMap.addMarker(markerOptions);
        mPositionMark.setPositionByPixels(mMapView.getWidth() / 2,
                mMapView.getHeight() / 2);
        mPositionMark.setClickable(false);
    }

    @Override
    public void onLocationGet(PositionEntity entity) {
        // 这里在网络定位时可以减少一个逆地理编码
//        LogTool.e("onLocationGet" + entity.address);
        RouteTask.getInstance(getApplicationContext()).setStartPoint(entity);
        mStartPosition = new LatLng(entity.latitue, entity.longitude);
        if(mIsFirstShow) {
            mPrePositon = mStartPosition;
            CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(mStartPosition, 17);
            aMap.animateCamera(cameraUpate);
            // 添加模拟测试的车的点
            Utils.addEmulateData(aMap, mStartPosition);
            LogTool.d("onLocationGet" + entity.address + " " + entity.latitue + " " + entity.longitude);
            loadCarlistNear(entity.latitue, entity.longitude, mIsFirstShow);
            mIsFirstShow = false;
        }
        mInitialMark.setPosition(mStartPosition);
        initLocation = mStartPosition;
        BikeApplication.mCurrentAddress = entity.address;
        BikeApplication.setPosition(entity.longitude, entity.latitue);
    }

    @Override
    public void onRegecodeGet(PositionEntity entity) {
        LogTool.e("onRegecodeGet" + entity.address);
        entity.latitue = mStartPosition.latitude;
        entity.longitude = mStartPosition.longitude;
        RouteTask.getInstance(getApplicationContext()).setStartPoint(entity);
        RouteTask.getInstance(getApplicationContext()).search();
        LogTool.e("onRegecodeGet" + mStartPosition);
    }

    @Override
    public void onRouteCalculate(float cost, float distance, int duration) {
        LogTool.e("cost"+cost+"---"+"distance"+distance+"---"+"duration"+duration);
        PositionEntity endPoint = RouteTask.getInstance(getApplicationContext()).getEndPoint();
        mRecordPositon = new LatLng(endPoint.latitue,endPoint.longitude);
        clickMap();
        RouteTask.getInstance(getApplicationContext()).setEndPoint(null);
    }
    @Override
    public void onTouch(MotionEvent motionEvent) {
       if(motionEvent.getPointerCount()>=2)
       {
           aMap.getUiSettings().setScrollGesturesEnabled(false);
       }else
       {
           aMap.getUiSettings().setScrollGesturesEnabled(true);
       }
    }

    private void animMarker() {
        if (animator != null) {
            animator.start();
            return;
        }
        animator = ValueAnimator.ofFloat(mMapView.getHeight()/2, mMapView.getHeight()/2 - 30);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(150);
        animator.setRepeatCount(1);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                mPositionMark.setPositionByPixels(mMapView.getWidth() / 2, Math.round(value));
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mPositionMark.setIcon(moveBitmap);
            }
        });
        animator.start();
    }

    private void endAnim() {
        if (animator != null && animator.isRunning())
            animator.end();
    }


    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mWalkRouteResult = result;
                    final WalkPath walkPath = mWalkRouteResult.getPaths()
                            .get(0);
                    walkRouteOverlay = new WalkRouteOverlay(
                            this, aMap, walkPath,
                            mWalkRouteResult.getStartPos(),
                            mWalkRouteResult.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                    int dis = (int) walkPath.getDistance();
                    int dur = (int) walkPath.getDuration();
                    time = AMapUtil.getFriendlyTimeArray(dur);
                    distance = AMapUtil.getFriendlyLength(dis);
                    String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
                    tempMark.setTitle(des);
                    tempMark.showInfoWindow();
                    LogTool.e(des);
                } else if (result != null && result.getPaths() == null) {
                }
            } else {
            }
        } else {
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }
    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            toast("定位中，稍后再试...");
            return;
        }
        if (mEndPoint == null) {
            toast("终点未设置");
        }
        showDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, mode);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }
    }

    private void showDialog()
    {
    }

    @Override
    public void onMapClick(LatLng latLng) {
        clickMap();
    }
    private void clickRefresh()
    {
        clickInitInfo();
        if(initLocation!=null) {
            CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(initLocation, MAP_SCALING);
            aMap.animateCamera(cameraUpate);
        }
    }
    private void clickMap()
    {
        clickInitInfo();
        if(mRecordPositon!=null) {
            CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(
                    mRecordPositon, MAP_SCALING);
            aMap.animateCamera(cameraUpate);
        }
    }
    private void clickInitInfo()
    {
        isClickIdentification = false;
        if(null!=tempMark) {
            tempMark.setIcon(smallIdentificationBitmap);
            tempMark.hideInfoWindow();
            tempMark = null;
        }
        if(null!=walkRouteOverlay) {
            walkRouteOverlay.removeFromMap();
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        LogTool.e("getInfoWindow");
        View infoWindow = getLayoutInflater().inflate(R.layout.info_window, null);
        render(marker,infoWindow);
        return infoWindow;
    }
    /**
     * 自定义infowinfow窗口
     */
    public void render(Marker marker, View view) {
        TextView tv_time = (TextView) view.findViewById(R.id.tv_time);
        TextView tv_time_info = (TextView)view.findViewById(R.id.tv_time_info);
        TextView tv_distance =(TextView) view.findViewById(R.id.tv_distance);
        tv_time.setText(time[0]);
        tv_time_info.setText(time[1]);
        tv_distance.setText(distance);
    }

    @Override
    public View getInfoContents(Marker marker) {
        LogTool.e("getInfoContents");
        return null;
    }

    private void checkSDK(){
//        CheckPermission();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int hasReadPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            int gpsPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            int readStatePermission = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);

            List<String> permissions = new ArrayList<String>();
            if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (hasReadPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

            }
            if (gpsPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (readStatePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }

            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]),
                        REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
            }
        }
            PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
    }


    /**
     * 获取修改系统设置的权限
     */
    private void CheckPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.System.canWrite(this)) {
                    Uri selfPackageUri = Uri.parse("package:" + getPackageName());
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                            selfPackageUri);
                    startActivity(intent);
                }
            }
        }catch (Exception e){}
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_SOME_FEATURES_PERMISSIONS) {
            if ((grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
            } else {
                LogTool.e("We highly recommend that you need to grant the special permissions before initializing the SDK, otherwise some "
                        + "functions will not work");
                PushManager.getInstance().initialize(this.getApplicationContext(), userPushService);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            default:
                break;
        }
        checkBackFromBreak(requestCode, data);
    }

    private void checkLogin(){
        mUser = ((BikeApplication) this.getApplication()).getmUser();
        if(mUser == null){
            startActivity(new Intent(Main2Activity.this, WelcomeActivity.class));
        }
    }

    protected void initToolBar(Activity act, int titleId) {
        mToolbar = (Toolbar) act.findViewById(R.id.toolbar);
        mToolbar.getVisibility();
        mToolbar.setTitle("");
    }

    class LocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String time = intent.getStringExtra("totalTime");
            isGaming = true;
            if(getString(R.string.time_end).equalsIgnoreCase(time)) {
                LogTool.d("onReceive");
                isGaming = false;
            }
            if(Utils.isTopActivity(Main2Activity.this)){
                mHandler.sendEmptyMessage(MSG_UPDATE_UI);
            }
        }
    }

    public void openMenu() {
        if(!checkUser(this))
            return;
        mLeftDrawerLayout.openDrawer();
        shadowView.setVisibility(View.VISIBLE);
    }

    public void closeMenu() {
        if(mLeftDrawerLayout != null){
            mLeftDrawerLayout.closeDrawer();
            shadowView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_locale:
                getMyLocation();
                break;
            case R.id.iv_menu:
            case R.id.menu_icon:
                openMenu();
                break;
            case R.id.shadow:
                closeMenu();
                break;
        }
    }

    //通过load后台获得的设备列表没有显示到地图
    private void loadCarlistNear(double _latitude, double _longitude, boolean isFrist){
        if(!isFrist){
            if(mPrePositon == null){
                mPrePositon = mStartPosition;
            }
            double distance = Utils.GetDistance(mPrePositon.latitude, mPrePositon.longitude, _latitude, _longitude);
            double distanceStart = Utils.GetDistance(_latitude, _longitude, mStartPosition.latitude, mStartPosition.longitude);
            LogTool.d("distance : " + distance +  " distanceStart:" + distanceStart);
            if(distance < DISTANCE_RELOADCAR_DEFAULT || distanceStart < DISTANCE_RELOADCAR_DEFAULT){
                return;
            }else {
                mPrePositon = new LatLng(_latitude, _longitude);
            }
        }
        LogTool.d("loadCarlistNear");
        BmobQuery<Car> query = new BmobQuery<>();
        query.addWhereNear("position", new BmobGeoPoint(_longitude, _latitude));
//        query.addWhereWithinRadians("position", new BmobGeoPoint(_longitude, _latitude), 100.0);
        addSubscription(query.findObjects(new FindListener<Car>() {

            @Override
            public void done(List<Car> object, BmobException e) {
                mProgressHelper.dismissProgressDialog();
                if(e==null){
                    Message msg = mHandler.obtainMessage(Constant.MSG_RESPONSE_SUCCESS);
                    msg.obj = object;
                    msg.sendToTarget();
                }else{
                    LogTool.e("");
                    loge(e);
                }
            }
        }));
    }

    private void bikeOnUsing(){
        mIvScan.setVisibility(View.VISIBLE);
        countDownTimer.cancel();
        btn_locale.setVisibility(View.VISIBLE);
        //        if (routeOverlay != null)     //清除地图所有标记
//            routeOverlay.removeFromMap();
//        mBaiduMap.clear();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        LogTool.d("onNewIntent");
        super.onNewIntent(intent);
        mADDeviceId = intent.getStringExtra(BUNDLE_KEY_ADDEVICE_ID);
        checkCarExist(this, intent.getStringExtra(BUNDLE_KEY_CODE));//启动使用
    }


    @Override
    protected void onStart() {
        super.onStart();
        LogTool.i("Main2Activity------------onStart------------------");
    }
    @Override
    protected void onStop() {
        super.onStop();
        LogTool.d("onStop");
        if(mView != null){
            mView.dismissAllowingStateLoss();
            mView = null;
        }
        isShowingUsing = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
        // 关闭定位图层
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        Utils.removeMarkers();
        mMapView.onDestroy();
        mMapView = null;
        mLocationTask.onDestroy();
        RouteTask.getInstance(getApplicationContext()).removeRouteCalculateListener(this);
        unregisterReceiver(mReciver);

        BikeApplication.payloadData.delete(0, BikeApplication.payloadData.length());
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                toast(getString(R.string.exist_app));
                exitTime = System.currentTimeMillis();
            } else {
//                finish();
//                System.exit(0);
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private CountDownTimer countDownTimer = new CountDownTimer(10 * 60 * 1000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
        }
    };

    /**
     * 根据经纬度将图标绘制地图上
     * @param infos
     */
    private static ArrayList<Marker> markers = new ArrayList<Marker>();
    public void addInfosOverlay(List<BikeInfo> infos) {
        LogTool.d("addInfosOverlay : " + infos.size());
        for (BikeInfo info : infos) {
            MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.setFlat(true);
//                markerOptions.anchor(0.5f, 0.5f);
            markerOptions.icon(smallIdentificationBitmap);

            markerOptions.position(new LatLng(info.getLatitude(), info.getLongitude()));
            Marker marker = aMap.addMarker(markerOptions);
            markers.add(marker);
        }
//        Utils.addEmulateData(aMap, mStartPosition);
    }

    public void gotoCodeUnlock(View view) {
        if(!checkUser(this))
            return;
        if(!checkBalance(BmobUser.getCurrentUser(MyUser.class), Main2Activity.this)){
            return;
        }
        Intent intent = new Intent(this, QRCodeScanActivity.class);
        if(MODEL_DEVICE_ZXINGQR.equalsIgnoreCase(android.os.Build.MODEL)){
            intent = new Intent(this, ZxingActivity.class);
        }
        intent.putExtra(Constant.BUNDLE_KEY_UNLOCK, true);
        startActivity(intent);
    }

    public void gotoMycar(View view) {
        if(!checkUser(this))
            return;
        startActivity(new Intent(this, MyCarActivity.class));
    }

    public void gotoMyRoute(View view) {
        if(!checkUser(this))
            return;
        startActivity(new Intent(this, MyRouteActivity.class));
    }

    public void gotoWallet(View view) {
        if(!checkUser(this))
            return;
        startActivity(new Intent(this, WalletActivity.class));
    }

    public void gotoUser(View view) {
        if(!checkUser(this))
            return;
        startActivity(new Intent(this, UserInfoActivity.class));
    }

    public void gotoSetting(View view) {
        if(!checkUser(this))
            return;
        Intent intent = new Intent(this, SettingActivity.class);
        intent.putExtra(Constant.BUNDLE_TYPE_MENU, 0);
        startActivity(intent);
    }

    public void gotoGuide(View view){
        if(!checkUser(this))
            return;
        Intent intent = new Intent(this, SettingActivity.class);
        intent.putExtra(Constant.BUNDLE_TYPE_MENU, 1);
        startActivity(intent);
    }


    // DemoPushService.class 自定义服务名称, 核心服务
    private Class userPushService = DemoPushService.class;
    public void initGeTui(){
        PackageManager pkgManager = getPackageManager();

        // 读写 sd card 权限非常重要, android6.0默认禁止的, 建议初始化之前就弹窗让用户赋予该权限
        boolean sdCardWritePermission =
                pkgManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName()) == PackageManager.PERMISSION_GRANTED;

        // read phone state用于获取 imei 设备信息
        boolean phoneSatePermission =
                pkgManager.checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName()) == PackageManager.PERMISSION_GRANTED;

        // 注册 intentService 后 PushDemoReceiver 无效, sdk 会使用 DemoIntentService 传递数据,
        // AndroidManifest 对应保留一个即可(如果注册 DemoIntentService, 可以去掉 PushDemoReceiver, 如果注册了
        // IntentService, 必须在 AndroidManifest 中声明)
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);

        // 应用未启动, 个推 service已经被唤醒,显示该时间段内离线消息
        if (BikeApplication.payloadData != null) {
//            tLogView.append(DemoApplication.payloadData);
            LogTool.e(BikeApplication.payloadData.toString());
        }

        // cpu 架构
        LogTool.e("cpu arch = " + (Build.VERSION.SDK_INT < 21 ? Build.CPU_ABI : Build.SUPPORTED_ABIS[0]));

        // 检查 so 是否存在
        File file = new File(this.getApplicationInfo().nativeLibraryDir + File.separator + "libgetuiext2.so");
        LogTool.e("libgetuiext2.so exist = " + file.exists());
    }

    private void initGeX(){
        BikeApplication app = (BikeApplication)getApplication();
        app.registerGInsightListener(this);

        String giuid = app.getGiuid();
        LogTool.d("GIUID:" + giuid);
    }
}