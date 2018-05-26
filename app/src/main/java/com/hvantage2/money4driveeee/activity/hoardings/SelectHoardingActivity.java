package com.hvantage2.money4driveeee.activity.hoardings;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectHoardingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SelectHoarding";
    private ArrayList<SourceModel> list;
    private SourceAdapter adapter;
    private TextView tvEmpty;
    private FloatingActionButton fab;
    private String media_option_id = "";
    private int total_quantity = 0, added_quantity = 0;
    private String start_date = "", end_date = "";
    private ProgressHUD progressHD;
    private RecyclerView recycler_view;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_transit_media);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        if (getIntent().hasExtra("media_option_id")) {
            media_option_id = getIntent().getStringExtra("media_option_id");
            setRecyclerView();
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
                    Log.e(TAG, "onClick: total_quantity >> " + total_quantity);
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
                                Intent intent = new Intent(SelectHoardingActivity.this, AddHoardingActivity.class);
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
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (media_option_id != null && !media_option_id.equalsIgnoreCase(""))
            new GetShopList().execute();
    }

    private void setRecyclerView() {
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view_transit);
        LinearLayoutManager manager = new LinearLayoutManager(SelectHoardingActivity.this);
        recycler_view.setLayoutManager(manager);
        adapter = new SourceAdapter(SelectHoardingActivity.this, list);
        recycler_view.setAdapter(adapter);
        recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(this, recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (list.get(position).getStatus().equalsIgnoreCase("1")) {
                    Intent intent = new Intent(SelectHoardingActivity.this, PerformHoardingActivity.class);
                    intent.putExtra("media_option_id", media_option_id);
                    AppPreference.setSelectedHoardingId(SelectHoardingActivity.this, list.get(position).getId());
                    AppPreference.setSelectedHoardingName(SelectHoardingActivity.this, list.get(position).getName());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SelectHoardingActivity.this, HoardingDetailActivity.class);
                    intent.putExtra("media_option_id", media_option_id);
                    AppPreference.setSelectedHoardingId(SelectHoardingActivity.this, list.get(position).getId());
                    AppPreference.setSelectedHoardingName(SelectHoardingActivity.this, list.get(position).getName());
                    startActivity(intent);
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
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onResume();
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
                finish();
                break;
            case R.id.action_home:
                startActivity(new Intent(this, DashBoardActivity.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
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

    public class GetShopList extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
            tvEmpty.setVisibility(View.GONE);
            list.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.PROJECTHOARDINGLIST);
            jsonObject.addProperty("user_id", AppPreference.getUserId(SelectHoardingActivity.this)); //8
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SelectHoardingActivity.this));
            jsonObject.addProperty("media_option_id", media_option_id);
            Log.e(TAG, "Request GET HOARDING LIST >> " + jsonObject);

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_hoardings_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response GET HOARDING LIST >> " + response.body().toString());
                    String str = response.body().toString();
                    try {
                        JSONObject jsonObject1 = new JSONObject(str);
                        if (jsonObject1.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject1.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject11 = jsonArray.getJSONObject(i);
                                SourceModel model = new SourceModel();
                                model.setId(jsonObject11.getString("hoarding_id"));
                                model.setName(jsonObject11.getString("hoarding_name"));
                                model.setStatus(jsonObject11.getString("check_status"));
                                list.add(model);
                            }
                            JSONArray jsonArray1 = jsonObject1.getJSONArray("other_info");
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject jsonObject11 = jsonArray1.getJSONObject(i);
                                total_quantity = jsonObject11.getInt("total_quantity");
                                added_quantity = jsonObject11.getInt("added_quantity");
                                start_date = jsonObject11.getString("start_date");
                                end_date = jsonObject11.getString("end_date");
                            }
                            adapter.notifyDataSetChanged();
                            publishProgress("200", "");
                        } else if (jsonObject1.getString("status").equalsIgnoreCase("400")) {
                            String msg = jsonObject1.getJSONArray("result").getJSONObject(0).getString("msg");
                            JSONArray jsonArray1 = jsonObject1.getJSONArray("other_info");
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject jsonObject11 = jsonArray1.getJSONObject(i);
                                total_quantity = jsonObject11.getInt("total_quantity");
                                added_quantity = jsonObject11.getInt("added_quantity");
                                start_date = jsonObject11.getString("start_date");
                                end_date = jsonObject11.getString("end_date");
                            }
                            publishProgress("400", msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onResponse: " + e.getMessage());
                        publishProgress("400", getResources().getString(R.string.api_error_msg));
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    list.clear();
                    adapter.notifyDataSetChanged();
                    Log.e(TAG, "onFailure: " + t.getMessage());
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
                list.clear();
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(SelectHoardingActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

