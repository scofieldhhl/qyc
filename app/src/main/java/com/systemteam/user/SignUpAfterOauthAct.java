package com.systemteam.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.systemteam.R;


public class SignUpAfterOauthAct extends RequestBaseAct implements View.OnClickListener {

    private String type;
    private String mMemberId;
    private int oauthSite;

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
        type = getIntent().getStringExtra("type");
        mMemberId = getIntent().getStringExtra(ProtocolPreferences.MEMBER_ID);
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
            mPwd.setVisibility(View.GONE);
            mPwd.setMaxLength(20);
        }
        if (mConfirmPwd != null) {
            mConfirmPwd.setVisibility(View.GONE);
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

    }

    @Override
    protected void setupResponseCallback() {
        listener = new ResponseListener(this) {
            @Override
            public void onResponse(String s) {
                super.onResponse(s);
                if (LoginUtils.isResponseOk(s))
                    supplementUserInfo();
            }
        };
    }

    private void supplementUserInfo() {
        String email = mEmail.getInputText();
        String firstName = mFirstName.getInputText();
        String lastName = mLastName.getInputText();
        String fixMailUrl = ProtocolEncode.encodeInfoFixemail(this, email, firstName, lastName, mMemberId);
        StringRequest request = new StringRequest(Request.Method.GET, fixMailUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (LoginUtils.isResponseOk(s)) {
                    Intent it = new Intent(SignUpAfterOauthAct.this, SocialLoginAct.class);
                    it.putExtra("type", type);
                    startActivity(it);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                DFNToast.Show(SignUpAfterOauthAct.this, volleyError.toString(), Toast.LENGTH_LONG);
            }
        });
        requestQueue.add(request);
    }

    private boolean validateInput() {
        String email = mEmail.getInputText();
        String firstName = mFirstName.getInputText();
        String lastName = mLastName.getInputText();
        if (TextUtils.isEmpty(email)) {
            showToastShort(R.string.account_tip_email);
            return false;
        } else if (!LoginUtils.validateEmailAddress(email)) {
            showToastShort(R.string.account_tip_email_format);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_register:
                if (validateInput()) {
                    mProgressHelper.showProgressDialog(getResources().getString(R.string.account_tip_register_ing));
                    getRequestToken(listener);
                }
                break;
        }
    }
}
