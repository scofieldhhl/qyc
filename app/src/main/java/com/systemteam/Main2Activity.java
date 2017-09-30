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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
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
import com.systemteam.activity.BaseActiveActivity;
import com.systemteam.bean.BikeInfo;
import com.systemteam.bean.Car;
import com.systemteam.callback.AllInterface;
import com.systemteam.custom.LeftDrawerLayout;
import com.systemteam.fragment.LeftMenuFragment;
import com.systemteam.gdmap.AMapUtil;
import com.systemteam.gdmap.lib.LocationTask;
import com.systemteam.gdmap.lib.OnLocationGetListener;
import com.systemteam.gdmap.lib.PositionEntity;
import com.systemteam.gdmap.lib.RegeocodeTask;
import com.systemteam.gdmap.lib.RouteTask;
import com.systemteam.gdmap.lib.Sha1;
import com.systemteam.gdmap.lib.Utils;
import com.systemteam.gdmap.overlay.WalkRouteOverlay;
import com.systemteam.util.Constant;
import com.systemteam.util.LogTool;
import com.systemteam.view.CatLoadingView;
import com.systemteam.welcome.WelcomeActivity;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.systemteam.R.id.book_bt;
import static com.systemteam.bean.BikeInfo.infos;
import static com.systemteam.util.Constant.ACTION_BROADCAST_ACTIVE;
import static com.systemteam.util.Constant.BUNDLE_KEY_CODE;
import static com.systemteam.util.Constant.DISMISS_SPLASH;
import static com.systemteam.util.Constant.MSG_RESPONSE_SUCCESS;
import static com.systemteam.util.Constant.MSG_UPDATE_UI;

//63:EA:F2:A9:F8:38:29:90:CB:E5:07:2E:D3:71:37:DC:4B:A3:A3:E2
public class Main2Activity extends BaseActiveActivity implements AMap.OnCameraChangeListener,
        AMap.OnMapLoadedListener, OnLocationGetListener, View.OnClickListener,RouteTask.OnRouteCalculateListener,
        AMap.OnMapTouchListener,RouteSearch.OnRouteSearchListener,AMap.OnMapClickListener,AMap.InfoWindowAdapter,
        AllInterface.OnMenuSlideListener{
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
    private LatLng mStartPosition,mRecordPositon;
    //默认添加一次
    private boolean mIsFirst = true;
    //就第一次显示位置
    private boolean mIsFirstShow = true;

    private LatLng initLocation;


    private DrawerLayout drawerLayout;

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

    @Override
    public void onMenuSlide(float offset) {

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
                    break;
                case MSG_RESPONSE_SUCCESS:
                    List<Car> list = (List<Car>) msg.obj;
                    if(list != null && list.size() > 0){
                        if(infos == null){
                            infos = new ArrayList<>();
                        }
                        for(Car car : list){//TODO 两层for循环效率低
                            if(car != null){
                                infos.add(new BikeInfo(car));
                            }
                        }
                    }
                    infos.add(new BikeInfo(theActivity.currentLatitude - new Random().nextInt(5) * 0.0005,
                            theActivity.currentLongitude - new Random().nextInt(5) * 0.0005, R.mipmap.bike_icon, "001", "100米", "1分钟"));
                    infos.add(new BikeInfo(theActivity.currentLatitude - new Random().nextInt(5) * 0.0005,
                            theActivity.currentLongitude - new Random().nextInt(5) * 0.0005, R.mipmap.bike_icon, "002", "200米", "2分钟"));
                    infos.add(new BikeInfo(theActivity.currentLatitude - new Random().nextInt(5) * 0.0005,
                            theActivity.currentLongitude - new Random().nextInt(5) * 0.0005, R.mipmap.bike_icon, "003", "300米", "3分钟"));
                    infos.add(new BikeInfo(theActivity.currentLatitude - new Random().nextInt(5) * 0.0005,
                            theActivity.currentLongitude - new Random().nextInt(5) * 0.0005, R.mipmap.bike_icon, "004", "400米", "4分钟"));
                    BikeInfo bikeInfo = new BikeInfo(theActivity.currentLatitude - 0.0005,
                            theActivity.currentLongitude - 0.0005, R.mipmap.bike_icon, "005",
                            "50米", "0.5分钟");
                    infos.add(bikeInfo);
                    theActivity.addInfosOverlay(infos);
//                  initNearestBike(bikeInfo, new LatLng(_latitude - 0.0005, _longitude - 0.0005));
                    break;
                case MSG_UPDATE_UI:
                    if(theActivity.isGaming){
                        theActivity.bikeOnUsing();
                        theActivity.btn_locale.setEnabled(false);
                        theActivity.mIvScan.setEnabled(false);
                        theActivity.mIvMenu.setEnabled(false);
                        theActivity.mIvScan.setImageResource(R.drawable.middle_using);
                        theActivity.mTvUsingStatus.setText(R.string.main_using);
                    }else {
                        if(theActivity.mView != null){
                            theActivity.mView.dismiss();
                        }
                        theActivity.backFromRouteDetail();
                        theActivity.btn_locale.setEnabled(true);
                        theActivity.mIvScan.setEnabled(true);
                        theActivity.mIvMenu.setEnabled(true);
                        theActivity.mIvScan.setImageResource(R.drawable.middle);
                        theActivity.mTvUsingStatus.setText(R.string.main_scan);
                    }
                    break;
            }
        }
    }
    private MyHandler mHandler = new MyHandler(this);
    private LocationReceiver mReciver;
    private double currentLatitude, currentLongitude, changeLatitude, changeLongitude;
    private ImageView  btn_locale, mIvScan, mIvMenu;
    private TextView mTvUsingStatus;
    private long exitTime = 0;
    private boolean isFirstIn;
    private BikeInfo bInfo;
    private String mCarNo;
    private float mCurrentX;
    private boolean isFirstLoc = true; // 是否首次定位
    CatLoadingView mView;
    LeftDrawerLayout mLeftDrawerLayout;
    LeftMenuFragment mMenuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkSDK();
        LogTool.i("Main2Activity---------onCreate---------------");
        mContext = this;
        checkLogin();
        initToolBar(this, R.string.bybike);
        initView();
        initData();

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        initBitmap();
        initAMap();
        initLocation();
        RouteTask.getInstance(getApplicationContext()).addRouteCalculateListener(this);
        LogTool.e("sha1" + Sha1.sHA1(this));
        LogTool.d("oncreate end");
    }

    private void initBitmap()
    {
        initBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker);
        moveBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.location_center);
        smallIdentificationBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.stable_cluster_marker_one_normal);
        bigIdentificationBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.stable_cluster_marker_one_select);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
                return super.onOptionsItemSelected(item);
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
            aMap.setOnMapTouchListener(this);
            aMap.setOnMapLoadedListener(this);
            aMap.setOnCameraChangeListener(this);
            aMap.setOnMapClickListener(this);
            // 绑定 Marker 被点击事件
            aMap.setOnMarkerClickListener(markerClickListener);
            aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
            aMap.setMyLocationEnabled(true);// 设置为true表示系统定位按钮显示并响应点击，false表示隐藏，默认是false
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
    public void onCameraChange(CameraPosition cameraPosition) {
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        LogTool.e("onCameraChangeFinish" + cameraPosition.target);
        if(!isClickIdentification) {
            mRecordPositon = cameraPosition.target;
        }
        mStartPosition = cameraPosition.target;
        mRegeocodeTask.setOnLocationGetListener(this);
        mRegeocodeTask.search(mStartPosition.latitude, mStartPosition.longitude);
//        Utils.removeMarkers();
        if(mIsFirst) {
            //添加模拟测试的车的点
            Utils.addEmulateData(aMap, mStartPosition);
//            addOverLayout(currentLatitude, currentLongitude);
            createInitialPosition(cameraPosition.target.latitude, cameraPosition.target.longitude);
            createMovingPosition();
            mIsFirst = false;
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

    /**
     * 创建初始位置图标
     */
    private void createInitialPosition(double lat, double lng) {
        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.setFlat(true);
        markerOptions.anchor(0.5f, 0.5f);
        markerOptions.position(new LatLng(lat, lng));
//        markerOptions.icon(initBitmap);
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
        // todo 这里在网络定位时可以减少一个逆地理编码
        LogTool.e("onLocationGet" + entity.address);
        RouteTask.getInstance(getApplicationContext()).setStartPoint(entity);
        mStartPosition = new LatLng(entity.latitue, entity.longitude);
        if(mIsFirstShow) {
            CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(mStartPosition, 17);
            aMap.animateCamera(cameraUpate);
            mIsFirstShow = false;
        }
        mInitialMark.setPosition(mStartPosition);
        initLocation = mStartPosition;
        LogTool.e("onLocationGet" + mStartPosition);
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
            CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(
                    initLocation, 17f);
            aMap.animateCamera(cameraUpate);
        }
    }
    private void clickMap()
    {
        clickInitInfo();
        if(mRecordPositon!=null) {
            CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(
                    mRecordPositon, 17f);
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
        CheckPermission();
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
    }


    private void CheckPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.System.canWrite(this)) {
                    Uri selfPackageUri = Uri.parse("package:" + getPackageName());
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, selfPackageUri);
                    startActivity(intent);
                }
            }
        }catch (Exception e){}
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permissions --> " + "Permission Granted: " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        System.out.println("Permissions --> " + "Permission Denied: " + permissions[i]);
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
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
            String distance = intent.getStringExtra("totalDistance");
            mCarNo = distance;
            isGaming = true;
            if(getString(R.string.time_end).equalsIgnoreCase(time)) {
                isGaming = false;
                mCarNo = null;
            }
            mHandler.sendEmptyMessage(MSG_UPDATE_UI);
        }
    }

    public void openMenu() {
        if(!checkUser(this))
            return;
        mLeftDrawerLayout.openDrawer();
    }

    public void closeMenu() {
        if(mLeftDrawerLayout != null){
            mLeftDrawerLayout.closeDrawer();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case book_bt:
                countDownTimer.start();
                break;
            case R.id.btn_locale:
                /*getMyLocation();
                if (routeOverlay != null)
                    routeOverlay.removeFromMap();
                LogTool.i("currentLatitude-----btn_locale--------" + currentLatitude);
                LogTool.i("currentLongitude-----btn_locale--------" + currentLongitude);
//                startNodeStr = PlanNode.withLocation(currentLL);
                addOverLayout(currentLatitude, currentLongitude);*/
                break;
            case R.id.iv_menu:
            case R.id.menu_icon:
                openMenu();
                break;
        }
    }

    private void loadCarlistNear(double _latitude, double _longitude){
        mProgressHelper.showProgressDialog(getString(R.string.initing));
        BmobQuery<Car> query = new BmobQuery<>();
//        query.addWhereNear("position", new BmobGeoPoint(_longitude, _latitude));
        query.addWhereWithinRadians("position", new BmobGeoPoint(_longitude, _latitude), 100.0);
//        query.order("-position");
        addSubscription(query.findObjects(new FindListener<Car>() {

            @Override
            public void done(List<Car> object, BmobException e) {
                mProgressHelper.dismissProgressDialog();
                if(e==null){
                    Message msg = mHandler.obtainMessage(Constant.MSG_RESPONSE_SUCCESS);
                    msg.obj = object;
                    msg.sendToTarget();
                }else{
                    loge(e);
                }
            }
        }));
    }

    private void backFromRouteDetail() {
        LogTool.d("backFromRouteDetail");
        isFirstIn = true;

        mIvScan.setVisibility(View.VISIBLE);
        btn_locale.setVisibility(View.VISIBLE);

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
        isFirstIn = true;
        unregisterReceiver(mReciver);
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
        /*LatLng latLng = null;
        OverlayOptions overlayOptions = null;
        Marker marker = null;
        for (BikeInfo info : infos) {
            // 位置
            latLng = new LatLng(info.getLatitude(), info.getLongitude());
            // 图标
            overlayOptions = new MarkerOptions().position(latLng).icon(bikeIcon).zIndex(5);
            marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
            Bundle bundle = new Bundle();
            bundle.putSerializable("info", info);
            marker.setExtraInfo(bundle);
        }
        // 将地图移到到最后一个经纬度位置
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(u);*/

        for (BikeInfo info : infos) {
            MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.setFlat(true);
//                markerOptions.anchor(0.5f, 0.5f);
            markerOptions.icon(smallIdentificationBitmap);

            markerOptions.position(new LatLng(info.getLatitude(), info.getLongitude()));
            Marker marker = aMap.addMarker(markerOptions);
            markers.add(marker);
        }
    }

    /**
     * 移除marker
     */
    public static void removeMarkers() {
        for (Marker marker : markers) {
            marker.remove();
            marker.destroy();
        }
        markers.clear();
    }

    /**
     * 添加坐标点
     * @param _latitude
     * @param _longitude
     */
    private void addOverLayout(double _latitude, double _longitude) {//TODO 减少界面更新，地图跳跃
        LogTool.d("addOverLayout");
        //先清除图层
        removeMarkers();
        infos.clear();
        //loading car
        loadCarlistNear(_latitude, _longitude);
    }
}