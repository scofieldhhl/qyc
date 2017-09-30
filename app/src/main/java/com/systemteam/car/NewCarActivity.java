package com.systemteam.car;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.MapView;
import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.activity.QRCodeScanActivity;
import com.systemteam.bean.Car;
import com.systemteam.bean.MyUser;
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

public class NewCarActivity extends BaseActivity {
    private TextView mTvCode;
    private String mCarNo;
    private Car mCar = null;
    private MapView mMapView;
//    private BaiduMap mBaiduMap;
    private double mLatitude; //纬度
    private double mLongitude;
    private Button mBtnSubmit;
//    public MyLocationListenner myListener = new MyLocationListenner();
//    private LocationClient mlocationClient;

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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时销毁定位
//        mlocationClient.stop();
        // 关闭定位图层
//        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
    }

    @Override
    protected void initView() {
        initToolBar(NewCarActivity.this, R.string.new_car);
        mTvCode = (TextView) findViewById(R.id.tv_title_code);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);
        mMapView = (MapView) findViewById(R.id.id_bmapView);
        mMapView.setVisibility(View.GONE);
        if (!Utils.isGpsOPen(this)) {
            Utils.showDialog(this);
            return;
        }
        /*mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mlocationClient = new LocationClient(this);
        mlocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(Constant.MAP_SCAN_SPAN);//设置onReceiveLocation()获取位置的频率
        option.setIsNeedAddress(true);//如想获得具体位置就需要设置为true
        mlocationClient.setLocOption(option);
        mlocationClient.start();*/
    }

    @Override
    protected void initData() {

    }

    private void initInfo(String carNo){
        if(carNo != null && !TextUtils.isEmpty(carNo)){
            mTvCode.setText(getString(R.string.carNo_scan, mCarNo));
            mBtnSubmit.setVisibility(View.VISIBLE);
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

    /**
     * 定位SDK监听函数
     */
    /*public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            // map view 销毁后不在处理新接收的位置
            if (bdLocation == null || mMapView == null) {
                return;
            }
            mLatitude = bdLocation.getLatitude();
            mLongitude = bdLocation.getLongitude();
            LogTool.d("mLongitude: " + mLongitude + "  mLatitude: " + mLatitude );
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            LocationManager.getInstance().setAddress(bdLocation.getAddrStr());
            LatLng ll = new LatLng(bdLocation.getLatitude(),
                    bdLocation.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            //地图缩放比设置为18
            builder.target(ll).zoom(Constant.MAP_SCALING);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }*/
}