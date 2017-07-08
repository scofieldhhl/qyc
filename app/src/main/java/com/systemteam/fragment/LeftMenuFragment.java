package com.systemteam.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.systemteam.R;
import com.systemteam.bean.MyUser;

import static com.systemteam.util.Constant.BUNDLE_USER;

/**
 * Created by gaolei on 17/1/5.
 */

public class LeftMenuFragment extends BaseFragment {

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
    protected void initData() {
    }

    @Override
    public void onClick(View v) {

    }

}
