package com.systemteam.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.systemteam.BaseActivity;
import com.systemteam.BikeApplication;
import com.systemteam.R;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import static com.systemteam.util.Constant.REQUEST_IMAGE;

public class UserInfoActivity extends BaseActivity {
    private String mAvatarPath;
    private ImageView mIvUserPhoto, mIvAddPhoto;
    private TextView mTvName, mTvPhone, mTvSex, mTvEmail, mTvAddress;
    private static class MyHandler extends Handler {
        private WeakReference<UserInfoActivity> mActivity;

        public MyHandler(UserInfoActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final UserInfoActivity theActivity = mActivity.get();
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if (!TextUtils.isEmpty(theActivity.mAvatarPath)) {
                        File file = new File(theActivity.mAvatarPath);
                        if (file.exists()) {
                            Glide.with(theActivity)
                                    .load(theActivity.mAvatarPath)
                                    .asBitmap()
                                    .placeholder(R.drawable.account_default_head_portrait)
                                    .centerCrop()
                                    .into(new BitmapImageViewTarget(theActivity.mIvUserPhoto) {
                                        @Override
                                        protected void setResource(Bitmap resource) {
                                            RoundedBitmapDrawable circularBitmapDrawable =
                                                    RoundedBitmapDrawableFactory.create(theActivity.getResources(), resource);
                                            circularBitmapDrawable.setCircular(true);
                                            theActivity.mIvUserPhoto.setImageDrawable(circularBitmapDrawable);
                                        }
                                    });
                        }
                    }
                    break;
            }
        }
    }

    private MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        mContext = this;
        initView();
        initData();
    }

    @Override
    protected void initView() {
        initToolBar(this, R.string.account_profile);
        mIvUserPhoto = (ImageView) findViewById(R.id.iv_user_photo);
        mIvAddPhoto = (ImageView) findViewById(R.id.iv_add_photo);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvPhone = (TextView) findViewById(R.id.tv_phone);
        mTvSex = (TextView) findViewById(R.id.tv_sex);
        mTvEmail = (TextView) findViewById(R.id.tv_email);
    }

    @Override
    protected void initData() {
        mUser = ((BikeApplication) this.getApplication()).getmUser();
        if(mUser != null){
            mTvName.setText(mUser.getUsername());
            mTvPhone.setText(mUser.getMobilePhoneNumber());
            if(mUser.getSex() != null){
                mTvSex.setText(mUser.getSex() ? R.string.man : R.string.woman);
            }else {
                mTvSex.setText(R.string.man);
            }
            mTvName.setText(mUser.getEmail());
        }
    }

    @Override
    public void onClick(View v) {

    }

    public void doSubmit(View view){
        startActivity(new Intent(UserInfoActivity.this, ApplyActivity.class));
    }

    public void doSelectPicture(View view){
        MultiImageSelector.create(mContext)
                .showCamera(true)
                .count(1)
                .single()
                .start(UserInfoActivity.this, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE){
            if(resultCode == RESULT_OK){
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (path != null && path.size() > 0) {
                    mAvatarPath = path.get(0);
                    mHandler.sendEmptyMessage(1);
                }
            }
        }
    }
}
