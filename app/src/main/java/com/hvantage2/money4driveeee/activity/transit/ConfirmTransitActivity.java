package com.hvantage2.money4driveeee.activity.transit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.BuildConfig;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.activity.DashBoardActivity;
import com.hvantage2.money4driveeee.adapter.StateCityAdapter;
import com.hvantage2.money4driveeee.model.StateCityModel;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.Functions;
import com.hvantage2.money4driveeee.util.ProgressHUD;
import com.hvantage2.money4driveeee.util.UtilClass;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmTransitActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ConfirmTransitActivity";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int REQUEST_STORAGE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = REQUEST_STORAGE + 1;
    private static final int REQUEST_LOAD_IMAGE = REQUEST_IMAGE_CAPTURE + 1;
    ArrayList<StateCityModel> listState = new ArrayList<StateCityModel>();
    ArrayList<StateCityModel> listCity = new ArrayList<StateCityModel>();
    private Button btnConfirm;
    private EditText etDriverName, etDriverContact, /*etVehicle, */
            etRegNo, etDriverAddress, etDriverBookFor, etStartDate, etEndDate;
    private Button btnCancel;
    private String media_option_id = "0";
    private ProgressHUD progressHD;
    private ImageView imgDoc1, imgDoc2;
    private TextView tvImgDoc1Remark, tvImgDoc2Remark;
    private Context context;
    private String vehicle_id = "";
    private int imgCounter = 1;
    private String userChoosenTask;
    private Bitmap bitmapImage1;
    private String base64image1 = "", base64image2 = "";
    private String imgRemark1 = "", imgRemark2 = "";
    private LinearLayout llUpdate, llReport;
    private Spinner spinnerState, spinnerCity, spinnerVehicle;
    private StateCityAdapter adapterState, adapterCity;
    private String selectedStateId = "0", selectedCityId = "0";
    private String lastselectedCityId = "";
    private String lastselectedStateId = "";
    private FusedLocationProviderClient mFusedLocationClient;
    private ImageView imgDetectLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_transit_media);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        if (getIntent().hasExtra("media_option_id"))
            media_option_id = getIntent().getStringExtra("media_option_id");
        if (getIntent().getAction().equalsIgnoreCase("view")) {
            if (getIntent().hasExtra("vehicle_id"))
                vehicle_id = getIntent().getStringExtra("vehicle_id");
            setEnabled(false);
        } else if (getIntent().getAction().equalsIgnoreCase("edit")) {
            vehicle_id = AppPreference.getSelectedVehicleId(ConfirmTransitActivity.this);
            llUpdate.setVisibility(View.VISIBLE);
            llReport.setVisibility(View.GONE);
        }

        Log.e(TAG, "onCreate: media_option_id >> " + media_option_id);
        Log.e(TAG, "onCreate: vehicle_id >> " + vehicle_id);
        new getTransitDetail().execute();
        if (checkLocationPermission()) {
            setUpFused();
        }
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(ConfirmTransitActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {

            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case UtilClass.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Camera"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Gallery"))
                        galleryIntent();
                }
                break;
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //Request location updates:
                        setUpFused();
                    }

                } else {
                    checkLocationPermission();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    /*Toast.makeText(context, "Please grant location permission", Toast.LENGTH_SHORT).show();
                    finish();*/
                }
                return;
            }
        }
    }

    private void setUpFused() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        getCurrentLocation();

    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location != null) {
                            Log.e(TAG, "setUpFused: onSuccess: location >> " + location);
                            try {
                                etDriverAddress.setText(Functions.getLocationAddress(context, location));
                                etDriverAddress.setSelection(etDriverAddress.length());
                            } catch (IOException e) {
                                showLocationErrorDialog();
                                e.printStackTrace();
                                Log.e(TAG, "setUpFused: onSuccess: Exc >> " + e.getMessage());
                            }
                        }
                    }
                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                showLocationErrorDialog();
                Log.e(TAG, "setUpFused: onFailure: Exc >> " + e.getMessage());
            }
        });
    }

    private void showLocationErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Location Not Found");
        builder.setMessage("We are unable to get your current location please try again.");
        builder.setCancelable(false);
        builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setUpFused();

            }
        })
                .show();
    }


    private void setEnabled(boolean b) {
        etDriverName.setEnabled(b);
        spinnerVehicle.setEnabled(b);
      /*  spinnerCity.setEnabled(b);
        spinnerState.setEnabled(b);*/
        etDriverAddress.setEnabled(b);
        imgDoc1.setEnabled(b);
        imgDoc2.setEnabled(b);
        etDriverAddress.setEnabled(b);
        etDriverAddress.setEnabled(b);
        llUpdate.setVisibility(View.GONE);
        llReport.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkGps();
    }

    private void checkGps() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            Functions.showSettingsAlert(this);
        else {
            if (checkLocationPermission()) {
                setUpFused();
            }
        }
    }

    private void init() {
        imgDetectLocation = (ImageView) findViewById(R.id.imgDetectLocation);
        llUpdate = (LinearLayout) findViewById(R.id.llUpdate);
        llReport = (LinearLayout) findViewById(R.id.llReport);
        etDriverName = (EditText) findViewById(R.id.etDriverName);
        etDriverContact = (EditText) findViewById(R.id.etDriverContact);
//        etVehicle = (EditText) findViewById(R.id.etVehicle);
        etRegNo = (EditText) findViewById(R.id.etRegNo);
        etDriverAddress = (EditText) findViewById(R.id.etDriverAddress);
        etDriverBookFor = (EditText) findViewById(R.id.etDriverBookFor);
        etStartDate = (EditText) findViewById(R.id.etStartDate);
        etEndDate = (EditText) findViewById(R.id.etEndDate);
        spinnerVehicle = (Spinner) findViewById(R.id.spinnerVehicle);

        imgDoc1 = (ImageView) findViewById(R.id.imgDoc1);
        imgDoc2 = (ImageView) findViewById(R.id.imgDoc2);
        tvImgDoc1Remark = (TextView) findViewById(R.id.tvImgDoc1Remark);
        tvImgDoc2Remark = (TextView) findViewById(R.id.tvImgDoc2Remark);

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

        imgDoc1.setOnClickListener(this);
        imgDoc2.setOnClickListener(this);
        imgDetectLocation.setOnClickListener(this);

        //setStateAdapter();
        spinnerVehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                ((TextView) spinnerVehicle.getSelectedView()).setTextColor(getResources().getColor(R.color.hintcolor));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setStateAdapter() {
//        listState.add(new Gson().fromJson("{\"id\":\"0\",\"name\":\"Select state\"}", StateCityModel.class));
        spinnerState = (Spinner) findViewById(R.id.spinnerState);
        adapterState = new StateCityAdapter(ConfirmTransitActivity.this, R.layout.state_city_item_layout, R.id.tvTitle, listState);
        spinnerState.setAdapter(adapterState);
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedStateId = listState.get(position).getId();
                Log.e(TAG, "onItemSelected: selectedStateId >> " + selectedStateId);
                new GetCityTask().execute();
                ((TextView) spinnerState.getSelectedView().findViewById(R.id.tvTitle)).setTextColor(getResources().getColor(R.color.hintcolor));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void setCityAdapter() {
//        listCity.add(new Gson().fromJson("{\"id\":\"0\",\"name\":\"Select city\"}", StateCityModel.class));
        spinnerCity = (Spinner) findViewById(R.id.spinnerCity);
        adapterCity = new StateCityAdapter(ConfirmTransitActivity.this, R.layout.state_city_item_layout, R.id.tvTitle, listCity);
        spinnerCity.setAdapter(adapterCity);
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedCityId = listCity.get(position).getId();
                Log.e(TAG, "onItemSelected: selectedCityId >> " + selectedCityId);
                ((TextView) spinnerCity.getSelectedView().findViewById(R.id.tvTitle)).setTextColor(getResources().getColor(R.color.hintcolor));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
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
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgDoc1:
                imgCounter = 1;
                selectImage();
                break;
            case R.id.imgDoc2:
                imgCounter = 2;
                selectImage();
                break;
            case R.id.imgDetectLocation:
                if (checkLocationPermission())
                    getCurrentLocation();
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Upload Document");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = UtilClass.checkPermission(context);
                if (items[item].equals("Camera")) {
                    userChoosenTask = "Camera";
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Gallery")) {
                    userChoosenTask = "Gallery";
                    if (result)
                        galleryIntent();
                }
            }
        });
        builder.show();
    }

    @Nullable
    private Intent createPickIntent() {
        Intent picImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (picImageIntent.resolveActivity(getPackageManager()) != null) {
            return picImageIntent;
        } else {
            return null;
        }
    }

    private void cameraIntent() {
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/M4D/";
        File newdir = new File(dir);
        newdir.mkdirs();
        String file = dir + "report_img.jpg";
        Log.e("imagesss cam11", file);
        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Uri outputFileUri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            outputFileUri = FileProvider.getUriForFile(ConfirmTransitActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider", newfile);
        } else {
            outputFileUri = Uri.fromFile(newfile);
        }
        Log.e(TAG, "cameraIntent: outputFileUri >> " + outputFileUri);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    private void galleryIntent() {
        startActivityForResult(createPickIntent(), REQUEST_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_LOAD_IMAGE && data != null) {
                startCropImageActivity(data.getData());
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                File croppedImageFile1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        + "/M4D/" + "report_img.jpg");
                final Uri outputFileUri;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    outputFileUri = FileProvider.getUriForFile(ConfirmTransitActivity.this, BuildConfig.APPLICATION_ID + ".provider", croppedImageFile1);
                } else {
                    outputFileUri = Uri.fromFile(croppedImageFile1);
                }
                Log.e(TAG, " Inside REQUEST_IMAGE_CAPTURE uri :- " + outputFileUri);
                startCropImageActivity(outputFileUri);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showPreviewDialog(bitmap);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(false)
                .setAspectRatio(3, 4)
                .setRequestedSize(320, 240).setScaleType(CropImageView.ScaleType.FIT_CENTER)
                .setAutoZoomEnabled(false)
                .start(this);
    }

    private void showPreviewDialog(final Bitmap bitmap) {
        final Dialog dialog1 = new Dialog(context, R.style.image_preview_dialog);
        dialog1.setContentView(R.layout.image_doc_setup_layout);
        Window window = dialog1.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog1.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog1.setCancelable(true);
        dialog1.setCanceledOnTouchOutside(false);

        ImageView imageView = (ImageView) dialog1.findViewById(R.id.img_circle);
        ScrollView container = (ScrollView) dialog1.findViewById(R.id.container);

        ImageView imgBack = (ImageView) dialog1.findViewById(R.id.imgBack);
        Button btnSave = (Button) dialog1.findViewById(R.id.btnSave);
        final EditText remarkText = (EditText) dialog1.findViewById(R.id.remarkText);

        imageView.setImageBitmap(bitmap);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(remarkText.getText().toString())) {
                    remarkText.setError("Enter a remark");
                } else {
                    dialog1.dismiss();
                    if (imgCounter == 1) {
                        imgDoc1.setImageBitmap(bitmap);
                        tvImgDoc1Remark.setText(remarkText.getText().toString());
                        imgRemark1 = remarkText.getText().toString();
                    } else if (imgCounter == 2) {
                        imgDoc2.setImageBitmap(bitmap);
                        tvImgDoc2Remark.setText(remarkText.getText().toString());
                        imgRemark2 = remarkText.getText().toString();
                    }
                    new ImageTask().execute(bitmap);
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });
        dialog1.show();

        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return false;
            }
        });
    }

    private class GetStateTask extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.GETPROJECTSTATES);
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(ConfirmTransitActivity.this));
            Log.e(TAG, "GetStateTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.new_project_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "GetStateTask: Response >>" + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
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
            String status = values[0];
            String msg = values[1];
            JSONObject jsonObject = null;
            if (status.equalsIgnoreCase("200")) {
                try {
                    jsonObject = new JSONObject(msg);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        StateCityModel data = new Gson().fromJson(String.valueOf(object), StateCityModel.class);
                        listState.add(data);
                    }
                    setStateAdapter();
                    if (!lastselectedStateId.equalsIgnoreCase(""))
                        for (int i = 0; i < listState.size(); i++) {
                            if (lastselectedStateId.equalsIgnoreCase(listState.get(i).getId())) {
                                spinnerState.setSelection(i);
                                lastselectedStateId = "";
                            }
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetCityTask extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... strings) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.GETPROJECTCITIES);
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(ConfirmTransitActivity.this));
            jsonObject.addProperty("state_id", selectedStateId);
            Log.e(TAG, "GetCityTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.new_project_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "GetCityTask: Response >>" + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        listCity.clear();
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                StateCityModel data = new Gson().fromJson(String.valueOf(object), StateCityModel.class);
                                listCity.add(data);
                            }
                            publishProgress("200", "");
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
            setCityAdapter();
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("200")) {
                if (!lastselectedCityId.equalsIgnoreCase(""))
                    for (int i = 0; i < listCity.size(); i++) {
                        if (lastselectedCityId.equalsIgnoreCase(listCity.get(i).getId())) {
                            spinnerCity.setSelection(i);
                            lastselectedCityId = "";
                        }
                    }
            } else if (status.equalsIgnoreCase("400")) {
            }
        }
    }

    class ImageTask extends AsyncTask<Bitmap, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            Bitmap bitmapImage = bitmaps[0];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            if (imgCounter == 1)
                base64image1 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            else if (imgCounter == 2)
                base64image2 = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.e(TAG, "ImageTask: doInBackground: base64image1 >>" + base64image1);
            Log.e(TAG, "ImageTask: doInBackground: base64image2 >>" + base64image2);
            publishProgress("");
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            hideProgressDialog();
        }
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
            jsonObject.addProperty("user_id", AppPreference.getUserId(ConfirmTransitActivity.this));
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(ConfirmTransitActivity.this));
            jsonObject.addProperty("transit_id", media_option_id);
            jsonObject.addProperty("vehicle_id", vehicle_id);
            Log.e(TAG, "getTransitDetail: Request >> " + jsonObject);

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_transit_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "getTransitDetail: Response >> " + response.body().toString());
                    String str = response.body().toString();
                    try {
                        JSONObject jsonObject1 = new JSONObject(str);
                        if (jsonObject1.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject1.getJSONArray("result");
                            JSONObject jsonObject11 = jsonArray.getJSONObject(0);

                            publishProgress("200", String.valueOf(jsonObject11));
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
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    etDriverName.setText(jsonObject.getString("driver_name"));
                    etDriverContact.setText(jsonObject.getString("driver_contact_no"));
//                    etVehicle.setText(jsonObject.getString("vehicle_name"));
                    etRegNo.setText(jsonObject.getString("vehicle_no"));

                    etDriverAddress.setText(jsonObject.getString("address"));
                    etDriverBookFor.setText(jsonObject.getString("project_assign"));
                    etStartDate.setText(jsonObject.getString("start_date"));
                    etEndDate.setText(jsonObject.getString("end_date"));
                    tvImgDoc1Remark.setText(jsonObject.getString("img1_remark"));
                    tvImgDoc2Remark.setText(jsonObject.getString("img2_remark"));
                    if (!jsonObject.getString("doc_img1").equalsIgnoreCase(""))
                        Picasso.with(context).load(jsonObject.getString("doc_img1")).placeholder(R.drawable.no_image_placeholder).into(imgDoc1);
                    if (!jsonObject.getString("doc_img2").equalsIgnoreCase(""))
                        Picasso.with(context).load(jsonObject.getString("doc_img2")).placeholder(R.drawable.no_image_placeholder).into(imgDoc2);

                    lastselectedStateId = jsonObject.getString("state_id");
                    lastselectedCityId = jsonObject.getString("city_id");
                    Log.e(TAG, "onProgressUpdate: selectedStateId >> " + lastselectedStateId);
                    Log.e(TAG, "onProgressUpdate: lastselectedCityId >> " + lastselectedCityId);


                    String[] auto_list = getResources().getStringArray(R.array.auto_list);
                    for (int i = 0; i < auto_list.length; i++) {
                        if (jsonObject.getString("vehicle_name").equalsIgnoreCase(auto_list[i])) {
                            spinnerVehicle.setSelection(i);
                            break;
                        }
                    }

                    /* for (int i = 0; i < listState.size(); i++) {
                        if (listState.get(i).getId().equalsIgnoreCase(selectedStateId))
                            spinnerState.setSelection(i);
                    }*/
                    new GetStateTask().execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(ConfirmTransitActivity.this, msg, Toast.LENGTH_SHORT).show();
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
            jsonObject.addProperty(AppConstants.KEYS.USER_ID, AppPreference.getUserId(ConfirmTransitActivity.this)); //8
            jsonObject.addProperty(AppConstants.KEYS.PROJECT_ID, AppPreference.getSelectedProjectId(ConfirmTransitActivity.this));
            jsonObject.addProperty(AppConstants.KEYS.TRANSIT_ID, media_option_id);
            jsonObject.addProperty(AppConstants.KEYS.VEHICLE_ID, vehicle_id);
            jsonObject.addProperty(AppConstants.KEYS.DRIVER_NAME, etDriverName.getText().toString());
            jsonObject.addProperty(AppConstants.KEYS.DRIVER_CONTACT_NO, etDriverContact.getText().toString());
            jsonObject.addProperty(AppConstants.KEYS.VEHICLE_MODEL, (String) spinnerVehicle.getSelectedItem());
            jsonObject.addProperty(AppConstants.KEYS.VEHICLE_REGIS_NUMBER, etRegNo.getText().toString());
            jsonObject.addProperty(AppConstants.KEYS.STATE, selectedStateId);
            jsonObject.addProperty(AppConstants.KEYS.CITY, selectedCityId);
            jsonObject.addProperty(AppConstants.KEYS.ADDRESS, etDriverAddress.getText().toString());
            jsonObject.addProperty("img1_remark", tvImgDoc1Remark.getText().toString());
            jsonObject.addProperty("img2_remark", tvImgDoc2Remark.getText().toString());
            jsonObject.addProperty("doc_img1", base64image1);
            jsonObject.addProperty("doc_img2", base64image2);
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
                Intent intent = new Intent(ConfirmTransitActivity.this, PerformTransitActivity.class);
                intent.putExtra("media_option_id", media_option_id);
                startActivity(intent);
                finish();
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(ConfirmTransitActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
