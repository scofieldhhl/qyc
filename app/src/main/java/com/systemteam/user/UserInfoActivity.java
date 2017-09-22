package com.systemteam.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TextView;

import com.systemteam.BaseActivity;
import com.systemteam.BikeApplication;
import com.systemteam.R;
import com.systemteam.bean.MyUser;
import com.systemteam.view.IconEditTextView;

import java.lang.ref.WeakReference;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import static com.systemteam.util.Constant.MSG_LOGOOUT;
import static com.systemteam.util.Constant.MSG_UPDATE_UI;
import static com.systemteam.util.Constant.REQUEST_IMAGE;
import static com.systemteam.util.Constant.USER_TYPE_APPLYING;
import static com.systemteam.util.Constant.USER_TYPE_CUSTOMER;
import static com.systemteam.util.Constant.USER_TYPE_EXPERTER;
//TODO 正在使用中是否允许登出
public class UserInfoActivity extends BaseActivity {
    private String mAvatarPath;
    private ImageView mIvUserPhoto, mIvAddPhoto;
    private TextView mTvName, mTvPhone, mTvSex, mTvEmail, mTvAddress, mTvApply;
    private TableLayout mRlInfoMore;
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
                case MSG_UPDATE_UI:
                    theActivity.initData();
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
//        initBackgroudColor();
        mIvUserPhoto = (ImageView) findViewById(R.id.iv_user_photo);
        mIvAddPhoto = (ImageView) findViewById(R.id.iv_add_photo);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvPhone = (TextView) findViewById(R.id.tv_phone);
        mTvSex = (TextView) findViewById(R.id.tv_sex);
        mTvEmail = (TextView) findViewById(R.id.tv_email);
        mTvApply = (TextView) findViewById(R.id.tv_apply);
        mTvAddress  = (TextView) findViewById(R.id.tv_address);
        mRlInfoMore = (TableLayout) findViewById(R.id.tl_info_more);
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
            mTvAddress.setText(user.getAddress());
        }else {
            mTvName.setText("");
            mTvPhone.setText("");
            mTvSex.setText("");
            mTvEmail.setText("");
            mTvAddress.setText("");
        }

        if(user != null && user.getType() != null &&
                (user.getType().intValue() == USER_TYPE_CUSTOMER || user.getType().intValue() == USER_TYPE_APPLYING)){
            mTvApply.setVisibility(View.GONE);
            mRlInfoMore.setVisibility(View.VISIBLE);
            if(user.getType().intValue() == USER_TYPE_APPLYING){
                mIvAddPhoto.setVisibility(View.GONE);
            }
        }else if(user != null && user.getType() != null && (user.getType().intValue() == USER_TYPE_EXPERTER)){
            mIvAddPhoto.setVisibility(View.GONE);
            mTvApply.setVisibility(View.GONE);
            mRlInfoMore.setVisibility(View.GONE);
        }else {
            mIvAddPhoto.setVisibility(View.GONE);
            mRlInfoMore.setVisibility(View.GONE);
            String strApply =  getString(R.string.merchant_apply);
            SpannableString mMoreFeatrue = new SpannableString(strApply);
            mMoreFeatrue.setSpan(new UnderlineSpan(), 0, strApply.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mMoreFeatrue.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.common_blue_main)),
                    0, strApply.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvApply.setText(mMoreFeatrue);
        }
    }

    @Override
    public void onClick(View v) {

    }

    public void doUpdate(View view){
        if(mUser != null && mUser.getType() != null && (mUser.getType().intValue() == USER_TYPE_CUSTOMER
                || mUser.getType().intValue() == USER_TYPE_APPLYING)) {
            startActivity(new Intent(UserInfoActivity.this, ApplyActivity.class));
        }else {
            showInputDialog();
        }
    }

    public void doSubmit(View view){
        /*AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.notice_apply);
        builder.setMessage(R.string.notice_apply_content);
        builder.setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.setNegativeButton(R.string.disagree, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();*/
        startActivity(new Intent(UserInfoActivity.this, ApplyActivity.class));
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
        MultiImageSelector.create()
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

    public Dialog showInputDialog() {
        final Dialog dialog = new Dialog(mContext, R.style.MyDialog);
        //设置它的ContentView
        dialog.setContentView(R.layout.layout_info_input);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        final IconEditTextView mIetName = (IconEditTextView) dialog.findViewById(R.id.iet_name);
        final IconEditTextView mIetPhone = (IconEditTextView) dialog.findViewById(R.id.iet_phone);
        final RadioButton mRbMan = (RadioButton) dialog.findViewById(R.id.rb_man);
        final RadioButton mRbWonman = (RadioButton) dialog.findViewById(R.id.rb_woman);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_unlock:
                        mUser.setUsername(mIetName.getInputText());
                        mUser.setSex(mRbMan.isChecked());
                        if(TextUtils.isEmpty(mUser.getUsername())){
                            mUser.setUsername(mUser.getMobilePhoneNumber());
                        }
                        requestUpdate();
                        mImm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                        dialog.dismiss();
                        break;
                    case R.id.menu_icon:
                        mImm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                        dialog.dismiss();
                        break;
                }
            }
        };
        dialog.findViewById(R.id.btn_unlock).setOnClickListener(listener);
        dialog.findViewById(R.id.menu_icon).setOnClickListener(listener);
        ((IconEditTextView)dialog.findViewById(R.id.iet_phone)).setText(mUser.getMobilePhoneNumber());
        dialog.findViewById(R.id.tv_name_tip).setVisibility(View.GONE);
        if(mUser != null){
            mIetName.setText(mUser.getUsername());
            mIetPhone.setText(mUser.getMobilePhoneNumber());
            if(mUser.getSex() != null){
                if(mUser.getSex()){
                    mRbMan.setChecked(true);
                }else {
                    mRbWonman.setChecked(true);
                }
            }else {
                mRbMan.setChecked(true);
            }
        }
        dialog.show();
        return dialog;
    }

    private void requestUpdate(){
        mProgressHelper.showProgressDialog(getString(R.string.submiting));
        MyUser newUser = new MyUser();
        newUser.setUsername(mUser.getUsername());
        newUser.setSex(mUser.getSex());
        addSubscription(newUser.update(mUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                mProgressHelper.dismissProgressDialog();
                if(e == null){
                    toast(getString(R.string.submit_success));
                    mHandler.sendEmptyMessage(MSG_UPDATE_UI);
                }else {
                    toast(getString(R.string.submit_faile));
                }
            }
        }));
    }
}
