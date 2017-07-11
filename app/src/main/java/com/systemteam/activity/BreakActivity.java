package com.systemteam.activity;

import android.os.Bundle;
import android.view.View;

import com.systemteam.BaseActivity;
import com.systemteam.R;

public class BreakActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break);
        mContext = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        initToolBar(this, R.string.break_title);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_icon:
                finish();
                break;
        }
    }

    private void doBreakSubmit(){

    }
}
