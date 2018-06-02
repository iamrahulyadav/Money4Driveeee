package com.hvantage2.money4driveeee.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.activity.EditPhotoActivity;
import com.hvantage2.money4driveeee.model.ImageUploadModel;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.Functions;
import com.hvantage2.money4driveeee.util.TouchImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;


public class UploadedImageAdapter extends RecyclerView.Adapter<UploadedImageAdapter.ViewHolder> {
    Context context;
    ArrayList<ImageUploadModel> modalList;
    int selected_position = 0;
    private ProgressDialog progressDialog;
    private String action = "";

    public UploadedImageAdapter(Context context, ArrayList<ImageUploadModel> modalList, String action) {
        this.context = context;
        this.modalList = modalList;
        this.action = action;
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
        holder.tvDimen.setText(modal.getDimension());

        if (modal.getStatus().equalsIgnoreCase("new")) {
            holder.tvDateTime.setText(Functions.getCurrentDate() + " | " + Functions.getCurrentTime());
            if (modal.getImage() != null)
                holder.image.setImageBitmap(modal.getImage());
        } else if (modal.getStatus().equalsIgnoreCase("edit")) {
            holder.tvDateTime.setText(modal.getDatetime());
            if (!modal.getImage_url().equalsIgnoreCase(""))
                Picasso.with(context).load(modal.getImage_url()).placeholder(R.drawable.no_image_placeholder).into(holder.image);
            if (AppPreference.getSelectedProjectType(context).equalsIgnoreCase(AppConstants.PROJECT_TYPE.PENDING)) {
                holder.ll_edit.setVisibility(View.VISIBLE);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selected_position = position;
                        deleteDialog(modal.getUpdate_id());
                    }
                });

                holder.edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, EditPhotoActivity.class);
                        intent.putExtra("image_url", modal.getImage_url());
                        intent.putExtra("dimen", modal.getDimension());
                        intent.putExtra("remark", modal.getRemark());
                        intent.putExtra("update_id", modal.getUpdate_id());
                        intent.putExtra("action", action);
                        context.startActivity(intent);
                    }
                });
            }

        }

        holder.item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPreviewDialog(modal);
            }
        });
    }

    private void deleteDialog(final String update_id) {
        AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to delete?");
        builder.setTitle("Delete");
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selected_position = 0;
            }
        });
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new DeleteTask().execute(update_id);
            }
        });
        builder.show();
    }

    @Override
    public int getItemCount() {
        return modalList.size();
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void showPreviewDialog(ImageUploadModel modal) {
        Dialog dialog1 = new Dialog(context, R.style.image_preview_dialog);
        dialog1.setContentView(R.layout.image_preview_layout);
        Window window = dialog1.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog1.setCancelable(true);
        dialog1.setCanceledOnTouchOutside(true);

        TouchImageView imgPreview = (TouchImageView) dialog1.findViewById(R.id.imgPreview);
        TextView tvDimen = (TextView) dialog1.findViewById(R.id.tvDimen);
        TextView tvRemark = (TextView) dialog1.findViewById(R.id.tvRemark);
        imgPreview.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if (!modal.getImage_url().equalsIgnoreCase(""))
            Picasso.with(context)
                    .load(modal.getImage_url())
                    .placeholder(R.drawable.no_image_placeholder)
                    .into(imgPreview);

        tvDimen.setText("Dimensions : " + modal.getDimension());
        tvRemark.setText("Remark : " + modal.getRemark());
        dialog1.show();
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

    private class DeleteTask extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(String... strings) {
            JsonObject jsonObject = new JsonObject();
            if (action.equalsIgnoreCase("shop")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.DELETEACTIVITYDETAIL);
                jsonObject.addProperty("update_id", strings[0]);
            } else if (action.equalsIgnoreCase("transit")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.DELETETRANSITACTDET);
                jsonObject.addProperty("update_id", strings[0]);
            } else if (action.equalsIgnoreCase("print")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.DELETEPRINTDETAIL);
                jsonObject.addProperty("update_id", strings[0]);
            } else if (action.equalsIgnoreCase("hoarding")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.DELETEHOARDINGIDETAIL);
                jsonObject.addProperty("update_id", strings[0]);
            } else if (action.equalsIgnoreCase("wall")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.DELETEWALLDETAIL);
                jsonObject.addProperty("update_id", strings[0]);
            } else if (action.equalsIgnoreCase("emedia")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.DELETEEMEDIADETAIL);
                jsonObject.addProperty("update_id", strings[0]);
            }
            Log.e("Adapter", "Request DELETE IMAGE >> **" + action + "** >> " + jsonObject);
            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_activity_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response DELETE IMAGE >> " + response.body().toString());
                    String str = response.body().toString();
                    try {
                        JSONObject jsonObject1 = new JSONObject(str);
                        if (jsonObject1.getString("status").equalsIgnoreCase("200")) {
                            publishProgress("200", "");
                        } else {
                            String msg = jsonObject1.getJSONArray("result").getJSONObject(0).getString("msg");
                            publishProgress("400", msg);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onResponse: " + e.getMessage());
                        publishProgress("400", context.getResources().getString(R.string.api_error_msg));
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    publishProgress("400", context.getResources().getString(R.string.api_error_msg));
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            hideProgressDialog();
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("200")) {
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                modalList.remove(selected_position);
                notifyDataSetChanged();
                selected_position = 0;
            } else if (status.equalsIgnoreCase("400"))
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

}
