package com.systemteam.car;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.systemteam.util.Constant.REQUEST_KEY_BY_CARNO;

//myTODO 车辆信息管理，车收益统计（天／月），每辆车收益统计
public class CarDetailActivity extends BaseActivity
        implements MyCarAdapter.OnItemClickListener, MyCarAdapter.OnItemLongClickListener{
    XRecyclerView routeRecyclerView;
    MyRouteAdapter routeAdapter;
    List<Object> routeList;
    private Car mCar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_my_car);
        mContext = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        initToolBar(CarDetailActivity.this, R.string.detail_car_title);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.f_chart, new ChartFragment())
                .commit();
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if(intent != null){
            Bundle bundle = intent.getExtras();
            mCar = (Car) bundle.getSerializable(Constant.BUNDLE_CAR);
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
        if(mCar != null){
            initList();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.my_car, menu);
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
