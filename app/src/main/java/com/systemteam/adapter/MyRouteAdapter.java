package com.systemteam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.systemteam.R;
import com.systemteam.bean.UseRecord;

import java.util.List;

/**
 * Created by gaolei on 17/1/18.
 */

public class MyRouteAdapter extends BaseAdapter {

    public MyRouteAdapter(Context context, List<Object> list) {
        this.context = context;
        this.list = list;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_route_item, null);
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
        /*RouteRecord routeRecord=(RouteRecord)list.get(position);
//        holder.bike_time.setText(context.getString(R.string.cost_time, routeRecord.getCycle_time()));
        holder.bike_time.setText(routeRecord.getCycle_time());
//        holder.bike_distance.setText(context.getString(R.string.cost_distance, routeRecord.getCycle_distance()));
        holder.bike_distance.setText(routeRecord.getCarNo());
        holder.bike_price.setText(context.getString(R.string.cost_num, routeRecord.getCycle_price()));
        holder.bike_date.setText(routeRecord.getCycle_date());*/
        UseRecord routeRecord=(UseRecord)list.get(position);
//        holder.bike_time.setText(context.getString(R.string.cost_time, routeRecord.getCycle_time()));
        holder.bike_time.setText(routeRecord.getTimeUse());
//        holder.bike_distance.setText(context.getString(R.string.cost_distance, routeRecord.getCycle_distance()));
        holder.bike_distance.setText(routeRecord.getCarNo());
        holder.bike_price.setText(context.getString(R.string.cost_num, routeRecord.getCost()));
        holder.bike_date.setText(routeRecord.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}