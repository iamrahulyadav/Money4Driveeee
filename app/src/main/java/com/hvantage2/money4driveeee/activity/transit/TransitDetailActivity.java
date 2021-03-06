package com.hvantage2.money4driveeee.activity.transit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.BuildConfig;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.activity.DashBoardActivity;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransitDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "TransitDetailActivity";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final int REQUEST_STORAGE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = REQUEST_STORAGE + 1;
    private static final int REQUEST_LOAD_IMAGE = REQUEST_IMAGE_CAPTURE + 1;
    private EditText etDriverName, etDriverContact, etVehicle,
            etRegNo, etDriverAddress, etDriverBookFor, etStartDate, etEndDate;
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
    private LinearLayout llReport;
    private EditText etState, etCity;
    private Button btnRepaste;
    private String remark = "Repaste";
    private String booked_project_id = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transit_media_detail);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        if (getIntent().hasExtra("media_option_id"))
            media_option_id = getIntent().getStringExtra("media_option_id");
        if (getIntent().hasExtra("vehicle_id"))
            vehicle_id = getIntent().getStringExtra("vehicle_id");
        setEnabled(false);

        Log.e(TAG, "onCreate: media_option_id >> " + media_option_id);
        Log.e(TAG, "onCreate: vehicle_id >> " + vehicle_id);
        new getTransitDetail().execute();
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
        }
    }

    private void setEnabled(boolean b) {
        etDriverName.setEnabled(b);
        etVehicle.setEnabled(b);
        etCity.setEnabled(b);
        etState.setEnabled(b);
        etDriverAddress.setEnabled(b);
        imgDoc1.setEnabled(b);
        imgDoc2.setEnabled(b);
        etDriverAddress.setEnabled(b);
        etDriverAddress.setEnabled(b);
        llReport.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void init() {
        llReport = (LinearLayout) findViewById(R.id.llReport);
        etDriverName = (EditText) findViewById(R.id.etDriverName);
        etDriverContact = (EditText) findViewById(R.id.etDriverContact);
        etVehicle = (EditText) findViewById(R.id.etVehicle);
        etRegNo = (EditText) findViewById(R.id.etRegNo);
        etState = (EditText) findViewById(R.id.etState);
        etCity = (EditText) findViewById(R.id.etCity);
        etDriverAddress = (EditText) findViewById(R.id.etDriverAddress);
        etDriverBookFor = (EditText) findViewById(R.id.etDriverBookFor);
        etStartDate = (EditText) findViewById(R.id.etStartDate);
        etEndDate = (EditText) findViewById(R.id.etEndDate);

        imgDoc1 = (ImageView) findViewById(R.id.imgDoc1);
        imgDoc2 = (ImageView) findViewById(R.id.imgDoc2);
        tvImgDoc1Remark = (TextView) findViewById(R.id.tvImgDoc1Remark);
        tvImgDoc2Remark = (TextView) findViewById(R.id.tvImgDoc2Remark);

        btnRepaste = (Button) findViewById(R.id.btnRepaste);

        ((ScrollView) findViewById(R.id.container)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSoftKeyboard(view);
                return false;
            }
        });

        btnRepaste.setOnClickListener(this);
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
            case R.id.btnRepaste:
                selectImage();
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Upload Image");
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
            outputFileUri = FileProvider.getUriForFile(TransitDetailActivity.this,
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
                    outputFileUri = FileProvider.getUriForFile(TransitDetailActivity.this, BuildConfig.APPLICATION_ID + ".provider", croppedImageFile1);
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
                .setRequestedSize(320, 240)
                .setScaleType(CropImageView.ScaleType.FIT_CENTER)
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
//                dialog1.dismiss();
                if (remarkText.getText().toString().equalsIgnoreCase("")) {
                    remark = "REPASTED";
                } else
                    remark = "REPASTED : " + remarkText.getText().toString();
                new ImageTask().execute(bitmap);
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
            String base64image = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.d(TAG, "base64image >> " + base64image);
            //api call
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.TRANSIT_REPASTE);
            jsonObject.addProperty("user_id", AppPreference.getUserId(TransitDetailActivity.this));
            jsonObject.addProperty("project_id", booked_project_id);
            jsonObject.addProperty("vehicle_id", vehicle_id);
            jsonObject.addProperty("transit_id", media_option_id);
            jsonObject.addProperty("remark", remark);
            jsonObject.addProperty("dimension", "");
            jsonObject.addProperty("image", base64image);

            Log.e(TAG, "Request ADD IMAGE >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_activity_api(jsonObject);

            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response ADD IMAGE >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            publishProgress("200", "Repasted, Check in project activities");
                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            JSONArray resultArray = jsonObject.getJSONArray("result");
                            JSONObject jsonObject1 = resultArray.getJSONObject(0);
                            publishProgress("400", jsonObject1.getString("msg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        publishProgress("400", "Server not responding!");
                    }
                }

                @SuppressLint("LongLogTag")
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e(TAG, "error :- " + Log.getStackTraceString(t));
                    publishProgress("400", "Server not responding!");
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
            Toast.makeText(TransitDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
            if (status.equalsIgnoreCase("200"))
                onBackPressed();
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
            jsonObject.addProperty("user_id", AppPreference.getUserId(TransitDetailActivity.this));
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(TransitDetailActivity.this));
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
                    etVehicle.setText(jsonObject.getString("vehicle_name"));
                    etRegNo.setText(jsonObject.getString("vehicle_no"));
                    etState.setText(jsonObject.getString("state_id"));
                    etCity.setText(jsonObject.getString("city_id"));
                    etVehicle.setText(jsonObject.getString("vehicle_name"));
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
                    booked_project_id = jsonObject.getString("project_assign_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(TransitDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }


}
