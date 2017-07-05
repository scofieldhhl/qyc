package com.systemteam.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.systemteam.MainActivity;
import com.systemteam.R;

import okhttp3.internal.Util;


public class RegisterActivity extends RequestBaseAct implements View.OnClickListener {

    private IconEditFullTextView mEmail;
    private IconEditMissTextView mPwd;
    private IconEditFullTextView mConfirmPwd;
    private IconEditMissTextView mFirstName;
    private IconEditFullTextView mLastName;

    private ProgressDialogHelper mProgressHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeView();
        setupResponseCallback();
        mProgressHelper = new ProgressDialogHelper(this);
//        setUpOutsideTouchListener(findViewById(R.id.register_parent));
        getSupportActionBar().setTitle(R.string.title_register);
    }


    private void initializeView() {
        mEmail = (IconEditFullTextView) findViewById(R.id.register_email);
        mPwd = (IconEditMissTextView) findViewById(R.id.register_pwd);
        mConfirmPwd = (IconEditFullTextView) findViewById(R.id.register_pwd_confirm);
        mFirstName = (IconEditMissTextView) findViewById(R.id.register_first_name);
        mLastName = (IconEditFullTextView) findViewById(R.id.register_last_name);
        if (mEmail != null) {
            mEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            mEmail.setMaxLength(50);
        }
        if (mPwd != null) {
            mPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mPwd.setMaxLength(20);
        }
        if (mConfirmPwd != null) {
            mConfirmPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mConfirmPwd.setMaxLength(20);
        }
        if (mFirstName != null) {
            mFirstName.setMaxLength(20);
        }
        if (mLastName != null) {
            mLastName.setMaxLength(20);
        }
        View register = findViewById(R.id.register_register);
        if (register != null) {
            register.setOnClickListener(this);
        }

        View login = findViewById(R.id.register_login);
        if (login != null) {
            login.setOnClickListener(this);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void setupResponseCallback() {
        listener = new ResponseListener(this) {
            @Override
            public void onResponse(String s) {
                super.onResponse(s);
                registerAccount();
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_register:
                Util.hideSoftKeyboard(this);
                if (!NetworkUtil.isNetworkAvailable(this)) {
                    showToastShort(R.string.networkerror);
                    return;
                }
                if (validateInput()) {
                    mProgressHelper.showProgressDialog(getResources().getString(R.string.account_tip_register_ing));
                    getRequestToken(listener);
                }
                break;

            case R.id.register_login:
//                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }

    private boolean validateInput() {
        String email = mEmail.getInputText();
        String pwd = mPwd.getInputText();
        String confirmPwd = mConfirmPwd.getInputText();
        String firstName = mFirstName.getInputText();
        String lastName = mLastName.getInputText();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd)) {
            showToastShort(R.string.account_tip_login_info);
            return false;
        } else if (!LoginUtils.validateEmailAddress(email)) {
            showToastShort(R.string.account_tip_email_format);
            return false;
        } else if (!pwd.equals(confirmPwd)) {
            showToastShort(R.string.account_passwords_different);
            return false;
        } else if (pwd.length() < 6 || pwd.length() > 16) {
            showToastShort(R.string.account_tip_pwd_length);
            return false;
        } else if ("".equals(firstName)) {
            showToastShort(R.string.account_tip_name);
            return false;
        } else if (firstName.length() > 20) {
            showToastShort(R.string.account_tip_firstname_length);
            return false;
        } else if ("".equals(lastName)) {
            showToastShort(R.string.account_tip_last_name);
            return false;
        } else if (lastName.length() > 20) {
            showToastShort(R.string.account_tip_lastname_length);
            return false;
        } else
            return true;
    }

    private void registerAccount() {
        final String email = mEmail.getInputText();
        String pwd = mPwd.getInputText();
        final String firstName = mFirstName.getInputText();
        final String lastName = mLastName.getInputText();
        final String regUrl = ProtocolEncode.encodeAuthRegister(RegisterActivity.this, email, pwd, firstName, lastName);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, regUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                mProgressHelper.dismissProgressDialog();
                if (LoginUtils.isResponseOk(s)) {
                    AuthRegisterBean bean = new Gson().fromJson(s, AuthRegisterBean.class);

                    UserInfoBean mUserInfoBean = new UserInfoBean();
                    mUserInfoBean.setFirstname(firstName);
                    mUserInfoBean.setLastname(lastName);
                    mUserInfoBean.setNickname(firstName + " " + lastName);
                    mUserInfoBean.setEmail(email);
                    ProtocolPreferences.setUserInfo(RegisterActivity.this, new Gson().toJson(mUserInfoBean));

                    ProtocolPreferences.setRegInfo(RegisterActivity.this, s);
                    ProtocolPreferences.setMemberId(RegisterActivity.this, bean.getMember_id());
                    ProtocolPreferences.setPotocolLoginTimestamp(RegisterActivity.this, "" + System.currentTimeMillis() / 1000);
                    ProtocolPreferences.setTokenAccess(RegisterActivity.this, bean.getAccess_token());
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    PrefsAccessor.getInstance(RegisterActivity.this).saveBoolean(Constants.KEY_THIRD_LOGIN, false);
                    GACollect.getInstance().CollectEvent("SignUpSuccess");
                } else {
                    showToastShort(R.string.account_reg_email_registered);
                    GACollect.getInstance().CollectEvent("SignUpFail", getString(R.string.account_reg_email_registered));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mProgressHelper.dismissProgressDialog();
                showToastShort(volleyError.toString());
                GACollect.getInstance().CollectEvent("SignUpFail", volleyError.toString());
            }
        });
        requestQueue.add(stringRequest);
    }
}
