package com.systemteam.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.bean.Car;
import com.systemteam.util.Constant;

import java.lang.ref.WeakReference;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.systemteam.util.Constant.BUNDLE_CAR;
import static com.systemteam.util.Constant.BUNDLE_CARNO;
import static com.systemteam.util.Constant.BUNDLE_KEY_CODE;
import static com.systemteam.util.Constant.BUNDLE_KEY_IS_ACTIVING;
import static com.systemteam.util.Constant.BUNDLE_KEY_SUBMIT_SUCCESS;
import static com.systemteam.util.Constant.BUNDLE_TYPE_MENU;
import static com.systemteam.util.Constant.MODEL_DEVICE_ZXINGQR;
import static com.systemteam.util.Constant.MSG_RESPONSE_SUCCESS;
import static com.systemteam.util.Constant.MSG_UPDATE_UI;
import static com.systemteam.util.Constant.REQUEST_CODE;

public class BreakActivity extends BaseActivity {
    private int mType = -1;
    private LinearLayout mLlLock;
    private TableLayout mTlBreak;
    private TextView mTvCode;
    private String mCarNo;
    private CheckBox[] mCbArray;
    private Car mCar;
    private EditText mEtDescription;
    private boolean isSubmitSuccess = false;
    private boolean isActiving = false;
    private static class MyHandler extends Handler {
        private WeakReference<BreakActivity> mActivity;

        public MyHandler(BreakActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final BreakActivity theActivity = mActivity.get();
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_RESPONSE_SUCCESS:
                    theActivity.doSubmit();
                    break;
                case MSG_UPDATE_UI:
                    theActivity.initInfo();
                    break;
            }
        }
    }

    private MyHandler mHandler = new MyHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break);
        mContext = this;
        mType = getIntent().getIntExtra(BUNDLE_TYPE_MENU, Constant.BREAK_TYPE_LOCK);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTlBreak = (TableLayout) findViewById(R.id.tl_break);
        mLlLock = (LinearLayout) findViewById(R.id.ll_lock);
        mTvCode = (TextView) findViewById(R.id.tv_title_code);
        if(mType == Constant.BREAK_TYPE_LOCK){
            initToolBar(this, R.string.break_lock_title);
            mTlBreak.setVisibility(View.GONE);
            mLlLock.setVisibility(View.VISIBLE);
        }else {
            initToolBar(this, R.string.break_title);
            mTlBreak.setVisibility(View.VISIBLE);
            mLlLock.setVisibility(View.GONE);
        }
        mCbArray = new CheckBox[8];
        mCbArray[0] = (CheckBox) findViewById(R.id.cb1);
        mCbArray[1] = (CheckBox) findViewById(R.id.cb2);
        mCbArray[2] = (CheckBox) findViewById(R.id.cb3);
        mCbArray[3] = (CheckBox) findViewById(R.id.cb4);
        mCbArray[4] = (CheckBox) findViewById(R.id.cb5);
        mCbArray[5] = (CheckBox) findViewById(R.id.cb6);
        mCbArray[6] = (CheckBox) findViewById(R.id.cb7);
        mCbArray[7] = (CheckBox) findViewById(R.id.cb8);
        mEtDescription = (EditText) findViewById(R.id.et_description);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if(intent != null){
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                mCar = (Car) bundle.getSerializable(BUNDLE_CAR);
                if(mCar != null){
                    mCarNo = mCar.getCarNo();
                }else {
                    mCarNo = bundle.getString(BUNDLE_CARNO);
                }
                if(!TextUtils.isEmpty(mCarNo)){
                    mTvCode.setText(getString(R.string.break_carNo) + mCarNo);
                }
                isActiving = bundle.getBoolean(BUNDLE_KEY_IS_ACTIVING);
            }
        }
    }

    private void initInfo(){
        if(mCar == null){
            mCarNo = "";
            mEtDescription.setText("");
            mTvCode.setText(R.string.scan_break_hint1);
            if(mType == Constant.BREAK_TYPE_BREAK){
                for(int i = 0; i < mCbArray.length; i++){
                    mCbArray[i].setChecked(false);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_icon:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isSubmitSuccess){
            setResult(RESULT_OK, new Intent().putExtra(BUNDLE_KEY_SUBMIT_SUCCESS, isSubmitSuccess));
        }
    }

    public void doBreakSubmit(View view){
        if(mCarNo == null || TextUtils.isEmpty(mCarNo)){
            mProgressHelper.dismissProgressDialog();
            toast(getString(R.string.break_carNo_no));
            return;
        }else if(mType == Constant.BREAK_TYPE_BREAK){
            String desc = String.valueOf(mEtDescription.getText());
            boolean bool = false;
            for(int i = 0; i < mCbArray.length; i++){
                if(mCbArray[i].isChecked()){
                    bool = true;
                    break;
                }
            }
            if(TextUtils.isEmpty(desc) && !bool){
                toast(getString(R.string.break_breakdesc_no));
                return;
            }
        }
        mProgressHelper.showProgressDialog(getString(R.string.submiting));
        checkCarExist(mCarNo);
    }

    private void doSubmit(){

        if(mCar == null){
            mProgressHelper.dismissProgressDialog();
            toast(getString(R.string.break_car_no));
            return;
        }
        Car newCar = new Car();
        if(mType == Constant.BREAK_TYPE_LOCK){
            newCar.setStatus(Constant.BREAK_STATUS_LOCK);
        }else {
            int status = -1;
            for(int i = 0; i < mCbArray.length; i++){
                if(mCbArray[i].isChecked()){
                    status *= 10;
                    status += (i + 1);
                }
            }
            if(status == -1){//没有勾选故障原因
                status = -10;
            }
            newCar.setStatus(status);
        }
        String description = String.valueOf(mEtDescription.getText());
        if(!TextUtils.isEmpty(description)){
            newCar.setMark(description);
        }
        addSubscription(newCar.update(mCar.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                mProgressHelper.dismissProgressDialog();
                if(e==null){
                    isSubmitSuccess = true;
                    toast(getString(R.string.break_submit_success));
                    mCar = null;
                    mHandler.sendEmptyMessage(MSG_UPDATE_UI);
                }else{
                    isSubmitSuccess = false;
                    toast(getString(R.string.submit_faile));
                    loge(e);
                }
                if(isActiving){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(isSubmitSuccess){
                                setResult(RESULT_OK, new Intent().putExtra(BUNDLE_KEY_SUBMIT_SUCCESS, isSubmitSuccess));
                            }
                            finish();
                        }
                    }, 1000);
                }
            }
        }));
    }

    public void gotoScan(View view){
        Intent intent = new Intent(this, QRCodeScanActivity.class);
        if(MODEL_DEVICE_ZXINGQR.equalsIgnoreCase(android.os.Build.MODEL)){
            intent = new Intent(this, ZxingActivity.class);
        }
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && data != null){
            String result = data.getStringExtra(BUNDLE_KEY_CODE);
            if(!TextUtils.isEmpty(result)){
                mCarNo = result;
                mTvCode.setText(getString(R.string.break_carNo) + mCarNo);
            }
        }
    }

    private void checkCarExist(String carNo) {
        if(mCar == null){
            BmobQuery<Car> query = new BmobQuery<>();
            query.addWhereEqualTo("carNo", carNo);
            addSubscription(query.findObjects(new FindListener<Car>() {

                @Override
                public void done(List<Car> object, BmobException e) {
                    if(e==null){
                        if(object != null && object.size() > 0){
                            mCar = object.get(0);
                            mHandler.sendEmptyMessage(MSG_RESPONSE_SUCCESS);
                        }
                    }else{
                        mProgressHelper.dismissProgressDialog();
                        toast(getString(R.string.response_faile));
                        loge(e);
                    }
                }
            }));
        }else {
            mHandler.sendEmptyMessage(MSG_RESPONSE_SUCCESS);
        }
    }
}
