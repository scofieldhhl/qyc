package com.systemteam.user;

import android.accounts.AccountManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.internal.Util;


public class PersonInfoEditActivity extends BaseToolbarActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

    private static final int REQUEST_IMAGE = 2;

    private ImageView mAvatar;
    private EditText mFirstName;
    private EditText mLastName;

    private RequestQueue mRequestQueue;
    private UserInfoBean mUserInfoBean;
    private AccountManager mAccountManager;
    private ProgressDialogHelper mProgressHelper;

    private boolean isAvatarChanged;
    private boolean isNameChanged;

    private String mAvatarPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info_edit);

        EventBus.getDefault().register(this);

        initializeView();

        initializeData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_person_info_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void initializeView() {
        mAvatar = (ImageView) findViewById(R.id.person_avatar);
        mFirstName = (EditText) findViewById(R.id.person_edt_first_name);
        mLastName = (EditText) findViewById(R.id.person_edt_last_name);
        ImageButton mCamera = (ImageButton) findViewById(R.id.person_camera);
        final ImageButton mInputDeleteFirst = (ImageButton) findViewById(R.id.person_edit_first_delete);
        final ImageButton mInputDeleteLast = (ImageButton) findViewById(R.id.person_edit_last_delete);

        if (mCamera != null) {
            mCamera.setOnClickListener(this);
        }
        if (mInputDeleteFirst != null) {
            mInputDeleteFirst.setOnClickListener(this);
        }

        if (mInputDeleteLast != null) {
            mInputDeleteLast.setOnClickListener(this);
        }

        mToolbar.setOnMenuItemClickListener(this);

        getSupportActionBar().setTitle(R.string.title_profile);

        mFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mInputDeleteFirst.setVisibility(View.VISIBLE);
                } else {
                    mInputDeleteFirst.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mInputDeleteLast.setVisibility(View.VISIBLE);
                } else {
                    mInputDeleteLast.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initializeData() {

        mRequestQueue = Volley.newRequestQueue(this);

        mAccountManager = AccountManager.getInstance();
        mAccountManager.reload(this);

        mProgressHelper = new ProgressDialogHelper(this);

        if (mAccountManager.hasUserInfo(this)) {
            mUserInfoBean = mAccountManager.getUserInfo();
            mFirstName.setText(mAccountManager.getFirstName(this));
            mLastName.setText(mAccountManager.getLastName(this));

            Glide.with(this).load(mAccountManager.getAvatar()).asBitmap().placeholder(R.drawable.account_default_head_portrait).centerCrop().into(new BitmapImageViewTarget(mAvatar) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(PersonInfoEditActivity.this.getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    mAvatar.setImageDrawable(circularBitmapDrawable);
                }
            });

        } else {
            requestUserInfo();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 选择头像剪切回调
     *
     * @param image
     */
    @Subscribe
    public void onAvatarSelect(Image image) {
        Log.i("ashes", "image:  " + image.getPath());
        ImageUtil.compressByQuality(image.getPath());
        mAvatarPath = image.getPath();
        Glide.with(this).load(image.getPath()).asBitmap().placeholder(R.drawable.account_default_head_portrait).centerCrop().into(new BitmapImageViewTarget(mAvatar) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(PersonInfoEditActivity.this.getResources(), ImageCompressUtil.compressByQuality(resource));
                circularBitmapDrawable.setCircular(true);
                mAvatar.setImageDrawable(circularBitmapDrawable);
            }
        });
        isAvatarChanged = true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.person_camera:
                MultiImageSelector selector = MultiImageSelector.create(PersonInfoEditActivity.this);
                selector.showCamera(true);
                selector.single();
                selector.clip(true);
                selector.start(PersonInfoEditActivity.this, REQUEST_IMAGE);
                break;


            case R.id.person_edit_first_delete:
                mFirstName.setText("");
                break;
            case R.id.person_edit_last_delete:
                mLastName.setText("");
                break;
        }
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (!NetworkUtil.isNetworkAvailable(this)) {
                    showToastShort(R.string.networkerror);
                    break;
                }
                String firstname = mFirstName.getText().toString();
                if (TextUtils.isEmpty(firstname)) {
                    showToastShort(R.string.account_tip_first_name);
                    break;
                }
                if (firstname.length() > 20) {
                    showToastShort(R.string.account_tip_firstname_length);
                    break;
                }
                String lastname = mLastName.getText().toString();
                if (TextUtils.isEmpty(lastname)) {
                    showToastShort(R.string.account_tip_first_name);
                    break;
                }
                if (lastname.length() > 20) {
                    showToastShort(R.string.account_tip_lastname_length);
                    break;
                }
                if (!firstname.equals(mAccountManager.getFirstName(this)) || !lastname.equals(mAccountManager.getLastName(this))) {
                    isNameChanged = true;
                }
                if (isNameChanged || isAvatarChanged) {
                    mProgressHelper.showProgressDialog(getResources().getString(R.string.account_tip_avatar_uploading));
                } else {
                    finish();
                }
                if (isAvatarChanged) {
                    uploadAvatar();
                } else {
                    if (isNameChanged) {
                        updateUserInfo(mFirstName.getText().toString(), mLastName.getText().toString());
                    }
                }

                break;
        }
        return false;
    }


    private void uploadAvatar() {
        String url = ProtocolEncode.encodeUploadPicture(this, mAvatarPath);
        PhotoMultipartRequest imageUploadReq = new PhotoMultipartRequest<AvatarBean>(url, AvatarBean.class, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (isNameChanged) {
                    updateUserInfo(mFirstName.getText().toString(), mLastName.getText().toString());
                } else {
                    showToastShort(R.string.account_tip_change_avatar_failed + "   " + volleyError.getMessage());
                    mProgressHelper.dismissProgressDialog();
                }
            }
        }, new Response.Listener<AvatarBean>() {
            @Override
            public void onResponse(AvatarBean avatarBean) {
                Log.i("ashes", avatarBean.toString());
                if (!TextUtils.isEmpty(avatarBean.getAvatar())) {
                    showToastShort(R.string.account_tip_change_avatar_success);
                } else {
                    showToastShort(R.string.account_tip_change_avatar_failed);
                }
                if (isNameChanged) {
                    updateUserInfo(mFirstName.getText().toString(), mLastName.getText().toString());
                } else {
                    if (mUserInfoBean != null) {
                        mUserInfoBean.setAvatar(avatarBean.getAvatar());
                        ProtocolPreferences.setUserInfo(PersonInfoEditActivity.this, new Gson().toJson(mUserInfoBean));
                        EventBus.getDefault().post(mUserInfoBean);
                        Util.updateImInfo(PersonInfoEditActivity.this, mRequestQueue);
                    }
                    mProgressHelper.dismissProgressDialog();
                }
            }
        }, new File(mAvatarPath));

        mRequestQueue.add(imageUploadReq);
    }

    private void updateUserInfo(final String firstname, final String lastname) {
        String url = ProtocolEncode.encodeUpdateUserInfo(this, firstname, lastname);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                mProgressHelper.dismissProgressDialog();
                Log.i("ashes", "updateUserInfo:  " + s);
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.optString("status").equals("ok")) {
                        showToastShort(R.string.account_tip_change_name_success);
                        mUserInfoBean.setFirstname(firstname);
                        mUserInfoBean.setLastname(lastname);
                        mUserInfoBean.setNickname(firstname + " " + lastname);
                        ProtocolPreferences.setUserInfo(PersonInfoEditActivity.this, new Gson().toJson(mUserInfoBean));
                        EventBus.getDefault().post(mUserInfoBean);
                        Util.updateImInfo(PersonInfoEditActivity.this, mRequestQueue);
                    } else {
                        showToastShort(R.string.account_tip_change_name_failed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToastShort(R.string.account_tip_change_name_failed);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mProgressHelper.dismissProgressDialog();
                showToastShort(R.string.account_tip_change_name_failed);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("firstname", firstname);
                params.put("lastname", lastname);
                return params;
            }
        };

        mRequestQueue.add(request);
    }


    private void requestUserInfo() {
        String url = ProtocolEncode.encodeGetUserInfo(this);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if(LoginUtils.isResponseOk(s)) {
                    s = s.replaceFirst("\"social_network\":\\[.*?\\],", "");
                    ProtocolPreferences.setUserInfo(PersonInfoEditActivity.this, s);
                    mUserInfoBean = new Gson().fromJson(s, UserInfoBean.class);
                    mFirstName.setText(mUserInfoBean.getFirstname());
                    mLastName.setText(mUserInfoBean.getLastname());

                    Glide.with(PersonInfoEditActivity.this).load(mUserInfoBean.getAvatar()).asBitmap().placeholder(R.drawable.account_default_head_portrait).centerCrop().into(new BitmapImageViewTarget(mAvatar) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(PersonInfoEditActivity.this.getResources(), resource);
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
                    e.printStackTrace();
                }
                return Response.success(str, HttpHeaderParser.parseCacheHeaders(response));
            }

        };
        mRequestQueue.add(request);
    }


}
