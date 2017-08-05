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
import com.systemteam.util.LogTool;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.systemteam.BaseActivity.loge;
import static com.systemteam.util.Constant.REQUEST_KEY_BY_USER;

public class RouteFragment extends BaseFragment{

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
        initDataList();
    }

    @Override
    public void onClick(View v) {

    }

    private void initRecyclerview(View view){
        routeRecyclerView = (XRecyclerView) view.findViewById(R.id.recyclerview_route);
//        no_route = (TextView) findViewById(R.id.no_route);
        routeRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        routeList = new ArrayList<>();
        routeAdapter = new MyRouteAdapter(mContext, routeList);
//        routeAdapter.setOnClickListener(mContext);
//        routeAdapter.setOnLongClickListener(mContext);
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
                routeRecyclerView.loadMoreComplete();
                routeAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initDataList() {
        mProgressHelper.showProgressDialog(getString(R.string.initing));
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        BmobQuery<UseRecord> query = new BmobQuery<>();
        query.addWhereEqualTo(REQUEST_KEY_BY_USER, user.getObjectId());
        // 根据score字段升序显示数据
//        query.order("-updatedAt");
        // 根据score字段降序显示数据
        //query.order("-score");
        // 多个排序字段可以用（，）号分隔
//        query.order("-score,createdAt");
        addSubscription(query.findObjects(new FindListener<UseRecord>() {

            @Override
            public void done(List<UseRecord> object, BmobException e) {
                mProgressHelper.dismissProgressDialog();
                if(e==null){
                    routeList.clear();
                    LogTool.d("-----------------------" + object.size());
                    if(object != null && object.size() > 0){
                        routeList.addAll(object);
                    }
                    routeAdapter.notifyDataSetChanged();
                }else{
                    toast(mContext, mContext.getString(R.string.initing_fail));
                    loge(e);
                }
            }
        }));
    }
}
