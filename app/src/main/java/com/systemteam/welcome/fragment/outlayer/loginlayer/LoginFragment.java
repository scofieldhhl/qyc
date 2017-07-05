package com.systemteam.welcome.fragment.outlayer.loginlayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.systemteam.R;
import com.systemteam.user.ProtocolPreferences;


/**
 * 登录
 */
public class LoginFragment extends Fragment implements View.OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_login, null);
        TextView tv_down = (TextView)view.findViewById(R.id.tv_down);
        tv_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoRankMe();
            }
        });
        view.findViewById(R.id.btn_login).setOnClickListener(this);
        return view;
    }

    /**
     */
    private void gotoRankMe() {
        /*try {
            Uri uri = Uri.parse("market://details?id=com.xingin.xhs");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
        }*/
    }

    //TODO login
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                ProtocolPreferences.setMemberId(getActivity(), "158****1743");
                getActivity().finish();
                break;
        }
    }
}
