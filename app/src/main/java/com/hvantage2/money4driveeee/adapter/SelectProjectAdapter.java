package com.hvantage2.money4driveeee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.customview.CustomTextView;
import com.hvantage2.money4driveeee.model.ProjectModel;

import java.util.List;


public class SelectProjectAdapter extends  RecyclerView.Adapter<SelectProjectAdapter.ViewHolder> {
    Context context;
    List<ProjectModel> modalList;

    public SelectProjectAdapter(Context context, List<ProjectModel> modalList) {
        this.context = context;
        this.modalList = modalList;
    }

    @Override
    public SelectProjectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.select_project_single_item_layout,parent,false);
        SelectProjectAdapter.ViewHolder viewHolder =new SelectProjectAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SelectProjectAdapter.ViewHolder holder, int position) {
     ProjectModel ProjectModel = modalList.get(position);
     holder.tvTag.setText(ProjectModel.getProjectTitle().charAt(0)+"");
     holder.tvProjectTitle.setText(ProjectModel.getProjectTitle());
     holder.tvProjectSubtitle.setText(ProjectModel.getProjectDesc());
     holder.tvProjectSubtitle.setSelected(true);

    }

    @Override
    public int getItemCount() {
        return modalList.size();
    }

     public class ViewHolder extends RecyclerView.ViewHolder{
        CustomTextView tvTag,tvProjectTitle,tvProjectSubtitle;

         public ViewHolder(View itemView) {
             super(itemView);
             tvTag = (CustomTextView)itemView.findViewById(R.id.tvTag);
             tvProjectTitle = (CustomTextView)itemView.findViewById(R.id.tvProjectTitle);
             tvProjectSubtitle = (CustomTextView)itemView.findViewById(R.id.tvProjectSubtitle);
         }
     }
}
