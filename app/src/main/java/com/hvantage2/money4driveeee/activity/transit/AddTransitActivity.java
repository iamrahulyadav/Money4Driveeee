package com.hvantage2.money4driveeee.activity.transit;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.activity.DashBoardActivity;
import com.hvantage2.money4driveeee.adapter.DialogMultipleChoiceAdapter;
import com.hvantage2.money4driveeee.customview.CustomButton;
import com.hvantage2.money4driveeee.customview.CustomEditText;
import com.hvantage2.money4driveeee.model.ShopActivity;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.ProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTransitActivity extends AppCompatActivity implements View.OnClickListener {

    CustomEditText etDriverName, etDriverContact, etVehicle, etRegNo, etState, etDriverCity, etDriverAddress, etStartDate, etEndDate;
    CustomButton btnConfirm, btnCancel;
    String driverName, driverContact, vehicle, regNo, state, city, address, startDate, endDate;
    private String TAG = "AddTransitActivity";
    private ProgressDialog dialog;
    private List<ShopActivity> bOptionList;
    private String allBOptionIds = "";
    private String media_option_id = "";
    private String start_date = "", end_date = "";
    private ProgressHUD progressHD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bOptionList = new ArrayList<ShopActivity>();
        init();
        showDialog1();
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
        etDriverName = (CustomEditText) findViewById(R.id.etDriverName);
        etDriverContact = (CustomEditText) findViewById(R.id.etDriverContact);
        etVehicle = (CustomEditText) findViewById(R.id.etVehicle);
        etRegNo = (CustomEditText) findViewById(R.id.etRegNo);
        etState = (CustomEditText) findViewById(R.id.etState);
        etDriverCity = (CustomEditText) findViewById(R.id.etDriverCity);
        etDriverAddress = (CustomEditText) findViewById(R.id.etDriverAddress);
        etStartDate = (CustomEditText) findViewById(R.id.etStartDate);
        etEndDate = (CustomEditText) findViewById(R.id.etEndDate);

        btnConfirm = (CustomButton) findViewById(R.id.btnConfirm);
        btnCancel = (CustomButton) findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
       /* etStartDate.setOnClickListener(this);
        etEndDate.setOnClickListener(this);*/

        ((ScrollView) findViewById(R.id.container)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSoftKeyboard(view);
                return false;
            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enter Vehicle No.");
        builder.setCancelable(false);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.text_input_vehicle_no, (ViewGroup) findViewById(android.R.id.content), false);
        final CustomEditText input = (CustomEditText) viewInflated.findViewById(R.id.input);
        builder.setView(viewInflated);

        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String vehicle_no = input.getText().toString();
                dialog.dismiss();
                etRegNo.setText(vehicle_no);
                new CheckVehicleNoTask().execute(vehicle_no);
            }
        });
        builder.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.show();


    }

    private void showDialog1() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enter Vehicle No.");
        dialog.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.input_vehicle_no_layout, null);
        dialog.setView(dialogView);

        final EditText etState = (EditText) dialogView.findViewById(R.id.etState);
        final EditText etDistrictCode = (EditText) dialogView.findViewById(R.id.etDistrictCode);
        final EditText etSeries = (EditText) dialogView.findViewById(R.id.etSeries);
        final EditText etVehNo = (EditText) dialogView.findViewById(R.id.etVehNo);
        final ImageView imgRight = (ImageView) dialogView.findViewById(R.id.imgRight);

        dialog.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String vehicle_no = etState.getText().toString() + "-" + etDistrictCode.getText().toString() + "-" + etSeries.getText().toString() + "-" + etVehNo.getText().toString();
//                String vehicle_no = etState.getText().toString() + etDistrictCode.getText().toString() + etSeries.getText().toString() + etVehNo.getText().toString();
                Log.e(TAG, "showDialog1: onClick: Search: vehicle_no >> " + vehicle_no);
                dialog.dismiss();
                etRegNo.setText(vehicle_no);
                new CheckVehicleNoTask().execute(vehicle_no);
            }
        });
        dialog.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        etState.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (etState.length() == 2) {
                    etState.clearFocus();
                    etDistrictCode.requestFocus();
                    etDistrictCode.setCursorVisible(true);
                    etDistrictCode.setSelection(etDistrictCode.length());

                    if (etVehNo.length() == 4 && etDistrictCode.length() == 2 && etSeries.length() == 2) {
                        imgRight.setVisibility(View.VISIBLE);
                    } else
                        imgRight.setVisibility(View.GONE);

                } else {
                    imgRight.setVisibility(View.GONE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        etDistrictCode.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (etDistrictCode.length() == 2) {
                    etDistrictCode.clearFocus();
                    etSeries.requestFocus();
                    etSeries.setCursorVisible(true);
                    etVehNo.setCursorVisible(true);

                    if (etState.length() == 2 && etVehNo.length() == 4 && etSeries.length() == 2) {
                        imgRight.setVisibility(View.VISIBLE);
                    } else
                        imgRight.setVisibility(View.GONE);

                } else if (etDistrictCode.length() == 0) {
                    etDistrictCode.clearFocus();
                    etState.requestFocus();
                    etState.setCursorVisible(true);
                    etState.setSelection(etState.length());
                } else {
                    imgRight.setVisibility(View.GONE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        etSeries.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (etSeries.length() == 2) {
                    etSeries.clearFocus();
                    etVehNo.requestFocus();
                    etVehNo.setCursorVisible(true);
                    if (etState.length() == 2 && etDistrictCode.length() == 2 && etVehNo.length() == 4) {
                        imgRight.setVisibility(View.VISIBLE);
                    } else
                        imgRight.setVisibility(View.GONE);

                } else if (etSeries.length() == 0) {
                    etSeries.clearFocus();
                    etDistrictCode.requestFocus();
                    etDistrictCode.setCursorVisible(true);
                    etDistrictCode.setSelection(etDistrictCode.length());

                } else {
                    imgRight.setVisibility(View.GONE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        etVehNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etVehNo.length() == 0) {
                    imgRight.setVisibility(View.GONE);
                    etVehNo.clearFocus();
                    etSeries.requestFocus();
                    etSeries.setCursorVisible(true);
                } else if (etVehNo.length() == 4) {
                    if (etState.length() == 2 && etDistrictCode.length() == 2 && etSeries.length() == 2) {
                        imgRight.setVisibility(View.VISIBLE);
                    } else
                        imgRight.setVisibility(View.GONE);
                } else {
                    imgRight.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnConfirm:
                addTransit();
                break;
          /*  case R.id.etBOptions:
                showBrandingOptionDialog();
                break;*/
            case R.id.btnCancel:
                onBackPressed();
                break;
        }
    }

    private void showErrorDialog300(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(AddTransitActivity.this);
        dialog.setTitle("Message");
        dialog.setMessage(msg);
        dialog.setPositiveButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        dialog.setNegativeButton("Add New", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showErrorDialog400(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(AddTransitActivity.this);
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

    private void addTransit() {
        driverName = etDriverName.getText().toString();
        driverContact = etDriverContact.getText().toString();
        vehicle = etVehicle.getText().toString();
        regNo = etRegNo.getText().toString();
        state = etState.getText().toString();
        city = etDriverCity.getText().toString();
        address = etDriverAddress.getText().toString();
        startDate = etStartDate.getText().toString();
        endDate = etEndDate.getText().toString();

        if (TextUtils.isEmpty(driverName))
            etDriverName.setError("Enter driver name");
        else if (TextUtils.isEmpty(driverContact))
            etDriverContact.setError("Enter driver contact no.");
        else if (TextUtils.isEmpty(vehicle))
            etVehicle.setError("Enter vehicle name");
        else if (TextUtils.isEmpty(regNo))
            etRegNo.setError("Enter vehicle no.");
        else if (TextUtils.isEmpty(state))
            etState.setError("Enter state");
        else if (TextUtils.isEmpty(city))
            etDriverCity.setError("Enter city");
        else if (TextUtils.isEmpty(address))
            etDriverAddress.setError("Enter address");
        else if (TextUtils.isEmpty(media_option_id))
            Toast.makeText(this, "Please select transit media", Toast.LENGTH_SHORT).show();
        else
            new AddTransitTask().execute();

    }

    public void showBrandingOptionDialog() {
        if (bOptionList != null && !bOptionList.isEmpty()) {
            final DialogMultipleChoiceAdapter adapter = new DialogMultipleChoiceAdapter(AddTransitActivity.this, bOptionList);
            new AlertDialog.Builder(AddTransitActivity.this).setTitle("Select Branding Options")
                    .setAdapter(adapter, null)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            allBOptionIds = TextUtils.join(",", adapter.getSelectedActivitiesIds());
                            String allNames = TextUtils.join("\n", adapter.getSelectedActivitiesNames());
                            Log.e(TAG, "onClick: allBOptionIds >> " + allBOptionIds);
                            Log.e(TAG, "onClick: allNames >> " + allNames);
//                            etBOptions.setText(allNames);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
        } else {
            Toast.makeText(this, "No branding options found", Toast.LENGTH_SHORT).show();
        }
    }

    private class CheckVehicleNoTask extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(String... strings) {
            String vehicle_no = strings[0];
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.CHECKREGISTERATIONNUMBER);
            jsonObject.addProperty("user_id", AppPreference.getUserId(AddTransitActivity.this));
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(AddTransitActivity.this));
            jsonObject.addProperty("vehicle_regis_number", vehicle_no);

            Log.e(TAG, "Request CHECK VEHICLE NUMBER >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_transit_detail_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response CHECK VEHICLE NUMBER >>" + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            publishProgress("200", resp);
                        } else if (jsonObject.getString("status").equalsIgnoreCase("300")) {
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            publishProgress("300", msg);
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
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(msg);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    JSONObject object = jsonArray.getJSONObject(0);
                    etDriverName.setText(object.getString("driver_name"));
                    etDriverContact.setText(object.getString("driver_contact_no"));
                    etVehicle.setText(object.getString("vehicle_model"));
                    etRegNo.setText(object.getString("vehicle_regis_number"));
                    etDriverCity.setText(object.getString("city"));
                    etDriverAddress.setText(object.getString("address"));
                    etState.setText(object.getString("state"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (status.equalsIgnoreCase("300")) {
                //showErrorDialog300(msg);
            } else if (status.equalsIgnoreCase("400")) {
                showErrorDialog400(msg);
            }
        }
    }

    private class AddTransitTask extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(String... strings) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.ADDTRANSITDETAIL);
            jsonObject.addProperty("user_id", AppPreference.getUserId(AddTransitActivity.this));
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(AddTransitActivity.this));
            jsonObject.addProperty("driver_name", driverName);
            jsonObject.addProperty("driver_contact_no", driverContact);
            jsonObject.addProperty("branding_id", AppPreference.getSelectedAlloMediaId(AddTransitActivity.this));
            jsonObject.addProperty("media_option_id", media_option_id);
            jsonObject.addProperty("vehicle_model", vehicle);
            jsonObject.addProperty("vehicle_regis_number", regNo);
            jsonObject.addProperty("state", state);
            jsonObject.addProperty("city", city);
            jsonObject.addProperty("address", address);
            /*jsonObject.addProperty("start_date", startDate);
            jsonObject.addProperty("end_date", endDate);*/

            Log.e(TAG, "Request ADD TRANSIT >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_transit_detail_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response ADD TRANSIT >>" + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONObject jsonObject1 = jsonObject.getJSONArray("result").getJSONObject(0);
                            Log.e(TAG, "onResponse: inserted vehicle_id >> " + jsonObject1.getString("id"));
                            AppPreference.setSelectedVehicleId(AddTransitActivity.this, jsonObject1.getString("id"));
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
                AppPreference.setSelectedVehicleName(AddTransitActivity.this, etVehicle.getText().toString());
                Intent intent = new Intent(AddTransitActivity.this, PerformTransitActivity.class);
                intent.putExtra("media_option_id", media_option_id);
                startActivity(intent);
                finish();
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(AddTransitActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }


}
