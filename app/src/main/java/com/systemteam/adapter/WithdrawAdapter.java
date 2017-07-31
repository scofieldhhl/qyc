package com.systemteam.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.systemteam.R;
import com.systemteam.bean.Withdraw;
import com.systemteam.util.Constant;

import java.util.List;

/**
 * Created by gaolei on 17/1/18.
 */

public class WithdrawAdapter extends BaseAdapter {

    public WithdrawAdapter(Context context, List<Object> list) {
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
        holder.bike_date.setVisibility(View.GONE);
        switch(getItemViewType(position)){
            case TYPE_TITLE:
                holder.bike_time.setText(context.getString(R.string.date));
                holder.bike_distance.setText(context.getString(R.string.amount));
                holder.bike_price.setText(context.getString(R.string.status));
                break;
            case TYPE_CONTENT:
                Withdraw object= (Withdraw) list.get(position);
                holder.bike_time.setText(object.getCreatedAt());
                holder.bike_distance.setText(context.getString(R.string.amout_format, object.getAmout()));
                if(object.getStatus() == null){
                    holder.bike_price.setText(context.getString(R.string.waiting));
                }else {
                    int status = object.getStatus().intValue();
                    switch (status){
                        case Constant.STATUS_NORMAL:
                            holder.bike_price.setText(context.getString(R.string.waiting));
                            holder.bike_price.setTextColor(ContextCompat.getColor(context, R.color.color_393939));
                            break;
                        case Constant.WITHDRAW_SUCCESS:
                            holder.bike_price.setText(context.getString(R.string.success));
                            holder.bike_price.setTextColor(ContextCompat.getColor(context, R.color.text_black_87));
                            break;
                        case Constant.WITHDRAW_FAIL:
                            holder.bike_price.setText(context.getString(R.string.fail));
                            holder.bike_price.setTextColor(ContextCompat.getColor(context, R.color.red));
                            break;
                        default:
                            holder.bike_price.setText(context.getString(R.string.waiting));
                            holder.bike_price.setTextColor(ContextCompat.getColor(context, R.color.color_393939));
                            break;
                    }
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}