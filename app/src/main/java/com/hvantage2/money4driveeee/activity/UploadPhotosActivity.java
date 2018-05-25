package com.hvantage2.money4driveeee.activity;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.BuildConfig;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.adapter.UploadedImageAdapter;


import com.hvantage2.money4driveeee.model.ImageUploadModel;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.Functions;
import com.hvantage2.money4driveeee.util.ProgressHUD;
import com.hvantage2.money4driveeee.util.UtilClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadPhotosActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_STORAGE = 0;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_IMAGE_CAPTURE = REQUEST_STORAGE + 1;
    private static final int REQUEST_LOAD_IMAGE = REQUEST_IMAGE_CAPTURE + 1;
    private static final int PIC_CROP = REQUEST_LOAD_IMAGE + 1;

    private static final String TAG = "UploadPhotosActivity";
    ArrayList<ImageUploadModel> imageList;
    JsonArray jsonArrayImages = new JsonArray();
    String action = "";
    private String userChoosenTask;
    private RecyclerView recycler_view;
    private UploadedImageAdapter adapter;
    private Dialog dialog;
    private Bitmap bitmapImage1;
    private JsonObject jsonObjectImages;
    private String tempDimen = "", tempRemark = "";
    private Uri originalImageUri;
    private Button takepicture;
    private ProgressHUD progressHD;
    private String media_option_id;
    private String hoarding_name;
    private String media_option_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageList = new ArrayList<ImageUploadModel>();

        init();
        if (getIntent().getAction().equalsIgnoreCase("shop")) {
            action = "shop";
            if (getIntent().hasExtra("media_option_id") && getIntent().hasExtra("media_option_name")) {
                media_option_id = getIntent().getStringExtra("media_option_id");
                media_option_name = getIntent().getStringExtra("media_option_name");
            }
        } else if (getIntent().getAction().equalsIgnoreCase("transit")) {
            action = "transit";
            if (getIntent().hasExtra("media_option_id") && getIntent().hasExtra("media_option_name")) {
                media_option_id = getIntent().getStringExtra("media_option_id");
                media_option_name = getIntent().getStringExtra("media_option_name");
            }
        } else if (getIntent().getAction().equalsIgnoreCase("print")) {
            action = "print";
            if (getIntent().hasExtra("media_option_id") && getIntent().hasExtra("media_option_name")) {
                media_option_id = getIntent().getStringExtra("media_option_id");
                media_option_name = getIntent().getStringExtra("media_option_name");
            }
        } else if (getIntent().getAction().equalsIgnoreCase("hoarding")) {
            action = "hoarding";
            if (getIntent().hasExtra("media_option_id") && getIntent().hasExtra("media_option_name")) {
                media_option_id = getIntent().getStringExtra("media_option_id");
                media_option_name = getIntent().getStringExtra("media_option_name");
            }
        } else if (getIntent().getAction().equalsIgnoreCase("wall")) {
            action = "wall";
            if (getIntent().hasExtra("media_option_id") && getIntent().hasExtra("media_option_name")) {
                media_option_id = getIntent().getStringExtra("media_option_id");
                media_option_name = getIntent().getStringExtra("media_option_name");
            }
        } else if (getIntent().getAction().equalsIgnoreCase("emedia")) {
            action = "emedia";
            if (getIntent().hasExtra("media_option_id") && getIntent().hasExtra("media_option_name")) {
                media_option_id = getIntent().getStringExtra("media_option_id");
                media_option_name = getIntent().getStringExtra("media_option_name");
            }
        }

        if (!media_option_name.equalsIgnoreCase(""))
            getSupportActionBar().setTitle(media_option_name);

        Log.e(TAG, "onCreate: action >> " + action);
        if (ContextCompat.checkSelfPermission(UploadPhotosActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(UploadPhotosActivity.this, android.Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(UploadPhotosActivity.this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
            }
        }

        setImageAdapter();

    }

    private void setImageAdapter() {
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(UploadPhotosActivity.this);
        recycler_view.setLayoutManager(manager);
        adapter = new UploadedImageAdapter(UploadPhotosActivity.this, imageList, action);
        recycler_view.setAdapter(adapter);
    }

    private void init() {
        ((Button) findViewById(R.id.btnSubmit)).setOnClickListener(this);
        takepicture = ((Button) findViewById(R.id.takepicture));
        takepicture.setOnClickListener(this);
        if (AppPreference.getSelectedProjectType(UploadPhotosActivity.this).equalsIgnoreCase(AppConstants.PROJECT_TYPE.COMPLETED)) {
            ((Button) findViewById(R.id.btnSubmit)).setVisibility(View.GONE);
            takepicture.setVisibility(View.GONE);
        }
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
                finish();
                break;
            case R.id.action_home:
                Intent intent = new Intent(UploadPhotosActivity.this, DashBoardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSubmit) {
            if (jsonArrayImages.size() == 0) {
                Toast.makeText(this, "Please add atleast one picture", Toast.LENGTH_SHORT).show();
            } else
                new CompleteSingleTask().execute();
        } else if (view.getId() == R.id.takepicture) {
            if (adapter.getItemCount() == 5)
                Toast.makeText(this, "You cannot upload more than 5 images", Toast.LENGTH_SHORT).show();
            else
                selectImage();
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
                    outputFileUri = FileProvider.getUriForFile(UploadPhotosActivity.this, BuildConfig.APPLICATION_ID + ".provider", croppedImageFile1);
                    originalFileUri = FileProvider.getUriForFile(UploadPhotosActivity.this, BuildConfig.APPLICATION_ID + ".provider", croppedImageFile);
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
                showPreviewDialog();

                //Bitmap bitmapImage = data.getExtras().getParcelable("data");
              /*  if (data.getData() != null) {
                    try {
                        bitmapImage = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                        FirebaseCrash.report(e);
                    }
                } else {
                    bitmapImage = (Bitmap) data.getExtras().get("data");
                }*/
                // MediaStore.Images.Media.insertImage(getContentResolver(), bitmapImage, "report_img", "report_img_cropped.jpg");
            }
        }
    }


    private void showPreviewDialog() {
        dialog = new Dialog(UploadPhotosActivity.this, R.style.image_preview_dialog);
        dialog.setContentView(R.layout.image_setup_layout);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        ImageView imageView = (ImageView) dialog.findViewById(R.id.img_circle);
        ScrollView container = (ScrollView) dialog.findViewById(R.id.container);

        ImageView imgBack = (ImageView) dialog.findViewById(R.id.imgBack);
        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
        final EditText height = (EditText) dialog.findViewById(R.id.dimensionTextheight);
        final EditText width = (EditText) dialog.findViewById(R.id.dimensionTextwidth);
        final EditText remarkText = (EditText) dialog.findViewById(R.id.remarkText);

        String croppedfilePath = Environment.getExternalStorageDirectory() + "/activity_image.jpg";
        bitmapImage1 = BitmapFactory.decodeFile(croppedfilePath);
        imageView.setImageBitmap(bitmapImage1);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (height.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(UploadPhotosActivity.this, "Enter height", Toast.LENGTH_SHORT).show();
                else if (width.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(UploadPhotosActivity.this, "Enter width", Toast.LENGTH_SHORT).show();
                else if (remarkText.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(UploadPhotosActivity.this, "Enter remark", Toast.LENGTH_SHORT).show();
                else {
                    tempDimen = height.getText().toString() + "x" + width.getText().toString();
                    Log.e(TAG, "tempDimen: " + tempDimen);
                    tempRemark = remarkText.getText().toString();
                    ImageUploadModel model = new ImageUploadModel(bitmapImage1, tempDimen, tempRemark);
                    imageList.add(model);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                    new ImageTask().execute(bitmapImage1);
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

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
            Toast.makeText(UploadPhotosActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadPhotosActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = UtilClass.checkPermission(UploadPhotosActivity.this);
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
            outputFileUri = FileProvider.getUriForFile(UploadPhotosActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider", newfile);
        } else {
            outputFileUri = Uri.fromFile(newfile);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    class ImageTask extends AsyncTask<Bitmap, Void, Void> {
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
            String Encoded_userimage = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.d(TAG, "Picture Image :-" + Encoded_userimage);
            jsonObjectImages = new JsonObject();
            try {
                jsonObjectImages.addProperty("image", Encoded_userimage);
                jsonObjectImages.addProperty("remark", tempRemark);
                jsonObjectImages.addProperty("dimension", tempDimen);
                jsonArrayImages.add(jsonObjectImages);
                Log.e(TAG, "Json Image Array size :- " + imageList.size());
                Log.w(TAG, "Images Json object :- " + jsonObjectImages);
                Log.w(TAG, "Images Json Array :- " + jsonArrayImages);
                publishProgress();
            } catch (Exception e) {
                publishProgress();
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                publishProgress();
                e.printStackTrace();
            }
            return null;
        }

        private void publishProgress() {
            hideProgressDialog();
        }
    }

    class CompleteSingleTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = null;
            if (action.equalsIgnoreCase("shop")) {
                jsonObject = new JsonObject();
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.SINGLE_TASK_COMP);
                jsonObject.addProperty("user_id", AppPreference.getUserId(UploadPhotosActivity.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(UploadPhotosActivity.this));
                jsonObject.addProperty("shop_id", AppPreference.getSelectedShopid(UploadPhotosActivity.this));
                jsonObject.addProperty("activity_id", media_option_id);
                jsonObject.addProperty("date_time", Functions.getCurrentDate() + " " + Functions.getCurrentTime());
                jsonObject.add("ActivityDetails", jsonArrayImages);
            } else if (action.equalsIgnoreCase("transit")) {
                jsonObject = new JsonObject();
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.TRANSIT_SINGLE_ACTIVITY_TASKCOMPLETE);
                jsonObject.addProperty("user_id", AppPreference.getUserId(UploadPhotosActivity.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(UploadPhotosActivity.this));
                jsonObject.addProperty("vehicle_id", AppPreference.getSelectedVehicleId(UploadPhotosActivity.this));
                jsonObject.addProperty("transit_id", media_option_id);
                jsonObject.addProperty("date_time", Functions.getCurrentDate() + " " + Functions.getCurrentTime());
                jsonObject.add("ActivityDetails", jsonArrayImages);
            } else if (action.equalsIgnoreCase("print")) {
                jsonObject = new JsonObject();
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.PRINT_SINGLE_ACTIVITY_TASKCOMPLETE);
                jsonObject.addProperty("user_id", AppPreference.getUserId(UploadPhotosActivity.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(UploadPhotosActivity.this));
                jsonObject.addProperty("print_id", AppPreference.getSelectedPMediaId(UploadPhotosActivity.this));
                jsonObject.addProperty("media_option_id", media_option_id);
                jsonObject.addProperty("date_time", Functions.getCurrentDate() + " " + Functions.getCurrentTime());
                jsonObject.add("ActivityDetails", jsonArrayImages);
            } else if (action.equalsIgnoreCase("hoarding")) {
                jsonObject = new JsonObject();
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.SINGLEHOARDINGTASKCOMPLETE);
                jsonObject.addProperty("user_id", AppPreference.getUserId(UploadPhotosActivity.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(UploadPhotosActivity.this));
                jsonObject.addProperty("hoarding_id", AppPreference.getSelectedHoardingId(UploadPhotosActivity.this));
                jsonObject.addProperty("media_option_id", media_option_id);
                jsonObject.addProperty("date_time", Functions.getCurrentDate() + " " + Functions.getCurrentTime());
                jsonObject.add("ActivityDetails", jsonArrayImages);
            } else if (action.equalsIgnoreCase("wall")) {
                jsonObject = new JsonObject();
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.SINGLEWALLTASKCOMPLETE);
                jsonObject.addProperty("user_id", AppPreference.getUserId(UploadPhotosActivity.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(UploadPhotosActivity.this));
                jsonObject.addProperty("wall_id", AppPreference.getSelectedWallId(UploadPhotosActivity.this));
                jsonObject.addProperty("media_option_id", media_option_id);
                jsonObject.addProperty("date_time", Functions.getCurrentDate() + " " + Functions.getCurrentTime());
                jsonObject.add("ActivityDetails", jsonArrayImages);
            } else if (action.equalsIgnoreCase("emedia")) {
                jsonObject = new JsonObject();
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.SINGLEEMEDIATASKCOMPLETE);
                jsonObject.addProperty("user_id", AppPreference.getUserId(UploadPhotosActivity.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(UploadPhotosActivity.this));
                jsonObject.addProperty("emedia_id", AppPreference.getSelectedEMediaId(UploadPhotosActivity.this));
                jsonObject.addProperty("media_option_id", media_option_id);
                jsonObject.addProperty("date_time", Functions.getCurrentDate() + " " + Functions.getCurrentTime());
                jsonObject.add("ActivityDetails", jsonArrayImages);
            }

            Log.e(TAG, "Request COMPLETE SINGLE TASK **" + action + "** >>" + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_api(jsonObject);

            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response COMPLETE SINGLE TASK >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            publishProgress("200", "");
                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
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
            String status = values[0];
            String msg = values[1];
            hideProgressDialog();
            if (status.equalsIgnoreCase("200")) {
                Intent intent = new Intent(UploadPhotosActivity.this, ThankYouActivity.class);
                Intent intent1 = new Intent(UploadPhotosActivity.this, SingleActivityDetail.class);
                if (action.equalsIgnoreCase("shop")) {
                    intent1.setAction("shop");
                } else if (action.equalsIgnoreCase("transit")) {
                    intent1.setAction("transit");
                } else if (action.equalsIgnoreCase("print")) {
                    intent1.setAction("print");
                } else if (action.equalsIgnoreCase("hoarding")) {
                    intent1.setAction("hoarding");
                } else if (action.equalsIgnoreCase("wall")) {
                    intent1.setAction("wall");
                } else if (action.equalsIgnoreCase("emedia")) {
                    intent1.setAction("emedia");
                }
                intent1.putExtra("media_option_id", media_option_id);
                intent1.putExtra("media_option_name", media_option_name);
                Intent[] intents = new Intent[2];
                intents[0] = intent1;
                intents[1] = intent;
                startActivities(intents);
                finish();
            } else
                Toast.makeText(UploadPhotosActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
