package com.systemteam.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.systemteam.R;
import com.systemteam.activity.SwitchVideoModel;

import java.util.List;


/**
 * Created by chenjiang on 2017/6/19.
 */

public class VideoPlayerListAdapter extends RecyclerView.Adapter {
    private LayoutInflater layoutInflater;
    private List<SwitchVideoModel> mVideos;
    private int mPlayPosition;
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    public VideoPlayerListAdapter(Context context, List<SwitchVideoModel> mVideos, int pos) {
        this.mVideos = mVideos;
        this.mPlayPosition = pos;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updatePlayPosition(int pos){
        mPlayPosition=pos;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.item_video_playlist, viewGroup, false);
        RecyclerView.ViewHolder viewHolder = new ViewHolderBookmark(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
        final SwitchVideoModel data = mVideos.get(i);
        final ViewHolderBookmark viewHolderSmall = (ViewHolderBookmark) viewHolder;

        if (mPlayPosition == i) {
            viewHolderSmall.title.setTextColor(Color.WHITE);
        } else {
            viewHolderSmall.title.setTextColor(Color.parseColor("#99ffffff"));
        }
        viewHolderSmall.title.setText(data.getName());

        viewHolderSmall.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(i);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mVideos != null ? mVideos.size() : 0;
    }


    private static class ViewHolderBookmark extends RecyclerView.ViewHolder {
        TextView title;
        View parent;

        ViewHolderBookmark(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.video_playlist_title);
            parent = view.findViewById(R.id.video_playlist_parent);
        }
    }

}
