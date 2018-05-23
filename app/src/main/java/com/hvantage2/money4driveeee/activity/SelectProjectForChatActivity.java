package com.hvantage2.money4driveeee.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.adapter.SelectProjectAdapter;
import com.hvantage2.money4driveeee.customview.CustomTextView;
import com.hvantage2.money4driveeee.model.ProjectModel;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.Functions;
import com.hvantage2.money4driveeee.util.ProgressHUD;
import com.hvantage2.money4driveeee.util.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectProjectForChatActivity extends AppCompatActivity {

    private static final String TAG = "SelectProjectFChat";
    private RecyclerView recyclerView;
    private CustomTextView tvEmpty;
    private ArrayList<ProjectModel> list;
    private SelectProjectAdapter adapter;
    private ProgressHUD progressHD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_project_for_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        setAdapter();
        getAllProject();
    }

    private void init() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        tvEmpty = (CustomTextView) findViewById(R.id.tvEmpty);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void setAdapter() {
        list = new ArrayList<ProjectModel>();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new SelectProjectAdapter(this, list);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(SelectProjectForChatActivity.this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent= new Intent(SelectProjectForChatActivity.this, ChatActivity.class);
                intent.putExtra("project_title",list.get(position).getProjectTittle());
                intent.putExtra("project_subtitle",list.get(position).getProject_desc());
                intent.putExtra("project_id",list.get(position).getProject_id());
                startActivity(intent);
                finish();
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));
    }


    private void getAllProject() {
        if (Functions.isConnectingToInternet(this)) {
            tvEmpty.setVisibility(View.GONE);
            new getAllProjectTask().execute();
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
        }
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

    public class getAllProjectTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.USERALLPROJECTLISTDATA);
            jsonObject.addProperty("user_id", AppPreference.getUserId(SelectProjectForChatActivity.this)); //8
            jsonObject.addProperty("user_type", AppPreference.getUserTypeId(SelectProjectForChatActivity.this));
            Log.e(TAG, "Request GET ALL PROJECTS >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.user_chat_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response GET ALL PROJECTS >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                ProjectModel model = new ProjectModel();
                                model.setProject_id(jsonObject1.getString("project_id"));
                                model.setProjectTittle(jsonObject1.getString("project_title"));

                                String survey_user_name = jsonObject1.getString("survey_user_name");
                                String execution_user_name = jsonObject1.getString("execution_user_name");
                                String data_user_name = jsonObject1.getString("data_user_name");
                                String sales_user_name = jsonObject1.getString("sales_user_name");
                                ArrayList<String> group_list = new ArrayList<String>();
                                if (!survey_user_name.equalsIgnoreCase(""))
                                    group_list.add(survey_user_name);
                                if (!execution_user_name.equalsIgnoreCase(""))
                                    group_list.add(execution_user_name);
                                if (!data_user_name.equalsIgnoreCase(""))
                                    group_list.add(data_user_name);
                                if (!sales_user_name.equalsIgnoreCase(""))
                                    group_list.add(sales_user_name);

                                String groupnames = TextUtils.join(", ", group_list);
                                Log.e(TAG, "onResponse: "+ groupnames);
                                model.setProject_desc(groupnames);
                                list.add(model);
                            }

                            publishProgress("200","");

                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            list.clear();
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            publishProgress("400", msg);
                        }
                    } catch (JSONException e) {
                        list.clear();
                        e.printStackTrace();
                        publishProgress("400", getResources().getString(R.string.api_error_msg));
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
            adapter.notifyDataSetChanged();
            if (status.equalsIgnoreCase("400")) {
            }
            if (adapter != null) {
                if (adapter.getItemCount() == 0)
                    tvEmpty.setVisibility(View.VISIBLE);
                else
                    tvEmpty.setVisibility(View.GONE);
            }
        }
    }
}
