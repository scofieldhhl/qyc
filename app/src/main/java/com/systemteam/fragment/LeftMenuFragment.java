package com.systemteam.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.systemteam.R;
import com.systemteam.bean.MyUser;

import cn.bmob.v3.BmobUser;

import static com.systemteam.util.Constant.BUNDLE_USER;

/**
 * Created by gaolei on 17/1/5.
 */

public class LeftMenuFragment extends BaseFragment {

    private LinearLayout mLLMycar;
    public LeftMenuFragment(){
    }

    public static LeftMenuFragment newInstance(MyUser user){
        LeftMenuFragment fragment = new LeftMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable( BUNDLE_USER, user);
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.main_menu,null);
        initView(view);
        return view;
    }

    @Override
    protected void initView(View view) {
        mLLMycar = (LinearLayout) view.findViewById(R.id.ll_mycar);
        if(mUser != null){
            ((TextView) view.findViewById(R.id.user_name)).setText(mUser.getUsername());
        }
        try {
            String pkName = mContext.getPackageName();
            String versionName = mContext.getPackageManager().getPackageInfo(pkName, 0).versionName;
            versionName = versionName.substring(0, versionName.lastIndexOf("."));
            ((TextView) view.findViewById(R.id.tv_version)).setText(versionName);
        } catch (Exception e) {

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void initData() {
        mUser = BmobUser.getCurrentUser(MyUser.class);
        if(mUser != null){
            if(mUser.getType() == 1){
                mLLMycar.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

}
