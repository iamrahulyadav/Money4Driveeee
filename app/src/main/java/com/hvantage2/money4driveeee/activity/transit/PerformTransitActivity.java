package com.hvantage2.money4driveeee.activity.transit;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.activity.DashBoardActivity;
import com.hvantage2.money4driveeee.activity.SingleActivityDetail;
import com.hvantage2.money4driveeee.activity.UploadPhotosActivity;
import com.hvantage2.money4driveeee.adapter.ActivityAdapter;
import com.hvantage2.money4driveeee.customview.CustomTextView;
import com.hvantage2.money4driveeee.model.ShopActivity;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.ProgressHUD;
import com.hvantage2.money4driveeee.util.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerformTransitActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<ShopActivity> activityList;
    private ProgressDialog dialog;
    private RecyclerView recycler_view;
    private ActivityAdapter adapter;
    private ImageView imagViewEdit;
    private String TAG = "PerformTransitActivity";
    private CustomTextView tvVehicle;
    private String media_option_id = "0";
    private ProgressHUD progressHD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activityList = new ArrayList<ShopActivity>();
        if (getIntent().hasExtra("media_option_id"))
            media_option_id = getIntent().getStringExtra("media_option_id");
        Log.e(TAG, "onCreate: media_option_id >> " + media_option_id);
        init();
        setRecycler_view();
        tvVehicle.setText(AppPreference.getSelectedVehicleName(this));


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (media_option_id != null && !media_option_id.equalsIgnoreCase(""))
            new GetActivityTask().execute();

    }

    private void showProgressDialog() {
        progressHD = ProgressHUD.show(this, "Processing...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void hideProgressDialog() {
        if (progressHD != null && progressHD.isShowing())
            progressHD.dismiss();
    }

    private void setRecycler_view() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(PerformTransitActivity.this);
        recycler_view.setLayoutManager(layoutManager);
        adapter = new ActivityAdapter(PerformTransitActivity.this, activityList);
        recycler_view.setAdapter(adapter);
        recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(PerformTransitActivity.this, recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ShopActivity model = activityList.get(position);
                if (activityList.get(position).getActivity_status() == 1) {
                    Intent intent = new Intent(PerformTransitActivity.this, SingleActivityDetail.class);
                    intent.setAction("transit");
                    intent.putExtra("media_option_id", model.getActivity_id());
                    intent.putExtra("media_option_name", model.getActivity_name());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(PerformTransitActivity.this, UploadPhotosActivity.class);
                    intent.setAction("transit");
                    intent.putExtra("media_option_id", model.getActivity_id());
                    intent.putExtra("media_option_name", model.getActivity_name());
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }

    private void init() {
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        ((CustomTextView) findViewById(R.id.tvTitle)).setText("Vehicle");
        tvVehicle = (CustomTextView) findViewById(R.id.tvProjectTittle);
        if (AppPreference.getSelectedProjectType(this).equalsIgnoreCase(AppConstants.PROJECT_TYPE.PENDING)) {
            imagViewEdit = (ImageView) findViewById(R.id.imagViewEdit);
            imagViewEdit.setVisibility(View.VISIBLE);
            imagViewEdit.setOnClickListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.product_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_home:
                Intent intent = new Intent(this, DashBoardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imagViewEdit) {
            Intent intent = new Intent(PerformTransitActivity.this, VehicleDetailActivity.class);
            intent.putExtra("media_option_id", media_option_id);
            startActivity(intent);
            finish();
        }
    }

    class GetActivityTask extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.PROJECTTRANSITACTIVITYLIST);
            jsonObject.addProperty("user_id", AppPreference.getUserId(PerformTransitActivity.this));
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(PerformTransitActivity.this));
            jsonObject.addProperty("vehicle_id", AppPreference.getSelectedVehicleId(PerformTransitActivity.this));
            jsonObject.addProperty("media_option_id", media_option_id);
            Log.e(TAG, "Request GET ACTIVITIES >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_transit_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response GET PROJECT ACTIVITIES >>" + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        activityList.clear();
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                int activity_status = jsonObject1.getInt("activity_status");
                                String activity_name = jsonObject1.getString("activity_name");
                                String activity_id = jsonObject1.getString("activity_id");
                                ShopActivity data = new ShopActivity(activity_id, activity_name, activity_status);
                                activityList.add(data);
                            }
                            adapter.notifyDataSetChanged();
                            publishProgress("200", "");
                        } else {
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            publishProgress("400", msg);
                        }

                    } catch (JSONException e) {
                        publishProgress("400", getResources().getString(R.string.api_error_msg));
                        e.printStackTrace();
                    }
                }

                @SuppressLint("LongLogTag")
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e(TAG, "error :- " + Log.getStackTraceString(t));
                    publishProgress("400", getResources().getString(R.string.api_error_msg));
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
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(PerformTransitActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
