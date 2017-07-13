package com.systemteam.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.systemteam.BaseActivity;
import com.systemteam.R;

import static com.systemteam.util.Constant.BUNDLE_TYPE_MENU;

public class BreakActivity extends BaseActivity {
    private int mType = -1;
    private LinearLayout mLlLock;
    private TableLayout mTlBreak;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break);
        mContext = this;
        mType = getIntent().getIntExtra(BUNDLE_TYPE_MENU, 0);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTlBreak = (TableLayout) findViewById(R.id.tl_break);
        mLlLock = (LinearLayout) findViewById(R.id.ll_lock);
        if(mType == 0){
            initToolBar(this, R.string.break_lock_title);
            mTlBreak.setVisibility(View.GONE);
            mLlLock.setVisibility(View.VISIBLE);
        }else {
            initToolBar(this, R.string.break_title);
            mTlBreak.setVisibility(View.VISIBLE);
            mLlLock.setVisibility(View.GONE);
        }
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
