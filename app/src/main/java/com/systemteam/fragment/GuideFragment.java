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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.systemteam.R;
import com.systemteam.activity.BreakActivity;
import com.systemteam.util.Constant;
import com.systemteam.util.Utils;

/**
 */
public class GuideFragment extends BaseFragment{


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
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        int type = -1;
        switch (v.getId()){
            case R.id.tr_break:
                type = Constant.BREAK_TYPE_BREAK;
                break;
            case R.id.tr_lock:
                type = Constant.BREAK_TYPE_LOCK;
                break;
            case R.id.tr_pay:
                type = Constant.GUIDE_TYPE_PAY;
                break;
            case R.id.tr_protocol:
                type = Constant.GUIDE_TYPE_PROCOTOL;
                break;
        }
        if(type == Constant.BREAK_TYPE_LOCK || type == Constant.BREAK_TYPE_BREAK){
            Intent intent = new Intent(getActivity(), BreakActivity.class);
            intent.putExtra(Constant.BUNDLE_TYPE_MENU, type);
            startActivity(intent);
        }else {
            Utils.showProtocol(getActivity(), type);
        }
    }

}
