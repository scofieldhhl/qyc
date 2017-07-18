package com.systemteam.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.systemteam.BaseActivity;
import com.systemteam.R;
import com.systemteam.bean.MyUser;

import cn.bmob.v3.BmobUser;

import static com.systemteam.util.Constant.BUNDLE_USER;

/**
 * Created by gaolei on 17/1/5.
 */

public class LeftMenuFragment extends BaseFragment {

    private LinearLayout mLLMycar;
    private ImageView mPhoto;
    private TextView mTvName, mTvVersion, mTvCoupon;
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
        mTvName = (TextView) view.findViewById(R.id.user_name);
        mPhoto = (ImageView) view.findViewById(R.id.user_photo);
        mTvVersion = (TextView) view.findViewById(R.id.tv_version);
        mTvCoupon = (TextView) view.findViewById(R.id.tv_coupon);
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
            (mTvName).setText(mUser.getUsername());
            ((BaseActivity)getActivity()).loadAvatar(getActivity(), mUser.getPhotoPath(), mPhoto);
            mTvCoupon.setText(mUser.getCoupon() == null ? "0" : String.valueOf(mUser.getCoupon()));
        }
        try {
            String pkName = mContext.getPackageName();
            String versionName = mContext.getPackageManager().getPackageInfo(pkName, 0).versionName;
            mTvVersion.setText(versionName);
        } catch (Exception e) {

        }
        if(mUser != null && mUser.getType() != null){
            if(mUser.getType() == 1){
                mLLMycar.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {

    }

}
