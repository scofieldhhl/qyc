package com.systemteam.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.systemteam.Main2Activity;
import com.systemteam.R;
import com.systemteam.bean.MyUser;
import com.systemteam.util.LogTool;
import com.systemteam.util.Utils;
import com.systemteam.view.ProgressDialogHelper;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.systemteam.BaseActivity.loge;

/**
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/11/15 11:46
 */

public abstract class BaseFragment extends Fragment implements View.OnClickListener{
    protected MyUser mUser;
    protected Context mContext;
    protected InputMethodManager mImm;
    private CompositeSubscription mCompositeSubscription;
    protected ProgressDialogHelper mProgressHelper;
    protected long mTimeSMSCode = 0;
    protected final int TIME_SMSCODE_WIAT = 20 * 1000;

    /**
     * 解决Subscription内存泄露问题
     * @param s
     */
    protected void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

    public void toast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mProgressHelper = new ProgressDialogHelper(getActivity());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    protected abstract void initView(View view);
    protected abstract void initData();

    protected void requestSMSCode(final Context context, String phone){
        /*BmobSMS.requestSMSCode(context, phone, "模板名称", new RequestSMSCodeListener() {
            @Override
            public void done(Integer integer, cn.bmob.sms.exception.BmobException e) {
                if(e==null){//验证码发送成功
                    Log.i("smile", "短信id："+smsId);//用于查询本次短信发送详情
                }
            }
        });*/
        BmobSMS.requestSMSCode(phone,"短信模板", new QueryListener<Integer>() {

            @Override
            public void done(Integer smsId,BmobException ex) {
                if(ex==null){//验证码发送成功
                    toast(context, mContext.getString(R.string.SMS_send));
                    LogTool.i("短信id："+smsId);//用于查询本次短信发送详情
                }else {
                    Utils.showDialog(context, context.getString(R.string.notice),
                            context.getString(R.string.SMS_failed));
                }
            }
        });
    }

    protected void registerUser(final Context context, String phone, String psd){
        MyUser myUser = new MyUser();
        myUser.setPassword(psd);
        myUser.setMobilePhoneNumber(phone);
        addSubscription(myUser.signOrLogin(psd, new SaveListener<MyUser>() {
            @Override
            public void done(MyUser s, BmobException e) {
                if(mProgressHelper != null){
                    mProgressHelper.dismissProgressDialog();
                }
                if(e==null){
                    toast(context, mContext.getString(R.string.reg_success));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getActivity(), Main2Activity.class));
                            getActivity().finish();
                        }
                    }, 500);
                }else{
                    toast(context, mContext.getString(R.string.reg_failed));
                    loge(e);
                }
            }
        }));
    }
}
