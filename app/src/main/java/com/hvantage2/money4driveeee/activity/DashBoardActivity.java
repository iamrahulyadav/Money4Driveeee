package com.hvantage2.money4driveeee.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.database.DBHelper;
import com.hvantage2.money4driveeee.fragment.HomeFragment;
import com.hvantage2.money4driveeee.fragment.MessageFragment;
import com.hvantage2.money4driveeee.fragment.MyProfileFragment;
import com.hvantage2.money4driveeee.fragment.ProjectHistoryFragment;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.FragmentIntraction;
import com.hvantage2.money4driveeee.util.ProgressHUD;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DashBoardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentIntraction {
    private static final String TAG = "DashBoardActivity";
    Toolbar toolbar;
    Toolbar toolbar1;
    private String fcm_token = "";
    private ProgressHUD progressHD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        defaultFragment();
        clearPreferenceData();

        if (ContextCompat.checkSelfPermission(DashBoardActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(DashBoardActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(DashBoardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(DashBoardActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(DashBoardActivity.this, android.Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(DashBoardActivity.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            }
        }

        fcm_token = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "onCreate: fcm_token >> " + fcm_token);
        new UpdateFCMTask().execute();
        if (getIntent().hasExtra("logged_in"))
            Snackbar.make(findViewById(android.R.id.content), "Welcome " + AppPreference.getUserName(DashBoardActivity.this), Snackbar.LENGTH_LONG).show();
    }

    private void clearPreferenceData() {
        AppPreference.setSelectedProjectId(DashBoardActivity.this, "");
        AppPreference.setSelectedProjectType(DashBoardActivity.this, "");

        AppPreference.setSelectedAlloMediaId(DashBoardActivity.this, "");

        AppPreference.setSelectedShopId(DashBoardActivity.this, "");
        AppPreference.setSelectedShopName(DashBoardActivity.this, "");

        AppPreference.setSelectedVehicleId(DashBoardActivity.this, "");
        AppPreference.setSelectedVehicleName(DashBoardActivity.this, "");

        AppPreference.setSelectedPMediaId(DashBoardActivity.this, "");
        AppPreference.setSelectedPMediaName(DashBoardActivity.this, "");

        AppPreference.setSelectedEMediaId(DashBoardActivity.this, "");
        AppPreference.setSelectedEMediaName(DashBoardActivity.this, "");

        AppPreference.setSelectedHoardingId(DashBoardActivity.this, "");
        AppPreference.setSelectedHoardingName(DashBoardActivity.this, "");

        AppPreference.setSelectedWallId(DashBoardActivity.this, "");
        AppPreference.setSelectedWallName(DashBoardActivity.this, "");

    }

    private void defaultFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.main_frame, new HomeFragment());
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            exitApplication();
        } else {
            super.onBackPressed();
        }
    }

    private void exitApplication() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to quit this app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Fragment fragment = new Fragment();

        int id = item.getItemId();
        if (id == R.id.myHome) {
            fragment = new HomeFragment();
            ft.addToBackStack(null);
            ft.replace(R.id.main_frame, fragment);
            ft.addToBackStack(null);
            ft.commitAllowingStateLoss();
            clearBackStack();
        } else if (id == R.id.myProfile) {
            fragment = new MyProfileFragment();
            ft.addToBackStack(null);
            ft.replace(R.id.main_frame, fragment);
            ft.addToBackStack(null);
            ft.commitAllowingStateLoss();
        } else if (id == R.id.projectHistory) {
            fragment = new ProjectHistoryFragment();
            ft.addToBackStack(null);
            ft.replace(R.id.main_frame, fragment);
            ft.addToBackStack(null);
            ft.commitAllowingStateLoss();
        } else if (id == R.id.message) {
            fragment = new MessageFragment();
            ft.addToBackStack(null);
            ft.replace(R.id.main_frame, fragment);
            ft.commitAllowingStateLoss();
        } else if (id == R.id.logout) {
            logoutAlert();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private void logoutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoardActivity.this);
        builder.setMessage("Are you sure you want to logout this app?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new logoutUser().execute();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
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
    public void actionbarsetTitle(String title) {
        toolbar.setTitle(title);
    }

    class logoutUser extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.USERLOGOUT);
            jsonObject.addProperty("user_id", AppPreference.getUserId(DashBoardActivity.this)); //8
            Log.e(TAG, "Request LOGOUT >>" + jsonObject.toString());
            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.user_login(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response LOGOUT >> " + response.body().toString());
                    JsonObject jsonObject = response.body();
                    Log.d("status", String.valueOf(jsonObject));
                    if (jsonObject.get("status").getAsString().equals("200")) {
                        JsonArray jsonArray = jsonObject.getAsJsonArray("result");
                        publishProgress("200", "");
                    } else {
                        String msg = jsonObject.getAsJsonArray("result").get(0).getAsJsonObject().get("msg").getAsString();
                        publishProgress("400", msg);
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
                DBHelper db = new DBHelper(DashBoardActivity.this);
                Log.e(TAG, "Logout: db.deleteDashboard() >> " + db.deleteDashboard());
                Log.e(TAG, "Logout: db.db.deleteProjects(PENDING) >> " + db.deleteProjects(AppConstants.PROJECT_TYPE_IDS.PENDING_ID));
                Log.e(TAG, "Logout: db.deleteDashboard(COMPLETED) >> " + db.deleteProjects(AppConstants.PROJECT_TYPE_IDS.COMPLETED_ID));
                Log.e(TAG, "Logout: db.deleteAllMediaTypes() >> " + db.deleteAllMediaTypes());
                Log.e(TAG, "Logout: db.deleteAllMediaOptions() >> " + db.deleteAllMediaOptions());


                Intent intent = new Intent(DashBoardActivity.this, LoginActivity.class);
                intent.putExtra("logged_out", "yes");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                AppPreference.clearPreference(getApplicationContext());
                startActivity(intent);
                finish();

            } else {
                Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    class UpdateFCMTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(AppConstants.KEYS.METHOD, AppConstants.FEILDEXECUTATIVE.UPDATE_FCM_TOKEN);
            jsonObject.addProperty(AppConstants.KEYS.USER_ID, AppPreference.getUserId(DashBoardActivity.this));
            jsonObject.addProperty(AppConstants.KEYS.FCM_ID, fcm_token);
            Log.e(TAG, "Request UDPATE_FCM >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.register_log_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response UDPATE_FCM >> " + response.body().toString());
                    JsonObject jsonObject = response.body();
                    if (jsonObject.get("status").getAsString().equals("200")) {
                    } else {
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                }
            });
            return null;
        }
    }
}
