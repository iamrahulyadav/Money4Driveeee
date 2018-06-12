package com.hvantage2.money4driveeee.activity.print;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class SelectMediaActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SelectMediaActivity";
    ArrayList<SourceModel> list;
    String end_date = "";
    private RecyclerView recycler_view;
    private SourceAdapter adapter;
    private ProgressDialog dialog;
    private String media_option_id = "";
    private TextView tvEmpty;
    private int total_quantity = 0, added_quantity = 0;
    private String start_date = "";
    private String media_option_name = "";
    private ProgressHUD progressHD;
    private SwipeRefreshLayout refreshLayout;


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
            getSupportActionBar().setTitle(media_option_name);
            setRecyclerView();
        } else {
            Toast.makeText(this, "Select Option", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (media_option_id != null && !media_option_id.equalsIgnoreCase("")) ;
        new GetShopList().execute();
    }

    private void setRecyclerView() {
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view_transit);
        LinearLayoutManager manager = new LinearLayoutManager(SelectMediaActivity.this);
        recycler_view.setLayoutManager(manager);
        adapter = new SourceAdapter(SelectMediaActivity.this, list);
        recycler_view.setAdapter(adapter);
        recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(this, recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (list.get(position).getStatus().equalsIgnoreCase("1")) {
                    Intent intent = new Intent(SelectMediaActivity.this, PerformPMediaActivity.class);
                    intent.putExtra("media_option_id", media_option_id);
                    AppPreference.setSelectedPMediaId(SelectMediaActivity.this, list.get(position).getId());
                    AppPreference.setSelectedPMediaName(SelectMediaActivity.this, list.get(position).getName());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SelectMediaActivity.this, MediaDetailActivity.class);
                    intent.putExtra("media_option_id", media_option_id);
                    AppPreference.setSelectedPMediaId(SelectMediaActivity.this, list.get(position).getId());
                    AppPreference.setSelectedPMediaName(SelectMediaActivity.this, list.get(position).getName());
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));


    }

    private void init() {
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
            refreshLayout.setRefreshing(false);
            showProgressDialog();
            tvEmpty.setVisibility(View.GONE);
            list.clear();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.GETPROJECTPRINTMEDIALIST);
            jsonObject.addProperty("user_id", AppPreference.getUserId(SelectMediaActivity.this)); //8
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SelectMediaActivity.this));
            jsonObject.addProperty("media_option_id", media_option_id);
            Log.e(TAG, "Request GET MEDIA LIST >> " + jsonObject);

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_print_detail_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response MEDIA SHOP LIST >> " + response.body().toString());
                    String str = response.body().toString();
                    try {
                        JSONObject jsonObject1 = new JSONObject(str);
                        if (jsonObject1.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject1.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject11 = jsonArray.getJSONObject(i);
                                SourceModel model = new SourceModel();
                                model.setId(jsonObject11.getString("print_id"));
                                model.setName(jsonObject11.getString("print_name"));
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
                Toast.makeText(SelectMediaActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
