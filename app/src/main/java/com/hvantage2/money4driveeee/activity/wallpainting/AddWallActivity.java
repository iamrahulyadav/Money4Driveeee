package com.hvantage2.money4driveeee.activity.wallpainting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.BuildConfig;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.activity.DashBoardActivity;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.Functions;
import com.hvantage2.money4driveeee.util.ProgressHUD;
import com.hvantage2.money4driveeee.util.UtilClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddWallActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_STORAGE = 0;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_IMAGE_CAPTURE = REQUEST_STORAGE + 1;
    private static final int REQUEST_LOAD_IMAGE = REQUEST_IMAGE_CAPTURE + 1;
    private static final int PIC_CROP = REQUEST_LOAD_IMAGE + 1;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String TAG = "AddWallActivity";
    String wallName, contName, contNo, state, city, address, startDate, endDate;
    private Button btnCancel, btnConfirm;
    private EditText etWallName, etContName, etContNo, etAddress, etStartDate, etEndDate;
    private String start_date = "", end_date = "";
    private String media_option_id = "";
    private ProgressHUD progressHD;
    private Context context;
    private FusedLocationProviderClient mFusedLocationClient;
    ArrayList<String> listState = new ArrayList<String>();
    ArrayList<String> listCity = new ArrayList<String>();
    private AppCompatAutoCompleteTextView atvStates;
    private AppCompatAutoCompleteTextView atvCities;
    private ImageView imgDoc1, imgDoc2;
    private String userChoosenTask;
    private int imgCounter = 1;
    private Bitmap bitmapImage1;
    private String base64image1 = "", base64image2 = "";
    private TextView tvImgDoc1Remark, tvImgDoc2Remark;
    private TextView tvRequestOtp;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wall);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (checkLocationPermission()) {
            setUpFused();
        }
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
        }).setNegativeButton("Set Manually", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        })
                .show();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(AddWallActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
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
                                getLocationAddress(location);
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

    private void init() {
        etWallName = (EditText) findViewById(R.id.etWallName);
        etContName = (EditText) findViewById(R.id.etContName);
        etContNo = (EditText) findViewById(R.id.etContNo);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etStartDate = (EditText) findViewById(R.id.etStartDate);
        etEndDate = (EditText) findViewById(R.id.etEndDate);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        imgDoc1 = (ImageView) findViewById(R.id.imgDoc1);
        imgDoc2 = (ImageView) findViewById(R.id.imgDoc2);
        tvImgDoc1Remark = (TextView) findViewById(R.id.tvImgDoc1Remark);
        tvImgDoc2Remark = (TextView) findViewById(R.id.tvImgDoc2Remark);

        tvRequestOtp = (TextView) findViewById(R.id.tvRequestOtp);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);

        imgDoc1.setOnClickListener(this);
        imgDoc2.setOnClickListener(this);

        tvRequestOtp.setOnClickListener(this);

        ((ScrollView) findViewById(R.id.container)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSoftKeyboard(view);
                return false;
            }
        });

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnConfirm:
                addWall();
                break;
            case R.id.tvRequestOtp:
                if (etContNo.getText().toString().length() == 10) {
                    etContNo.clearFocus();
                    hideSoftKeyboard(view);
                    progressBar.setVisibility(View.VISIBLE);
                    tvRequestOtp.setText("Sending");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showOtpDialog();
                            progressBar.setVisibility(View.GONE);
                            tvRequestOtp.setText("");
                        }
                    }, 5000);
                } else
                    etContNo.setError("Enter valid contact no.");
                break;
            case R.id.imgDoc1:
                imgCounter = 1;
                selectImage();
                break;
            case R.id.imgDoc2:
                imgCounter = 2;
                selectImage();
                break;
            case R.id.btnCancel:
                onBackPressed();
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};
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
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/m4d/";
        File newdir = new File(dir);
        newdir.mkdirs();

        String file = dir + "activity_image.jpg";
        Log.d("imagesss cam11", file);
        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //final Uri outputFileUri = Uri.fromFile(newfile);
        final Uri outputFileUri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            outputFileUri = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider", newfile);
        } else {
            outputFileUri = Uri.fromFile(newfile);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CAMERA);
    }


    private void galleryIntent() {
        startActivityForResult(createPickIntent(), REQUEST_LOAD_IMAGE);
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File croppedImageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/m4d/" + "activity_image.jpg");
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage = null;
            if (requestCode == REQUEST_LOAD_IMAGE && data != null) {
                selectedImage = data.getData();
                try {
                    performCrop(selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                File croppedImageFile1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        + "/m4d/" + "activity_image1.jpg");
                final Uri originalFileUri, outputFileUri;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    outputFileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", croppedImageFile1);
                    originalFileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", croppedImageFile);
                } else {
                    outputFileUri = Uri.fromFile(croppedImageFile1);
                    originalFileUri = Uri.fromFile(croppedImageFile);
                }
                Log.v(TAG, " Inside REQUEST_IMAGE_CAPTURE uri :- " + outputFileUri);
                try {
                    performCrop(originalFileUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == PIC_CROP) {
                Log.e("img uri ", data.getData() + "");
                showPreviewDialog();
            }
        }
    }

    private void performCrop(Uri picUri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), picUri);
        Log.e(TAG, "performCrop: bitmap height >> " + bitmap.getHeight());
        Log.e(TAG, "performCrop: bitmap width >> " + bitmap.getWidth());

        String path1 = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "activity_image", "activity_image.jpg");
        File f = new File(Environment.getExternalStorageDirectory(), "/activity_image.jpg");
        try {
            f.createNewFile();
        } catch (IOException ex) {
            Log.e("io", ex.getMessage());
        }

        Uri uri = Uri.fromFile(f);

        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(Uri.parse(path1), "image/*");
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 4);
            cropIntent.putExtra("aspectY", 3);
            cropIntent.putExtra("outputX", 800);
            cropIntent.putExtra("outputY", 600);
            cropIntent.putExtra("return-data", true);
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(cropIntent, PIC_CROP);
        } catch (ActivityNotFoundException anfe) {
            anfe.printStackTrace();
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void showPreviewDialog() {
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

        String croppedfilePath = Environment.getExternalStorageDirectory() + "/activity_image.jpg";
        bitmapImage1 = BitmapFactory.decodeFile(croppedfilePath);
        imageView.setImageBitmap(bitmapImage1);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(remarkText.getText().toString())) {
                    remarkText.setError("Enter a remark");
                } else {
                    dialog1.dismiss();
                    if (imgCounter == 1) {
                        imgDoc1.setImageBitmap(bitmapImage1);
                        tvImgDoc1Remark.setText(remarkText.getText().toString());
                    } else if (imgCounter == 2) {
                        imgDoc2.setImageBitmap(bitmapImage1);
                        tvImgDoc2Remark.setText(remarkText.getText().toString());
                    }
                    new ImageTask().execute(bitmapImage1);
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
        state = atvStates.getText().toString();
        city = atvCities.getText().toString();
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
            atvStates.setError("Enter state");
        else if (TextUtils.isEmpty(city))
            atvCities.setError("Enter city");
        else if (TextUtils.isEmpty(address))
            etAddress.setError("Enter address");
        else
            new AddWallActivity.AddWallTask().execute();

    }

    private void getLocationAddress(Location result) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        addresses = geocoder.getFromLocation(result.getLatitude(), result.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        etAddress.setText(address);
        atvStates.setText(state);
        atvCities.setText(city);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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

    private void showOtpDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enter OTP Here");
        dialog.setMessage("Please enter the OTP sent to your number (+91" + etContNo.getText().toString() + ")");
        dialog.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.input_otp_layout, null);
        dialog.setView(dialogView);

        final EditText etNo1 = (EditText) dialogView.findViewById(R.id.etNo1);
        final EditText etNo2 = (EditText) dialogView.findViewById(R.id.etNo2);
        final EditText etNo3 = (EditText) dialogView.findViewById(R.id.etNo3);
        final EditText etNo4 = (EditText) dialogView.findViewById(R.id.etNo4);

        dialog.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String otp = etNo1.getText().toString() + etNo2.getText().toString() + etNo3.getText().toString() + etNo4.getText().toString();
                Log.e(TAG, "showSearchDialog: onClick: Search: otp >> " + otp);
                tvRequestOtp.setText("");
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvRequestOtp.setText("Send OTP");
            }
        });
        dialog.setNeutralButton("Don't Have OTP?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvRequestOtp.setText("Resend OTP");
            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        etNo1.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (etNo1.length() == 1) {
                    etNo1.clearFocus();
                    etNo2.requestFocus();
                    etNo2.setCursorVisible(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        etNo2.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (etNo2.length() == 1) {
                    etNo2.clearFocus();
                    etNo3.requestFocus();
                    etNo3.setCursorVisible(true);

                } else if (etNo2.length() == 0) {
                    etNo2.clearFocus();
                    etNo1.requestFocus();
                    etNo1.setCursorVisible(true);
                    etNo1.setSelection(etNo1.length());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        etNo3.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (etNo3.length() == 1) {
                    etNo3.clearFocus();
                    etNo4.requestFocus();
                    etNo4.setCursorVisible(true);

                } else if (etNo3.length() == 0) {
                    etNo3.clearFocus();
                    etNo2.requestFocus();
                    etNo2.setCursorVisible(true);
                    etNo2.setSelection(etNo2.length());

                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        etNo4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etNo4.length() == 0) {
                    etNo4.clearFocus();
                    etNo3.requestFocus();
                    etNo3.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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


}

