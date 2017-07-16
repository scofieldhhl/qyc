package com.systemteam.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.systemteam.R;
import com.systemteam.bean.Car;

import java.util.List;

/**
 * Created by gaolei on 17/1/18.
 */

public class MyCarAdapter extends RecyclerView.Adapter<MyCarAdapter.MyViewHolder> {

    public Context context;
    int selectPosition = 0;
    OnItemClickListener listener;
    List<Car> list;

    public MyCarAdapter(Context context, List<Car> list) {
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
        Car Car=list.get(position);
        holder.bike_time.setText(context.getString(R.string.cost_time, Car.getCarNo()));
        holder.bike_distance.setText(context.getString(R.string.cost_distance, String.valueOf(Car.getEarn())));
        holder.bike_price.setText(context.getString(R.string.cost_num, String.valueOf(Car.getStatus())));
        holder.bike_date.setText(Car.getUpdatedAt());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

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

    public interface OnItemClickListener {
        public void onItemClick(View v, int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}