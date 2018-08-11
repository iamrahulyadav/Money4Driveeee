package com.hvantage2.money4driveeee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.model.UserModal;

import java.util.List;


public class SelectUserAdapter extends RecyclerView.Adapter<SelectUserAdapter.ViewHolder> {
    Context context;
    List<UserModal> modalList;

    public SelectUserAdapter(Context context, List<UserModal> modalList) {
        this.context = context;
        this.modalList = modalList;
    }

    @Override
    public SelectUserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_user_single_item_layout, parent, false);
        SelectUserAdapter.ViewHolder viewHolder = new SelectUserAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SelectUserAdapter.ViewHolder holder, int position) {
        UserModal UserModal = modalList.get(position);
        holder.tvTag.setText(UserModal.getName().charAt(0) + "");
        holder.tvProjectTitle.setText(UserModal.getName());
        /*holder.tvProjectSubtitle.setText(UserModal.getProjectDesc());*/
        holder.tvProjectSubtitle.setSelected(true);
    }

    @Override
    public int getItemCount() {
        return modalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag, tvProjectTitle, tvProjectSubtitle;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTag = (TextView) itemView.findViewById(R.id.tvTag);
            tvProjectTitle = (TextView) itemView.findViewById(R.id.tvProjectTitle);
            tvProjectSubtitle = (TextView) itemView.findViewById(R.id.tvProjectSubtitle);
        }
    }
}
