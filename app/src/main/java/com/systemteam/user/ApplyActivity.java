package com.systemteam.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.systemteam.BaseActivity;
import com.systemteam.BikeApplication;
import com.systemteam.R;
import com.systemteam.bean.MyUser;
import com.systemteam.util.Constant;
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
import static com.systemteam.util.Utils.imm;

/**
 * @author scofield.hhl@gmail.com
 * @Description
 * @time 2016/6/16
 */
public class ApplyActivity extends BaseActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
    private ImageView mIvUserPhoto, mIvAddPhoto;
    private IconEditTextView mIetName, mIetPhone, mIetEmail, mIetAddress, mIetSex;
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
        initToolBar(this, R.string.merchant_title);
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
        mIetSex = (IconEditTextView) findViewById(R.id.iet_sex);
        mProgressHelper = new ProgressDialogHelper(this);
    }

    @Override
    protected void initData() {
        mSharedPre = mContext.getSharedPreferences(Constant.SHAERD_FILE_NAME, Context.MODE_PRIVATE);
        initInfo(((BikeApplication)this.getApplication()).getmUser());
    }

    private void initInfo(MyUser user){
        if(user != null){
            mIetName.setText(user.getUsername());
            loadAvatar(mContext, user.getPhotoPath(), mIvUserPhoto);
            mIetPhone.setText(user.getMobilePhoneNumber());
            if(user.getSex() != null){
                mIetSex.setText(getString(user.getSex() ? R.string.man : R.string.woman));
            }else {
                mIetSex.setText(getString(R.string.man));
            }
            mIetName.setText(user.getEmail());
        }else {
            mIetName.setText("");
            mIetPhone.setText("");
            mIetSex.setText("");
            mIetEmail.setText("");
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
        switch (v.getId()) {
            case R.id.iv_close:
                finish();
                break;
            case R.id.iet_sex:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        imm.hideSoftInputFromWindow(buttonView.getWindowToken(), 0); //强制隐藏键盘
    }

    /**
     * 提交前判空提醒
     */
    private boolean checkInput() {
        String fist_name = mIetName.getInputText();
        String email = mIetEmail.getInputText();
        String Addr = mIetAddress.getInputText();
        String phoneNum = mIetPhone.getInputText();
        if (fist_name == null || TextUtils.isEmpty(fist_name)) {
            Toast.makeText(mContext, getString(R.string.apply_check_input_null,
                    getString(R.string.account_hint_nickname)), Toast.LENGTH_SHORT).show();
            return false;
        } else if (email == null || TextUtils.isEmpty(email)) {
            Toast.makeText(mContext, getString(R.string.apply_check_input_null,
                    getString(R.string.account_hint_email)), Toast.LENGTH_SHORT).show();
            return false;
        } else if (Addr == null || TextUtils.isEmpty(Addr)) {
            Toast.makeText(mContext, getString(R.string.apply_check_input_null,
                    getString(R.string.address)), Toast.LENGTH_SHORT).show();
            return false;
        } else if (phoneNum == null || TextUtils.isEmpty(phoneNum)) {
            Toast.makeText(mContext, getString(R.string.apply_check_input_null,
                    getString(R.string.account_hint_phone)), Toast.LENGTH_SHORT).show();
            return false;
        }


        /*else if(skill == null || TextUtils.isEmpty(skill)){
            DFNToast.Show(mContext, getString(R.string.apply_skill_info) + getString(R.string.apply_check_input_null), Toast.LENGTH_SHORT);
            return false;
        }*/
        return true;
    }

    public void doSelectPicture(View view){
        MultiImageSelector.create(mContext)
                .showCamera(true)
                .count(1)
                .single()
                .start(ApplyActivity.this, REQUEST_IMAGE);
    }

    public void doSignOut(View view){
//        checkInput();
        updateUser();
    }

    private void updateUser() {
        MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
        if (bmobUser != null) {
            final MyUser newUser = new MyUser();
            newUser.setAge(25);
            newUser.setSex(false);
            newUser.setType(1);
            addSubscription(newUser.update(bmobUser.getObjectId(),new UpdateListener() {

                @Override
                public void done(BmobException e) {
                    if(e==null){
                        Toast.makeText(mContext, R.string.reg_success, Toast.LENGTH_SHORT).show();
                    }else{
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
