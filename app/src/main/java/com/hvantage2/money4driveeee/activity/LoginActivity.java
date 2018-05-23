package com.hvantage2.money4driveeee.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.model.UserModal;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.Functions;
import com.hvantage2.money4driveeee.customview.CustomEditText;
import com.hvantage2.money4driveeee.customview.CustomTextView;
import com.hvantage2.money4driveeee.util.ProgressHUD;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.hvantage2.money4driveeee.util.Functions.errorMessage;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "FeildExeLoginA";
    CustomEditText etEmail, etPassword;
    private ProgressDialog dialog;
    private CustomTextView btnLogin;
    private String fcm_token = "";
    private ProgressHUD progressHD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feild_exe_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fcm_token = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "onCreate: fcm_token >> " + fcm_token);

        ((RelativeLayout) findViewById(R.id.container)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSoftKeyboard(view);
                return false;
            }
        });
        init();
        if (getIntent().hasExtra("logged_out"))
            Snackbar.make(findViewById(android.R.id.content), "Logged out", Snackbar.LENGTH_LONG).setAction("EXIT", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            }).show();
    }


    private void init() {
        etEmail = (CustomEditText) findViewById(R.id.email);
        etPassword = (CustomEditText) findViewById(R.id.password);
        btnLogin = (CustomTextView) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        ((CustomTextView) findViewById(R.id.btnForgotPassword)).setOnClickListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnForgotPassword:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
            case R.id.btnLogin:
                if (TextUtils.isEmpty(etEmail.getText())) {
                    etEmail.setError("Enter email address");
                } else if (!Functions.isEmailValid(etEmail)) {
                    etEmail.setError(errorMessage);
                } else if (TextUtils.isEmpty(etPassword.getText())) {
                    etPassword.setError("Enter password");
                } else {
                    hideSoftKeyboard(view);
                    performLogin();
                }
                break;
            default:
                break;
        }


    }

    private void performLogin() {
        if (Functions.isConnectingToInternet(LoginActivity.this))
            new LoginTask().execute();
        else {
            Snackbar.make(findViewById(android.R.id.content), "No internet connection", Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    performLogin();
                }
            }).show();
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

    class LoginTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.USER_LOGIN);
            jsonObject.addProperty("email", etEmail.getText().toString());
            jsonObject.addProperty("password", etPassword.getText().toString());
            jsonObject.addProperty("FCM_ID", fcm_token);
            Log.e(TAG, "Request LOGIN >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.user_login(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response LOGIN >> " + response.body().toString());
                    JsonObject jsonObject = response.body();
                    if (jsonObject.get("status").getAsString().equals("200")) {
                        JsonArray jsonArray = jsonObject.getAsJsonArray("result");
                        JsonObject result = jsonArray.get(0).getAsJsonObject();
                        UserModal user = new Gson().fromJson(result, UserModal.class);
                        AppPreference.setLoggedIn(LoginActivity.this, true);
                        AppPreference.setInstalled(LoginActivity.this, true);
                        AppPreference.setUserId(LoginActivity.this, String.valueOf(user.getUserId()));
                        AppPreference.setUserName(LoginActivity.this, String.valueOf(user.getName()));
                        AppPreference.setUserData(LoginActivity.this, result.toString());
                        AppPreference.setUserType(LoginActivity.this, String.valueOf(user.getLoginType()));
                        AppPreference.setUserTypeId(LoginActivity.this, String.valueOf(user.getLogin_type_id()));
                        AppPreference.setManagerContactNo(LoginActivity.this, String.valueOf(user.getManager_contact_no()));
                        Log.e(TAG, "User Data >> " + user);
                        Log.e(TAG, "onClick: manager_contact_no >> "+AppPreference.getManagerContactNo(LoginActivity.this));
                        publishProgress("200", "");
                    } else {
                        JsonArray jsonArray = jsonObject.getAsJsonArray("result");
                        JsonObject result = jsonArray.get(0).getAsJsonObject();
                        publishProgress("400", result.get("msg").getAsString());
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
                Intent intent = new Intent(LoginActivity.this, DashBoardActivity.class);
                intent.putExtra("logged_in", "yes");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
            } else if (status.equalsIgnoreCase("400")) {
                Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        performLogin();
                    }
                }).show();
            }
        }
    }


}
