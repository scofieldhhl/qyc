package com.systemteam.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.systemteam.R;
import com.systemteam.bean.CashRecord;

import java.util.List;

import static com.systemteam.util.Constant.PAY_TYPE_ALI;

/**
 * Created by gaolei on 17/1/18.
 */

public class MyCashRecordAdapter extends BaseAdapter {
    public MyCashRecordAdapter(Context context, List<Object> list) {
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
                CashRecord cashRecord= (CashRecord) list.get(position);
                String strPay = context.getString(R.string.pay_wechat);
                if(cashRecord.getType() != null && cashRecord.getType() == PAY_TYPE_ALI){
                    strPay = context.getString(R.string.pay_ali);
                }
                viewHolder.bike_time.setText(context.getString(R.string.charge_success, strPay));
                viewHolder.bike_distance.setVisibility(View.GONE);
                viewHolder.bike_price.setText(context.getString(R.string.amout_num,
                        String.valueOf(cashRecord.getAmount())));
                viewHolder.bike_price.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                viewHolder.bike_date.setText(cashRecord.getCreatedAt());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}