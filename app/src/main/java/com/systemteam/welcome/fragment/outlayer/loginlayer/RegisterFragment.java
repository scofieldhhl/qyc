package com.systemteam.welcome.fragment.outlayer.loginlayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.systemteam.Main2Activity;
import com.systemteam.R;
import com.systemteam.bean.MyUser;
import com.systemteam.fragment.BaseFragment;
import com.systemteam.util.Utils;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static com.systemteam.BaseActivity.loge;


/**
 * 注册
 */
public class RegisterFragment extends BaseFragment implements View.OnClickListener{
    private EditText mEtPhone, mEtPsd;
    private Button mBtnSend, mBtnRegister;
    private String mPhone, mPwd;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_register, null);
        mContext = getActivity();
        mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void onClick(View v) {
        mImm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
        mPhone = String.valueOf(mEtPhone.getText());
        mPwd = String.valueOf(mEtPsd.getText());
        if(TextUtils.isEmpty(mPhone)){

            return;
        }
        if(!Utils.isMobile(mPhone)){
            toast(mContext, mContext.getString(R.string.smssdk_mobile_phone_error));
            return;
        }
        switch (v.getId()){
            case R.id.btn_next:
                if ((System.currentTimeMillis() - mTimeSMSCode) < TIME_SMSCODE_WIAT) {
                    toast(mContext, mContext.getString(R.string.sms_code_wait));
                } else {
                    requestSMSCode(mContext, mPhone);
                }
                mTimeSMSCode = System.currentTimeMillis();
                break;
            case R.id.btn_login:
                if(TextUtils.isEmpty(mPwd)){
                    toast(mContext, mContext.getString(R.string.reg_write_identify_code));
                    return;
                }
                mProgressHelper.showProgressDialog(getString(R.string.account_tip_login_ing));
                registerUser(mContext, mPhone, mPwd);
                break;
        }
    }



    @Override
    protected void initView(View view) {
        mEtPhone = (EditText) view.findViewById(R.id.et_write_phone);
        mEtPsd = (EditText) view.findViewById(R.id.et_put_identify);
        mBtnSend = (Button) view.findViewById(R.id.btn_next);
        mBtnRegister = (Button) view.findViewById(R.id.btn_login);
        mBtnSend.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    private void registerUser(){
        MyUser myUser = new MyUser();
        myUser.setUsername(mPhone);
        myUser.setPassword(mPwd);
        myUser.setMobilePhoneNumber(mPhone);
        addSubscription(myUser.signOrLogin(mPwd, new SaveListener<MyUser>() {
            @Override
            public void done(MyUser s, BmobException e) {
                if(e==null){
                    toast(mContext, mContext.getString(R.string.reg_success));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getActivity(), Main2Activity.class));
                            getActivity().finish();
                        }
                    }, 500);
                }else{
                    toast(mContext, mContext.getString(R.string.reg_failed));
                    loge(e);
                }
            }
        }));
    }
}
