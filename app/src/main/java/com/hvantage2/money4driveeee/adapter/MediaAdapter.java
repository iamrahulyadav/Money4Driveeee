package com.hvantage2.money4driveeee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hvantage2.money4driveeee.R;

import com.hvantage2.money4driveeee.model.MediaModel;

import java.util.ArrayList;


public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    String TAG = "MediaAdapter";
    Context context;
    ArrayList<MediaModel> modalList;

    public MediaAdapter(Context context, ArrayList<MediaModel> modalList) {
        this.context = context;
        this.modalList = modalList;
    }

    @Override
    public MediaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_list_item_layout, parent, false);
        MediaAdapter.ViewHolder viewHolder = new MediaAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MediaAdapter.ViewHolder holder, int position) {
        final MediaModel modal = modalList.get(position);
        Log.e(TAG, "onBindViewHolder: modal >> " + modal.toString());
        holder.tvTitle.setText(modal.getMedia_name());
    }

    @Override
    public int getItemCount() {
        return modalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }
    }
}
