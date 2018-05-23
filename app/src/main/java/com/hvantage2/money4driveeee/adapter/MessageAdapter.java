package com.hvantage2.money4driveeee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hvantage2.money4driveeee.model.MessageModel;
import com.hvantage2.money4driveeee.model.MessageModel;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.customview.CustomTextView;


import java.util.List;


public class MessageAdapter extends  RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    Context context;
    List<MessageModel> modalList;

    public MessageAdapter(Context context, List<MessageModel> modalList) {
        this.context = context;
        this.modalList = modalList;
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.single_msg_layout,parent,false);
        MessageAdapter.ViewHolder viewHolder =new MessageAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageAdapter.ViewHolder holder, int position) {
     MessageModel MessageModel = modalList.get(position);
     holder.tvName.setText(MessageModel.getUser());
     holder.tvMsg.setText(MessageModel.getMessage());
     holder.tvDate.setText(MessageModel.getDate_time());

    }

    @Override
    public int getItemCount() {
        return modalList.size();
    }

     public class ViewHolder extends RecyclerView.ViewHolder{
        CustomTextView tvName,tvMsg,tvDate;

         public ViewHolder(View itemView) {
             super(itemView);
             tvName = (CustomTextView)itemView.findViewById(R.id.name);
             tvMsg = (CustomTextView)itemView.findViewById(R.id.message);
             tvDate = (CustomTextView)itemView.findViewById(R.id.date);
         }
     }
}
