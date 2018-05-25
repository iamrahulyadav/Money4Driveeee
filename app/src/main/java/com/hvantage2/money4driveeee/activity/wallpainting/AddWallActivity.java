package com.hvantage2.money4driveeee.activity.wallpainting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddWallActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddWallActivity";
    String wallName, contName, contNo, state, city, address, startDate, endDate;
    private Button btnCancel, btnConfirm;
    private EditText etWallName, etContName, etContNo, etState, etCity, etAddress, etStartDate, etEndDate;
    private String start_date = "", end_date = "";
    private String media_option_id = "";
    private ProgressHUD progressHD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wall);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        if (getIntent().hasExtra("media_option_id"))
            media_option_id = getIntent().getStringExtra("media_option_id");
        if (media_option_id == null || media_option_id.equalsIgnoreCase("")) {
            Toast.makeText(this, "Select Media Option", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (getIntent().hasExtra("start_date"))
            start_date = getIntent().getStringExtra("start_date");
        if (getIntent().hasExtra("end_date"))
            end_date = getIntent().getStringExtra("end_date");
        etStartDate.setText(start_date);
        etEndDate.setText(end_date);
    }

    private void init() {
        etWallName = (EditText) findViewById(R.id.etWallName);
        etContName = (EditText) findViewById(R.id.etContName);
        etContNo = (EditText) findViewById(R.id.etContNo);
        etState = (EditText) findViewById(R.id.etState);
        etCity = (EditText) findViewById(R.id.etCity);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etStartDate = (EditText) findViewById(R.id.etStartDate);
        etEndDate = (EditText) findViewById(R.id.etEndDate);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        btnCancel = (Button) findViewById(R.id.btnCancel);

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnConfirm:
                addWall();
                break;
            case R.id.btnCancel:
                onBackPressed();
                break;
        }
    }


    private void showErrorDialog400(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(AddWallActivity.this);
        dialog.setTitle("Message");
        dialog.setMessage(msg);
        dialog.setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    private void addWall() {
        wallName = etWallName.getText().toString();
        contName = etContName.getText().toString();
        contNo = etContNo.getText().toString();
        state = etState.getText().toString();
        city = etCity.getText().toString();
        address = etAddress.getText().toString();
        startDate = etStartDate.getText().toString();
        endDate = etEndDate.getText().toString();


        if (TextUtils.isEmpty(wallName))
            etWallName.setError("Enter wall name");
        else if (TextUtils.isEmpty(contName))
            etContName.setError("Enter contact person name");
        else if (TextUtils.isEmpty(contNo))
            etContNo.setError("Enter contact person no.");
        else if (TextUtils.isEmpty(state))
            etState.setError("Enter state");
        else if (TextUtils.isEmpty(city))
            etCity.setError("Enter city");
        else if (TextUtils.isEmpty(address))
            etAddress.setError("Enter address");
        else
            new AddWallActivity.AddWallTask().execute();

    }


    private class AddWallTask extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(String... strings) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.ADDWALLDETAIL);
            jsonObject.addProperty("user_id", AppPreference.getUserId(AddWallActivity.this));
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(AddWallActivity.this));
            jsonObject.addProperty("contact_per_number", contNo);
            jsonObject.addProperty("contact_per_name", contName);
            jsonObject.addProperty("branding_id", AppPreference.getSelectedAlloMediaId(AddWallActivity.this));
            jsonObject.addProperty("media_option_id", media_option_id);
            jsonObject.addProperty("wall_name", wallName);
            jsonObject.addProperty("state", state);
            jsonObject.addProperty("city", city);
            jsonObject.addProperty("address", address);

            Log.e(TAG, "Request ADD WALL >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_wall_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response ADD WALL >>" + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONObject jsonObject1 = jsonObject.getJSONArray("result").getJSONObject(0);
                            Log.e(TAG, "onResponse: inserted wall_id >> " + jsonObject1.getString("id"));
                            AppPreference.setSelectedWallId(AddWallActivity.this, jsonObject1.getString("id"));
                            publishProgress("200", resp);
                        } else {
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
            if (status.equalsIgnoreCase("200")) {
                AppPreference.setSelectedWallName(AddWallActivity.this, etWallName.getText().toString());
                Intent intent = new Intent(AddWallActivity.this, PerformWallActivity.class);
                intent.putExtra("media_option_id", media_option_id);
                startActivity(intent);
                finish();
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(AddWallActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

}

