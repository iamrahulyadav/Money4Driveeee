package com.hvantage2.money4driveeee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hvantage2.money4driveeee.model.ShopActivity;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.customview.CustomTextView;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {
    Context context;
    ArrayList<ShopActivity> modalList;

    public ActivityAdapter(Context context, ArrayList<ShopActivity> modalList) {
        this.context = context;
        this.modalList = modalList;
    }

    @Override
    public ActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_layout, parent, false);
        ActivityAdapter.ViewHolder viewHolder = new ActivityAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ActivityAdapter.ViewHolder holder, int position) {
        final ShopActivity modal = modalList.get(position);
        Log.e(TAG, "onBindViewHolder: Activity Model >> "+modal.toString());
        if (modal.getActivity_status() == 1) {
            holder.imageViewDone.setVisibility(View.VISIBLE);
        } else
            holder.imageViewDone.setVisibility(View.GONE);

        int counter=position+1;
        holder.tvCounter.setText(String.valueOf(counter));
        holder.tvTitle.setText(modal.getActivity_name());
    }

    @Override
    public int getItemCount() {
        return modalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CustomTextView tvCounter, tvTitle;
        ImageView imageViewDone;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCounter = (CustomTextView) itemView.findViewById(R.id.tvCounter);
            tvTitle = (CustomTextView) itemView.findViewById(R.id.tvTitle);
            imageViewDone = (ImageView) itemView.findViewById(R.id.imageViewDone);
        }
    }
}
