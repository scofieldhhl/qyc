package com.systemteam.car;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.adapter.MyCarAdapter;
import com.systemteam.adapter.MyRouteDividerDecoration;
import com.systemteam.bean.Car;

import java.util.ArrayList;
import java.util.List;

public class MyCarActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, MyCarAdapter.OnItemClickListener {
    XRecyclerView routeRecyclerView;
    MyCarAdapter routeAdapter;
    List<Car> routeList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_car);
        initView();
        initData();
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void initData() {
        routeRecyclerView = (XRecyclerView) findViewById(R.id.recyclerview_route);
//        no_route = (TextView) findViewById(R.id.no_route);
        routeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

//        routeList = getAllPoints();
        routeList = new ArrayList<Car>();

        routeList = loadPage();
        if (routeList != null) {
            routeAdapter = new MyCarAdapter(this, routeList);
            routeRecyclerView.setAdapter(routeAdapter);
            routeRecyclerView.addItemDecoration(new MyRouteDividerDecoration(10));
            routeAdapter.setOnClickListener(this);
        }else{
//            no_route.setVisibility(View.VISIBLE);
        }

        routeRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        routeRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallScale);
        routeRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
        routeRecyclerView.setPullRefreshEnabled(false);
//        View header = LayoutInflater.from(this).inflate(R.layout.recyclerview_header, (ViewGroup)findViewById(android.R.id.content),false);
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
        getMenuInflater().inflate(R.menu.my_car, menu);
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

    public List<Car> loadPage() {
        return  null;

    }

    @Override
    public void onItemClick(View v, int position) {

    }
}
