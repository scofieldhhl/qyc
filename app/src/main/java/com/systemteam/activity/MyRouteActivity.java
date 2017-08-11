package com.systemteam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.adapter.MyRouteAdapter;
import com.systemteam.adapter.MyRouteDividerDecoration;
import com.systemteam.bean.RouteRecord;
import com.systemteam.database.db.DBManager;
import com.systemteam.fragment.RouteFragment;

import java.util.ArrayList;
import java.util.List;
//TODO 增加分页加载
public class MyRouteActivity extends BaseActivity implements MyRouteAdapter.OnItemClickListener {

    XRecyclerView routeRecyclerView;
    MyRouteAdapter routeAdapter;
    List<Object> routeList;
    TextView no_route;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_route);
        mContext = this;
        initView();
//        initData();
    }

    @Override
    public void onItemClick(View v, int position) {
        Intent intent = new Intent(MyRouteActivity.this, RouteDetailActivity.class);
        RouteRecord routeRecord = (RouteRecord)routeList.get(position);
//        bundle.putParcelable("routeContent",routeRecord );
        Bundle bundle = new Bundle();
        bundle.putString("totalTime", routeRecord.getCycle_time());
        bundle.putString("totalDistance", routeRecord.getCycle_distance());
        bundle.putString("totalPrice", routeRecord.getCycle_price());
        bundle.putString("routePoints", routeRecord.getCycle_points());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public List<Object> loadPage() {
        routeList.clear();
        routeList.addAll(new DBManager().getAllRouteRecord());
        return routeList;

    }

    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void initView() {
        initToolBar(MyRouteActivity.this, R.string.route);
//        initRecyclerview();
        RouteFragment fragment = new RouteFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ll_content, fragment)
                .commit();
    }

    private void initRecyclerview(){
        routeRecyclerView = (XRecyclerView) findViewById(R.id.recyclerview_route);
        no_route = (TextView) findViewById(R.id.no_route);
        routeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        routeList = new ArrayList<>();

        routeList = loadPage();
        if (routeList != null) {
            routeAdapter = new MyRouteAdapter(this, routeList);
            routeRecyclerView.setAdapter(routeAdapter);
            routeRecyclerView.addItemDecoration(new MyRouteDividerDecoration(10));
            routeAdapter.setOnClickListener(this);
        }else{
            no_route.setVisibility(View.VISIBLE);
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
                toast("onRefresh");
                routeRecyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                toast("onLoadMore");
                loadPage();
                routeRecyclerView.loadMoreComplete();
                routeAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {

    }
}
