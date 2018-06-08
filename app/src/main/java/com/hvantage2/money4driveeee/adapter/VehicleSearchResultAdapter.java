package com.hvantage2.money4driveeee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hvantage2.money4driveeee.R;

import java.util.ArrayList;


public class VehicleSearchResultAdapter extends RecyclerView.Adapter<VehicleSearchResultAdapter.ViewHolder> {
    Context context;
    ArrayList<String> modalList;

    public VehicleSearchResultAdapter(Context context, ArrayList<String> modalList) {
        this.context = context;
        this.modalList = modalList;
    }

    @Override
    public VehicleSearchResultAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_search_result_item_layout, parent, false);
        VehicleSearchResultAdapter.ViewHolder viewHolder = new VehicleSearchResultAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final VehicleSearchResultAdapter.ViewHolder holder, int position) {
        String text = modalList.get(position);
        holder.tvTitle.setText(text);
    }

    @Override
    public int getItemCount() {
        return modalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCounter, tvTitle;
        ImageView imageViewDone;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCounter = (TextView) itemView.findViewById(R.id.tvCounter);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            imageViewDone = (ImageView) itemView.findViewById(R.id.imageViewDone);
        }
    }
}
