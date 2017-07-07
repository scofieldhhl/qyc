package com.systemteam.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import com.android.volley.RequestQueue;
import com.systemteam.R;
import com.systemteam.BaseActivity;
import com.systemteam.view.IconEditFullTextView;
import com.systemteam.view.ProgressDialogHelper;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private IconEditFullTextView mEmailEdt;
    private IconEditFullTextView mPwdEdt;
    private ProgressDialogHelper mProgressHelper;
    private RequestQueue mRequestQueue;
    public static String RestartApp = "RestartApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent = getIntent();
        if (intent != null) {
            boolean restart = intent.getBooleanExtra(RestartApp, false);
            /*if (restart) {
                ExpertApplication mapp = (ExpertApplication) getApplication();
                mapp.Initial(this);
            }*/
        }
        initializeView();

        mProgressHelper = new ProgressDialogHelper(this);

    }

    private void initializeView() {
        mEmailEdt = (IconEditFullTextView) findViewById(R.id.login_email);
        if (mEmailEdt != null) {
            mEmailEdt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            mEmailEdt.setMaxLength(50);
        }
        mPwdEdt = (IconEditFullTextView) findViewById(R.id.login_pwd);
        if (mPwdEdt != null) {
            mPwdEdt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mPwdEdt.setMaxLength(20);
        }

        View login = findViewById(R.id.login_login);
        if (login != null) {
            login.setOnClickListener(this);
        }
        View pwd = findViewById(R.id.login_forget_pwd);
        if (pwd != null) {
            pwd.setOnClickListener(this);
        }
        View signUp = findViewById(R.id.login_sign_up);
        if (signUp != null) {
            signUp.setOnClickListener(this);
        }
        View facebook = findViewById(R.id.login_facebook);
        if (facebook != null) {
            facebook.setOnClickListener(this);
        }
        View twitter = findViewById(R.id.login_twitter);
        if (twitter != null) {
            twitter.setOnClickListener(this);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgressHelper.dismissProgressDialog();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_login:
                /*Util.hideSoftKeyboard(this);
                if (!NetworkUtil.isNetworkAvailable(this)) {
                    showToastShort(R.string.networkerror);
                    return;
                }*/
                if (validateInput()) {
                    mProgressHelper.showProgressDialog(getResources().getString(R.string.account_tip_login_ing));
                    getRequestToken();
                }
                break;
            /*case R.id.login_forget_pwd:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(LoginUtils.FIND_PASSWORD_URL)));
                break;
            case R.id.login_sign_up:
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
//                finish();
                break;
            case R.id.login_facebook:
                Intent fIntent = new Intent(LoginActivity.this, SocialLoginAct.class);
                fIntent.putExtra("type", LoginUtils.TYPE_FACEBOOK);
                startActivity(fIntent);
                break;
            case R.id.login_twitter:
                Intent tIntent = new Intent(LoginActivity.this, SocialLoginAct.class);
                tIntent.putExtra("type", LoginUtils.TYPE_TWITTER);
                startActivity(tIntent);
                break;*/
        }
    }

    protected void getRequestToken() {
        /*String requestUrl = ProtocolEncode.encodeRequestToken(this);
        StringRequest request = new StringRequest(Request.Method.GET, requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (LoginUtils.isResponseOk(s)) {
                    RequestTokenBean requestToken = new Gson().fromJson(s, RequestTokenBean.class);
                    ProtocolPreferences.setTokenRequest(LoginActivity.this, requestToken.getRequest_token());
                    login();
                } else {
                    mProgressHelper.dismissProgressDialog();
                    showToastShort(R.string.account_tip_login_failed);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mProgressHelper.dismissProgressDialog();
                showToastShort(R.string.account_tip_login_failed);
            }
        });
        mRequestQueue.add(request);*/
    }


    private boolean validateInput() {
        String userName = mEmailEdt.getInputText();
        String passWd = mPwdEdt.getInputText();
        /*if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(passWd)) {
            showToastShort(R.string.account_tip_login_info);
            return false;
        } else if (!LoginUtils.validateEmailAddress(userName)) {
            showToastShort(R.string.account_tip_email_format);
            return false;
        } else*/
            return true;
    }


    private void login() {
        /*String userName = mEmailEdt.getInputText();
        String passWd = mPwdEdt.getInputText();
        String loginUrl = ProtocolEncode.encodeCommonLogin(this, userName, passWd);

        StringRequest request = new StringRequest(Request.Method.GET, loginUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (LoginUtils.isResponseOk(s)) {
                    ProtocolPreferences.setAccountInfo(getApplicationContext(), s);
                    ProtocolPreferences.setPotocolLoginTimestamp(LoginActivity.this, "" + System.currentTimeMillis() / 1000);
                    CommonLoginBean bean = new Gson().fromJson(s, CommonLoginBean.class);
                    ProtocolPreferences.setTokenAccess(getApplicationContext(), bean.getAccess_token());
                    requestUserInfo(bean.getMember_id());
                    GACollect.getInstance().CollectEvent("LoginSuccess");
                } else {
                    mProgressHelper.dismissProgressDialog();
                    showToastShort(R.string.account_name_or_password_wrong);
                    GACollect.getInstance().CollectEvent("LoginFail", getString(R.string.account_name_or_password_wrong));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mProgressHelper.dismissProgressDialog();
                showToastShort(R.string.account_tip_login_failed);
                GACollect.getInstance().CollectEvent("LoginFail", getString(R.string.account_tip_login_failed));
            }
        });
        mRequestQueue.add(request);*/
    }


    /*private void requestUserInfo(final String memberId) {
        String url = ProtocolEncode.encodeGetUserInfo(this, memberId);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
//                mProgressHelper.dismissProgressDialog();
                if (LoginUtils.isResponseOk(s)) {
                    s = s.replaceFirst("\"social_network\":\\[.*?\\],", "");
                    ProtocolPreferences.setUserInfo(LoginActivity.this, s);
                    ProtocolPreferences.setMemberId(getApplicationContext(), memberId);
                    PrefsAccessor.getInstance(LoginActivity.this).saveBoolean(Constants.KEY_THIRD_LOGIN, false);
                } else {
                    mProgressHelper.dismissProgressDialog();
                    showToastShort(R.string.account_tip_login_failed);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mProgressHelper.dismissProgressDialog();
                showToastShort(R.string.account_tip_login_failed);
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String str = null;
                try {
                    str = new String(response.data, getParamsEncoding());
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
            }

        };
        mRequestQueue.add(request);
    }*/
}
