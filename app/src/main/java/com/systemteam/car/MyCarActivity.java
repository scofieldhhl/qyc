package com.systemteam.car;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.systemteam.R;
import com.systemteam.activity.BaseListActivity;
import com.systemteam.adapter.MyCarAdapter;
import com.systemteam.bean.Car;
import com.systemteam.bean.MyUser;
import com.systemteam.fragment.PieChartFragment;
import com.systemteam.util.Constant;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.systemteam.util.Constant.REQUEST_KEY_BY_USER;
//TODO 小车状态修改：故障改为正常，维修申报
public class MyCarActivity extends BaseListActivity
        implements NavigationView.OnNavigationItemSelectedListener, MyCarAdapter.OnItemClickListener{
    PieChartFragment mChartFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_car);
        mContext = this;
        initView();
    }

    @Override
    protected void initView() {
        initToolBar(MyCarActivity.this, R.string.my_car);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyCarActivity.this, NewCarActivity.class));
            }
        });

        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/
        mChartFragment = new PieChartFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.f_chart, mChartFragment)
                .commit();

        initRecyclerview();
        routeList = new ArrayList<>();
        routeAdapter = new MyCarAdapter(mContext, routeList);
        routeAdapter.setOnClickListener(this);
        routeAdapter.setOnLongClickListener(this);
        routeRecyclerView.setAdapter(routeAdapter);
    }

    @Override
    protected void initData() {
        mPage = 0;
        initDataList(mPage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_car_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(View v, int position) {
        Intent intent = new Intent(MyCarActivity.this, CarDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.BUNDLE_CAR, (Car)routeList.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View v, final int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
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
        alertDialog.show();
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
        if(page == 0)
            mProgressHelper.showProgressDialog(getString(R.string.initing));
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        BmobQuery<Car> query = new BmobQuery<>();
        query.addWhereEqualTo(REQUEST_KEY_BY_USER, user.getObjectId());
        initQueryByPage(query, page);
        addSubscription(query.findObjects(new FindListener<Car>() {

            @Override
            public void done(List<Car> object, BmobException e) {
                onResponse(object, e, page);
                mChartFragment.setRouteList(routeList);
            }
        }));
    }
}
