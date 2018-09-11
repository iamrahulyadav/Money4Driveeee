package com.hvantage2.money4driveeee.activity.electronic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

public class EMediaDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EMediaDetailAc";
    private static final int REQUEST_STORAGE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = REQUEST_STORAGE + 1;
    private static final int REQUEST_LOAD_IMAGE = REQUEST_IMAGE_CAPTURE + 1;
    ArrayList<StateCityModel> listState = new ArrayList<StateCityModel>();
    ArrayList<StateCityModel> listCity = new ArrayList<StateCityModel>();
    private EditText etContName, etContNo, etAddress, etStartDate, etEndDate;
    private Button btnCancel;
    private Button btnConfirm;
    private ProgressHUD progressHD;
    private String media_option_id;
    private String project_id;
    private String emedia_id;
    private ImageView imgDoc1, imgDoc2;
    private TextView tvImgDoc1Remark, tvImgDoc2Remark;
    private Context context;
    private Spinner spinnerState;
    private StateCityAdapter adapterState;
    private String selectedStateId = "0", selectedCityId = "0";
    private Spinner spinnerCity;
    private StateCityAdapter adapterCity;
    private int imgCounter = 1;
    private String userChoosenTask = "";
    private String base64image1 = "", base64image2 = "";
    private String lastselectedStateId = "", lastselectedCityId;
    private EditText etEMediaName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emedia_detail);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        emedia_id = AppPreference.getSelectedEMediaId(EMediaDetailActivity.this);
        project_id = AppPreference.getSelectedProjectId(EMediaDetailActivity.this);
        if (getIntent().hasExtra("media_option_id"))
            media_option_id = getIntent().getStringExtra("media_option_id");
        Log.e(TAG, "onCreate: media_option_id >> " + media_option_id);
        Log.e(TAG, "onCreate: project_id >> " + project_id);
        Log.e(TAG, "onCreate: emedia_id >> " + emedia_id);
        init();
        new getDetailTask().execute();
    }

    private void init() {
        etEMediaName = (EditText) findViewById(R.id.etEMediaName);
        etContName = (EditText) findViewById(R.id.etContName);
        etContNo = (EditText) findViewById(R.id.etContNo);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etStartDate = (EditText) findViewById(R.id.etStartDate);
        etEndDate = (EditText) findViewById(R.id.etEndDate);
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

        imgDoc1 = (ImageView) findViewById(R.id.imgDoc1);
        imgDoc2 = (ImageView) findViewById(R.id.imgDoc2);

        tvImgDoc1Remark = (TextView) findViewById(R.id.tvImgDoc1Remark);
        tvImgDoc2Remark = (TextView) findViewById(R.id.tvImgDoc2Remark);

        imgDoc1.setOnClickListener(this);
        imgDoc2.setOnClickListener(this);

        setStateAdapter();
    }

    private void setStateAdapter() {
        spinnerState = (Spinner) findViewById(R.id.spinnerState);
        adapterState = new StateCityAdapter(EMediaDetailActivity.this, R.layout.state_city_item_layout, R.id.tvTitle, listState);
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
        spinnerCity = (Spinner) findViewById(R.id.spinnerCity);
        adapterCity = new StateCityAdapter(EMediaDetailActivity.this, R.layout.state_city_item_layout, R.id.tvTitle, listCity);
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
            case R.id.btnCancel:
                onBackPressed();
                break;
            case R.id.btnConfirm:
                if (TextUtils.isEmpty(etEMediaName.getText().toString()))
                    etEMediaName.setError("Enter media name");
                else if (TextUtils.isEmpty(etContName.getText().toString()))
                    etContName.setError("Enter contact person name");
                else if (TextUtils.isEmpty(etContNo.getText().toString()))
                    etContNo.setError("Enter contact person no");
                else if (TextUtils.isEmpty(etAddress.getText().toString()))
                    etAddress.setError("Enter address");
                else {
                    new updateDetailTask().execute();
                }
                break;
            case R.id.imgDoc1:
                imgCounter = 1;
                selectImage();
                break;
            case R.id.imgDoc2:
                imgCounter = 2;
                selectImage();
                break;

        }
        if (view.getId() == R.id.btnCancel)
            onBackPressed();
        else if (view.getId() == R.id.btnConfirm) {

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
            outputFileUri = FileProvider.getUriForFile(context,
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
                    outputFileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", croppedImageFile1);
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
                if (TextUtils.isEmpty(remarkText.getText().toString())) {
                    remarkText.setError("Enter a remark");
                } else {
                    dialog1.dismiss();
                    if (imgCounter == 1) {
                        imgDoc1.setImageBitmap(bitmap);
                        tvImgDoc1Remark.setText(remarkText.getText().toString());
                    } else if (imgCounter == 2) {
                        imgDoc2.setImageBitmap(bitmap);
                        tvImgDoc2Remark.setText(remarkText.getText().toString());
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

    private class GetStateTask extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.GETPROJECTSTATES);
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(EMediaDetailActivity.this));
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
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(EMediaDetailActivity.this));
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

    public class getDetailTask extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.PROJECTSEMEDIADETAILS);
            jsonObject.addProperty("user_id", AppPreference.getUserId(EMediaDetailActivity.this)); //8
            jsonObject.addProperty("project_id", project_id);
            jsonObject.addProperty("emedia_id", emedia_id);
            Log.e(TAG, "Request GET EMEDIA DETAILS >> " + jsonObject);

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_emedia_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response GET EMEDIA DETAILS >> " + response.body().toString());
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

                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    etEMediaName.setText(jsonObject.getString("emedia_name"));
                    etContName.setText(jsonObject.getString("shop_contact_per_name"));
                    etContNo.setText(jsonObject.getString("shop_contact_per_number"));
                    etAddress.setText(jsonObject.getString("address"));
                    etStartDate.setText(jsonObject.getString("start_date"));
                    etEndDate.setText(jsonObject.getString("end_date"));
                    tvImgDoc1Remark.setText(jsonObject.getString("img1_remark"));
                    tvImgDoc2Remark.setText(jsonObject.getString("img2_remark"));

                    if (!jsonObject.getString("doc_img1").equalsIgnoreCase(""))
                        Picasso.with(context).load(jsonObject.getString("doc_img1")).placeholder(R.drawable.no_image_placeholder).into(imgDoc1);
                    if (!jsonObject.getString("doc_img2").equalsIgnoreCase(""))
                        Picasso.with(context).load(jsonObject.getString("doc_img2")).placeholder(R.drawable.no_image_placeholder).into(imgDoc2);

                    lastselectedStateId = jsonObject.getString("state");
                    lastselectedCityId = jsonObject.getString("city");

                    Log.e(TAG, "onProgressUpdate: lastselectedStateId >> " + lastselectedStateId);
                    Log.e(TAG, "onProgressUpdate: lastselectedCityId >> " + lastselectedCityId);

                    new GetStateTask().execute();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(EMediaDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
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
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.SAVEEMEDIADETAIL);
            jsonObject.addProperty("user_id", AppPreference.getUserId(EMediaDetailActivity.this)); //8
            jsonObject.addProperty("project_id", project_id);
            jsonObject.addProperty("emedia_id", AppPreference.getSelectedEMediaId(EMediaDetailActivity.this));
            jsonObject.addProperty("shop_contact_per_name", etContName.getText().toString());
            jsonObject.addProperty("shop_contact_per_number", etContNo.getText().toString());
            jsonObject.addProperty("address", etAddress.getText().toString());
            jsonObject.addProperty("state", selectedStateId);
            jsonObject.addProperty("city", selectedCityId);
            jsonObject.addProperty("doc_img1", base64image1);
            jsonObject.addProperty("doc_img2", base64image2);
            jsonObject.addProperty("img1_remark", tvImgDoc1Remark.getText().toString());
            jsonObject.addProperty("img2_remark", tvImgDoc2Remark.getText().toString());
            Log.e(TAG, "Request UPDATE DETAIL >> " + jsonObject);

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_emedia_api(jsonObject);
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
                Intent intent = new Intent(EMediaDetailActivity.this, PerformEMediaActivity.class);
                intent.putExtra("media_option_id", media_option_id);
                startActivity(intent);
                finish();
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(EMediaDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
