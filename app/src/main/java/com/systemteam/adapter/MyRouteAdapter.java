package com.systemteam.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.systemteam.R;
import com.systemteam.bean.UseRecord;
import com.systemteam.car.CarDetailActivity;

import java.util.List;

public class MyRouteAdapter extends BaseAdapter {

    public MyRouteAdapter(Context context, List<Object> list) {
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
        switch(getItemViewType(position)){
            case TYPE_TITLE:
                viewHolder.bike_time.setText(context.getString(R.string.carNo));
                viewHolder.bike_distance.setText(context.getString(R.string.earn));
                viewHolder.bike_price.setText(context.getString(R.string.status));
                viewHolder.bike_date.setVisibility(View.GONE);
                break;
            case TYPE_CONTENT:
                UseRecord routeRecord=(UseRecord)list.get(position);
//        viewHolder.bike_time.setText(context.getString(R.string.cost_time, routeRecord.getCycle_time()));
                viewHolder.bike_time.setText(routeRecord.getTimeUse());
//        viewHolder.bike_distance.setText(context.getString(R.string.cost_distance, routeRecord.getCycle_distance()));
                viewHolder.bike_distance.setText(routeRecord.getCarNo());
                if(context instanceof CarDetailActivity){
                    viewHolder.bike_price.setText(context.getString(R.string.cost_num, String.valueOf(routeRecord.getEarn())));
                }else {
                    viewHolder.bike_price.setText(context.getString(R.string.cost_num, String.valueOf(routeRecord.getCost())));
                }
                viewHolder.bike_date.setText(routeRecord.getCreatedAt());
                break;
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}