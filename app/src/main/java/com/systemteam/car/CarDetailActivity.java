package com.systemteam.car;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.adapter.MyCarAdapter;
import com.systemteam.adapter.MyRouteAdapter;
import com.systemteam.adapter.MyRouteDividerDecoration;
import com.systemteam.bean.Car;
import com.systemteam.bean.UseRecord;
import com.systemteam.fragment.ChartFragment;
import com.systemteam.util.Constant;
import com.systemteam.util.LogTool;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.systemteam.util.Constant.QUERY_LIMIT_DEFAULT;
import static com.systemteam.util.Constant.REQUEST_KEY_BY_CARNO;

//TODO 车辆信息管理，车收益统计（天／月），每辆车收益统计
public class CarDetailActivity extends BaseActivity implements MyCarAdapter.OnItemClickListener,
        MyCarAdapter.OnItemLongClickListener{
    XRecyclerView routeRecyclerView;
    MyRouteAdapter routeAdapter;
    List<Object> routeList;
    private boolean isChartShow = false;
    private Car mCar;
    FrameLayout mLayoutChart;
    private ChartFragment mChartFragment;
    private Menu mMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);
        mContext = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        initToolBar(CarDetailActivity.this, R.string.detail_car_title);
        mToolbar.inflateMenu(R.menu.menu_car_detail);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings:
                        isChartShow = !isChartShow;
                        if(isChartShow){
                            mLayoutChart.setVisibility(View.VISIBLE);
                            if(mChartFragment == null){
                                mChartFragment = new ChartFragment(mCar);
                            }
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.ll_content, mChartFragment)
                                    .commit();
                            item.setTitle(R.string.action_detail);
                            /*mMenu.getItem(1).setVisible(true);
                            mMenu.getItem(2).setVisible(true);*/
                        }else {
                            mLayoutChart.setVisibility(View.GONE);
                            item.setTitle(R.string.action_chart);
                            /*mMenu.getItem(1).setVisible(false);
                            mMenu.getItem(2).setVisible(false);*/
                        }
                        //判断当前屏幕方向
                        if(getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                            //切换竖屏
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        }else{
                            //切换横屏
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
                        break;
                }
                return true;
            }
        });
        mLayoutChart = (FrameLayout) findViewById(R.id.ll_content);
        mLayoutChart.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if(intent != null){
            Bundle bundle = intent.getExtras();
            mCar = (Car) bundle.getSerializable(Constant.BUNDLE_CAR);
            if(mCar != null){
                initList();
                LogTool.d("mcar : " + mCar.getCarNo());
            }else {
                LogTool.e("mcar == null");
            }
        }

        routeRecyclerView = (XRecyclerView) findViewById(R.id.recyclerview_route);
//        no_route = (TextView) findViewById(R.id.no_route);
        routeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

//        routeList = getAllPoints();
        routeList = new ArrayList<>();
        routeAdapter = new MyRouteAdapter(mContext, routeList);
        routeAdapter.setOnClickListener(CarDetailActivity.this);
        routeAdapter.setOnLongClickListener(CarDetailActivity.this);
        routeRecyclerView.setAdapter(routeAdapter);
        routeRecyclerView.addItemDecoration(new MyRouteDividerDecoration(1));

        routeRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        routeRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallScale);
        routeRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
        routeRecyclerView.setPullRefreshEnabled(false);
//        View header = LayoutInflater.from(this).inflate(R.layout.recyclerview_header,
//                (ViewGroup)findViewById(android.R.id.content),false);
//        routeRecyclerView.addHeaderView(header);

        routeRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
//                Toast.makeText(MyRouteActivity.this, "onRefresh", Toast.LENGTH_SHORT).show();
                routeRecyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {
//                Toast.makeText(MyRouteActivity.this, "onLoadMore", Toast.LENGTH_SHORT).show();
                loadPage();
                routeRecyclerView.loadMoreComplete();
                routeAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(routeList != null){
            routeList.clear();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_car_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View view) {

    }

    public List<Car> loadPage() {
        return  null;

    }

    @Override
    public void onItemClick(View v, int position) {

    }

    private void initList() {
        mProgressHelper.showProgressDialog(getString(R.string.initing));
        BmobQuery<UseRecord> query = new BmobQuery<>();
        query.addWhereEqualTo(REQUEST_KEY_BY_CARNO, mCar.getCarNo());
        query.order("-createdAt");
        query.setLimit(QUERY_LIMIT_DEFAULT);
        addSubscription(query.findObjects(new FindListener<UseRecord>() {

            @Override
            public void done(List<UseRecord> object, BmobException e) {
                mProgressHelper.dismissProgressDialog();
                if(e==null){
                    routeList.clear();
                    if(object != null && object.size() > 0){
                        routeList.add("");
                        routeList.addAll(object);
                    }
                    routeAdapter.notifyDataSetChanged();
                }else{
                    toast(getString(R.string.initing_fail));
                    loge(e);
                }
            }
        }));
    }

    @Override
    public void onItemLongClick(View v, final int position) {
        /*AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.setTitle(R.string.notice);
        alertDialog.setMessage(getString(R.string.del_tip_car));
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, mContext.getString(R.string.del_comfirm),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            doUpdate((Car) routeList.get(position));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getString(R.string.del_cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alertDialog.show();*/
    }

    private void doUpdate(Car car){
        if(car == null){
            return;
        }
        mProgressHelper.showProgressDialog(getString(R.string.submiting));
        Car newCar = new Car();
        newCar.setAuthor(null);
        newCar.setEarn(0f);
        newCar.setIncome(0f);
        newCar.setPosition(null);
        addSubscription(newCar.delete(car.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                mProgressHelper.dismissProgressDialog();
                if(e == null){
                    toast(getString(R.string.del_success));
                    initList();
                }else {
                    toast(getString(R.string.submit_faile));
                }
            }
        }));
    }
}
