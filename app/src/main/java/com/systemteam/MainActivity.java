package com.systemteam;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
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
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.systemteam.activity.BaseActiveActivity;
import com.systemteam.activity.BreakActivity;
import com.systemteam.activity.MyRouteActivity;
import com.systemteam.activity.NavigationActivity;
import com.systemteam.activity.QRCodeScanActivity;
import com.systemteam.activity.RouteDetailActivity;
import com.systemteam.activity.SettingActivity;
import com.systemteam.activity.WalletActivity;
import com.systemteam.bean.BikeInfo;
import com.systemteam.bean.Car;
import com.systemteam.bean.MyUser;
import com.systemteam.callback.AllInterface;
import com.systemteam.car.MyCarActivity;
import com.systemteam.custom.LeftDrawerLayout;
import com.systemteam.fragment.LeftMenuFragment;
import com.systemteam.map.MyOrientationListener;
import com.systemteam.map.RouteLineAdapter;
import com.systemteam.user.UserInfoActivity;
import com.systemteam.util.Constant;
import com.systemteam.util.LocationManager;
import com.systemteam.util.LogTool;
import com.systemteam.util.Utils;
import com.systemteam.view.CatLoadingView;
import com.systemteam.welcome.WelcomeActivity;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import overlayutil.OverlayManager;
import overlayutil.WalkingRouteOverlay;

import static com.systemteam.bean.BikeInfo.infos;
import static com.systemteam.util.Constant.ACTION_BROADCAST_ACTIVE;
import static com.systemteam.util.Constant.BUNDLE_CAR;
import static com.systemteam.util.Constant.BUNDLE_CARNO;
import static com.systemteam.util.Constant.BUNDLE_KEY_CODE;
import static com.systemteam.util.Constant.BUNDLE_KEY_IS_ACTIVING;
import static com.systemteam.util.Constant.DISMISS_SPLASH;
import static com.systemteam.util.Constant.MAP_SCAN_SPAN;
import static com.systemteam.util.Constant.MSG_RESPONSE_SUCCESS;
import static com.systemteam.util.Constant.MSG_UPDATE_UI;
// 屏蔽预约功能后，获取services获取小车编号失败
// 关屏3min使用完成后，主界面没有推出使用中模式
// 首次安装使用，main界面没有小车编号
// 使用过程中申报故障后，main界面没有退出使用中模式
// BUG登出在登录，进入主界面后自动启动编号 2588

public class MainActivity extends BaseActiveActivity implements OnGetRoutePlanResultListener,
        AllInterface.OnMenuSlideListener, NavigationView.OnNavigationItemSelectedListener{

    private double currentLatitude, currentLongitude, changeLatitude, changeLongitude;
    private ImageView splash_img, btn_locale, btn_refresh, mIvScan;
    private TextView book_bt, cancel_book, end_route, current_addr, bike_distance, bike_time, bike_price;
    private LinearLayout bike_layout, bike_distance_layout, bike_info_layout, confirm_cancel_layout;
    private TextView bike_code, bike_sound, book_countdown, prompt,
            textview_time, textview_distance, textview_price, unlock;
    private long exitTime = 0;
    private View divider;
    private boolean isFirstIn;

    //自定义图标
    private BitmapDescriptor mIconLocation, dragLocationIcon, bikeIcon, nearestIcon;
    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
    //定位图层显示方式
    private MyLocationConfiguration.LocationMode locationMode;
    private BikeInfo bInfo;
    private String mCarNo;

    PlanNode startNodeStr, endNodeStr;
    int nodeIndex = -1, distance;
    WalkingRouteResult nowResultwalk = null;
    boolean useDefaultIcon = true, hasPlanRoute = false, isServiceLive = false;
    RouteLine routeLine = null;
    OverlayManager routeOverlay = null;
    LatLng currentLL;
    LeftDrawerLayout mLeftDrawerLayout;
    LeftMenuFragment mMenuFragment;
    View shadowView;
    // 定位相关
    LocationClient mlocationClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    private MyOrientationListener myOrientationListener;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private float mCurrentX;
    private boolean isFirstLoc = true; // 是否首次定位
    CatLoadingView mView;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            gotoWallet(null);
        } else if (id == R.id.nav_gallery) {
            gotoMycar(null);
        } else if (id == R.id.nav_slideshow) {
            gotoMyRoute(null);
        } else if (id == R.id.nav_manage) {
            gotoGuide(null);
        } else if (id == R.id.nav_share) {
            gotoSetting(null);
        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.id_drawerlayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final MainActivity theActivity = mActivity.get();
            super.handleMessage(msg);
            switch (msg.what){
                case DISMISS_SPLASH:
                    /*Animator animator = AnimatorInflater.loadAnimator(MainActivity.this, R.animator.splash);
                    animator.setTarget(splash_img);
                    animator.start();*/
                    break;
                case MSG_RESPONSE_SUCCESS:
                    List<Car> list = (List<Car>) msg.obj;
                    if(list != null && list.size() > 0){
                        List<BikeInfo> listBike = new ArrayList<>();
                        for(Car car : list){//TODO 两层for循环效率低
                            if(car != null){
                                listBike.add(new BikeInfo(car));
                            }
                        }
                        theActivity.addInfosOverlay(listBike);
                    }
                    break;
                case MSG_UPDATE_UI:
                    if(theActivity.isGaming){
                        if(theActivity.mView == null){
                            theActivity.mView = new CatLoadingView();
                            theActivity.mView.show(theActivity.getSupportFragmentManager(), "");
                        }
                        theActivity.bikeOnUsing();
                        theActivity.btn_locale.setEnabled(false);
                        theActivity.mIvScan.setEnabled(false);
                    }else {
                        if(theActivity.mView != null){
                            theActivity.mView.dismiss();
                        }
                        theActivity.backFromRouteDetail();
                        theActivity.btn_locale.setEnabled(true);
                        theActivity.mIvScan.setEnabled(true);
                    }
                    break;
            }
        }
    }
    private MyHandler mHandler = new MyHandler(this);
    private LocationReceiver mReciver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在Application的onCreate()不行，必须在activity的onCreate()中
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        checkSDK();
        LogTool.i("MainActivity---------onCreate---------------");
        mContext = this;
        checkLogin();
        initMap();
        initToolBar(this, R.string.bybike);
        initView();
        initData();
        /*isServiceLive = Utils.isServiceWork(this, getPackageName() + ".service.RouteService");
        if (isServiceLive)
            beginService();*/
    }

    protected void initToolBar(Activity act, int titleId) {
        mToolbar = (Toolbar) act.findViewById(R.id.toolbar);
        mToolbar.getVisibility();
        mToolbar.setTitle("");
        /*if (titleId == 0) {
            mToolbarTitle.setText("");
        } else {
            mToolbarTitle.setText(titleId);
        }*/
        /*if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_menu_white);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMenu();
            }
        });*/
    }

    private void checkLogin(){
        mUser = ((BikeApplication) this.getApplication()).getmUser();
        if(mUser == null){
            startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
        }
    }

    private void initMap() {
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.id_bmapView);
        mBaiduMap = mMapView.getMap();
        mMapView.showZoomControls(false);
        mMapView.showScaleControl(false);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                bike_layout.setVisibility(View.GONE);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        // 定位初始化
        mlocationClient = new LocationClient(this);
        mlocationClient.registerLocationListener(myListener);
        /*LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(MAP_SCAN_SPAN);//设置onReceiveLocation()获取位置的频率
        option.setIsNeedAddress(true);//如想获得具体位置就需要设置为true
        mlocationClient.setLocOption(option);*/
        initLocation();
        mlocationClient.start();
        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, null));
        myOrientationListener = new MyOrientationListener(this);
        //通过接口回调来实现实时方向的改变
        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX = x;
            }
        });
        myOrientationListener.start();
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        initMarkerClickEvent();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备

        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系

        option.setScanSpan(MAP_SCAN_SPAN);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要

        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps

        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果

        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”

        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到

        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死

        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集

        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要

        mlocationClient.setLocOption(option);
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // map view 销毁后不在处理新接收的位置
            if (bdLocation == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    .direction(mCurrentX)//设定图标方向     // 此处设置开发者获取到的方向信息，顺时针0-360
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            currentLatitude = bdLocation.getLatitude();
            currentLongitude = bdLocation.getLongitude();
            BikeApplication.mCurrentAddress = bdLocation.getAddrStr();
            BikeApplication.setPosition(currentLongitude, currentLatitude);
            current_addr.setText(bdLocation.getAddrStr());
            currentLL = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            LocationManager.getInstance().setCurrentLL(currentLL);
            LocationManager.getInstance().setAddress(bdLocation.getAddrStr());
            startNodeStr = PlanNode.withLocation(currentLL);
            //option.setScanSpan(MAP_SCAN_SPAN)，每隔10000ms这个方法就会调用一次，而有些我们只想调用一次，所以要判断一下isFirstLoc
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                //地图缩放比设置为18
                builder.target(ll).zoom(Constant.MAP_SCALING);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                changeLatitude = bdLocation.getLatitude();
                changeLongitude = bdLocation.getLongitude();
                if (!isServiceLive) {
                    addOverLayout(currentLatitude, currentLongitude);
                }
            }
            getLocationInfo(bdLocation);
        }
    }

    private void getLocationInfo(BDLocation location){
        //获取定位结果
        StringBuffer sb = new StringBuffer(256);

        sb.append("time : ");
        sb.append(location.getTime());    //获取定位时间

        sb.append("\nerror code : ");
        sb.append(location.getLocType());    //获取类型类型

        sb.append("\nlatitude : ");
        sb.append(location.getLatitude());    //获取纬度信息

        sb.append("\nlontitude : ");
        sb.append(location.getLongitude());    //获取经度信息

        sb.append("\nradius : ");
        sb.append(location.getRadius());    //获取定位精准度

        if (location.getLocType() == BDLocation.TypeGpsLocation){

            // GPS定位结果
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());    // 单位：公里每小时

            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());    //获取卫星数

            sb.append("\nheight : ");
            sb.append(location.getAltitude());    //获取海拔高度信息，单位米

            sb.append("\ndirection : ");
            sb.append(location.getDirection());    //获取方向信息，单位度

            sb.append("\naddr : ");
            sb.append(location.getAddrStr());    //获取地址信息

            sb.append("\ndescribe : ");
            sb.append("gps定位成功");

        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){

            // 网络定位结果
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());    //获取地址信息

            sb.append("\noperationers : ");
            sb.append(location.getOperators());    //获取运营商信息

            sb.append("\ndescribe : ");
            sb.append("网络定位成功");

        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

            // 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");

        } else if (location.getLocType() == BDLocation.TypeServerError) {

            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");

        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");

        }

        sb.append("\nlocationdescribe : ");
        sb.append(location.getLocationDescribe());    //位置语义化信息

        List<Poi> list = location.getPoiList();    // POI数据
        if (list != null) {
            sb.append("\npoilist size = : ");
            sb.append(list.size());
            for (Poi p : list) {
                sb.append("\npoi= : ");
                sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
            }
        }

//        LogTool.i("BaiduLocationApiDem : "+sb.toString());
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
    protected void initData() {
        mReciver = new LocationReceiver();
        registerBroadcast(ACTION_BROADCAST_ACTIVE, mReciver);
    }

    @Override
    protected void initView() {
//        new SpeechUtil(this).startSpeech("欢迎光临");
        splash_img = (ImageView) findViewById(R.id.splash_img);
//        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.guide_1);
        current_addr = (TextView) findViewById(R.id.current_addr);
        bike_layout = (LinearLayout) findViewById(R.id.bike_layout);
        bike_distance_layout = (LinearLayout) findViewById(R.id.bike_distance_layout);
        bike_info_layout = (LinearLayout) findViewById(R.id.bike_info_layout);
        confirm_cancel_layout = (LinearLayout) findViewById(R.id.confirm_cancel_layout);
        bike_time = (TextView) findViewById(R.id.bike_time);
        bike_distance = (TextView) findViewById(R.id.bike_distance);
        bike_price = (TextView) findViewById(R.id.bike_price);
        bike_price.setText(R.string.price);
        textview_time = (TextView) findViewById(R.id.textview_time);
        textview_distance = (TextView) findViewById(R.id.textview_distance);
        textview_price = (TextView) findViewById(R.id.textview_price);
        unlock = (TextView) findViewById(R.id.unlock);
        divider = (View) findViewById(R.id.divider);
        mIvScan = (ImageView) findViewById(R.id.iv_scan);

        bike_code = (TextView) findViewById(R.id.bike_code);
        bike_sound = (TextView) findViewById(R.id.bike_sound);
        book_countdown = (TextView) findViewById(R.id.book_countdown);
        prompt = (TextView) findViewById(R.id.prompt);
        cancel_book = (TextView) findViewById(R.id.cancel_book);
        //侧滑栏
        mLeftDrawerLayout = (LeftDrawerLayout) findViewById(R.id.id_drawerlayout);
        FragmentManager fm = getSupportFragmentManager();
        mMenuFragment = (LeftMenuFragment) fm.findFragmentById(R.id.id_container_menu);
        mLeftDrawerLayout.setOnMenuSlideListener(this);

        if (mMenuFragment == null) {
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment =
                    LeftMenuFragment.newInstance(mUser)).commit();
        }

        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.id_drawerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/

        shadowView = (View) findViewById(R.id.shadow);
        bike_sound.setOnClickListener(this);
        shadowView.setOnClickListener(this);
//        mLeftDrawerLayout.setListener(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dp2px(this, 50));
        layoutParams.setMargins(0, statusBarHeight, 0, 0);//4个参数按顺序分别是左上右下
//        title_layout.setLayoutParams(layoutParams);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LogTool.i("statusBarHeight---------------" + statusBarHeight);
        layoutParams2.setMargins(40, statusBarHeight + Utils.dp2px(MainActivity.this, 50), 0, 0);//4个参数按顺序分别是左上右下
//      person_layout.setLayoutParams(layoutParams2);

//        String price = "1元";
//        setSpannableStr(bike_price, price, 0, price.length() - 1);

        mBaiduMap = mMapView.getMap();

        mBaiduMap.setOnMapStatusChangeListener(changeListener);
        btn_locale = (ImageView) findViewById(R.id.btn_locale);
        btn_refresh = (ImageView) findViewById(R.id.btn_refresh);
        end_route = (TextView) findViewById(R.id.end_route);
        book_bt = (TextView) findViewById(R.id.book_bt);
        book_bt.setOnClickListener(this);
        cancel_book.setOnClickListener(this);
        btn_locale.setOnClickListener(this);
        btn_refresh.setOnClickListener(this);
        end_route.setOnClickListener(this);
        mMapView.setOnClickListener(this);
        dragLocationIcon = BitmapDescriptorFactory.fromResource(R.mipmap.drag_location);
        bikeIcon = BitmapDescriptorFactory.fromResource(R.drawable.bike_icon);
        mHandler.sendEmptyMessageDelayed(DISMISS_SPLASH, 3000);
    }

    public void getMyLocation() {
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMap.setMapStatus(msu);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.book_bt:
                bike_info_layout.setVisibility(View.VISIBLE);
                confirm_cancel_layout.setVisibility(View.VISIBLE);
                prompt.setVisibility(View.VISIBLE);
                bike_distance_layout.setVisibility(View.GONE);
                book_bt.setVisibility(View.GONE);
                bike_code.setText(bInfo.getName());
                countDownTimer.start();
                break;
            case R.id.cancel_book:
                cancelBook();
                break;
            case R.id.btn_locale:
                getMyLocation();
                if (routeOverlay != null)
                    routeOverlay.removeFromMap();
                LogTool.i("currentLatitude-----btn_locale--------" + currentLatitude);
                LogTool.i("currentLongitude-----btn_locale--------" + currentLongitude);
//                startNodeStr = PlanNode.withLocation(currentLL);
                addOverLayout(currentLatitude, currentLongitude);
                break;
            case R.id.btn_refresh:
//                Intent intent = new Intent(MainActivity.this, LocationDemo.class);
//                startActivity(intent);
                if (routeOverlay != null)
                    routeOverlay.removeFromMap();
                LogTool.i("changeLatitude-----btn_refresh--------" + changeLatitude);
                LogTool.i("changeLongitude-----btn_refresh--------" + changeLongitude);
                addOverLayout(changeLatitude, changeLongitude);
//                drawPlanRoute(endNodeStr);
                break;
            case R.id.end_route:
                toastDialog(MainActivity.this, false);
                break;
            case R.id.iv_menu:
            case R.id.menu_icon:
                openMenu();
                break;
            case R.id.bike_sound:
                if(checkNetworkAvailable(mContext) == Constant.NETWORK_STATUS_NO){
                    return;
                }
                if (checkBalance(mUser, MainActivity.this)) {
                    //TODO 检查车的状态是否是可用状态
                    beginService();
                }
                break;
            case R.id.shadow:
                closeMenu();
                break;
        }
    }

    private void cancelBook() {
        countDownTimer.cancel();
        bike_layout.setVisibility(View.GONE);
        bike_info_layout.setVisibility(View.GONE);
        confirm_cancel_layout.setVisibility(View.GONE);
        prompt.setVisibility(View.GONE);
        bike_distance_layout.setVisibility(View.VISIBLE);
        bike_distance_layout.setVisibility(View.VISIBLE);
        book_bt.setVisibility(View.VISIBLE);
        if (routeOverlay != null)
            routeOverlay.removeFromMap();
        MapStatus.Builder builder = new MapStatus.Builder();
        //地图缩放比设置为18
        builder.target(currentLL).zoom(Constant.MAP_SCALING);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    @Override
    public void onGetWalkingRouteResult(final WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            toast(getString(R.string.no_found));
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;

            if (result.getRouteLines().size() > 1) {
                nowResultwalk = result;

                MyTransitDlg myTransitDlg = new MyTransitDlg(MainActivity.this,
                        result.getRouteLines(),
                        RouteLineAdapter.Type.WALKING_ROUTE);
                myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                    public void onItemClick(int position) {
                        routeLine = nowResultwalk.getRouteLines().get(position);
                        WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);


                        routeOverlay = overlay;
                        //路线查询成功
                        try {
                            overlay.setData(nowResultwalk.getRouteLines().get(position));
                            overlay.addToMap();
                            overlay.zoomToSpan();
                        } catch (Exception e) {
                            e.printStackTrace();
                            toast(getString(R.string.error_line));
                        }
                    }

                });
                myTransitDlg.show();

            } else if (result.getRouteLines().size() == 1) {
                // 直接显示
                routeLine = result.getRouteLines().get(0);
                int totalDistance = routeLine.getDistance();
                int totalTime = routeLine.getDuration() / 60;
                bike_distance.setText(Utils.distanceFormatter(totalDistance));
                bike_time.setText(Utils.timeFormatter(totalTime));
                String distanceStr = Utils.distanceFormatter(totalDistance);
                String timeStr = Utils.timeFormatter(totalTime);
//                setSpannableStr(bike_time, timeStr, 0, timeStr.length() - 2);
//                setSpannableStr(bike_distance, distanceStr, 0, distanceStr.length() - 1);
                LogTool.i("totalDistance------------------" + totalDistance);

                WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
//                    mBaiduMap.setOnMarkerClickListener(overlay);
                routeOverlay = overlay;
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            } else {
                Log.d("route result", "结果数<0");
                return;
            }
        }
    }

    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
    }

    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
    }

    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
    }

    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
    }

    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {
    }

    @Override
    public void onMenuSlide(float offset) {
        shadowView.setVisibility(offset == 0 ? View.INVISIBLE : View.VISIBLE);
        int alpha = (int) Math.round(offset * 255 * 0.4);
//        String hex = Integer.toHexString(alpha).toUpperCase();
//        LogTool.i("color------------" + "#" + hex + "000000");
        shadowView.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
    }


    private BaiduMap.OnMapStatusChangeListener changeListener = new BaiduMap.OnMapStatusChangeListener() {
        public void onMapStatusChangeStart(MapStatus mapStatus) {
        }

        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            String _str = mapStatus.toString();
            String _regex = "target lat: (.*)\ntarget lng";
            String _regex2 = "target lng: (.*)\ntarget screen x";
            changeLatitude = Double.parseDouble(latlng(_regex, _str));
            changeLongitude = Double.parseDouble(latlng(_regex2, _str));
            LatLng changeLL = new LatLng(changeLatitude, changeLongitude);
            startNodeStr = PlanNode.withLocation(changeLL);
//            LogTool.i("changeLatitude-----change--------" + changeLatitude);
//            LogTool.i("changeLongitude-----change--------" + changeLongitude);
        }

        public void onMapStatusChange(MapStatus mapStatus) {
        }
    };

    private String latlng(String regexStr, String str) {
        Pattern pattern = Pattern.compile(regexStr);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            str = matcher.group(1);
        }
        return str;
    }

    public void addInfosOverlay(List<BikeInfo> infos) {
        LatLng latLng = null;
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
        mBaiduMap.setMapStatus(u);
    }

    /**
     * 添加坐标点
     * @param _latitude
     * @param _longitude
     */
    private void addOverLayout(double _latitude, double _longitude) {
        //先清除图层
        mBaiduMap.clear();
        mlocationClient.requestLocation();
        // 定义Maker坐标点
        LatLng point = new LatLng(_latitude, _longitude);
        // 构建MarkerOption，用于在地图上添加Marker
        MarkerOptions options = new MarkerOptions().position(point)
                .icon(dragLocationIcon);
        // 在地图上添加Marker，并显示
        mBaiduMap.addOverlay(options);
        infos.clear();
        //loading car
        loadCarlistNear(_latitude, _longitude);
        infos.add(new BikeInfo(_latitude - new Random().nextInt(5) * 0.0005,
                _longitude - new Random().nextInt(5) * 0.0005, R.mipmap.bike_icon, "001", "100米", "1分钟"));
        infos.add(new BikeInfo(_latitude - new Random().nextInt(5) * 0.0005,
                _longitude - new Random().nextInt(5) * 0.0005, R.mipmap.bike_icon, "002", "200米", "2分钟"));
        infos.add(new BikeInfo(_latitude - new Random().nextInt(5) * 0.0005,
                _longitude - new Random().nextInt(5) * 0.0005, R.mipmap.bike_icon, "003", "300米", "3分钟"));
        infos.add(new BikeInfo(_latitude - new Random().nextInt(5) * 0.0005,
                _longitude - new Random().nextInt(5) * 0.0005, R.mipmap.bike_icon, "004", "400米", "4分钟"));
        BikeInfo bikeInfo = new BikeInfo(_latitude - 0.0005, _longitude - 0.0005, R.mipmap.bike_icon, "005",
                "50米", "0.5分钟");
        infos.add(bikeInfo);
        addInfosOverlay(infos);
//        initNearestBike(bikeInfo, new LatLng(_latitude - 0.0005, _longitude - 0.0005));
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

    private void initMarkerClickEvent() {
        // 对Marker的点击
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                // 获得marker中的数据
                if (marker != null && marker.getExtraInfo() != null) {
                    BikeInfo bikeInfo = (BikeInfo) marker.getExtraInfo().get("info");
                    if (bikeInfo != null)
                        updateBikeInfo(bikeInfo);
                }
                return true;
            }
        });
    }

    private void initNearestBike(final BikeInfo bikeInfo, LatLng ll) {
        ImageView nearestIcon = new ImageView(getApplicationContext());
        nearestIcon.setImageResource(R.mipmap.nearest_icon);
        InfoWindow.OnInfoWindowClickListener listener = null;
        listener = new InfoWindow.OnInfoWindowClickListener() {
            public void onInfoWindowClick() {
                updateBikeInfo(bikeInfo);
                mBaiduMap.hideInfoWindow();
            }
        };
        InfoWindow mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(nearestIcon), ll, -108, listener);
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    private void updateBikeInfo(BikeInfo bikeInfo) {

        if (!hasPlanRoute) {
            /*bike_layout.setVisibility(View.VISIBLE);
            bike_time.setText(bikeInfo.getTime());
            bike_distance.setText(bikeInfo.getDistance());*/
            bInfo = bikeInfo;
            endNodeStr = PlanNode.withLocation(new LatLng(bikeInfo.getLatitude(), bikeInfo.getLongitude()));
            drawPlanRoute(endNodeStr);
        }
    }

    private void drawPlanRoute(PlanNode endNodeStr) {
        if (routeOverlay != null)
            routeOverlay.removeFromMap();
        if (endNodeStr != null) {

            LogTool.i("changeLatitude-----startNode--------" + startNodeStr.getLocation().latitude);
            LogTool.i("changeLongitude-----startNode--------" + startNodeStr.getLocation().longitude);
            mSearch.walkingSearch((new WalkingRoutePlanOption()).from(startNodeStr).to(endNodeStr));

        }
    }

    private CountDownTimer countDownTimer = new CountDownTimer(10 * 60 * 1000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            book_countdown.setText(millisUntilFinished / 60000 + "分" + ((millisUntilFinished / 1000) % 60) + "秒");
        }

        @Override
        public void onFinish() {
            book_countdown.setText(R.string.end_book);
            toast(getString(R.string.cancel_book_toast));
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(checkUser(this)){
            switch (item.getItemId()){
                case R.id.id_portrait:
                    Utils.showDialog(MainActivity.this, getString(R.string.tip), getString(R.string.break_portrait));
                    break;
                case R.id.id_lock:
                    Intent intent = new Intent(MainActivity.this, BreakActivity.class);
                    intent.putExtra(Constant.BUNDLE_TYPE_MENU, Constant.BREAK_TYPE_LOCK);
                    Bundle extras = new Bundle();
                    if(bInfo != null){//TODO binfo null
                        extras.putSerializable(BUNDLE_CAR, bInfo.getCar());
                    }else {
                        extras.putString(BUNDLE_CARNO, mCarNo);
                    }
                    extras.putBoolean(BUNDLE_KEY_IS_ACTIVING, isGaming);
                    intent.putExtras(extras);
                    startActivityForResult(intent, Constant.REQUEST_CODE_BREAK);
                    break;
                case R.id.id_break:
                    Intent intentBreak = new Intent(MainActivity.this, BreakActivity.class);
                    intentBreak.putExtra(Constant.BUNDLE_TYPE_MENU, Constant.BREAK_TYPE_BREAK);
                    Bundle bundle = new Bundle();
                    if(bInfo != null) {//TODO binfo null
                        bundle.putSerializable(BUNDLE_CAR, bInfo.getCar());
                    }else {
                        bundle.putString(BUNDLE_CARNO, mCarNo);
                    }
                    bundle.putBoolean(BUNDLE_KEY_IS_ACTIVING, isGaming);
                    intentBreak.putExtras(bundle);
                    startActivityForResult(intentBreak, Constant.REQUEST_CODE_BREAK);
                    break;
            }
        }
        return true;
    }

        // 供路线选择的Dialog
    class MyTransitDlg extends Dialog {

        private List<? extends RouteLine> mtransitRouteLines;
        private ListView transitRouteList;
        private RouteLineAdapter mTransitAdapter;

        OnItemInDlgClickListener onItemInDlgClickListener;

        public MyTransitDlg(Context context, int theme) {
            super(context, theme);
        }

        public MyTransitDlg(Context context, List<? extends RouteLine> transitRouteLines, RouteLineAdapter.Type
                type) {
            this(context, 0);
            mtransitRouteLines = transitRouteLines;
            mTransitAdapter = new RouteLineAdapter(context, mtransitRouteLines, type);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transit_dialog);

            transitRouteList = (ListView) findViewById(R.id.transitList);
            transitRouteList.setAdapter(mTransitAdapter);

            transitRouteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    onItemInDlgClickListener.onItemClick( position);
//                    mBtnPre.setVisibility(View.VISIBLE);
//                    mBtnNext.setVisibility(View.VISIBLE);
//                    dismiss();

                }
            });
        }

        public void setOnItemInDlgClickLinster(OnItemInDlgClickListener itemListener) {
            onItemInDlgClickListener = itemListener;
        }
    }

    // 响应DLg中的List item 点击
    interface OnItemInDlgClickListener {
        void onItemClick(int position);
    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
//            if (useDefaultIcon) {
            return BitmapDescriptorFactory.fromResource(R.mipmap.transparent_icon);
//            }
//            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
//            if (useDefaultIcon) {
            return BitmapDescriptorFactory.fromResource(R.mipmap.transparent_icon);
//            }
//            return null;
        }
    }

    public void gotoCodeUnlock(View view) {
        if(!checkUser(this))
            return;
        if(!checkBalance(BmobUser.getCurrentUser(MyUser.class), MainActivity.this)){
            return;
        }
//        startActivity(new Intent(this, CodeUnlockActivity.class));
        Intent intent = new Intent(this, QRCodeScanActivity.class);
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

    public void gotoNavigation(View view) {
        if(!checkUser(this))
            return;
        startActivity(new Intent(this, NavigationActivity.class));
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

    protected void onRestart() {
        super.onRestart();
        mBaiduMap.setMyLocationEnabled(true);
        mlocationClient.start();
        myOrientationListener.start();
        mlocationClient.requestLocation();
        isServiceLive = Utils.isServiceWork(this, getPackageName() + ".service.RouteService");
        LogTool.i("MainActivity------------onRestart------------------");
        /*if (CodeUnlockActivity.unlockSuccess || isServiceLive) {
            beginService();
        }*/
        if (RouteDetailActivity.completeRoute || !isServiceLive)
            backFromRouteDetail();
    }

    private void backFromRouteDetail() {
        isFirstIn = true;
//        mTvTitle.setText(getString(R.string.bybike));
        textview_time.setText(getString(R.string.foot));
        textview_distance.setText(getString(R.string.distance));
        textview_price.setText(getString(R.string.price));

        textview_time.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textview_distance.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textview_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        bike_time.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        bike_distance.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        bike_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        bike_layout.setVisibility(View.GONE);
        prompt.setVisibility(View.GONE);
        /*current_addr.setVisibility(View.VISIBLE);
//        mIvMenu.setVisibility(View.VISIBLE);
        book_bt.setVisibility(View.VISIBLE);
//        unlock.setVisibility(View.VISIBLE);
        mIvScan.setVisibility(View.VISIBLE);
        divider.setVisibility(View.VISIBLE);
        btn_refresh.setVisibility(View.VISIBLE);
        btn_locale.setVisibility(View.VISIBLE);*/
        //隐藏使用时长显示
        current_addr.setVisibility(View.GONE);
//        mIvMenu.setVisibility(View.VISIBLE);
        book_bt.setVisibility(View.GONE);
//        unlock.setVisibility(View.VISIBLE);
        mIvScan.setVisibility(View.VISIBLE);
        divider.setVisibility(View.GONE);
        btn_refresh.setVisibility(View.GONE);
        btn_locale.setVisibility(View.VISIBLE);

        end_route.setVisibility(View.GONE);
        mMapView.showZoomControls(false);

        getMyLocation();
        if (routeOverlay != null)
            routeOverlay.removeFromMap();
        addOverLayout(currentLatitude, currentLongitude);
    }

    private void beginService() {
        if (!Utils.isGpsOPen(this)) {
            Utils.showDialog(this);
            return;
        }

        if(bInfo != null){
            Car car = bInfo.getCar();
            if(car == null){//TODO for TEST
               car = new Car();
                car.setCarNo(bInfo.getCarNo());
            }
//            startRouteService(this, car);
            checkCarAvaliable(MainActivity.this, car);
        }else {
            toast(getString(R.string.break_car_no));
            return;
        }
        MyLocationConfiguration configuration
                = new MyLocationConfiguration(locationMode, true, mIconLocation);
        //设置定位图层配置信息，只有先允许定位图层后设置定位图层配置信息才会生
    }

    private void bikeOnUsing(){
        //        mTvTitle.setText(getString(R.string.routing));
        textview_time.setText(getString(R.string.bike_time));
        textview_distance.setText(getString(R.string.bike_distance));
        textview_price.setText(getString(R.string.bike_price));
        prompt.setText(getString(R.string.routing_prompt));

        textview_time.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textview_distance.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textview_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        bike_time.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        bike_distance.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        bike_price.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        prompt.setVisibility(View.GONE);
        bike_layout.setVisibility(View.GONE);
        current_addr.setVisibility(View.GONE);
//        mIvMenu.setVisibility(View.GONE);
//        unlock.setVisibility(View.GONE);
        mIvScan.setVisibility(View.VISIBLE);

        divider.setVisibility(View.GONE);
        btn_refresh.setVisibility(View.GONE);

        countDownTimer.cancel();
        bike_info_layout.setVisibility(View.GONE);
        confirm_cancel_layout.setVisibility(View.GONE);
        bike_distance_layout.setVisibility(View.GONE);
        book_bt.setVisibility(View.GONE);
        if (routeOverlay != null)
            routeOverlay.removeFromMap();

        btn_locale.setVisibility(View.VISIBLE);
        bike_info_layout.setVisibility(View.GONE);
        end_route.setVisibility(View.GONE);
        mMapView.showZoomControls(false);
        mBaiduMap.clear();
        if (isServiceLive)
            mlocationClient.requestLocation();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        LogTool.d("onNewIntent");
        super.onNewIntent(intent);
        checkCarExist(this, intent.getStringExtra(BUNDLE_KEY_CODE));//启动使用
    }

    @Override
    protected void onResume() {
        LogTool.d("onResume");
        mMapView.onResume();
        super.onResume();
        MobclickAgent.onPageStart("MainScreen");
        MobclickAgent.onResume(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        MobclickAgent.onPageEnd("MainScreen");
        MobclickAgent.onPause(this);
        closeMenu();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogTool.i("MainActivity------------onStart------------------");
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
        mlocationClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        countDownTimer.cancel();
        isFirstIn = true;
        unregisterReceiver(mReciver);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (bike_layout.getVisibility() == View.VISIBLE) {
                if (!Utils.isServiceWork(this, getPackageName() + ".service.RouteService"))
                    cancelBook();
                return true;
            }

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
            if (Utils.isTopActivity(context)) {
                String price = intent.getStringExtra("totalPrice");
                bike_time.setText(time);
                bike_distance.setText(distance);
                bike_price.setText(price);
            } else {
                LogTool.i("MainActivity-------TopActivity---------false");
            }
            mHandler.sendEmptyMessage(MSG_UPDATE_UI);
        }
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
            /*case Constant.REQUEST_CODE_WELCOME:
                if(resultCode == RESULT_CANCELED){
                    finish();
                    System.exit(0);
                }else {
                    Bundle b=data.getExtras(); //data为B中回传的Intent
                    String str=b.getString("str1");//str即为回传的值
                }
                break;*/
            default:
                break;
        }
        checkBackFromBreak(requestCode, data);
    }

}