package com.systemteam.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.systemteam.R;
import com.systemteam.bean.Car;
import com.systemteam.util.Constant;

import java.util.List;

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
                if (listener != null && getItemViewType(position) != TYPE_TITLE) {
                    listener.onItemClick(view, position);
                }
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int position = (int) view.getTag();
                if (longClickListener != null && getItemViewType(position) != TYPE_TITLE) {
                    longClickListener.onItemLongClick(view, position);
                }
                return false;
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
                holder.bike_distance.setText(context.getString(R.string.earn_format,
                        Car.getEarn() == null ? 0.0 : Car.getEarn()));
                if(Car.getStatus() == null){
                    holder.bike_price.setText(context.getString(R.string.status_normal));
                }else {
                    int status = Car.getStatus().intValue();
                    switch (status){
                        case Constant.STATUS_NORMAL:
                            holder.bike_price.setText(context.getString(R.string.status_normal));
                            holder.bike_price.setTextColor(ContextCompat.getColor(context, R.color.color_393939));
                            break;
                        case Constant.BREAK_STATUS_LOCK:
                            holder.bike_price.setText(context.getString(R.string.status_lock));
                            holder.bike_price.setTextColor(ContextCompat.getColor(context, R.color.red));
                            break;
                        default:
                            holder.bike_price.setText(context.getString(R.string.status_break));
                            holder.bike_price.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
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