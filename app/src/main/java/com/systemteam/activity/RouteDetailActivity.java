package com.systemteam.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.bean.RoutePoint;

import java.util.ArrayList;
import java.util.List;

import static com.systemteam.util.Constant.MAP_SCAN_SPAN;

/**
 * @Description 使用完成后点击toolbar返回值Main界面，Main界面没有退出使用中模式
 * @author scofield.hhl@gmail.com
 * @time 2017/8/18
 */
public class RouteDetailActivity extends BaseActivity {

    private MapView route_detail_mapview;
    BaiduMap routeBaiduMap;
    private BitmapDescriptor startBmp, endBmp;
    private MylocationListener mlistener;
    LocationClient mlocationClient;
    TextView total_time, total_distance, total_price;
    public ArrayList<RoutePoint> routePoints;
    public static boolean completeRoute = false;
    String time, distance, price, routePointsStr;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);
        mContext = this;
        initView();
        initData();
    }

    private void initMap() {
        mlocationClient = new LocationClient(this);
//        mlistener = new MylocationListener();
//        mlocationClient.registerLocationListener(mlistener);

        LocationClientOption mOption = new LocationClientOption();
        //设置坐标类型
        mOption.setCoorType("bd09ll");
        //设置是否需要地址信息，默认为无地址
        mOption.setIsNeedAddress(true);
        //设置是否打开gps进行定位
        mOption.setOpenGps(true);
        //设置扫描间隔，单位是毫秒 当<1000(1s)时，定时定位无效
        mOption.setScanSpan(MAP_SCAN_SPAN);
        //设置 LocationClientOption
        mlocationClient.setLocOption(mOption);
        if (!mlocationClient.isStarted()) {
            mlocationClient.start();
        }
        UiSettings settings = routeBaiduMap.getUiSettings();
        settings.setScrollGesturesEnabled(true);
    }

    @Override
    public void onClick(View view) {

    }

    public class MylocationListener implements BDLocationListener {
        //定位请求回调接口
        private boolean isFirstIn = true;

        //定位请求回调函数,这里面会得到定位信息
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //判断是否为第一次定位,是的话需要定位到用户当前位置
            if (isFirstIn) {
                Log.d("gaolei", "onReceiveLocation----------RouteDetail-----" + bdLocation.getAddrStr());
//                LatLng currentLL = new LatLng(bdLocation.getLatitude(),
//                        bdLocation.getLongitude());
////                startNodeStr = PlanNode.withLocation(currentLL);
//                MapStatus.Builder builder = new MapStatus.Builder();
//                builder.target(currentLL).zoom(18.0f);
//                routeBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                isFirstIn = false;

            }
        }
    }

    private void addOverLayout(LatLng startPosition, LatLng endPosition) {
        //先清除图层
//        mBaiduMap.clear();
        // 定义Maker坐标点
        // 构建MarkerOption，用于在地图上添加Marker
        MarkerOptions options = new MarkerOptions().position(startPosition)
                .icon(startBmp);
        // 在地图上添加Marker，并显示
        routeBaiduMap.addOverlay(options);
        MarkerOptions options2 = new MarkerOptions().position(endPosition)
                .icon(endBmp);
        // 在地图上添加Marker，并显示
        routeBaiduMap.addOverlay(options2);
    }

    public void onDestroy() {
        super.onDestroy();
        routeBaiduMap.setMyLocationEnabled(false);
        mlocationClient.stop();
        completeRoute = false;
    }

    @Override
    protected void initView() {
        initToolBar(RouteDetailActivity.this, R.string.route_detail);
        route_detail_mapview = (MapView) findViewById(R.id.route_detail_mapview);
        total_time = (TextView) findViewById(R.id.total_time);
        total_distance = (TextView) findViewById(R.id.total_distance);
        total_price = (TextView) findViewById(R.id.total_pricce);
        routeBaiduMap = route_detail_mapview.getMap();
        route_detail_mapview.showZoomControls(false);
        startBmp = BitmapDescriptorFactory.fromResource(R.mipmap.route_start);
        endBmp = BitmapDescriptorFactory.fromResource(R.mipmap.route_end);
        initMap();
    }

    protected void initToolBar(Activity act, int titleId) {
        mToolbar = (Toolbar) act.findViewById(R.id.toolbar);
        mToolbarTitle = (TextView) act.findViewById(R.id.toolbar_title);
        mToolbar.getVisibility();
        mToolbar.setTitle("");
        if (titleId == 0) {
            mToolbarTitle.setText("");
        } else {
            mToolbarTitle.setTextColor(ContextCompat.getColor(act, R.color.white));
            mToolbarTitle.setText(titleId);
        }


        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.mipmap.return_icon);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeRoute = true;
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        String time = intent.getStringExtra("totalTime");
        String distance = intent.getStringExtra("totalDistance");
        String price = intent.getStringExtra("totalPrice");
        routePointsStr = intent.getStringExtra("routePoints");
        routePoints = new Gson().fromJson(routePointsStr, new TypeToken<List<RoutePoint>>() {
        }.getType());


        List<LatLng> points = new ArrayList<LatLng>();

        for (int i = 0; i < routePoints.size(); i++) {
            RoutePoint point = routePoints.get(i);
            LatLng latLng = new LatLng(point.getRouteLat(), point.getRouteLng());
            points.add(latLng);
        }
        if (points.size() > 2) {
            OverlayOptions ooPolyline = new PolylineOptions().width(10)
                    .color(0xFF36D19D).points(points);
            routeBaiduMap.addOverlay(ooPolyline);
            RoutePoint startPoint = routePoints.get(0);
            LatLng startPosition = new LatLng(startPoint.getRouteLat(), startPoint.getRouteLng());

            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(startPosition).zoom(18.0f);
            routeBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

            RoutePoint endPoint = routePoints.get(routePoints.size() - 1);
            LatLng endPosition = new LatLng(endPoint.getRouteLat(), endPoint.getRouteLng());
            addOverLayout(startPosition, endPosition);
        }

//        total_time.setText(getString(R.string.bike_time)+ " ：" +
//                getString(R.string.cost_time, String.valueOf(time)));
        total_time.setText(getString(R.string.bike_time)+ " ：" + time);
//        total_distance.setText(getString(R.string.bike_distance)+ " ：" +
//                getString(R.string.cost_distance, String.valueOf(distance)));
        total_distance.setText(getString(R.string.bike_distance)+ " ：" + String.valueOf(distance));
        total_price.setText(getString(R.string.bike_price)+ " ：" +
                getString(R.string.cost_num, String.valueOf(price)));
    }

    public void finishActivity(View view) {
        completeRoute = true;
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            completeRoute = true;
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
