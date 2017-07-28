package com.systemteam.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class BaseAdapter extends RecyclerView.Adapter<MyViewHolder> {
    protected final int TYPE_TITLE = 0;
    protected final int TYPE_CONTENT = 1;

    public Context context;
    OnItemClickListener listener;
    OnItemLongClickListener longClickListener;
    List<Object> list;
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        Object object = list.get(position);
        if(object instanceof String){
            return TYPE_TITLE;
        }else {
            return TYPE_CONTENT;
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public interface OnItemClickListener {
        public void onItemClick(View v, int position);
    }

    public interface OnItemLongClickListener {
        public void onItemLongClick(View v, int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnLongClickListener(OnItemLongClickListener listener){
        this.longClickListener = listener;
    }
}
