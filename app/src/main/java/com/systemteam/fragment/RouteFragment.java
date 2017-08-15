package com.systemteam.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.systemteam.R;
import com.systemteam.adapter.MyRouteAdapter;
import com.systemteam.adapter.MyRouteDividerDecoration;
import com.systemteam.bean.MyUser;
import com.systemteam.bean.UseRecord;
import com.systemteam.util.Constant;
import com.systemteam.util.LogTool;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.systemteam.BaseActivity.loge;
import static com.systemteam.util.Constant.REQUEST_KEY_BY_USER;

public class RouteFragment extends BaseFragment implements MyRouteAdapter.OnItemClickListener {

    XRecyclerView routeRecyclerView;
    MyRouteAdapter routeAdapter;
    List<Object> routeList;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public RouteFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RechargeFragment.
     */
    public static RouteFragment newInstance(String param1, String param2) {
        RouteFragment fragment = new RouteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recyclerview, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initView(View view) {
        initRecyclerview(view);
    }

    @Override
    protected void initData() {
        mPage = 0;
        initDataList(mPage);
    }

    @Override
    public void onClick(View v) {

    }

    private void initRecyclerview(View view){
        routeRecyclerView = (XRecyclerView) view.findViewById(R.id.recyclerview_route);
//      no_route = (TextView) findViewById(R.id.no_route);
        routeRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        routeList = new ArrayList<>();
        routeAdapter = new MyRouteAdapter(mContext, routeList);
        routeAdapter.setOnClickListener(this);
//      routeAdapter.setOnLongClickListener(mContext);
        routeRecyclerView.setAdapter(routeAdapter);
        routeRecyclerView.addItemDecoration(new MyRouteDividerDecoration(1));

        routeRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        routeRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallScale);
        routeRecyclerView.setArrowImageView(R.drawable.iconfont_downgrey);
        routeRecyclerView.setPullRefreshEnabled(false);
        routeRecyclerView.setLoadingMoreEnabled(true);
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
                initDataList(++mPage);
            }
        });
    }

    @Override
    public void onItemClick(View v, int position) {//TODO 查看行程详情
        /*Intent intent = new Intent(getActivity(), RouteDetailActivity.class);
        UseRecord routeRecord = (UseRecord)routeList.get(position);
//        bundle.putParcelable("routeContent",routeRecord );
        Bundle bundle = new Bundle();
        bundle.putString("totalTime", routeRecord.getCycle_time());
        bundle.putString("totalDistance", routeRecord.getCycle_distance());
        bundle.putString("totalPrice", routeRecord.getCycle_price());
        bundle.putString("routePoints", routeRecord.getCycle_points());
        intent.putExtras(bundle);
        startActivity(intent);*/
    }

    private void initDataList(final int page) {
        if(page == 0)
            mProgressHelper.showProgressDialog(getString(R.string.initing));
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        BmobQuery<UseRecord> query = new BmobQuery<>();
        query.addWhereEqualTo(REQUEST_KEY_BY_USER, user.getObjectId());
        query.order("-createdAt");// 根据score字段升序显示数据
//        query.order("-score,createdAt");// 多个排序字段可以用（，）号分隔
        query.setLimit(Constant.QUERY_LIMIT_DEFAULT);
        query.setSkip(Constant.QUERY_LIMIT_DEFAULT * page);
        //判断是否有缓存，该方法必须放在查询条件（如果有的话）都设置完之后再来调用才有效，就像这里一样。
        boolean isCache = query.hasCachedResult(UseRecord.class);
        if(isCache){//此为举个例子，并不一定按这种方式来设置缓存策略
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        }else{
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        }
        addSubscription(query.findObjects(new FindListener<UseRecord>() {

            @Override
            public void done(List<UseRecord> object, BmobException e) {
                mProgressHelper.dismissProgressDialog();
                if(e==null){
                    if(page == 0){
                        routeList.clear();
                    }
                    LogTool.d("-----------------------" + object.size());
                    if(object != null && object.size() > 0){
                        routeList.addAll(object);
                        routeAdapter.notifyDataSetChanged();
                    }else {
                        toast(mContext, mContext.getString(R.string.nomore_loading));
                    }
                    if (page > 0){
                        routeRecyclerView.loadMoreComplete();
                    }
                }else{
                    if(page == 0){
                        toast(mContext, mContext.getString(R.string.initing_fail));
                    }else {
                        toast(mContext, mContext.getString(R.string.loading_fail));
                        routeRecyclerView.loadMoreComplete();
                    }
                    loge(e);
                }
            }
        }));
    }
}
