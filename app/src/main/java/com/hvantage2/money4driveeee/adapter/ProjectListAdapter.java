package com.hvantage2.money4driveeee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.model.ProjectModel;

import java.util.List;

/**
 * Created by Hvantage2 on 2018-02-21.
 */

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ViewHolder> {
    Context context;
    List<ProjectModel> modalList;


    public ProjectListAdapter(Context context, List<ProjectModel> modalList) {
        this.context = context;
        this.modalList = modalList;
    }


    @Override
    public ProjectListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item_layout, parent, false);
        ProjectListAdapter.ViewHolder viewHolder = new ProjectListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProjectListAdapter.ViewHolder holder, int position) {
        final ProjectModel projectModel = modalList.get(position);
        holder.tvLabel.setText(projectModel.getProjectTitle().charAt(0) + "");
        holder.projecttittle.setText(projectModel.getProjectTitle());
        holder.city.setText(projectModel.getCity());
        holder.date.setText(projectModel.getCreatedDate());
    }

    @Override
    public int getItemCount() {
        return modalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, projecttittle, city, date;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvLabel = (TextView) itemView.findViewById(R.id.tvLabel);
            projecttittle = (TextView) itemView.findViewById(R.id.projecttittle);
            city = (TextView) itemView.findViewById(R.id.city);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }
}
