package com.systemteam.activity;

import android.os.Bundle;
import android.view.View;

import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.fragment.RouteFragment;
//TODO 增加分页加载
public class MyRouteActivity extends BaseActivity{

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_route);
        mContext = this;
        initView();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initView() {
        initToolBar(MyRouteActivity.this, R.string.route);
        RouteFragment fragment = new RouteFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ll_content, fragment)
                .commit();
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {

    }
}
