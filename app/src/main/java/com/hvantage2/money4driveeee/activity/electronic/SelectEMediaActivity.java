package com.hvantage2.money4driveeee.activity.electronic;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.activity.DashBoardActivity;
import com.hvantage2.money4driveeee.adapter.SourceAdapter;

import com.hvantage2.money4driveeee.model.SourceModel;
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

public class SelectEMediaActivity extends AppCompatActivity {
    private static final String TAG = "SelectEMediaActivity";
    ArrayList<SourceModel> list;
    private SourceAdapter adapter;
    private String media_option_id = "";
    private TextView tvEmpty;
    private ProgressHUD progressHD;
    private RecyclerView recycler_view_reg_no;
    private String media_option_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_transit_media);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        list = new ArrayList<SourceModel>();
        init();
        if (getIntent().hasExtra("media_option_id")) {
            media_option_id = getIntent().getStringExtra("media_option_id");
            media_option_name = getIntent().getStringExtra("media_option_name");
            Log.e(TAG, "onCreate: media_option_id >> " + media_option_id);
            Log.e(TAG, "onCreate: media_option_name >> " + media_option_name);
            if(!media_option_name.equalsIgnoreCase("")&&media_option_name!=null)
                getSupportActionBar().setTitle(media_option_name);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (media_option_id != null && !media_option_id.equalsIgnoreCase(""))
            new getTransitList().execute();
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

    private void setTransitListRecyclerView() {
        recycler_view_reg_no = (RecyclerView) findViewById(R.id.recycler_view_transit);
        LinearLayoutManager manager = new LinearLayoutManager(SelectEMediaActivity.this);
        recycler_view_reg_no.setLayoutManager(manager);
        adapter = new SourceAdapter(SelectEMediaActivity.this, list);
        recycler_view_reg_no.setAdapter(adapter);
        recycler_view_reg_no.addOnItemTouchListener(new RecyclerItemClickListener(SelectEMediaActivity.this, recycler_view_reg_no, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AppPreference.setSelectedEMediaId(SelectEMediaActivity.this, list.get(position).getId());
                AppPreference.setSelectedEMediaName(SelectEMediaActivity.this, list.get(position).getName());
                if (list.get(position).getStatus().equalsIgnoreCase("1")) {
                    Intent intent = new Intent(SelectEMediaActivity.this, PerformEMediaActivity.class);
                    intent.putExtra("media_option_id", media_option_id);
                    startActivity(intent);
                } else {
                    Intent intent1 = new Intent(SelectEMediaActivity.this, EMediaDetailActivity.class);
                    intent1.putExtra("media_option_id", media_option_id);
                    startActivity(intent1);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }

    private void init() {
        list = new ArrayList<SourceModel>();
        tvEmpty = (TextView) findViewById(R.id.tvEmpty);
        setTransitListRecyclerView();
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
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class getTransitList extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
            tvEmpty.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.PROJECTEMEDIALIST);
            jsonObject.addProperty("user_id", AppPreference.getUserId(SelectEMediaActivity.this));
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SelectEMediaActivity.this));
            jsonObject.addProperty("media_option_id", media_option_id);
            Log.e(TAG, "Request GET EMEDIA >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_emedia_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response GET EMEDIA >>" + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        list.clear();
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                SourceModel model = new SourceModel();
                                JSONObject object = jsonArray.getJSONObject(i);
                                model.setId(object.getString("emedia_id"));
                                model.setName(object.getString("emedia_name"));
                                model.setStatus(object.getString("check_status"));
                                list.add(model);
                            }
                           /* JSONArray jsonArray1 = jsonObject.getJSONArray("other_info");
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject jsonObject11 = jsonArray1.getJSONObject(i);
                                total_quantity = jsonObject11.getInt("total_quantity");
                                added_quantity = jsonObject11.getInt("added_quantity");
                                start_date = jsonObject11.getString("start_date");
                                end_date = jsonObject11.getString("end_date");
                            }*/
                            adapter.notifyDataSetChanged();
                            publishProgress("200", "");
                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
//                            JSONArray jsonArray1 = jsonObject.getJSONArray("other_info");
                            /*for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject jsonObject11 = jsonArray1.getJSONObject(i);
                                total_quantity = jsonObject11.getInt("total_quantity");
                                added_quantity = jsonObject11.getInt("added_quantity");
                                start_date = jsonObject11.getString("start_date");
                                end_date = jsonObject11.getString("end_date");
                            }*/
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
                    list.clear();
                    adapter.notifyDataSetChanged();
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
            if (status.equalsIgnoreCase("200"))
                tvEmpty.setVisibility(View.GONE);
            if (status.equalsIgnoreCase("400")) {
                tvEmpty.setVisibility(View.VISIBLE);
                list.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(SelectEMediaActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
