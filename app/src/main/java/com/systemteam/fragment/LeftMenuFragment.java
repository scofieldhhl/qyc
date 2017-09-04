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
import com.systemteam.util.ProtocolPreferences;

import cn.bmob.v3.BmobUser;

import static com.systemteam.util.Constant.BUNDLE_USER;

/**
 * Created by gaolei on 17/1/5.
 */

public class LeftMenuFragment extends BaseFragment {

    private LinearLayout mLLMycar;
    private ImageView mPhoto;
    private TextView mTvName, mTvVersion, mTvCoupon, mTvBalance, mTvCarCount;
    public LeftMenuFragment(){}

    public static LeftMenuFragment newInstance(MyUser user){
        LeftMenuFragment fragment = new LeftMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable( BUNDLE_USER, user);
        fragment.setArguments(bundle);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
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
        mTvBalance = (TextView) view.findViewById(R.id.tv_balance);
        mTvCarCount = (TextView) view.findViewById(R.id.tv_carcount);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void initData() {
        mUser = BmobUser.getCurrentUser(MyUser.class);
        initInfo(mUser);
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

    private void initInfo(MyUser user){
        if(user != null){
            (mTvName).setText(user.getUsername());
            ((BaseActivity)getActivity()).loadAvatar(getActivity(), user.getPhotoPath(), mPhoto);
            mTvCoupon.setText(mUser.getCoupon() == null ? "0" : String.valueOf(mUser.getCoupon()));
            mTvBalance.setText(mUser.getBalance() == null ? "0.0" :
                    getString(R.string.balance_format, mUser.getBalance()));
            mTvCarCount.setText(String.valueOf(ProtocolPreferences.getCarCount(mContext)));
        }else {
            (mTvName).setText("");
            ((BaseActivity)getActivity()).loadAvatar(getActivity(), "", mPhoto);
            mTvCoupon.setText("0");
            mTvBalance.setText("0.0");
            mTvCarCount.setText("0");
        }
    }

    @Override
    public void onClick(View v) {

    }

}
