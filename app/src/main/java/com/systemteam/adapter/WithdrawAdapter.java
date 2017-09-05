package com.systemteam.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
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

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.bike_date.setVisibility(View.GONE);
        switch(getItemViewType(position)){
            case TYPE_TITLE:
                viewHolder.bike_time.setText(context.getString(R.string.date));
                viewHolder.bike_distance.setText(context.getString(R.string.amount));
                viewHolder.bike_price.setText(context.getString(R.string.status));
                break;
            case TYPE_CONTENT:
                Withdraw object= (Withdraw) list.get(position);
                viewHolder.bike_time.setText(object.getCreatedAt());
                viewHolder.bike_distance.setText(context.getString(R.string.amout_format, object.getAmout()));
                if(object.getStatus() == null){
                    viewHolder.bike_price.setText(context.getString(R.string.waiting));
                }else {
                    int status = object.getStatus().intValue();
                    switch (status){
                        case Constant.STATUS_NORMAL:
                            viewHolder.bike_price.setText(context.getString(R.string.waiting));
                            viewHolder.bike_price.setTextColor(ContextCompat.getColor(context, R.color.color_393939));
                            break;
                        case Constant.WITHDRAW_SUCCESS:
                            viewHolder.bike_price.setText(context.getString(R.string.success));
                            viewHolder.bike_price.setTextColor(ContextCompat.getColor(context, R.color.text_black_87));
                            break;
                        case Constant.WITHDRAW_FAIL:
                            viewHolder.bike_price.setText(context.getString(R.string.fail));
                            viewHolder.bike_price.setTextColor(ContextCompat.getColor(context, R.color.red));
                            break;
                        default:
                            viewHolder.bike_price.setText(context.getString(R.string.waiting));
                            viewHolder.bike_price.setTextColor(ContextCompat.getColor(context, R.color.color_393939));
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