package com.systemteam.activity;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;

import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.bean.BankCard;
import com.systemteam.bean.MyUser;

import java.lang.ref.WeakReference;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.systemteam.util.Constant.MSG_UPDATE_UI;

public class WithdrawActivity extends BaseActivity {
    private BankCard mBankCard;
    private static class MyHandler extends Handler {
        private WeakReference<WithdrawActivity> mActivity;

        public MyHandler(WithdrawActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final WithdrawActivity theActivity = mActivity.get();
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_UPDATE_UI:
                    theActivity.initInfo(theActivity.mBankCard);
                    break;
            }
        }
    }

    private MyHandler mHandler = new MyHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
        mContext = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        initToolBar(this, R.string.withdraw_title);
    }

    @Override
    protected void initData() {
        requestInfo();
    }

    private void initInfo(BankCard bankCard){
        if(bankCard == null){
            toast(getString(R.string.bankcard_no_hint));
            showInputDialog();
        }else {

        }
    }

    @Override
    public void onClick(View view) {

    }
    private void requestInfo(){
        mProgressHelper.showProgressDialog(getString(R.string.initing));
        mUser = BmobUser.getCurrentUser(MyUser.class);
        BmobQuery<BankCard> query = new BmobQuery<>();
        query.addWhereEqualTo("author", mUser.getObjectId());
        addSubscription(query.findObjects(new FindListener<BankCard>() {

            @Override
            public void done(List<BankCard> object, BmobException e) {
                mProgressHelper.dismissProgressDialog();
                if(e==null){
                    if(object != null && object.size() > 0){
                        mBankCard = object.get(0);
                    }else {
                        mBankCard = null;
                    }
                    mHandler.sendEmptyMessage(MSG_UPDATE_UI);
                }else{
                    toast(getString(R.string.initing_fail));
                    loge(e);
                }
            }
        }));

    }

    public Dialog showInputDialog() {
        final Dialog dialog = new Dialog(mContext, R.style.MyDialog);
        //设置它的ContentView
        dialog.setContentView(R.layout.activity_code_unlock);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_unlock:
                        break;
                    case R.id.iv_light:
                        break;
                    case R.id.menu_icon:
                    case R.id.iv_scan:
                        mImm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                        dialog.dismiss();
                        break;
                }
            }
        };
        dialog.findViewById(R.id.btn_unlock).setOnClickListener(listener);
        dialog.findViewById(R.id.iv_light).setOnClickListener(listener);
        dialog.findViewById(R.id.menu_icon).setOnClickListener(listener);
        dialog.findViewById(R.id.iv_scan).setOnClickListener(listener);
        dialog.show();
//        mImm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        return dialog;
    }
}