package com.hvantage2.money4driveeee.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
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
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.activity.electronic.SelectEMediaTypeActivity;
import com.hvantage2.money4driveeee.activity.hoardings.SelectHoardingMediaTypeActivity;
import com.hvantage2.money4driveeee.activity.print.SelectPrintMediaTypeActivity;
import com.hvantage2.money4driveeee.activity.shop.SelectShopMediaTypeActivity;
import com.hvantage2.money4driveeee.activity.transit.SelectTransitMediaTypeActivity;
import com.hvantage2.money4driveeee.activity.wallpainting.SelectWallPaintTypeActivity;
import com.hvantage2.money4driveeee.adapter.AllocationMediaAdapter;
import com.hvantage2.money4driveeee.model.MediaModel;
import com.hvantage2.money4driveeee.model.ProjectModel;
import com.hvantage2.money4driveeee.model.ProjectDetailsModal;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.ProgressHUD;
import com.hvantage2.money4driveeee.util.RecyclerItemClickListener;
import com.hvantage2.money4driveeee.customview.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectDetailsActivity extends AppCompatActivity {
    private static final String TAG = "ProjectDetailsActivity";
    CustomTextView tvProjectTittle, tvProjectDesc, tvCreationDate, tvAllocationDate;
    RecyclerView recyclerView;
    ArrayList<ProjectDetailsModal> modalList;
    ArrayList<MediaModel> allocationMediaList;
    AllocationMediaAdapter mediaAdapter;
    String project_id;
    private ProgressDialog dialog;
    private ProgressHUD progressHD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();

        ProjectModel modal = getIntent().getParcelableExtra("messageModal");
        Log.e(TAG, "onCreate: Model >> " + modal.toString());
        project_id = modal.getProject_id();
        new getProjectDetails().execute();
        setRecyclerView();
    }

    private void setRecyclerView() {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        mediaAdapter = new AllocationMediaAdapter(this, allocationMediaList);
        recyclerView.setAdapter(mediaAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(ProjectDetailsActivity.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent;
                switch (allocationMediaList.get(position).getMedia_id())
                {
                    case AppConstants.MEDIATYPE.PRINT_MEDIA:
                        AppPreference.setSelectedAlloMediaId(ProjectDetailsActivity.this,allocationMediaList.get(position).getMedia_id());
                        intent = new Intent(ProjectDetailsActivity.this, SelectPrintMediaTypeActivity.class);
                        startActivity(intent);
                        break;
                    case AppConstants.MEDIATYPE.SHOP_MEDIA:
                        AppPreference.setSelectedAlloMediaId(ProjectDetailsActivity.this,allocationMediaList.get(position).getMedia_id());
                        intent = new Intent(ProjectDetailsActivity.this, SelectShopMediaTypeActivity.class);
                        startActivity(intent);
                        break;
                    case AppConstants.MEDIATYPE.TRANSIT_MEDIA:
                        AppPreference.setSelectedAlloMediaId(ProjectDetailsActivity.this,allocationMediaList.get(position).getMedia_id());
                        intent= new Intent(ProjectDetailsActivity.this, SelectTransitMediaTypeActivity.class);
                        startActivity(intent);
                        break;
                    case AppConstants.MEDIATYPE.ELECTRONIC_MEDIA:
                        AppPreference.setSelectedAlloMediaId(ProjectDetailsActivity.this,allocationMediaList.get(position).getMedia_id());
                        intent= new Intent(ProjectDetailsActivity.this, SelectEMediaTypeActivity.class);
                        startActivity(intent);
                        break;
                    case AppConstants.MEDIATYPE.WALL_PAINTING:
                        AppPreference.setSelectedAlloMediaId(ProjectDetailsActivity.this,allocationMediaList.get(position).getMedia_id());
                        intent= new Intent(ProjectDetailsActivity.this, SelectWallPaintTypeActivity.class);
                        startActivity(intent);
                        break;
                    case AppConstants.MEDIATYPE.HOARDINGS:
                        AppPreference.setSelectedAlloMediaId(ProjectDetailsActivity.this,allocationMediaList.get(position).getMedia_id());
                        intent= new Intent(ProjectDetailsActivity.this, SelectHoardingMediaTypeActivity.class);
                        startActivity(intent);
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));
    }

    private void init() {
        modalList = new ArrayList<ProjectDetailsModal>();
        allocationMediaList = new ArrayList<MediaModel>();
        tvProjectTittle = (CustomTextView) findViewById(R.id.tvProjectTittle);
        tvProjectDesc = (CustomTextView) findViewById(R.id.tvProjectDesc);
        tvCreationDate = (CustomTextView) findViewById(R.id.tvCreationDate);
        tvAllocationDate = (CustomTextView) findViewById(R.id.tvAllocationDate);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerAllocation);
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

    public class getProjectDetails extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.PROJECTSDETAILS);
            jsonObject.addProperty("user_id", AppPreference.getUserId(ProjectDetailsActivity.this)); //8
            jsonObject.addProperty("project_id", project_id); //8
            jsonObject.addProperty(AppConstants.KEYS.LOGIN_TYPE_ID, AppPreference.getUserTypeId(ProjectDetailsActivity.this));

            Log.e(TAG, "Request GET PROJECT DETAILS >> " + jsonObject.toString());


            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response GET PROJECT DETAILS >>" + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ProjectDetailsModal detailsModal = new ProjectDetailsModal();
                                JSONObject object = jsonArray.getJSONObject(i);
                                detailsModal.setProject_id(object.getString("project_id"));
                                detailsModal.setProject_title(object.getString("project_title"));
                                detailsModal.setCity(object.getString("city"));
                                detailsModal.setAddress(object.getString("address"));
                                detailsModal.setCreated_date(object.getString("created_date"));
                                detailsModal.setAllotted_date(object.getString("allotted_date"));
                                detailsModal.setProject_desc(object.getString("project_desc"));

                                JSONArray json_array_allocation_media = object.getJSONArray("media_detail");
                                for (int j = 0; j < json_array_allocation_media.length(); j++) {
                                   MediaModel modal = new MediaModel();
                                    JSONObject jsonObject1 = json_array_allocation_media.getJSONObject(j);
                                    modal.setMedia_id(jsonObject1.getString("media_id"));
                                    modal.setMedia_name(jsonObject1.getString("media_name"));
                                    allocationMediaList.add(modal);
                                }


                                modalList.add(detailsModal);

                                tvProjectTittle.setText(detailsModal.getProject_title());
                                tvProjectDesc.setText(detailsModal.getProject_desc());
                                tvCreationDate.setText(detailsModal.getCreated_date());
                                tvAllocationDate.setText(detailsModal.getAllotted_date());
                            }
                            publishProgress("200", "");
                        } else
                        {
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            publishProgress("400", msg);
                        }

                        mediaAdapter.notifyDataSetChanged();
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
            String status=values[0];
            String msg=values[1];
            if (status.equalsIgnoreCase("400")) {
                Toast.makeText(ProjectDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
        /* private void publishProgress(int status, String msg) {
            hideProgressDialog();
            if (status == 400) {
                Toast.makeText(ProjectDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }*/
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
                onBackPressed();
                break;
            case R.id.action_home:
                Intent intent = new Intent(this, DashBoardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
        if (taskList.get(0).numActivities == 1 && taskList.get(0).topActivity.getClassName().equals(this.getClass().getName())) {
            Log.e(TAG, "This is Last activity in the stack");
            startActivity(new Intent(ProjectDetailsActivity.this, DashBoardActivity.class));
            super.onBackPressed();
        } else
            super.onBackPressed();
    }
}
