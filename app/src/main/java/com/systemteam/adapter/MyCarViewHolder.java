package com.systemteam.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.systemteam.R;

class MyCarViewHolder extends RecyclerView.ViewHolder {
    TextView tvCarNo, tvCarEarn,tvCarStatus;
    ImageView ivIcon;

    public MyCarViewHolder(View view) {
        super(view);
        tvCarNo = (TextView) view.findViewById(R.id.tv_carno);
        tvCarEarn = (TextView) view.findViewById(R.id.tv_earn);
        tvCarStatus = (TextView) view.findViewById(R.id.tv_status);
        ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
    }
}
