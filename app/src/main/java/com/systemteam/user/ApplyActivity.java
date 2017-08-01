package com.systemteam.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.systemteam.BaseActivity;
import com.systemteam.BikeApplication;
import com.systemteam.R;
import com.systemteam.bean.MyUser;
import com.systemteam.util.Constant;
import com.systemteam.util.Utils;
import com.systemteam.view.IconEditTextView;
import com.systemteam.view.ProgressDialogHelper;

import java.lang.ref.WeakReference;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

import static com.systemteam.util.Constant.REQUEST_IMAGE;
import static com.systemteam.util.Constant.USER_TYPE_APPLYING;
import static com.systemteam.util.Utils.imm;

/**
 * @author scofield.hhl@gmail.com
 * @Description
 * @time 2016/6/16
 */
public class ApplyActivity extends BaseActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
    private ImageView mIvUserPhoto, mIvAddPhoto;
    private IconEditTextView mIetName, mIetPhone, mIetEmail, mIetAddress;
    private RadioButton mRbMan, mRbWoman;
    private String mAvatarPath;
    private static class MyHandler extends Handler {
        private WeakReference<ApplyActivity> mActivity;

        public MyHandler(ApplyActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final ApplyActivity theActivity = mActivity.get();
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    theActivity.loadAvatar(theActivity, theActivity.mAvatarPath, theActivity.mIvUserPhoto);
                    break;
            }
        }
    }

    private MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);
        mUser = BmobUser.getCurrentUser(MyUser.class);
        if(mUser.getType() == null) {
            initToolBar(this, R.string.merchant_title);
        }else {
            initToolBar(this, R.string.edit_info);
        }
        mContext = this;
        mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        initView();
        initData();
    }

    protected void initView() {
        mIvUserPhoto = (ImageView) findViewById(R.id.iv_user_photo);
        mIvAddPhoto = (ImageView) findViewById(R.id.iv_add_photo);
        mIvAddPhoto.setVisibility(View.VISIBLE);
        mIetName = (IconEditTextView) findViewById(R.id.iet_name);
        mIetPhone = (IconEditTextView) findViewById(R.id.iet_phone);
        mIetEmail = (IconEditTextView) findViewById(R.id.iet_email);
        mIetAddress = (IconEditTextView) findViewById(R.id.iet_address);
        mIetAddress.setText(BikeApplication.mCurrentAddress);
        mRbMan = (RadioButton) findViewById(R.id.rb_man);
        mRbWoman = (RadioButton) findViewById(R.id.rb_woman);
        mProgressHelper = new ProgressDialogHelper(this);
    }

    @Override
    protected void initData() {
        mSharedPre = mContext.getSharedPreferences(Constant.SHAERD_FILE_NAME, Context.MODE_PRIVATE);
        initInfo(mUser);
    }

    private void initInfo(MyUser user){
        if(user != null){
            loadAvatar(mContext, user.getPhotoPath(), mIvUserPhoto);
            mIetName.setText(user.getUsername());
            mIetPhone.setText(user.getMobilePhoneNumber());
            if(user.getSex() != null){
                mRbWoman.setChecked(user.getSex());
            }else {
                mRbMan.setChecked(true);
            }
            mIetEmail.setText(user.getEmail());
            if(!TextUtils.isEmpty(user.getAddress()))
                mIetAddress.setText(user.getAddress());
        }else {
            mIetName.setText("");
            mIetPhone.setText("");
            mRbMan.setChecked(true);
            mIetEmail.setText("");
            mIetAddress.setText("");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }



    @Override
    public void onClick(View v) {
        v.requestFocus();
        mImm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        imm.hideSoftInputFromWindow(buttonView.getWindowToken(), 0); //强制隐藏键盘
    }

    /**
     * 提交前判空提醒
     */
    private boolean checkInput() {
        mUser.setUsername(mIetName.getInputText());
        mUser.setEmail(mIetEmail.getInputText());
        mUser.setAddress(mIetAddress.getInputText());
        if (mUser.getUsername() == null || TextUtils.isEmpty(mUser.getUsername())) {
            toast(getString(R.string.apply_check_input_null,
                    getString(R.string.account_hint_nickname)));
            return false;
        } else if (!TextUtils.isEmpty(mUser.getEmail())) {
            if(Utils.isEmail(mUser.getEmail())){
                toast(getString(R.string.account_tip_email_format));
                return false;
            }
        } else if (mUser.getAddress() == null || TextUtils.isEmpty(mUser.getAddress())) {
            toast(getString(R.string.apply_check_input_null,
                    getString(R.string.address)));
            return false;
        }
        return true;
    }

    public void doSelectPicture(View view){
        MultiImageSelector.create(mContext)
                .showCamera(true)
                .count(1)
                .single()
                .start(ApplyActivity.this, REQUEST_IMAGE);
    }

    public void doApply(View view){
        if(checkInput())
            updateUser();
    }
    String msg;
    private void updateUser() {
        if (mUser != null) {
            mProgressHelper.showProgressDialog(getString(R.string.submiting));
            MyUser newUser = new MyUser();
            newUser.setUsername(mUser.getUsername());
            newUser.setSex(mRbMan.isChecked());
            newUser.setEmail(mUser.getEmail());
            newUser.setAddress(mUser.getAddress());
            msg = getString(R.string.edit_info_success);
            if(mUser.getType() == null){
                newUser.setType(USER_TYPE_APPLYING);
                msg = getString(R.string.apply_success_hint);
            }
            addSubscription(newUser.update(mUser.getObjectId(),new UpdateListener() {

                @Override
                public void done(BmobException e) {
                    mProgressHelper.dismissProgressDialog();
                    if(e==null){
                        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                        alertDialog.setTitle(getString(R.string.submit_success));
                        alertDialog.setMessage(msg);
                        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, mContext.getString(R.string.confirm),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ApplyActivity.this.finish();
                                    }
                                });
                        alertDialog.show();
                    }else{
                        toast(getString(R.string.submit_faile));
                        loge(e);
                    }
                }
            }));
        } else {
            checkUser(this);
        }
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
