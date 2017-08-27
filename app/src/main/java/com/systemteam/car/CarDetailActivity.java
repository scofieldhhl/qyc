package com.systemteam.car;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.systemteam.R;
import com.systemteam.activity.BaseListActivity;
import com.systemteam.adapter.MyRouteAdapter;
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

import static com.systemteam.util.Constant.REQUEST_KEY_BY_CARNO;

public class CarDetailActivity extends BaseListActivity {

    private boolean isChartShow = false;
    private Car mCar;
    FrameLayout mLayoutChart;
    LinearLayout mLlDetail;
    private ChartFragment mChartFragment;
    private Menu mMenu;
    private TextView mTvCarNo, mTvEarn, mTvAddress, mTvStatus, mTvMark;
    Button mBtnUpdate, mBtnExpert;
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
                            mLlDetail.setVisibility(View.GONE);
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
                            mLlDetail.setVisibility(View.VISIBLE);
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

        initRecyclerview();
        routeList = new ArrayList<>();
        routeAdapter = new MyRouteAdapter(mContext, routeList);
        routeAdapter.setOnClickListener(this);
        routeAdapter.setOnLongClickListener(this);
        routeRecyclerView.setAdapter(routeAdapter);

        mTvCarNo = (TextView) findViewById(R.id.tv_carno);
        mTvEarn = (TextView) findViewById(R.id.tv_earn);
        mTvAddress = (TextView) findViewById(R.id.tv_address);
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mTvMark = (TextView) findViewById(R.id.tv_mark);

        mBtnUpdate = (Button) findViewById(R.id.btn_update);
        mBtnExpert = (Button) findViewById(R.id.btn_expert);
        mLlDetail = (LinearLayout) findViewById(R.id.ll_detail);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if(intent != null){
            Bundle bundle = intent.getExtras();
            mCar = (Car) bundle.getSerializable(Constant.BUNDLE_CAR);
            initInfo(mCar);
            if(mCar != null){
                mPage = 0;
                initDataList(mPage);
                LogTool.d("mcar : " + mCar.getCarNo());
            }else {
                LogTool.e("mcar == null");
            }
        }

    }

    private void initInfo(Car car){
        if(car != null){
            mTvCarNo.setText(car.getCarNo());
            mTvEarn.setText(car.getEarn() != null ? String.valueOf(car.getEarn()) : "0.00");
            mTvAddress.setText(car.getAddress());
            if(car.getStatus() == null){
                mTvStatus.setText(getString(R.string.status_normal));
                mBtnUpdate.setVisibility(View.GONE);
                mBtnExpert.setVisibility(View.GONE);
            }else {
                switch (car.getStatus()){
                    case Constant.STATUS_NORMAL:
                        mTvStatus.setText(getString(R.string.status_normal));
                        mTvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                        mBtnUpdate.setVisibility(View.GONE);
                        mBtnExpert.setVisibility(View.GONE);
                        break;
                    case Constant.BREAK_STATUS_LOCK:
                        mTvStatus.setText(getString(R.string.status_lock));
                        mTvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                        mBtnUpdate.setVisibility(View.VISIBLE);
                        mBtnExpert.setVisibility(View.VISIBLE);
                        break;
                    default:
                        mTvStatus.setText(getString(R.string.status_break));
                        mTvStatus.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                        mBtnUpdate.setVisibility(View.VISIBLE);
                        mBtnExpert.setVisibility(View.VISIBLE);
                        break;
                }
            }
            mTvMark.setText(car.getMark());

        }else {
            mTvCarNo.setText("");
            mTvEarn.setText("");
            mTvAddress.setText("");
            mTvStatus.setText("");
            mTvMark.setText("");

            mBtnUpdate.setVisibility(View.GONE);
            mBtnExpert.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    public void onItemClick(View v, int position) {

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
                    mPage = 0;
                    initDataList(mPage);
                }else {
                    toast(getString(R.string.submit_faile));
                }
            }
        }));
    }

    @Override
    protected void initDataList(final int page) {
        if (page == 0)
            mProgressHelper.showProgressDialog(getString(R.string.initing));
        BmobQuery<UseRecord> query = new BmobQuery<>();
        query.addWhereEqualTo(REQUEST_KEY_BY_CARNO, mCar.getCarNo());
        query.order("-createdAt");
        initQueryByPage(query, page);
        addSubscription(query.findObjects(new FindListener<UseRecord>() {

            @Override
            public void done(List<UseRecord> object, BmobException e) {
                onResponse(object, e, page);
            }
        }));
    }

    public void doRequestCarNormal(View view){
        if(mCar == null){
            mProgressHelper.dismissProgressDialog();
            toast(getString(R.string.break_car_no));
            return;
        }
        mProgressHelper.showProgressDialog(getString(R.string.submiting));
        Car newCar = new Car();
        newCar.setStatus(Constant.STATUS_NORMAL);
        addSubscription(newCar.update(mCar.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                mProgressHelper.dismissProgressDialog();
                if(e==null){
                    toast(getString(R.string.submiting));
                    mCar.setStatus(Constant.STATUS_NORMAL);
                    initInfo(mCar);
                }else{
                    toast(getString(R.string.submit_faile));
                    loge(e);
                }
            }
        }));
    }

    public void doRequestCarExpert(View view){
        //TODO 维修表增加维修记录
        toast(getString(R.string.break_submit_success));
    }
}
