package com.systemteam.activity;

import android.os.Bundle;
import android.view.View;

import com.systemteam.BaseActivity;
import com.systemteam.R;

public class BalanceDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_detail);
        mContext = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        initToolBar(this, R.string.bala_detail_title);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View view) {

    }
}
