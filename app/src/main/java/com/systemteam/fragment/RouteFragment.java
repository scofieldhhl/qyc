package com.systemteam.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.systemteam.R;
import com.systemteam.adapter.MyRouteAdapter;
import com.systemteam.bean.MyUser;
import com.systemteam.bean.UseRecord;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.systemteam.util.Constant.REQUEST_KEY_BY_USER;

public class RouteFragment extends BaseListFragment{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public RouteFragment() {}

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
        routeList = new ArrayList<>();
        routeAdapter = new MyRouteAdapter(mContext, routeList);
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
    public void onClick(View v) {

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

    @Override
    public void onItemLongClick(View v, int position) {

    }

    protected void initDataList(final int page) {
        if(page == 0)
            mProgressHelper.showProgressDialog(getString(R.string.initing));
        MyUser user = BmobUser.getCurrentUser(MyUser.class);
        BmobQuery<UseRecord> query = new BmobQuery<>();
        query.addWhereEqualTo(REQUEST_KEY_BY_USER, user.getObjectId());
        query.order("-createdAt");
        initQueryByPage(query, page);
        addSubscription(query.findObjects(new FindListener<UseRecord>() {

            @Override
            public void done(List<UseRecord> object, BmobException e) {
                onResponse(object, e, page);
            }
        }));
    }

}
