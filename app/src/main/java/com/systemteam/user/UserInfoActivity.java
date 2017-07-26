package com.systemteam.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.systemteam.BaseActivity;
import com.systemteam.BikeApplication;
import com.systemteam.R;
import com.systemteam.bean.MyUser;

import java.lang.ref.WeakReference;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import static com.systemteam.util.Constant.MSG_LOGOOUT;
import static com.systemteam.util.Constant.REQUEST_IMAGE;

public class UserInfoActivity extends BaseActivity {
    private String mAvatarPath;
    private ImageView mIvUserPhoto, mIvAddPhoto;
    private TextView mTvName, mTvPhone, mTvSex, mTvEmail, mTvAddress, mTvApply;
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
                    theActivity.loadAvatar(theActivity, theActivity.mAvatarPath, theActivity.mIvUserPhoto);
                    break;
                case MSG_LOGOOUT:
                    theActivity.initData();
                    theActivity.finish();
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
        mTvApply = (TextView) findViewById(R.id.tv_apply);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void initData() {
        mUser = BmobUser.getCurrentUser(MyUser.class);
        initInfo(mUser);
    }

    private void initInfo(MyUser user){
        if(user != null){
            loadAvatar(mContext, user.getPhotoPath(), mIvUserPhoto);
            mTvName.setText(user.getUsername());
            mTvPhone.setText(user.getMobilePhoneNumber());
            if(user.getSex() != null){
                mTvSex.setText(user.getSex() ? R.string.man : R.string.woman);
            }else {
                mTvSex.setText(R.string.man);
            }
            mTvEmail.setText(user.getEmail());
        }else {
            mTvName.setText("");
            mTvPhone.setText("");
            mTvSex.setText("");
            mTvEmail.setText("");
        }

        if(user != null && user.getType() != null && user.getType().intValue() == 1){
            mTvApply.setVisibility(View.GONE);
        }else {
            String strApply =  getString(R.string.merchant_apply);
            SpannableString mMoreFeatrue = new SpannableString(strApply);
            mMoreFeatrue.setSpan(new UnderlineSpan(), 0, strApply.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mMoreFeatrue.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.common_blue_main)),
                    0, strApply.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvApply.setText(mMoreFeatrue);
        }
    }

    @Override
    public void onClick(View v) {

    }

    public void doSubmit(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.notice_apply);
        builder.setMessage(R.string.notice_apply_content);
        builder.setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(UserInfoActivity.this, ApplyActivity.class));
            }
        });
        builder.setNegativeButton(R.string.disagree, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void doSignOut(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.sign_out_tip);
        builder.setTitle(R.string.tip);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                BmobUser.logOut();
                ((BikeApplication) UserInfoActivity.this.getApplication()).setmUser(null);
                mHandler.sendEmptyMessage(MSG_LOGOOUT);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
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
                    updateUserPhoto(mAvatarPath);
                }
            }
        }
    }

    private void updateUserPhoto(String path) {
        MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
        if (bmobUser != null) {
            MyUser newUser = new MyUser();
            newUser.setPhotoPath(path);
            addSubscription(newUser.update(bmobUser.getObjectId(),new UpdateListener() {

                @Override
                public void done(BmobException e) {
                    if(e==null){
                    }else{
                        loge(e);
                    }
                }
            }));
        } else {
            checkUser(this);
        }
    }
}
