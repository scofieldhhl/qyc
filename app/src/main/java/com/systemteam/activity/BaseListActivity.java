package com.systemteam.activity;

import android.support.v7.widget.LinearLayoutManager;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.adapter.BaseAdapter;
import com.systemteam.adapter.MyRouteDividerDecoration;
import com.systemteam.util.Constant;
import com.systemteam.util.LogTool;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;

public abstract class BaseListActivity extends BaseActivity implements BaseAdapter.OnItemClickListener,
                BaseAdapter.OnItemLongClickListener{
    protected int mPage = 0;    //分页页码
    protected XRecyclerView routeRecyclerView;
    protected BaseAdapter routeAdapter;
    protected List<Object> routeList;

    protected void initRecyclerview(){
        routeRecyclerView = (XRecyclerView) findViewById(R.id.recyclerview_route);
//      no_route = (TextView) findViewById(R.id.no_route);
        routeRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
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
                routeRecyclerView.refreshComplete();
            }

            @Override
            public void onLoadMore() {
                initDataList(++mPage);
            }
        });
    }

    protected void initQueryByPage(BmobQuery query, int page){
        query.setLimit(Constant.QUERY_LIMIT_DEFAULT);
        query.setSkip(Constant.QUERY_LIMIT_DEFAULT * page);
        //判断是否有缓存，该方法必须放在查询条件（如果有的话）都设置完之后再来调用才有效，就像这里一样。
        /*boolean isCache = query.hasCachedResult(UseRecord.class);
        if(isCache){//此为举个例子，并不一定按这种方式来设置缓存策略
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        }else{
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        }*/
    }

    protected abstract void initDataList(final int page);

    protected void onResponse(List object, BmobException e, int page){
        mProgressHelper.dismissProgressDialog();
        if(e==null){
            if(page == 0){
                routeList.clear();
            }
            LogTool.d("---------onResponse--------------" + object.size());
            if(object != null && object.size() > 0){
                routeList.addAll(object);
                routeAdapter.notifyDataSetChanged();
            }else {
                toast(getString(R.string.nomore_loading));
            }
            if (page > 0){
                routeRecyclerView.loadMoreComplete();
            }
        }else{
            if(page == 0){
                toast(getString(R.string.initing_fail));
            }else {
                toast(getString(R.string.loading_fail));
                routeRecyclerView.loadMoreComplete();
            }
            LogTool.e("失败："+e.getMessage()+","+e.getErrorCode());
        }
    }
}
