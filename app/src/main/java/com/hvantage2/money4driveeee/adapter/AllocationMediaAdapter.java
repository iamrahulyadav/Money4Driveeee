package com.hvantage2.money4driveeee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hvantage2.money4driveeee.model.MediaModel;
import com.hvantage2.money4driveeee.R;


import java.util.List;


public class AllocationMediaAdapter extends RecyclerView.Adapter<AllocationMediaAdapter.ViewHolder> {
    Context context;
    List<MediaModel> modalList;

    public AllocationMediaAdapter(Context context, List<MediaModel> modalList) {
        this.context = context;
        this.modalList = modalList;
    }

    @Override
    public AllocationMediaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allocation_layout, parent, false);
        AllocationMediaAdapter.ViewHolder viewHolder = new AllocationMediaAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AllocationMediaAdapter.ViewHolder holder, int position) {
        final MediaModel modal = modalList.get(position);
        int counter = position + 1;
        holder.tvCounter.setText(String.valueOf(counter));
        holder.tvTitle.setText(modal.getMedia_name());
    }

    @Override
    public int getItemCount() {
        return modalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCounter;
        RelativeLayout list_item;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvCounter = (TextView) itemView.findViewById(R.id.tvCounter);
            list_item = (RelativeLayout) itemView.findViewById(R.id.list_item);
        }
    }
}
