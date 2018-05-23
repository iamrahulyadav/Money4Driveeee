package com.hvantage2.money4driveeee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.hvantage2.money4driveeee.model.SourceModel;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.customview.CustomTextView;

import java.util.ArrayList;


public class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.ViewHolder> {
    String TAG = "SourceAdapter";
    Context context;
    ArrayList<SourceModel> modalList;

    public SourceAdapter(Context context, ArrayList<SourceModel> modalList) {
        this.context = context;
        this.modalList = modalList;
    }

    @Override
    public SourceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_list_item_layout, parent, false);
        SourceAdapter.ViewHolder viewHolder = new SourceAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SourceAdapter.ViewHolder holder, int position) {
        SourceModel modal = modalList.get(position);
        Log.e(TAG, "onBindViewHolder: modal >> " + modal.toString());
        holder.tvTitle.setText(modal.getName());
    }

    @Override
    public int getItemCount() {
        return modalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CustomTextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (CustomTextView) itemView.findViewById(R.id.tvTitle);
        }
    }
}
