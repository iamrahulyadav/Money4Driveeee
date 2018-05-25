package com.hvantage2.money4driveeee.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.BuildConfig;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;


import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.ProgressHUD;
import com.hvantage2.money4driveeee.util.UtilClass;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPhotoActivity extends AppCompatActivity {
    private static final int REQUEST_STORAGE = 0;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_IMAGE_CAPTURE = REQUEST_STORAGE + 1;
    private static final int REQUEST_LOAD_IMAGE = REQUEST_IMAGE_CAPTURE + 1;
    private static final int PIC_CROP = REQUEST_LOAD_IMAGE + 1;

    private static final String TAG = "UploadPhotosActivity";
    String activity_id = "", activity_name = "";
    private String userChoosenTask;
    private ProgressDialog progressDialog;
    private Bitmap bitmapImage1;
    private String new_remark = "", new_dimen = "";
    private Uri originalImageUri;
    private ImageView imageView, imgBack;
    private EditText etheight, etwidth, etremarkText;
    private Button btnSave;
    private ScrollView container;
    private String base64image = "";
    private ImageView editImage;
    private String old_image_url = "";
    private String old_dimen = "";
    private String old_remark = "";
    private String old_height = "", old_width = "";
    private String update_id = "";
    private String action;
    private ProgressHUD progressHD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo);
        init();

        if (getIntent().hasExtra("update_id")) {
            update_id = getIntent().getStringExtra("update_id");
        }
        if (getIntent().hasExtra("image_url")) {
            old_image_url = getIntent().getStringExtra("image_url");
            Picasso.with(EditPhotoActivity.this).load(old_image_url).placeholder(R.drawable.no_image_placeholder).into(imageView);
        }
        if (getIntent().hasExtra("dimen")) {
            old_dimen = getIntent().getStringExtra("dimen");
            if (old_dimen.contains("x")) {
                String parts[] = old_dimen.split("x");
                old_height = parts[0];
                old_width = parts[1];
                etheight.setText(old_height);
                etwidth.setText(old_width);
            }
        }

        if (getIntent().hasExtra("action"))
            action = getIntent().getStringExtra("action");

        if (getIntent().hasExtra("remark")) {
            old_remark = getIntent().getStringExtra("remark");
            etremarkText.setText(old_remark);
        }

        Log.e(TAG, "onCreate: action >> " + action);
        Log.e(TAG, "onCreate: update_id >> " + old_image_url);
        Log.e(TAG, "onCreate: old_image_url >> " + old_image_url);
        Log.e(TAG, "onCreate: old_remark >> " + old_remark);
        Log.e(TAG, "onCreate: old_dimen >> " + old_dimen);

        Log.e(TAG, "onCreate: old_height >> " + old_height);
        Log.e(TAG, "onCreate: old_width >> " + old_width);

        if (ContextCompat.checkSelfPermission(EditPhotoActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(EditPhotoActivity.this, android.Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(EditPhotoActivity.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
            }
        }
    }


    private void init() {
        imageView = (ImageView) findViewById(R.id.img_circle);
        editImage = (ImageView) findViewById(R.id.editImage);
        container = (ScrollView) findViewById(R.id.container);
        imgBack = (ImageView) findViewById(R.id.imgBack);
        btnSave = (Button) findViewById(R.id.btnSave);
        etheight = (EditText) findViewById(R.id.dimensionTextheight);
        etwidth = (EditText) findViewById(R.id.dimensionTextwidth);
        etremarkText = (EditText) findViewById(R.id.remarkText);

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etheight.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(EditPhotoActivity.this, "Enter height", Toast.LENGTH_SHORT).show();
                else if (etwidth.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(EditPhotoActivity.this, "Enter width", Toast.LENGTH_SHORT).show();
                else if (etremarkText.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(EditPhotoActivity.this, "Enter remark", Toast.LENGTH_SHORT).show();
                else {
                    new_dimen = etheight.getText().toString() + "x" + etwidth.getText().toString();
                    new_remark = etremarkText.getText().toString();
                    Log.e(TAG, "new_dimen : " + new_dimen);
                    Log.e(TAG, "new_remark : " + new_remark);
                    new ImageTask().execute(bitmapImage1);
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return false;
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File croppedImageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/m4d/" + "activity_image.jpg");
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage = null;
            if (requestCode == REQUEST_LOAD_IMAGE && data != null) {
                selectedImage = data.getData();
                originalImageUri = data.getData();
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
                    outputFileUri = FileProvider.getUriForFile(EditPhotoActivity.this, BuildConfig.APPLICATION_ID + ".provider", croppedImageFile1);
                    originalFileUri = FileProvider.getUriForFile(EditPhotoActivity.this, BuildConfig.APPLICATION_ID + ".provider", croppedImageFile);
                } else {
                    outputFileUri = Uri.fromFile(croppedImageFile1);
                    originalFileUri = Uri.fromFile(croppedImageFile);
                }
                Log.v(TAG, " Inside REQUEST_IMAGE_CAPTURE uri :- " + outputFileUri);
                originalImageUri = originalFileUri;
                try {
                    performCrop(originalFileUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == PIC_CROP) {
                Log.e("img uri ", data.getData() + "");
                String croppedfilePath = Environment.getExternalStorageDirectory() + "/activity_image.jpg";
                bitmapImage1 = BitmapFactory.decodeFile(croppedfilePath);
                imageView.setImageBitmap(bitmapImage1);
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
            Toast.makeText(EditPhotoActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }


    private void selectImage() {
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditPhotoActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = UtilClass.checkPermission(EditPhotoActivity.this);
                if (items[item].equals("Camera")) {
                    userChoosenTask = "Camera";
                    if (result)
                        dispatchTakePictureIntent();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case UtilClass.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Camera"))
                        dispatchTakePictureIntent();
                    else if (userChoosenTask.equals("Gallery"))
                        galleryIntent();
                } else {
                }
                break;
        }
    }

    private void dispatchTakePictureIntent() {

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
            outputFileUri = FileProvider.getUriForFile(EditPhotoActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider", newfile);
        } else {
            outputFileUri = Uri.fromFile(newfile);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CAMERA);
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
            if (bitmapImage != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                base64image = Base64.encodeToString(byteArray, Base64.DEFAULT);
                Log.d(TAG, "Picture Image :-" + base64image);
            }

            //api call
            JsonObject jsonObject = new JsonObject();
            if (action.equalsIgnoreCase("shop")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.EDITACTIVITYDETAIL);
                jsonObject.addProperty("user_id", AppPreference.getUserId(EditPhotoActivity.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(EditPhotoActivity.this));
                jsonObject.addProperty("shop_id", AppPreference.getSelectedShopid(EditPhotoActivity.this));
                jsonObject.addProperty("update_id", update_id);
                jsonObject.addProperty("remark", new_remark);
                jsonObject.addProperty("dimension", new_dimen);
                jsonObject.addProperty("image", base64image);
            } else if (action.equalsIgnoreCase("transit")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.TRANSITEDITACTIVITYDETAIL);
                jsonObject.addProperty("user_id", AppPreference.getUserId(EditPhotoActivity.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(EditPhotoActivity.this));
                jsonObject.addProperty("vehicle_id", AppPreference.getSelectedVehicleId(EditPhotoActivity.this));
                jsonObject.addProperty("update_id", update_id);
                jsonObject.addProperty("remark", new_remark);
                jsonObject.addProperty("dimension", new_dimen);
                jsonObject.addProperty("image", base64image);
            } else if (action.equalsIgnoreCase("print")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.EDITPRINTDETAIL);
                jsonObject.addProperty("user_id", AppPreference.getUserId(EditPhotoActivity.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(EditPhotoActivity.this));
                jsonObject.addProperty("print_id", AppPreference.getSelectedPMediaId(EditPhotoActivity.this));
                jsonObject.addProperty("update_id", update_id);
                jsonObject.addProperty("remark", new_remark);
                jsonObject.addProperty("dimension", new_dimen);
                jsonObject.addProperty("image", base64image);
            } else if (action.equalsIgnoreCase("hoarding")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.EDITHOARDINGDETAIL);
                jsonObject.addProperty("user_id", AppPreference.getUserId(EditPhotoActivity.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(EditPhotoActivity.this));
                jsonObject.addProperty("hoarding_id", AppPreference.getSelectedHoardingId(EditPhotoActivity.this));
                jsonObject.addProperty("update_id", update_id);
                jsonObject.addProperty("remark", new_remark);
                jsonObject.addProperty("dimension", new_dimen);
                jsonObject.addProperty("image", base64image);
            } else if (action.equalsIgnoreCase("wall")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.EDITWALLDETAIL);
                jsonObject.addProperty("user_id", AppPreference.getUserId(EditPhotoActivity.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(EditPhotoActivity.this));
                jsonObject.addProperty("wall_id", AppPreference.getSelectedWallId(EditPhotoActivity.this));
                jsonObject.addProperty("update_id", update_id);
                jsonObject.addProperty("remark", new_remark);
                jsonObject.addProperty("dimension", new_dimen);
                jsonObject.addProperty("image", base64image);
            } else if (action.equalsIgnoreCase("emedia")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.EDITEMEDIADETAIL);
                jsonObject.addProperty("user_id", AppPreference.getUserId(EditPhotoActivity.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(EditPhotoActivity.this));
                jsonObject.addProperty("emedia_id", AppPreference.getSelectedEMediaId(EditPhotoActivity.this));
                jsonObject.addProperty("update_id", update_id);
                jsonObject.addProperty("remark", new_remark);
                jsonObject.addProperty("dimension", new_dimen);
                jsonObject.addProperty("image", base64image);
            } else
                return null;

            Log.e(TAG, "Request UPDATE  >> **" + action + "** >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_activity_api(jsonObject);

            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response UPDATE >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            publishProgress("200", "Updated");
                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                            String msg = jsonObject1.getString("msg");
                            publishProgress("400", msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        publishProgress("400", getResources().getString(R.string.api_error_msg));
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
                Toast.makeText(EditPhotoActivity.this, msg, Toast.LENGTH_SHORT).show();
                finish();
            } else if (status.equalsIgnoreCase("400")) {
                Toast.makeText(EditPhotoActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
