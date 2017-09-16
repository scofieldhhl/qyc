package com.systemteam.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
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

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType){
            case TYPE_TITLE:
                view = LayoutInflater.from(context).inflate(R.layout.item_layout_mycar, null);
                break;
            case TYPE_CONTENT:
                view = LayoutInflater.from(context).inflate(R.layout.item_layout_mycar, null);
                break;
        }
        MyCarViewHolder holder = new MyCarViewHolder(view);
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
        switch(getItemViewType(position)){
            case TYPE_TITLE:
                break;
            case TYPE_CONTENT:
                Car car = (Car) list.get(position);
                MyCarViewHolder viewHolder = (MyCarViewHolder)holder;
                viewHolder.tvCarNo.setText(car.getCarNo());
                viewHolder.tvCarEarn.setText(context.getString(R.string.earn_format,
                        car.getEarn() == null ? 0.0 : car.getEarn()));
                if(car.getStatus() == null){
                    viewHolder.tvCarStatus.setText(context.getString(R.string.status_normal));
                }else {
                    int status = car.getStatus().intValue();
                    switch (status){
                        case Constant.STATUS_NORMAL:
                            viewHolder.tvCarStatus.setText(context.getString(R.string.status_normal));
                            viewHolder.tvCarStatus.setTextColor(ContextCompat.getColor(context, R.color.black));
                            break;
                        case Constant.BREAK_STATUS_LOCK:
                            viewHolder.tvCarStatus.setText(context.getString(R.string.status_lock));
                            viewHolder.tvCarStatus.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                            break;
                        default:
                            viewHolder.tvCarStatus.setText(context.getString(R.string.status_break));
                            viewHolder.tvCarStatus.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                            break;
                    }
                }
                if(car.getStatusExpert() != null && car.getStatusExpert() == Constant.STATUS_EXPERT_WAITING){
                    viewHolder.tvCarStatus.setText(context.getString(R.string.status_experting));
                    viewHolder.tvCarStatus.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
                if(car.getCarType() != null){
                    switch (car.getCarType()){
                        case 1:
                            viewHolder.ivIcon.setImageResource(R.drawable.car_icon1);
                            break;
                        case 2:
                            viewHolder.ivIcon.setImageResource(R.drawable.car_icon2);
                            break;
                        case 3:
                            viewHolder.ivIcon.setImageResource(R.drawable.car_icon3);
                            break;
                        case 4:
                            viewHolder.ivIcon.setImageResource(R.drawable.car_icon4);
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