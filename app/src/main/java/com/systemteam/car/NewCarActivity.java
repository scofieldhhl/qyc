package com.systemteam.car;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.activity.QRCodeScanActivity;
import com.systemteam.bean.Car;
import com.systemteam.bean.MyUser;
import com.systemteam.util.LocationManager;
import com.systemteam.util.LogTool;
import com.systemteam.util.Utils;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.systemteam.util.Constant.BUNDLE_KEY_CODE;
import static com.systemteam.util.Constant.REQUEST_CODE;

public class NewCarActivity extends BaseActivity {
    private TextView mTvCode;
    private String mCarNo;
    private Car mCar = null;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private double mLatitude; //纬度
    private double mLongitude;
    public MyLocationListenner myListener = new MyLocationListenner();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_car);
        mContext = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        initToolBar(NewCarActivity.this, R.string.new_car);
        mTvCode = (TextView) findViewById(R.id.tv_title_code);
        mMapView = (MapView) findViewById(R.id.id_bmapView);
        if (!Utils.isGpsOPen(this)) {
            Utils.showDialog(this);
            return;
        }
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        LocationClient mlocationClient = new LocationClient(this);
        mlocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);//设置onReceiveLocation()获取位置的频率
        option.setIsNeedAddress(true);//如想获得具体位置就需要设置为true
        mlocationClient.setLocOption(option);
        mlocationClient.start();
    }

    @Override
    protected void initData() {

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
        saveNewCar();
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
            mTvCode.setText(mCarNo);
            findViewById(R.id.btn_submit).setVisibility(View.VISIBLE);
        }
    }

    private void saveNewCar() {
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        checkExist();
        if(mCar == null){
            mCar = new Car();
            mCar.setCarNo(mCarNo);
            mCar.setPosition(new BmobGeoPoint(mLongitude, mLatitude));
            mCar.setAuthor(user);
            addSubscription(mCar.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    LogTool.d("SaveListener: " + s);
                    if(e==null){
                        toast("注册成功:" +s.toString());
                    }else{
                        loge(e);
                    }
                }
            }));
        }else if(mCar.getAuthor() != null){
            if(user.getObjectId().equalsIgnoreCase(mCar.getAuthor().getObjectId())){
                toast("该摇摇车已被激活");
            }else {
                Utils.showDialog(NewCarActivity.this, getString(R.string.tip),
                        "该摇摇车已被其他商户激活，有什么问题及时联系客服！");
            }
        }else {
            mCar.setCarNo(mCarNo);
            mCar.setPosition(new BmobGeoPoint(mLongitude, mLatitude));
            mCar.setAuthor(user);
            addSubscription(mCar.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        toast("成功:");
                    }else{
                        loge(e);
                    }
                }
            }));
        }

    }

    private void checkExist() {
        BmobQuery<Car> query = new BmobQuery<>();
        query.addWhereEqualTo("carNo", mCarNo);
        addSubscription(query.findObjects(new FindListener<Car>() {

            @Override
            public void done(List<Car> object, BmobException e) {
                if(e==null){
                    toast("查询密码成功:" + object.size());
                    if(object != null && object.size() > 0){
                        mCar = object.get(0);
                    }
                }else{
                    loge(e);
                }
            }
        }));
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
            builder.target(ll).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }
}