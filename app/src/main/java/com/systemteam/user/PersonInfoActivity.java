package com.systemteam.user;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.systemteam.R;

import java.io.UnsupportedEncodingException;


public class PersonInfoActivity extends BaseToolbarActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

    private ImageView mAvatar;
    private TextView mName;
    private ImageButton mCamera;
    private Button mLogOut;

    private RequestQueue mRequestQueue;
    private UserInfoBean mUserInfoBean;
    private AccountManager mAccountManager;
//    private ProgressDialogHelper mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);

        EventBus.getDefault().register(this);
        initializeView();
        initializeData();

    }

    @Override
    protected void initializeToolbar(int layoutResID) {
        mToolbarHelper = new ToolbarHelper(this, layoutResID);
        mToolbar = mToolbarHelper.getToolBar();
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mToolbar.setNavigationIcon(R.drawable.ic_back_white);
        setContentView(mToolbarHelper.getContentView());
        /*把 mToolbar 设置到Activity 中*/
        setSupportActionBar(mToolbar);
        /*自定义的一些操作*/
        onCreateCustomToolBar(mToolbar);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_person_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initializeView() {

//        mProgress = new ProgressDialogHelper(this);

        mAvatar = (ImageView) findViewById(R.id.person_avatar);
        mName = (TextView) findViewById(R.id.person_name);
        mLogOut = (Button) findViewById(R.id.person_log_out);
        View mChangePwd = findViewById(R.id.person_change_pwd);

        mLogOut.setOnClickListener(this);
        if (mChangePwd != null) {
            mChangePwd.setOnClickListener(this);
        }
        mToolbar.setOnMenuItemClickListener(this);

        getSupportActionBar().setTitle(R.string.title_profile);

        if (PrefsAccessor.getInstance(PersonInfoActivity.this).getBoolean(Constants.KEY_THIRD_LOGIN, false)) {
            mChangePwd.setVisibility(View.INVISIBLE);
        }
    }

    private void initializeData() {
        mRequestQueue = Volley.newRequestQueue(this);

        mAccountManager = AccountManager.getInstance();
        mAccountManager.reload(this);

        if (mAccountManager.hasUserInfo(this)) {
            mName.setText(mAccountManager.getNickName(this));

            Glide.with(this).load(mAccountManager.getAvatar()).asBitmap().placeholder(R.drawable.account_default_head_portrait).centerCrop().into(new BitmapImageViewTarget(mAvatar) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(PersonInfoActivity.this.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    mAvatar.setImageDrawable(circularBitmapDrawable);
                }
            });
        } else {
            String url = ProtocolEncode.encodeGetUserInfo(this);
            requestUserInfo(url);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 修改信息回调
     *
     * @param userInfoBean
     */
    @Subscribe
    public void onUserInfoChanged(UserInfoBean userInfoBean) {
        if (userInfoBean != null) {
            mName.setText(userInfoBean.getNickname());
            Glide.with(this).load(userInfoBean.getAvatar()).asBitmap().placeholder(R.drawable.account_default_head_portrait).centerCrop().into(new BitmapImageViewTarget(mAvatar) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(PersonInfoActivity.this.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    mAvatar.setImageDrawable(circularBitmapDrawable);
                }
            });
        }
    }


    private void requestUserInfo(String url) {
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (LoginUtils.isResponseOk(s)) {
                    s = s.replaceFirst("\"social_network\":\\[.*?\\],", "");
                    ProtocolPreferences.setUserInfo(PersonInfoActivity.this, s);
                    mUserInfoBean = new Gson().fromJson(s, UserInfoBean.class);
                    mName.setText(mUserInfoBean.getNickname());

                    Glide.with(PersonInfoActivity.this).load(mUserInfoBean.getAvatar()).asBitmap().placeholder(R.drawable.account_default_head_portrait).centerCrop().into(new BitmapImageViewTarget(mAvatar) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(PersonInfoActivity.this.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            mAvatar.setImageDrawable(circularBitmapDrawable);
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_log_out:
                mContext.getSharedPreferences(Constants.SHAERD_FILE_NAME, Context.MODE_PRIVATE).edit().remove(com.wondershare.dfnexpert.utils.Constants.Shared_Experter.STATUS_CODE);
//                mProgress.showProgressDialog(getString(R.string.account_tip_logging_out));
                String urlLogout = ProtocolEncode.encodeAuthLogout(PersonInfoActivity.this);
                StringRequest request = new StringRequest(Request.Method.GET, urlLogout, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (LoginUtils.isResponseOk(s)) {

                            // delegate startActivity to requestImInfo@GetMemberId, automatic finish this
//                        PersonInfoActivity.this.finish();

                        } else {
//                            mProgress.dismissProgressDialog();
                            showToastShort(R.string.account_tip_logging_out_failed);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        showToastShort(R.string.account_tip_logging_out_failed);
                    }
                });
                mRequestQueue.add(request);
                mAccountManager.processLogout(getApplicationContext());
                PrefsAccessor.getInstance(this).remove(Constants.KEY_THIRD_LOGIN);
                ProtocolPreferences.RefreshId(getApplicationContext());
                Intent intent = new Intent(PersonInfoActivity.this, LoginActivity.class);
                intent.putExtra(LoginActivity.RestartApp, true);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                stopService(new Intent(this, MonitorService.class));

                break;

            case R.id.person_change_pwd:
                startActivity(new Intent(this, ChangePasswordAct.class));
                break;


        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                startActivity(new Intent(PersonInfoActivity.this, PersonInfoEditActivity.class));
                break;
        }
        return false;
    }
}
