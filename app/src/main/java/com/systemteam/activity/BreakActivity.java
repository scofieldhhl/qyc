package com.systemteam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.systemteam.BaseActivity;
import com.systemteam.R;

import static com.systemteam.util.Constant.BUNDLE_KEY_CODE;
import static com.systemteam.util.Constant.BUNDLE_TYPE_MENU;
import static com.systemteam.util.Constant.REQUEST_CODE;

public class BreakActivity extends BaseActivity {
    private int mType = -1;
    private LinearLayout mLlLock;
    private TableLayout mTlBreak;
    private TextView mTvCode;
    private String mCarNo;
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
        mTvCode = (TextView) findViewById(R.id.tv_title_code);
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

    public void doBreakSubmit(View view){

    }

    public void gotoScan(View view){
        startActivityForResult(new Intent(BreakActivity.this, QRCodeScanActivity.class),
                REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            mCarNo = data.getStringExtra(BUNDLE_KEY_CODE);
            mTvCode.setText(mCarNo);
        }
    }
}
