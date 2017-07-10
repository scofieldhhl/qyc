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
        initTitle(this, R.string.break_title, R.mipmap.refresh_icon, 0);
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
