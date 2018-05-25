package com.hvantage2.money4driveeee.activity.wallpainting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.activity.DashBoardActivity;


import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.ProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WallDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "WallDetailActivity";
    private EditText etContName;
    private EditText etContNo;
    private EditText etState;
    private EditText etCity;
    private EditText etAddress;
    private Button btnCancel;
    private Button btnConfirm;
    private ProgressHUD progressHD;
    private String media_option_id;
    private String project_id;
    private String wall_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        wall_id = AppPreference.getSelectedWallId(WallDetailActivity.this);
        project_id = AppPreference.getSelectedProjectId(WallDetailActivity.this);
        if (getIntent().hasExtra("media_option_id"))
            media_option_id = getIntent().getStringExtra("media_option_id");
        Log.e(TAG, "onCreate: media_option_id >> " + media_option_id);
        Log.e(TAG, "onCreate: project_id >> " + project_id);
        Log.e(TAG, "onCreate: wall_id >> " + wall_id);
        init();
        new getDetailTask().execute();
    }

    private void init() {
        etContName = (EditText) findViewById(R.id.etContName);
        etContNo = (EditText) findViewById(R.id.etContNo);
        etState = (EditText) findViewById(R.id.etState);
        etCity = (EditText) findViewById(R.id.etCity);
        etAddress = (EditText) findViewById(R.id.etAddress);

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);

        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);

        ((ScrollView) findViewById(R.id.container)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSoftKeyboard(view);
                return false;
            }
        });
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
        if (view.getId() == R.id.btnCancel)
            onBackPressed();
        else if (view.getId() == R.id.btnConfirm) {
            if (TextUtils.isEmpty(etContName.getText().toString()))
                etContName.setError("Enter contact person name");
            else if (TextUtils.isEmpty(etContNo.getText().toString()))
                etContNo.setError("Enter contact person no");
            else if (TextUtils.isEmpty(etState.getText().toString()))
                etState.setError("Enter state");
            else if (TextUtils.isEmpty(etCity.getText().toString()))
                etCity.setError("Enter city");
            else if (TextUtils.isEmpty(etAddress.getText().toString()))
                etAddress.setError("Enter address");
            else {
                new updateDetailTask().execute();
            }
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
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.PROJECTSWALLDETAILS);
            jsonObject.addProperty("user_id", AppPreference.getUserId(WallDetailActivity.this)); //8
            jsonObject.addProperty("project_id", project_id);
            jsonObject.addProperty("wall_id", wall_id);
            Log.e(TAG, "Request GET PRINT_MEDIA DETAILS >> " + jsonObject);

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_wall_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response GET PRINT_MEDIA DETAILS >> " + response.body().toString());
                    String str = response.body().toString();
                    try {
                        JSONObject jsonObject1 = new JSONObject(str);
                        if (jsonObject1.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject1.getJSONArray("result");
                            JSONObject jsonObject11 = jsonArray.getJSONObject(0);
                            String shop_contact_per_name = jsonObject11.getString("shop_contact_per_name");
                            String shop_contact_per_number = jsonObject11.getString("shop_contact_per_number");
                            String address = jsonObject11.getString("address");
                            String state = jsonObject11.getString("state");
                            String city = jsonObject11.getString("city");
                            
                            etContName.setText(shop_contact_per_name);
                            etContNo.setText(shop_contact_per_number);
                            etState.setText(state);
                            etCity.setText(city);
                            etAddress.setText(address);
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
                Toast.makeText(WallDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
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

    public class updateDetailTask extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.SAVEWALLDETAIL);
            jsonObject.addProperty("user_id", AppPreference.getUserId(WallDetailActivity.this)); //8
            jsonObject.addProperty("project_id", project_id);
            jsonObject.addProperty("wall_id", AppPreference.getSelectedWallId(WallDetailActivity.this));
            jsonObject.addProperty("shop_contact_per_name", etContName.getText().toString());
            jsonObject.addProperty("shop_contact_per_number", etContNo.getText().toString());
            jsonObject.addProperty("state", etState.getText().toString());
            jsonObject.addProperty("address", etAddress.getText().toString());
            jsonObject.addProperty("city", etCity.getText().toString());
            Log.e(TAG, "Request UPDATE DETAIL >> " + jsonObject);

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_wall_api(jsonObject);
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
                Intent intent = new Intent(WallDetailActivity.this, PerformWallActivity.class);
                intent.putExtra("media_option_id", media_option_id);
                startActivity(intent);
                finish();
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(WallDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
