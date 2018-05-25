package com.hvantage2.money4driveeee.activity.shop;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.activity.DashBoardActivity;
import com.hvantage2.money4driveeee.model.ShopActivity;
import com.hvantage2.money4driveeee.adapter.DialogMultipleChoiceAdapter;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;


import com.hvantage2.money4driveeee.util.ProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddShopActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AddShopActivity";
    private Button btnCancel, btnConfirm;
    private EditText etShopID, etShopName, etContName, etContNo, etState, etCity, etAddress,  etStartDate, etEndDate;
    private ArrayList<ShopActivity> bTypeList;
    private ProgressDialog dialog;
    private String allBTypeIds = "", allBOptionIds = "";
    String shop_id, shopName, contName, contNo, state, city, address, startDate, endDate;
    private ArrayList<ShopActivity> bOptionList;
    private String start_date="", end_date="";
    private String media_option_id="";
    private ProgressHUD progressHD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bTypeList = new ArrayList<ShopActivity>();
        bOptionList = new ArrayList<ShopActivity>();
        init();
        if(getIntent().hasExtra("media_option_id"))
            media_option_id=getIntent().getStringExtra("media_option_id");
        if(media_option_id==null||media_option_id.equalsIgnoreCase("")) {
            Toast.makeText(this, "Select Media Option", Toast.LENGTH_SHORT).show();
            finish();
        }
        if(getIntent().hasExtra("start_date"))
            start_date= getIntent().getStringExtra("start_date");
        if(getIntent().hasExtra("end_date"))
            end_date= getIntent().getStringExtra("end_date");
        etStartDate.setText(start_date);
        etEndDate.setText(end_date);
        showDialog();
    }

    private void init() {
        etShopID = (EditText) findViewById(R.id.etShopID);
        etShopName = (EditText) findViewById(R.id.etShopName);
        etContName = (EditText) findViewById(R.id.etContName);
        etContNo = (EditText) findViewById(R.id.etContNo);
        etState = (EditText) findViewById(R.id.etState);
        etCity = (EditText) findViewById(R.id.etCity);
        etAddress = (EditText) findViewById(R.id.etAddress);
        /*etBType = (EditText) findViewById(R.id.etBType);
        etBOptions = (EditText) findViewById(R.id.etBOptions);*/
        etStartDate = (EditText) findViewById(R.id.etStartDate);
        etEndDate = (EditText) findViewById(R.id.etEndDate);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        /*etBType.setOnClickListener(this);
        etBOptions.setOnClickListener(this);*/

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


    public class GetBTypeList extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(AppConstants.KEYS.METHOD, AppConstants.FEILDEXECUTATIVE.PROJECTACTIVIYLISTS);
            jsonObject.addProperty(AppConstants.KEYS.USER_ID, AppPreference.getUserId(AddShopActivity.this));
            jsonObject.addProperty(AppConstants.KEYS.PROJECT_ID, AppPreference.getSelectedProjectId(AddShopActivity.this));
            jsonObject.addProperty(AppConstants.KEYS.BRANDING_ID, AppPreference.getSelectedAlloMediaId(AddShopActivity.this));
            Log.e(TAG, "Request GET ACTIVITY LIST >> " + jsonObject);

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_shop_detail_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response GET ACTIVITY LIST >> " + response.body().toString());
                    String str = response.body().toString();
                    bTypeList.clear();
                    try {
                        JSONObject jsonObject1 = new JSONObject(str);
                        if (jsonObject1.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject1.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject11 = jsonArray.getJSONObject(i);
                                ShopActivity model = new ShopActivity();
                                model.setActivity_id(jsonObject11.getString("branding_type_id"));
                                model.setActivity_name(jsonObject11.getString("branding_name"));
                                model.setActivity_status(0);
                                bTypeList.add(model);
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
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("200")) {
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(AddShopActivity.this, msg, Toast.LENGTH_SHORT).show();
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

    private void showDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Shop Contact No.");
        builder.setCancelable(false);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.text_input_contact_no, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        builder.setView(viewInflated);

        builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String contact_no = input.getText().toString();
                Log.e(TAG, "onClick: contact_no >> " + contact_no.length());

                dialog.dismiss();
                etContNo.setText(contact_no);
                new CheckNoTask().execute(contact_no);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /*case R.id.etBType:
                showBrandingTypeDialog();
                break;*/
           /* case R.id.etBOptions:
                showBrandingOptionDialog();
                break;*/
            case R.id.btnConfirm:
                addShop();
                break;
            case R.id.btnCancel:
                onBackPressed();
                break;
        }
    }


    public void showBrandingTypeDialog() {
        if (bTypeList != null && !bTypeList.isEmpty()) {
            final DialogMultipleChoiceAdapter adapter = new DialogMultipleChoiceAdapter(AddShopActivity.this, bTypeList);
            new AlertDialog.Builder(AddShopActivity.this).setTitle("Select Branding Type")
                    .setAdapter(adapter, null)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            allBTypeIds = TextUtils.join(",", adapter.getSelectedActivitiesIds());
                            String allNames = TextUtils.join("\n", adapter.getSelectedActivitiesNames());
                            Log.e(TAG, "onClick: allBTypeIds >> " + allBTypeIds);
                            Log.e(TAG, "onClick: allNames >> " + allNames);
//                            etBType.setText(allNames);

                            bOptionList.clear();
                            bOptionList.add(new ShopActivity("00", "Test Option 1", 0));
                            bOptionList.add(new ShopActivity("00", "Test Option 2", 0));
                            bOptionList.add(new ShopActivity("00", "Test Option 3", 0));
                            bOptionList.add(new ShopActivity("00", "Test Option 4", 0));
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
        } else {
            Toast.makeText(this, "No activities found", Toast.LENGTH_SHORT).show();
        }
    }

    public void showBrandingOptionDialog() {
        if (bOptionList != null && !bOptionList.isEmpty()) {
            final DialogMultipleChoiceAdapter adapter = new DialogMultipleChoiceAdapter(AddShopActivity.this, bOptionList);
            new AlertDialog.Builder(AddShopActivity.this).setTitle("Select Branding Options")
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


    private class CheckNoTask extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();

        }

        @Override
        protected Void doInBackground(String... strings) {
            String contact_no = strings[0];
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.CHECKSHOPNUMBER);
            jsonObject.addProperty("user_id", AppPreference.getUserId(AddShopActivity.this));
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(AddShopActivity.this));
            jsonObject.addProperty("shop_contact_per_number", contact_no);

            Log.e(TAG, "Request CHECK SHOP NUMBER >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_shop_detail_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response CHECK SHOP NUMBER >>" + response.body().toString());
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
//                    etShopID.setText(object.getString("shop_id"));
                    etShopName.setText(object.getString("shop_name"));
                    etShopID.setText(object.getString("shop_number"));
                    etState.setText(object.getString("state"));
                    etCity.setText(object.getString("city"));
                    etAddress.setText(object.getString("address"));
                    etContName.setText(object.getString("shop_contact_per_name"));
                    etContNo.setText(object.getString("shop_contact_per_number"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (status.equalsIgnoreCase("300")) {
                // showErrorDialog300(msg);
            } else if (status.equalsIgnoreCase("400")) {
                showErrorDialog400(msg);
            }
        }
    }

    private void showErrorDialog400(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(AddShopActivity.this);
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


    private void addShop() {
        shop_id = etShopID.getText().toString();
        shopName = etShopName.getText().toString();
        contName = etContName.getText().toString();
        contNo = etContNo.getText().toString();
        state = etState.getText().toString();
        city = etCity.getText().toString();
        address = etAddress.getText().toString();
        startDate = etStartDate.getText().toString();
        endDate = etEndDate.getText().toString();

        if (TextUtils.isEmpty(shop_id))
            etShopID.setError("Enter shop id");
        else if (TextUtils.isEmpty(shopName))
            etShopName.setError("Enter shop name");
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
        /*else if (TextUtils.isEmpty(allBTypeIds))
            etBType.setError("Select Branding Types");*/
        else
            new AddShopTask().execute();

    }

    private class AddShopTask extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(String... strings) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.ADDSHOPDETAIL);
            jsonObject.addProperty("user_id", AppPreference.getUserId(AddShopActivity.this));
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(AddShopActivity.this));
            jsonObject.addProperty("shop_contact_per_number", contNo);
            jsonObject.addProperty("shop_contact_per_name", contName);
            jsonObject.addProperty("branding_id", AppPreference.getSelectedAlloMediaId(AddShopActivity.this));
            jsonObject.addProperty("media_option_id", media_option_id);
            jsonObject.addProperty("shop_name", shopName);
            jsonObject.addProperty("shop_id", shop_id);
            jsonObject.addProperty("state", state);
            jsonObject.addProperty("city", city);
            jsonObject.addProperty("address", address);
            /*jsonObject.addProperty("start_date", startDate);
            jsonObject.addProperty("end_date", endDate);*/

            Log.e(TAG, "Request ADD SHOP >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_shop_detail_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response ADD SHOP >>" + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONObject jsonObject1= jsonObject.getJSONArray("result").getJSONObject(0);
                            Log.e(TAG, "onResponse: inserted shop_id >> "+jsonObject1.getString("id"));
                            AppPreference.setSelectedShopId(AddShopActivity.this, jsonObject1.getString("id"));
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
                AppPreference.setSelectedShopName(AddShopActivity.this, etShopName.getText().toString());
                Intent intent= new Intent(AddShopActivity.this, PerformShopActivity.class);
                intent.putExtra("media_option_id", media_option_id);
                startActivity(intent);
                finish();
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(AddShopActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetBOptionsTasks extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(String... strings) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(AppConstants.KEYS.METHOD, AppConstants.FEILDEXECUTATIVE.PROJECTACTIVIYLISTS);
            jsonObject.addProperty(AppConstants.KEYS.USER_ID, AppPreference.getUserId(AddShopActivity.this));
            jsonObject.addProperty(AppConstants.KEYS.PROJECT_ID, AppPreference.getSelectedProjectId(AddShopActivity.this));
            jsonObject.addProperty(AppConstants.KEYS.BRANDING_ID, AppPreference.getSelectedAlloMediaId(AddShopActivity.this));
            Log.e(TAG, "Request GET ACTIVITY LIST >> " + jsonObject);

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_shop_detail_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response GET ACTIVITY LIST >> " + response.body().toString());
                    String str = response.body().toString();
                    bTypeList.clear();
                    try {
                        JSONObject jsonObject1 = new JSONObject(str);
                        if (jsonObject1.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject1.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject11 = jsonArray.getJSONObject(i);
                                ShopActivity model = new ShopActivity();
                                model.setActivity_id(jsonObject11.getString("branding_type_id"));
                                model.setActivity_name(jsonObject11.getString("branding_name"));
                                model.setActivity_status(0);
                                bTypeList.add(model);
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
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("200")) {
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(AddShopActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
