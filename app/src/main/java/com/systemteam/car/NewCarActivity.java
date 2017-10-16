package com.systemteam.car;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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
import com.systemteam.BaseActivity;
import com.systemteam.BikeApplication;
import com.systemteam.R;
import com.systemteam.activity.QRCodeScanActivity;
import com.systemteam.bean.Car;
import com.systemteam.bean.MyUser;
import com.systemteam.gdmap.lib.LocationTask;
import com.systemteam.gdmap.lib.OnLocationGetListener;
import com.systemteam.gdmap.lib.PositionEntity;
import com.systemteam.gdmap.lib.RegeocodeTask;
import com.systemteam.gdmap.lib.RouteTask;
import com.systemteam.util.LogTool;
import com.systemteam.util.Utils;

import java.lang.ref.WeakReference;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.systemteam.util.Constant.BUNDLE_KEY_CODE;
import static com.systemteam.util.Constant.MSG_RESPONSE_SUCCESS;
import static com.systemteam.util.Constant.MSG_UPDATE_UI;
import static com.systemteam.util.Constant.REQUEST_CODE;

public class NewCarActivity extends BaseActivity implements AMap.OnCameraChangeListener,
        AMap.OnMapLoadedListener, OnLocationGetListener {
    private TextView mTvCode;
    private String mCarNo;
    private Car mCar = null;
    private double mLatitude; //纬度
    private double mLongitude;
    private Button mBtnSubmit;
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
    private BitmapDescriptor initBitmap,moveBitmap,smallIdentificationBitmap,bigIdentificationBitmap;//定位圆点、可移动、所有标识（车）

    private static class MyHandler extends Handler {
        private WeakReference<NewCarActivity> mActivity;

        public MyHandler(NewCarActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final NewCarActivity theActivity = mActivity.get();
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_RESPONSE_SUCCESS:
                    theActivity.saveNewCar();
                    break;
                case MSG_UPDATE_UI:
                    theActivity.initInfo(theActivity.mCarNo);
                    break;
            }
        }
    }

    private MyHandler mHandler = new MyHandler(this);
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_car);
        mContext = this;
        initView();
        initData();

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        initBitmap();
        initAMap();
        initLocation();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mMapView = null;
        mLocationTask.onDestroy();
    }

    @Override
    protected void initView() {
        initToolBar(NewCarActivity.this, R.string.new_car);
        mTvCode = (TextView) findViewById(R.id.tv_title_code);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);
        if (!Utils.isGpsOPen(this)) {
            Utils.showDialog(this);
            return;
        }
    }

    @Override
    protected void initData() {

    }

    private void initInfo(String carNo){
        if(carNo != null && !TextUtils.isEmpty(carNo)){
            mTvCode.setText(getString(R.string.carNo_scan, mCarNo));
            mBtnSubmit.setVisibility(View.VISIBLE);
            mMapView.setVisibility(View.VISIBLE);
            mMapView.setVisibility(View.VISIBLE);
        }else {
            mCar = null;
            mTvCode.setText(getString(R.string.scan_break_hint1));
            mBtnSubmit.setVisibility(View.GONE);
            mMapView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_icon:
                finish();
                break;
        }
    }

    public void doSubmit(View view) {
        mProgressHelper.showProgressDialog(getString(R.string.submiting));
        checkExist(mCarNo);
    }

    public void gotoScan(View view) {
        startActivityForResult(new Intent(NewCarActivity.this, QRCodeScanActivity.class),
                REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data !=null) {
            mCarNo = data.getStringExtra(BUNDLE_KEY_CODE);
            mHandler.sendEmptyMessage(MSG_UPDATE_UI);
        }
    }

    private void saveNewCar() {
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        if(mCar == null){
            mCar = new Car();
            mCar.setCarNo(mCarNo);
            mCar.setPosition(new BmobGeoPoint(mLongitude, mLatitude));
            mCar.setAuthor(user);
            addSubscription(mCar.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    mProgressHelper.dismissProgressDialog();
                    if(e==null){
                        toast(getString(R.string.add_success));
                        mCarNo = null;
                        mHandler.sendEmptyMessage(MSG_UPDATE_UI);
                    }else{
                        loge(e);
                        toast(getString(R.string.submit_faile));
                    }
                }
            }));
        }else if(mCar.getAuthor() != null){
            mProgressHelper.dismissProgressDialog();
            if(user.getObjectId().equalsIgnoreCase(mCar.getAuthor().getObjectId())){
                toast(getString(R.string.have_added));
            }else {
                Utils.showDialog(NewCarActivity.this, getString(R.string.tip),
                        getString(R.string.own_other));
            }
        }else {
            mCar.setCarNo(mCarNo);
            mCar.setPosition(new BmobGeoPoint(mLongitude, mLatitude));
            mCar.setAuthor(user);
            addSubscription(mCar.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    mProgressHelper.dismissProgressDialog();
                    if(e==null){
                        toast(getString(R.string.activate_success));
                        mCarNo = null;
                        mHandler.sendEmptyMessage(MSG_UPDATE_UI);
                    }else{
                        loge(e);
                        toast(getString(R.string.submit_faile));
                    }
                }
            }));
        }

    }

    private void checkExist(String carNo) {
        BmobQuery<Car> query = new BmobQuery<>();
        query.addWhereEqualTo("carNo", carNo);
        addSubscription(query.findObjects(new FindListener<Car>() {

            @Override
            public void done(List<Car> object, BmobException e) {
                if(e==null){
                    if(object != null && object.size() > 0){
                        mCar = object.get(0);
                    }
                    mHandler.sendEmptyMessage(MSG_RESPONSE_SUCCESS);
                }else{
                    loge(e);
                    toast(getString(R.string.response_faile));
                    mProgressHelper.dismissProgressDialog();
                }
            }
        }));
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
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.getUiSettings().setGestureScaleByMapCenter(true);
//            aMap.getUiSettings().setScrollGesturesEnabled(false);
            aMap.setOnMapLoadedListener(this);
            aMap.setOnCameraChangeListener(this);
            // 绑定 Marker 被点击事件
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
        mLatitude = cameraPosition.target.latitude;
        mLongitude = cameraPosition.target.longitude;
//        mStartPosition = cameraPosition.target;
        if(mIsFirst) {
            createInitialPosition(cameraPosition.target.latitude, cameraPosition.target.longitude);
            createMovingPosition();
            mIsFirst = false;
            return;
        }
        if (mInitialMark != null) {
            mInitialMark.setToTop();
        }
        if (mPositionMark != null) {
            mPositionMark.setToTop();
        }
        toast(getString(R.string.location_changed));
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
}