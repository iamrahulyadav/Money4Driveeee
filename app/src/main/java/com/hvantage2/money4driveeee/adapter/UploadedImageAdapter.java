package com.hvantage2.money4driveeee.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.model.ImageUploadModel;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.Functions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class UploadedImageAdapter extends RecyclerView.Adapter<UploadedImageAdapter.ViewHolder> {
    private MyAdapterListener listener;
    Context context;
    ArrayList<ImageUploadModel> modalList;
    private ProgressDialog progressDialog;
    private String action = "";

    public UploadedImageAdapter(Context context, ArrayList<ImageUploadModel> modalList, String action, MyAdapterListener listener) {
        this.context = context;
        this.modalList = modalList;
        this.action = action;
        this.listener = listener;
    }

    @Override
    public UploadedImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.uploaded_image_layout, parent, false);
        UploadedImageAdapter.ViewHolder viewHolder = new UploadedImageAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final UploadedImageAdapter.ViewHolder holder, final int position) {
        final ImageUploadModel modal = modalList.get(position);
        Log.e("UploadedImageAdapter", " ImageModel >> " + modal);
        Log.e("UploadedImageAdapter", " action >> " + action);

        if (modal.getDimension().equalsIgnoreCase(""))
            holder.llDimen.setVisibility(View.GONE);
        else
            holder.llDimen.setVisibility(View.VISIBLE);

        holder.tvRemark.setText(modal.getRemark());
        if (TextUtils.isEmpty(modal.getRemark())) {
            holder.tvRemark.setVisibility(View.GONE);
        } else
            holder.tvRemark.setVisibility(View.VISIBLE);

        holder.tvDimen.setText(modal.getDimension());

        if (modal.getStatus().equalsIgnoreCase("new")) {
            holder.tvDateTime.setText(Functions.getCurrentDate() + " | " + Functions.getCurrentTime());
            if (modal.getImage() != null) {
                Bitmap imgThumb = Bitmap.createScaledBitmap(modal.getImage(), 80, 120, false);
                holder.image.setImageBitmap(imgThumb);
                listener.onStartUploading(modal.getImage(), position);
            }
        } else if (modal.getStatus().equalsIgnoreCase("edit")) {
            holder.tvDateTime.setText(modal.getDatetime());
            if (!modal.getImage_url().equalsIgnoreCase(""))
                Picasso.with(context).load(modal.getImage_url()).placeholder(R.drawable.no_image_placeholder).into(holder.image);
            if (AppPreference.getSelectedProjectType(context).equalsIgnoreCase(AppConstants.PROJECT_TYPE.PENDING)) {
                holder.ll_edit.setVisibility(View.VISIBLE);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onDelete(view, position);
                    }
                });

                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onEdit(view, position);
                    }
                });
            }
        }

        holder.item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onView(view, position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return modalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRemark, tvDimen, tvDateTime;
        CircleImageView image;
        ImageView edit, delete;
        LinearLayout ll_edit;
        LinearLayout llDimen;
        CardView item_view;

        public ViewHolder(View itemView) {
            super(itemView);
            item_view = (CardView) itemView.findViewById(R.id.item_view);
            llDimen = (LinearLayout) itemView.findViewById(R.id.llDimen);
            tvRemark = (TextView) itemView.findViewById(R.id.tvRemark);
            tvDimen = (TextView) itemView.findViewById(R.id.tvDimen);
            tvDateTime = (TextView) itemView.findViewById(R.id.tvDateTime);
            image = (CircleImageView) itemView.findViewById(R.id.image);
            edit = (ImageView) itemView.findViewById(R.id.edit);
            delete = (ImageView) itemView.findViewById(R.id.delete);
            ll_edit = (LinearLayout) itemView.findViewById(R.id.ll_edit);
        }
    }


    public interface MyAdapterListener {
        void onStartUploading(Bitmap bitmap, int position);

        void onDelete(View view, int position);

        void onEdit(View view, int position);

        void onView(View view, int position);
    }

}
