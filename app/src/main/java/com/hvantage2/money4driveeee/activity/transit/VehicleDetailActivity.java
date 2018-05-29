package com.hvantage2.money4driveeee.activity.transit;

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

public class VehicleDetailActivity extends AppCompatActivity {

    private static final String TAG = "TransitDetailActivity";
    ArrayList<String> listState = new ArrayList<String>();
    ArrayList<String> listCity = new ArrayList<String>();
    private Button btnConfirm;
    private EditText etDriverName, etDriverContact, etVehicle, etRegNo, etDriverAddress;
    private Button btnCancel;
    private String media_option_id = "0";
    private ProgressHUD progressHD;
    private ImageView imgDoc1, imgDoc2;
    private TextView tvImgDoc1Remark, tvImgDoc2Remark;
    private AppCompatAutoCompleteTextView atvStates;
    private AppCompatAutoCompleteTextView atvCities;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transit_media_detail);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().hasExtra("media_option_id"))
            media_option_id = getIntent().getStringExtra("media_option_id");
        Log.e(TAG, "onCreate: media_option_id >> " + media_option_id);
        init();
        new getTransitDetail().execute();

    }

    private void init() {

        etDriverName = (EditText) findViewById(R.id.etDriverName);
        etDriverContact = (EditText) findViewById(R.id.etDriverContact);
        etVehicle = (EditText) findViewById(R.id.etVehicle);
        etRegNo = (EditText) findViewById(R.id.etRegNo);
        etDriverAddress = (EditText) findViewById(R.id.etDriverAddress);

        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new updateTransitTask().execute();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

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

    @Override
    public void onBackPressed() {
        finish();
    }

    public class getTransitDetail extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.GETTRANSITDETAIL);
            jsonObject.addProperty("user_id", AppPreference.getUserId(VehicleDetailActivity.this));
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(VehicleDetailActivity.this));
            jsonObject.addProperty("transit_id", media_option_id);
            jsonObject.addProperty("vehicle_id", AppPreference.getSelectedVehicleId(VehicleDetailActivity.this));
            Log.e(TAG, "Request GET SHOP DETAILS >> " + jsonObject);

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_transit_api(jsonObject);
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
                            String transit_name = jsonObject11.getString("transit_name");
                            String driver_name = jsonObject11.getString("driver_name");
                            String driver_contact_no = jsonObject11.getString("driver_contact_no");
                            String vehicle_name = jsonObject11.getString("vehicle_name");
                            String vehicle_no = jsonObject11.getString("vehicle_no");
                            //vehicle_id = jsonObject11.getString("vehicle_id");
                            String state = jsonObject11.getString("state");
                            String city = jsonObject11.getString("city");
                            String address = jsonObject11.getString("address");

                            etDriverName.setText(driver_name);
                            etDriverContact.setText(driver_contact_no);
                            etVehicle.setText(vehicle_name);
                            etRegNo.setText(vehicle_no);
                            atvStates.setText(state);
                            atvCities.setText(city);
                            etDriverAddress.setText(address);
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
                Toast.makeText(VehicleDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class updateTransitTask extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(AppConstants.KEYS.METHOD, AppConstants.FEILDEXECUTATIVE.UPDATETRANSITDETAILS);
            jsonObject.addProperty(AppConstants.KEYS.USER_ID, AppPreference.getUserId(VehicleDetailActivity.this)); //8
            jsonObject.addProperty(AppConstants.KEYS.PROJECT_ID, AppPreference.getSelectedProjectId(VehicleDetailActivity.this));
            jsonObject.addProperty(AppConstants.KEYS.TRANSIT_ID, media_option_id);
            jsonObject.addProperty(AppConstants.KEYS.VEHICLE_ID, AppPreference.getSelectedVehicleId(VehicleDetailActivity.this));
            jsonObject.addProperty(AppConstants.KEYS.DRIVER_NAME, etDriverName.getText().toString());
            jsonObject.addProperty(AppConstants.KEYS.DRIVER_CONTACT_NO, etDriverContact.getText().toString());
            jsonObject.addProperty(AppConstants.KEYS.VEHICLE_MODEL, etVehicle.getText().toString());
            jsonObject.addProperty(AppConstants.KEYS.VEHICLE_REGIS_NUMBER, etRegNo.getText().toString());
            jsonObject.addProperty(AppConstants.KEYS.STATE, atvStates.getText().toString());
            jsonObject.addProperty(AppConstants.KEYS.CITY, atvCities.getText().toString());
            jsonObject.addProperty(AppConstants.KEYS.ADDRESS, etDriverAddress.getText().toString());

            Log.e(TAG, "Request UPDATE DETAIL >> " + jsonObject);

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_transit_api(jsonObject);
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
                Intent intent = new Intent(VehicleDetailActivity.this, PerformTransitActivity.class);
                intent.putExtra("media_option_id", media_option_id);
                startActivity(intent);
                finish();
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(VehicleDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
