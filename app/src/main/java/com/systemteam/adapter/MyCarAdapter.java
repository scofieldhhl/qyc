package com.systemteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.systemteam.R;
import com.systemteam.bean.Car;
import com.systemteam.util.Constant;

import java.util.List;

/**
 * Created by gaolei on 17/1/18.
 */

public class MyCarAdapter extends BaseAdapter {
    public MyCarAdapter(Context context, List<Object> list) {
        this.context = context;
        this.list = list;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType){
            case TYPE_TITLE:
                view = LayoutInflater.from(context).inflate(R.layout.item_my_car, null);
                break;
            case TYPE_CONTENT:
                view = LayoutInflater.from(context).inflate(R.layout.item_my_car, null);
                break;
        }
        MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = (int) view.getTag();
                if (listener != null) {
                    listener.onItemClick(view, position);
                }
            }
        });
        return holder;
    }

    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.itemView.setTag(position);
        switch(getItemViewType(position)){
            case TYPE_TITLE:
                holder.bike_time.setText(context.getString(R.string.carNo));
                holder.bike_distance.setText(context.getString(R.string.earn));
                holder.bike_price.setText(context.getString(R.string.status));
                holder.bike_date.setVisibility(View.GONE);
                break;
            case TYPE_CONTENT:
                Car Car= (Car) list.get(position);
                holder.bike_time.setText(Car.getCarNo());
                holder.bike_distance.setText(String.valueOf(Car.getEarn() == null ? 0.0 : Car.getEarn()));
                holder.bike_price.setText(String.valueOf(Car.getStatus() == null ? 0 : Car.getStatus()));
                if(Car.getStatus() == null){
                    holder.bike_price.setText(context.getString(R.string.status_normal));
                }else {
                    int status = Car.getStatus().intValue();
                    switch (status){
                        case Constant.STATUS_NORMAL:
                            holder.bike_price.setText(context.getString(R.string.status_normal));
                            break;
                        case Constant.BREAK_STATUS_LOCK:
                            holder.bike_price.setText(context.getString(R.string.status_lock));
                            break;
                        default:
                            holder.bike_price.setText(context.getString(R.string.status_break));
                            break;
                    }
                }
                holder.bike_date.setText(Car.getUpdatedAt());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}