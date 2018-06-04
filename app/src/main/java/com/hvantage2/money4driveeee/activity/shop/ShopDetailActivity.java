package com.hvantage2.money4driveeee.activity.shop;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.activity.DashBoardActivity;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.Functions;
import com.hvantage2.money4driveeee.util.ProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ShopDetailActivity";
    private static final int REQUEST_STORAGE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = REQUEST_STORAGE + 1;
    private static final int REQUEST_LOAD_IMAGE = REQUEST_IMAGE_CAPTURE + 1;
    ArrayList<String> listState = new ArrayList<String>();
    ArrayList<String> listCity = new ArrayList<String>();
    String project_id = "", shop_id = "";
    ProgressDialog dialog;
    private EditText etname, etMobile, etShopID, etAddress;
    private AppCompatAutoCompleteTextView atvStates;
    private AppCompatAutoCompleteTextView atvCities;
    private String shop_name = "";
    private String media_option_id = "";
    private ProgressHUD progressHD;
    private ImageView imgDoc1, imgDoc2;
    private TextView tvImgDoc1Remark, tvImgDoc2Remark;
    private Context context;
    private int imgCounter = 1;
    private String userChoosenTask = "";
    private String base64image1 = "", base64image2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        shop_id = AppPreference.getSelectedShopid(ShopDetailActivity.this);
        project_id = AppPreference.getSelectedProjectId(ShopDetailActivity.this);
        if (getIntent().hasExtra("media_option_id"))
            media_option_id = getIntent().getStringExtra("media_option_id");
        Log.e(TAG, "onCreate: project_id >> " + project_id);
        Log.e(TAG, "onCreate: shop_id >> " + shop_id);
        new getDetailTask().execute();
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

    private void init() {
        etname = (EditText) findViewById(R.id.etname);
        etMobile = (EditText) findViewById(R.id.etMobile);
        etShopID = (EditText) findViewById(R.id.etShopID);
        etAddress = (EditText) findViewById(R.id.etAddress);
        ((Button) findViewById(R.id.btnConfirm)).setOnClickListener(this);
        ((Button) findViewById(R.id.btnCancel)).setOnClickListener(this);

        ((ScrollView) findViewById(R.id.container)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSoftKeyboard(view);
                return false;
            }
        });

        imgDoc1 = (ImageView) findViewById(R.id.imgDoc1);
        imgDoc2 = (ImageView) findViewById(R.id.imgDoc2);

        tvImgDoc1Remark = (TextView) findViewById(R.id.tvImgDoc1Remark);
        tvImgDoc2Remark = (TextView) findViewById(R.id.tvImgDoc2Remark);

        atvStates = (AppCompatAutoCompleteTextView) findViewById(R.id.atvStates);
        atvCities = (AppCompatAutoCompleteTextView) findViewById(R.id.atvCities);

        imgDoc1.setOnClickListener(this);
        imgDoc2.setOnClickListener(this);

        atvStates.setThreshold(2);
        atvCities.setThreshold(2);
        setStateAdapter();
        setCityAdapter();
        atvStates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int p, long l) {
                setCityAdapter();
            }
        });
    }

    private void setCityAdapter() {
        try {
            JSONObject jsonObject = new JSONObject(Functions.loadJSONFromAsset(context, "json_cities.json"));
            JSONObject cityJObj = jsonObject.getJSONObject(atvStates.getText().toString());
            JSONArray jsonArray = cityJObj.getJSONArray("name");
            for (int i = 0; i < jsonArray.length(); i++) {
                String state = jsonArray.getString(i);
                listCity.add(state);
            }
            ArrayAdapter<String> adapterCity = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listCity);
            atvCities.setAdapter(adapterCity);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setStateAdapter() {
        try {
            JSONObject jsonObject = new JSONObject(Functions.loadJSONFromAsset(context, "json_states.json"));
            JSONArray jsonArray = jsonObject.getJSONArray("name");
            for (int i = 0; i < jsonArray.length(); i++) {
                String state = jsonArray.getString(i);
                listState.add(state);
            }
            ArrayAdapter<String> adapterState = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listState);
            atvStates.setAdapter(adapterState);
            Log.e(TAG, "setAdapter: listState >> " + listState);
        } catch (JSONException e) {
            Log.e(TAG, "setAdapter: Exc >> " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                Intent intent = new Intent(ShopDetailActivity.this, DashBoardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.btnCancel:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnConfirm) {
            new updateDetailTask().execute();
        }
    }

    public class getDetailTask extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.PROJECTSSHOPDETAILS);
            jsonObject.addProperty("user_id", AppPreference.getUserId(ShopDetailActivity.this)); //8
            jsonObject.addProperty("project_id", project_id);
            jsonObject.addProperty("shop_id", shop_id);
            Log.e(TAG, "Request GET SHOP DETAILS >> " + jsonObject);

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response GET SHOP DETAILS >> " + response.body().toString());
                    String str = response.body().toString();
                    try {
                        JSONObject jsonObject1 = new JSONObject(str);
                        if (jsonObject1.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject1.getJSONArray("result");
                            JSONObject jsonObject11 = jsonArray.getJSONObject(0);
                            shop_name = jsonObject11.getString("shop_name");
                            etname.setText(jsonObject11.getString("shop_contact_per_name"));
                            etMobile.setText(jsonObject11.getString("shop_contact_per_number"));
                            etShopID.setText(jsonObject11.getString("shop_id"));
                            etAddress.setText(jsonObject11.getString("address"));
                            atvCities.setText(jsonObject11.getString("city"));
                            atvStates.setText(jsonObject11.getString("state"));
                            //JSONArray jsonArrayActivity = jsonObject11.getJSONArray("shop_activities");
                            /*for (int i = 0; i < jsonArrayActivity.length(); i++) {
                                JSONObject jsonObjectActivity = jsonArrayActivity.getJSONObject(i);
                                int activity_status = jsonObjectActivity.getInt("activity_status");
                                String activity_name = jsonObjectActivity.getString("activity_name");
                                String activity_id = jsonObjectActivity.getString("activity_id");
                                ShopActivity model = new ShopActivity(activity_id, activity_name, activity_status);
                                activityList.add(model);
                            }*/
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
                    Log.e(TAG, "onFailure: exc >> " + t.getMessage());
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
                Toast.makeText(ShopDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class updateDetailTask extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.SAVESHOPDETAIL);
            jsonObject.addProperty("user_id", AppPreference.getUserId(ShopDetailActivity.this)); //8
            jsonObject.addProperty("project_id", project_id);
            jsonObject.addProperty("shop_id", AppPreference.getSelectedShopid(ShopDetailActivity.this));
            jsonObject.addProperty("shop_contact_per_name", etname.getText().toString());
            jsonObject.addProperty("shop_contact_per_number", etMobile.getText().toString());
            jsonObject.addProperty("address", etAddress.getText().toString());
            jsonObject.addProperty("state", atvStates.getText().toString());
            jsonObject.addProperty("city", atvCities.getText().toString());
            jsonObject.addProperty("doc_img1", base64image1);
            jsonObject.addProperty("doc_img2", base64image2);
            jsonObject.addProperty("img1_remark", tvImgDoc1Remark.getText().toString());
            jsonObject.addProperty("img2_remark", tvImgDoc2Remark.getText().toString());
            Log.e(TAG, "Request UPDATE DETAIL >> " + jsonObject);

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response UPDATE DETAIL >> " + response.body().toString());
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
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("200")) {
                Intent intent = new Intent(ShopDetailActivity.this, PerformShopActivity.class);
                intent.putExtra("shop_name", shop_name);
                intent.putExtra("media_option_id", media_option_id);
                startActivity(intent);
                finish();
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(ShopDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
