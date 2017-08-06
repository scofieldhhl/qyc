/*
 *  Copyright 2014 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package com.systemteam.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.systemteam.BikeApplication;
import com.systemteam.R;
import com.systemteam.bean.Feedback;
import com.systemteam.bean.MyUser;
import com.systemteam.util.LogTool;
import com.systemteam.util.Utils;
import com.systemteam.view.ProgressDialogHelper;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.systemteam.BaseActivity.loge;

/**
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
    private Context mContext;
    protected InputMethodManager mImm;
    protected ProgressDialogHelper mProgressHelper;
    private Dialog mFeedbackDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mProgressHelper = new ProgressDialogHelper(mContext);
//        checkNetworkAvailable(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        LogTool.d("onPreferenceTreeClick: " + preference.getKey());
        if(preference.getKey().endsWith(getString(R.string.pref_check_update_key))){
            checkAppUpdate();
        }else if(preference.getKey().endsWith(getString(R.string.pref_about_us_key))){
            showAboutDialog();
        }else if(preference.getKey().endsWith(getString(R.string.pref_feed_back_key))){
            showFeedbackDialog();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, final Object newValue) {
        LogTool.d("onPreferenceChange: " + preference.getKey());
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    protected void checkAppUpdate() {
        //TODO 检测新版本
        Toast.makeText(getActivity(), R.string.no_update, Toast.LENGTH_SHORT).show();
    }

    public Dialog showFeedbackDialog() {
        mFeedbackDialog = new Dialog(mContext, R.style.MyDialog);
        //设置它的ContentView
        mFeedbackDialog.setContentView(R.layout.activity_feedback);
        mFeedbackDialog.setCancelable(true);
        mFeedbackDialog.setCanceledOnTouchOutside(true);
        mFeedbackDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mFeedbackDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        mFeedbackDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        final EditText mEtEmail = (EditText) mFeedbackDialog.findViewById(R.id.et_email);
        final EditText mEtContent = (EditText) mFeedbackDialog.findViewById(R.id.et_content);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.submit:
                        Feedback feedback = new Feedback(BmobUser.getCurrentUser(MyUser.class), BikeApplication.mCurrentAddress,
                                String.valueOf(mEtEmail.getText()), String.valueOf(mEtContent.getText()));
                        if(TextUtils.isEmpty(feedback.getContent())){
                            Toast.makeText(getActivity(), R.string.feedback_null, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(!TextUtils.isEmpty(feedback.getEmail()) && Utils.isEmail(feedback.getEmail())){
                           Toast.makeText(getActivity(), R.string.email_not_valid, Toast.LENGTH_SHORT).show();
                           return;
                        }
                        saveNewObject(feedback);
                        break;
                    case R.id.menu_icon:
                        mImm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                        mFeedbackDialog.dismiss();
                        break;
                }
            }
        };
        mFeedbackDialog.findViewById(R.id.submit).setOnClickListener(listener);
        mFeedbackDialog.findViewById(R.id.menu_icon).setOnClickListener(listener);
        mFeedbackDialog.show();
        return mFeedbackDialog;
    }

    public Dialog showAboutDialog() {
        final Dialog dialog = new Dialog(mContext, R.style.MyDialog);
        //设置它的ContentView
        dialog.setContentView(R.layout.layout_about);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.menu_icon:
                        mImm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                        dialog.dismiss();
                        break;
                }
            }
        };
        dialog.findViewById(R.id.menu_icon).setOnClickListener(listener);
        dialog.show();
        return dialog;
    }

    private void saveNewObject(Feedback feedback){
        mProgressHelper.showProgressDialog(getString(R.string.submiting));
        addSubscription(feedback.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                mProgressHelper.dismissProgressDialog();
                if(e==null){
                    Toast.makeText(getActivity(), R.string.submit_success, Toast.LENGTH_SHORT).show();
                    mFeedbackDialog.dismiss();
                }else{
                    loge(e);
                    Toast.makeText(getActivity(), R.string.submit_faile, Toast.LENGTH_SHORT).show();
                }
            }
        }));
    }

    private CompositeSubscription mCompositeSubscription;
    /**
     * 解决Subscription内存泄露问题
     * @param s
     */
    protected void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }
        this.mCompositeSubscription.add(s);
    }

}
