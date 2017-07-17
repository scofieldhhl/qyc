package com.systemteam.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.systemteam.R;

class MyViewHolder extends RecyclerView.ViewHolder {
    TextView bike_time,bike_distance,bike_price,bike_date;

    public MyViewHolder(View view) {
        super(view);
        bike_time = (TextView) view.findViewById(R.id.bike_time);
        bike_distance = (TextView) view.findViewById(R.id.bike_distance);
        bike_price = (TextView) view.findViewById(R.id.bike_price);
        bike_date = (TextView) view.findViewById(R.id.bike_date);
    }
}
