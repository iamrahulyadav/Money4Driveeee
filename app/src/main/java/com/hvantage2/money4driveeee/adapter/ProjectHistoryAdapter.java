package com.hvantage2.money4driveeee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hvantage2.money4driveeee.model.ProjectModel;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.customview.CustomTextView;


import java.util.List;

/**
 * Created by Hvantage2 on 2018-02-21.
 */

public class ProjectHistoryAdapter  extends  RecyclerView.Adapter<ProjectHistoryAdapter.ViewHolder> {
    Context context;
    List<ProjectModel> modalList;


    public ProjectHistoryAdapter(Context context, List<ProjectModel> modalList) {
        this.context = context;
        this.modalList = modalList;
    }


    @Override
    public ProjectHistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.project_item_layout,parent,false);
        ProjectHistoryAdapter.ViewHolder viewHolder =new ProjectHistoryAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ProjectHistoryAdapter.ViewHolder holder, int position) {
        final ProjectModel projectModel = modalList.get(position);
        holder.projecttittle.setText(projectModel.getProjectTittle());
        holder.city.setText(projectModel.getProjectCity());
        holder.date.setText(projectModel.getProjectDate());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onItemClick.itemClick(projectModel);
               /* Log.d("MODAL",projectModel.toString());
                PreferenceClass.setSelectedProjectId(context, projectModel.getProject_id());
                Intent intent =new Intent(context,ProjectDetailsActivity.class);
                intent.putExtra("projectModel", projectModel);
                context.startActivity(intent);*/
            }
        });

    }

    @Override
    public int getItemCount() {
        return modalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CustomTextView projecttittle,city,date;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view=itemView;

            projecttittle = (CustomTextView)itemView.findViewById(R.id.projecttittle);
            city = (CustomTextView)itemView.findViewById(R.id.city);
            date = (CustomTextView)itemView.findViewById(R.id.date);
        }
    }
}
