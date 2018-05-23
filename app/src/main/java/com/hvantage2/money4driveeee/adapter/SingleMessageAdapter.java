package com.hvantage2.money4driveeee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.model.MessageData;
import com.hvantage2.money4driveeee.util.AppPreference;

import java.util.ArrayList;


public class SingleMessageAdapter extends RecyclerView.Adapter {
    private static final String TAG = "SingleMessageAdapter";

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context mContext;
    private ArrayList<MessageData> mMessageList;

    public SingleMessageAdapter(Context context, ArrayList<MessageData> messageList) {
        mContext = context;
        mMessageList = messageList;
        Log.e(TAG, "SingleMessageAdapter: Constrctor");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_sent_layout, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_recieved_layout, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        Log.e(TAG, "getItemViewType");
        if (mMessageList.get(position).getMsgSenderId().equalsIgnoreCase(AppPreference.getUserId(mContext))) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageData message = mMessageList.get(position);
        Log.e("SingleMessageAdapter", "onBindViewHolder: message >>" + message);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(position);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(position);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvMsgText, tvMsgTime;
        RelativeLayout rr1;

        SentMessageHolder(View itemView) {
            super(itemView);
            rr1 = (RelativeLayout) itemView.findViewById(R.id.rr1);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvMsgText = (TextView) itemView.findViewById(R.id.tvMsgText);
            tvMsgTime = (TextView) itemView.findViewById(R.id.tvMsgTime);
        }

        void bind(int position) {
            MessageData message = mMessageList.get(position);
            tvMsgText.setText(message.getMsgText());
            tvMsgTime.setText(message.getMsgTime());

            if (position == 0) {
                rr1.setVisibility(View.VISIBLE);
                tvDate.setText(message.getMsgDate());
            } else if (!message.getMsgDate().equalsIgnoreCase(mMessageList.get(position - 1).getMsgDate())) {
                rr1.setVisibility(View.VISIBLE);
                tvDate.setText(message.getMsgDate());
            } else {
                rr1.setVisibility(View.GONE);
            }
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvSender, tvNameTag, tvMsgText, tvMsgTime;
        ImageView profileImage;
        RelativeLayout rr1;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            rr1 = (RelativeLayout) itemView.findViewById(R.id.rr1);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvNameTag = (TextView) itemView.findViewById(R.id.tvNameTag);
            tvSender = (TextView) itemView.findViewById(R.id.tvSender);
            tvMsgText = (TextView) itemView.findViewById(R.id.tvMsgText);
            tvMsgTime = (TextView) itemView.findViewById(R.id.tvMsgTime);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
        }

        void bind(int position) {
            MessageData message = mMessageList.get(position);
            tvNameTag.setText("" + message.getMsgSenderName().toUpperCase().charAt(0));
            tvSender.setText(message.getMsgSenderName());
            tvMsgText.setText(message.getMsgText());
            tvMsgTime.setText(message.getMsgTime());

            if (position == 0) {
                rr1.setVisibility(View.VISIBLE);
                tvDate.setText(message.getMsgDate());
            } else if (!message.getMsgDate().equalsIgnoreCase(mMessageList.get(position - 1).getMsgDate())) {
                rr1.setVisibility(View.VISIBLE);
                tvDate.setText(message.getMsgDate());
            } else {
                rr1.setVisibility(View.GONE);
            }
        }
    }
}
