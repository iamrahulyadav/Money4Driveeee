package com.hvantage2.money4driveeee.activity.transit;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.hvantage2.money4driveeee.activity.DashBoardActivity;
import com.hvantage2.money4driveeee.adapter.SourceAdapter;
import com.hvantage2.money4driveeee.model.SourceModel;
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

public class SelectVehicleActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SelectTransitMedia";
    private ArrayList<SourceModel> list;
    private RecyclerView recycler_view_reg_no;
    private ProgressDialog dialog;
    private SourceAdapter adapter;
    private TextView tvEmpty;
    private FloatingActionButton fab;
    private String media_option_id = "";
    private int total_quantity = 0, added_quantity = 0;
    private String start_date = "", end_date = "";
    private ProgressHUD progressHD;
    private SwipeRefreshLayout refreshLayout;
    private int total_days = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_transit_media);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select Vehicle");
        init();
        if (getIntent().hasExtra("media_option_id")) {
            media_option_id = getIntent().getStringExtra("media_option_id");
            Log.e(TAG, "onCreate: media_option_id >> " + media_option_id);
        } else {
            Toast.makeText(this, "Select media option", Toast.LENGTH_SHORT).show();
            finish();
        }
        setFAB();

    }

    private void setFAB() {
        if (AppPreference.getSelectedProjectType(this).equalsIgnoreCase(AppConstants.PROJECT_TYPE.PENDING)) {
            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setVisibility(View.VISIBLE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (added_quantity < total_quantity) {
                        Intent intent = new Intent(SelectVehicleActivity.this, AddTransitActivity.class);
                        intent.putExtra("total_days", total_days);
                        intent.putExtra("media_option_id", media_option_id);
                        startActivity(intent);
                    } else {
                        Snackbar.make(view, "Branding limit is over, can't add new vehicle!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        }).show();
                    }
                    /*Log.e(TAG, "onClick: total_quantity >> " + total_quantity);
                    Log.e(TAG, "onClick: added_quantity >> " + added_quantity);
                    if (added_quantity < total_quantity) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                        try {
                            Date today_date = Calendar.getInstance().getTime();
                            Date end_date1 = sdf.parse(end_date);
                            Log.e(TAG, "onClick: today_date >> " + today_date);
                            Log.e(TAG, "onClick: end_date >> " + end_date1);
                            if (end_date1.compareTo(today_date) < 0)
                                Snackbar.make(view, "Branding date is over", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                    }
                                }).show();
                            else {
                                Intent intent = new Intent(SelectVehicleActivity.this, AddTransitActivity.class);
                                intent.putExtra("start_date", start_date);
                                intent.putExtra("end_date", end_date);
                                intent.putExtra("media_option_id", media_option_id);
                                startActivity(intent);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Snackbar.make(view, "Branding limit is over, can't add new shop!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        }).show();
                    }*/
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (media_option_id != null && !media_option_id.equalsIgnoreCase(""))
            new getTransitList().execute(media_option_id);
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
        LinearLayoutManager manager = new LinearLayoutManager(SelectVehicleActivity.this);
        recycler_view_reg_no.setLayoutManager(manager);
        adapter = new SourceAdapter(SelectVehicleActivity.this, list);
        recycler_view_reg_no.setAdapter(adapter);
        recycler_view_reg_no.addOnItemTouchListener(new RecyclerItemClickListener(SelectVehicleActivity.this, recycler_view_reg_no, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AppPreference.setSelectedVehicleId(SelectVehicleActivity.this, list.get(position).getId());
                AppPreference.setSelectedVehicleName(SelectVehicleActivity.this, list.get(position).getName());
                if (list.get(position).getStatus().equalsIgnoreCase("1")) {
                    Intent intent = new Intent(SelectVehicleActivity.this, PerformTransitActivity.class);
                    intent.putExtra("media_option_id", media_option_id);
                    startActivity(intent);
                } else {
                    Log.e(TAG, "onItemClick: Clicked ");
                    Intent intent1 = new Intent(SelectVehicleActivity.this, ConfirmTransitActivity.class);
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
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (media_option_id != null && !media_option_id.equalsIgnoreCase(""))
                    new getTransitList().execute(media_option_id);
            }
        });
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

    @Override
    public void onClick(View view) {
    }

    public class getTransitList extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            refreshLayout.setRefreshing(false);
            showProgressDialog();
            tvEmpty.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            String transit_id = strings[0];
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.GETTRANSITLIST);
            jsonObject.addProperty("user_id", AppPreference.getUserId(SelectVehicleActivity.this));
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SelectVehicleActivity.this));
            jsonObject.addProperty("transit_id", transit_id);
            Log.e(TAG, "Request GET VEHICLE >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_transit_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response GET VEHICLE >>" + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        list.clear();
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                SourceModel model = new SourceModel();
                                JSONObject object = jsonArray.getJSONObject(i);
                                model.setId(object.getString("vehicle_id"));
                                model.setName(object.getString("vehicle_name") + " (" + object.getString("vehicle_no") + ")");
                                model.setStatus(object.getString("vehicle_status"));
                                list.add(model);
                            }
                            JSONArray jsonArray1 = jsonObject.getJSONArray("other_info");
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject jsonObject11 = jsonArray1.getJSONObject(i);
                                total_quantity = jsonObject11.getInt("total_quantity");
                                added_quantity = jsonObject11.getInt("added_quantity");
                                total_days = jsonObject11.getInt("total_days");
                            }
                            adapter.notifyDataSetChanged();
                            publishProgress("200", "");
                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            JSONArray jsonArray1 = jsonObject.getJSONArray("other_info");
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject jsonObject11 = jsonArray1.getJSONObject(i);
                                total_quantity = jsonObject11.getInt("total_quantity");
                                added_quantity = jsonObject11.getInt("added_quantity");
                                total_days = jsonObject11.getInt("total_days");
                            }
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
                Toast.makeText(SelectVehicleActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
