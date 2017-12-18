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

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.systemteam.BikeApplication;
import com.systemteam.R;
import com.systemteam.activity.BreakActivity;
import com.systemteam.push.QRCode;
import com.systemteam.util.Constant;
import com.systemteam.util.Utils;

import java.util.Locale;

/**
 */
public class GuideFragment extends BaseFragment{


    EditText editText;
    ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.layout_guide,null);
        initView(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void initView(View view) {
        view.findViewById(R.id.tr_break).setOnClickListener(this);
        view.findViewById(R.id.tr_lock).setOnClickListener(this);
        view.findViewById(R.id.tr_pay).setOnClickListener(this);
        view.findViewById(R.id.tr_protocol).setOnClickListener(this);
        Button btn = (Button) view.findViewById(R.id.btn_unlock);
        if(BikeApplication.mInstallationId != null && !TextUtils.isEmpty(BikeApplication.mInstallationId)){
            btn.setText(BikeApplication.mInstallationId);
            btn.setOnClickListener(this);
        }
        editText = (EditText)view.findViewById(R.id.et_code);
        imageView = (ImageView)view.findViewById(R.id.iv_newcode);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tr_break:
                Intent intent = new Intent(getActivity(), BreakActivity.class);
                intent.putExtra(Constant.BUNDLE_TYPE_MENU, Constant.BREAK_TYPE_BREAK);
                startActivity(intent);
                break;
            case R.id.tr_lock:
                Intent intent1 = new Intent(getActivity(), BreakActivity.class);
                intent1.putExtra(Constant.BUNDLE_TYPE_MENU, Constant.BREAK_TYPE_LOCK);
                startActivity(intent1);
                break;
            case R.id.tr_pay:
                Utils.showProtocol(getActivity(), Constant.GUIDE_TYPE_PAY);
                break;
            case R.id.tr_protocol:
                Utils.showProtocol(getActivity(), Constant.GUIDE_TYPE_PROCOTOL);
                break;
            case R.id.btn_unlock:
                if(TextUtils.isEmpty(editText.getText().toString())){
                    Toast.makeText(getActivity(), R.string.code_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                String content = String.format(Locale.US,
                        "http://android.myapp.com/myapp/detail.htm?apkName=com.systemteam&addevice=%s&no=%s",
                        BikeApplication.mInstallationId, editText.getText().toString());
                Bitmap bitmap = QRCode.createQRCode(content, 600);
                imageView.setImageBitmap(bitmap);
                break;
        }
    }

}
