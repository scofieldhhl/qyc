package com.systemteam.welcome.fragment.outlayer.loginlayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.systemteam.BikeApplication;
import com.systemteam.MainActivity;
import com.systemteam.R;
import com.systemteam.bean.MyUser;
import com.systemteam.fragment.BaseFragment;
import com.systemteam.util.Utils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import rx.Subscriber;

import static com.systemteam.BaseActivity.log;
import static com.systemteam.BaseActivity.loge;


/**
 * 登录
 */
public class LoginFragment extends BaseFragment {
    private EditText mEtPhone, mEtPsd;
    private Button mBtnSend;
    private String mPhone, mPwd;
    private boolean isLoginByPsd = true;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_login, null);
        mContext = getActivity();
        mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        initView(view);
        initData();
        return view;
    }

    //TODO login
    @Override
    public void onClick(View v) {
        mImm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
        mPhone = String.valueOf(mEtPhone.getText());
        mPwd = String.valueOf(mEtPsd.getText());
        switch (v.getId()){
            case R.id.btn_login:
                if(TextUtils.isEmpty(mPhone)){
                    toast(mContext, mContext.getString(R.string.smssdk_write_mobile_phone));
                    return;
                }
                if(!Utils.isMobile(mPhone)){
                    toast(mContext, mContext.getString(R.string.smssdk_mobile_phone_error));
                    return;
                }
                if(TextUtils.isEmpty(mPwd)){
                    toast(mContext, mContext.getString(R.string.welcomanim_title_psw_hint));
                    return;
                }
                mProgressHelper.showProgressDialog(getActivity().getString(R.string.account_tip_login_ing));
                if(isLoginByPsd){
                    testLogin();
                }else{
                    //TODO 登录成功后，密码还是首次注册时的密码 是否需要将密码重置为最新注册码
                    registerUser(mContext, mPhone, mPwd);
                }
                break;
            case R.id.iv_qq:
                onLoginQQ(v);
                break;
            case R.id.iv_weibo:
                onLoginWeibo(v);
                break;
            case R.id.iv_wechat:
                onLoginWechat(v);
                break;
            case R.id.tv_forgetpsw:
                requestSMSCode(mContext, mPhone);
                isLoginByPsd = false;
                break;
        }
    }

    @Override
    protected void initView(View view) {
        TextView tv_down = (TextView)view.findViewById(R.id.tv_down);
        tv_down.setOnClickListener(this);
        view.findViewById(R.id.btn_login).setOnClickListener(this);

        mEtPhone = (EditText) view.findViewById(R.id.et_phone);
        mEtPsd = (EditText) view.findViewById(R.id.et_psw);
        mBtnSend = (Button) view.findViewById(R.id.btn_login);
        mBtnSend.setOnClickListener(this);
        view.findViewById(R.id.iv_qq).setOnClickListener(this);
        view.findViewById(R.id.iv_wechat).setOnClickListener(this);
        view.findViewById(R.id.iv_weibo).setOnClickListener(this);
        view.findViewById(R.id.tv_forgetpsw).setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    /**
     * 注意下如果返回206错误 一般是多设备登录导致
     */
    private void testLogin() {
        final BmobUser user = new BmobUser();
        user.setUsername(mPhone);
        user.setMobilePhoneNumber(mPhone);
        user.setPassword(mPwd);
        //v3.5.0开始新增加的rx风格的Api
        user.loginObservable(BmobUser.class).subscribe(new Subscriber<BmobUser>() {
            @Override
            public void onCompleted() {
                mProgressHelper.dismissProgressDialog();
                log("----onCompleted----");
            }

            @Override
            public void onError(Throwable e) {
                mProgressHelper.dismissProgressDialog();
                toast(getActivity(), getString(R.string.account_tip_login_failed));
                loge(new BmobException(e));
            }

            @Override
            public void onNext(BmobUser bmobUser) {
                mProgressHelper.dismissProgressDialog();
                toast(mContext, bmobUser.getUsername() + getString(R.string.reg_success));
                testGetCurrentUser();
                ((BikeApplication)getActivity().getApplication()).getmUser();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
    }

    /**
     * 获取本地用户
     */
    private void testGetCurrentUser() {
		MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
		if (myUser != null) {
			log("本地用户信息:objectId = " + myUser.getObjectId() + ",name = " + myUser.getUsername()
					+ ",age = "+ myUser.getAge());
		} else {
			toast(mContext, "本地用户为null,请登录。");
		}
		BikeApplication application = (BikeApplication) getActivity().getApplication();
        application.setmUser(myUser);
        //V3.4.5版本新增加getObjectByKey方法获取本地用户对象中某一列的值
        /*String username = (String) BmobUser.getObjectByKey("username");
        Integer age = (Integer) BmobUser.getObjectByKey("age");
        Boolean sex = (Boolean) BmobUser.getObjectByKey("sex");
        JSONArray hobby= (JSONArray) BmobUser.getObjectByKey("hobby");
        JSONArray cards= (JSONArray) BmobUser.getObjectByKey("cards");
        JSONObject banker= (JSONObject) BmobUser.getObjectByKey("banker");
        JSONObject mainCard= (JSONObject) BmobUser.getObjectByKey("mainCard");
        log("username："+username+",\nage："+age+",\nsex："+ sex);
        log("hobby:"+(hobby!=null?hobby.toString():"为null")+"\ncards:"+(cards!=null ?cards.toString():"为null"));
        log("banker:"+(banker!=null?banker.toString():"为null")+"\nmainCard:"+(mainCard!=null ?mainCard.toString():"为null"));*/
    }

    public void onLoginWeibo(View view){
        /*ILoginManager iLoginManager = new WeiboLoginManager(getActivity());
        iLoginManager.login(new PlatformActionListener() {
            @Override
            public void onComplete(HashMap<String, Object> userInfo) {
                //TODO
            }

            @Override
            public void onError() {

            }

            @Override
            public void onCancel() {

            }
        });*/
    }

    public void onLoginWechat(View view){
        /*ILoginManager iLoginManager = new WechatLoginManager(getActivity());
        iLoginManager.login(new PlatformActionListener() {
            @Override
            public void onComplete(HashMap<String, Object> userInfo) {
                //TODO
            }

            @Override
            public void onError() {
                //TODO
            }

            @Override
            public void onCancel() {
                //TODO
            }
        });*/
    }

    public void onLoginQQ(View view){
        /*ILoginManager iLoginManager = new QQLoginManager(getActivity());
        iLoginManager.login(new PlatformActionListener() {
            @Override
            public void onComplete(HashMap<String, Object> userInfo) {
                //TODO
            }

            @Override
            public void onError() {
                //TODO
            }

            @Override
            public void onCancel() {
                //TODO
            }
        });*/
    }

}
