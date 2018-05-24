package com.hvantage2.money4driveeee.activity.print;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.activity.DashBoardActivity;
import com.hvantage2.money4driveeee.adapter.MediaAdapter;
import com.hvantage2.money4driveeee.customview.CustomTextView;
import com.hvantage2.money4driveeee.database.DBHelper;
import com.hvantage2.money4driveeee.model.MediaModel;
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

public class SelectPrintMediaOptionActivity extends AppCompatActivity {

    private static final String TAG = "SelectShopMediaOption";
    private ArrayList<MediaModel> list;
    private CustomTextView tvEmpty;
    private RecyclerView recycler_view;
    private MediaAdapter adapter;
    private String media_type_id = "";
    private ProgressHUD progressHD;
    private Context context;
    private DBHelper db;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_transit_media);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new DBHelper(context);
        list = new ArrayList<MediaModel>();
        init();
        media_type_id = getIntent().getStringExtra("media_type_id");
        Log.e(TAG, "onCreate: media_type_id >> " + media_type_id);
        if (db.getMediaOptions(AppPreference.getSelectedProjectId(context), media_type_id, AppPreference.getSelectedAlloMediaId(context)) != null) {
            list = db.getMediaOptions(AppPreference.getSelectedProjectId(context), media_type_id, AppPreference.getSelectedAlloMediaId(context));
            Log.e(TAG, "onCreateView: list >> " + list);
        } else {
            getDataFromServer();
        }
        setAdapter();

    }

    private void getDataFromServer() {
        if (Functions.isConnectingToInternet(context)) {
            Log.e(TAG, "getDataFromServer: deleteMediaTypes() >> " + db.deleteMediaOptions(AppPreference.getSelectedProjectId(context), media_type_id, AppPreference.getSelectedAlloMediaId(context)));
            tvEmpty.setVisibility(View.GONE);
            new GetData().execute();
        } else {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    private void init() {
        tvEmpty = (CustomTextView) findViewById(R.id.tvEmpty);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromServer();
            }
        });
    }

    private void setAdapter() {
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view_transit);
        LinearLayoutManager manager = new LinearLayoutManager(SelectPrintMediaOptionActivity.this);
        recycler_view.setLayoutManager(manager);
        adapter = new MediaAdapter(SelectPrintMediaOptionActivity.this, list);
        recycler_view.setAdapter(adapter);
        recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(SelectPrintMediaOptionActivity.this, recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(SelectPrintMediaOptionActivity.this, SelectMediaActivity.class);
                intent.putExtra("media_option_id", list.get(position).getMedia_id());
                intent.putExtra("media_option_name", list.get(position).getMedia_name());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
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

    public class GetData extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            refreshLayout.setRefreshing(false);
            showProgressDialog();
            list.clear();
            tvEmpty.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.GETSHOPMEDIAOPTION);
            jsonObject.addProperty("user_id", AppPreference.getUserId(SelectPrintMediaOptionActivity.this)); //8
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SelectPrintMediaOptionActivity.this));
            jsonObject.addProperty("branding_id", AppPreference.getSelectedAlloMediaId(SelectPrintMediaOptionActivity.this));
            jsonObject.addProperty("media_type_id", media_type_id);
            Log.e(TAG, "Request PRINT MEDIA_OPTIONS >> " + jsonObject);

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_shop_detail_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response PRINT MEDIA_OPTIONS >> " + response.body().toString());
                    String str = response.body().toString();


                    try {
                        JSONObject jsonObject1 = new JSONObject(str);
                        if (jsonObject1.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject1.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject11 = jsonArray.getJSONObject(i);
                                MediaModel data = new MediaModel(jsonObject11.getString("media_option_id"), jsonObject11.getString("media_option_name"));
                                list.add(data);
                                //insert into local
                                db.saveMediaOption(data, AppPreference.getSelectedProjectId(context), media_type_id, AppPreference.getSelectedAlloMediaId(context));
                            }
                            publishProgress("200", "");
                        } else {
                            String msg = jsonObject1.getJSONArray("result").getJSONObject(0).getString("msg");
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
            adapter.notifyDataSetChanged();
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("200")) {
            } else if (status.equalsIgnoreCase("400")) {
                tvEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
