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
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
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
import com.hvantage2.money4driveeee.adapter.UploadedImageAdapter;
import com.hvantage2.money4driveeee.model.ImageUploadModel;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.ProgressHUD;
import com.hvantage2.money4driveeee.util.RecyclerItemClickListener;
import com.hvantage2.money4driveeee.util.TouchImageView;
import com.hvantage2.money4driveeee.util.UtilClass;
import com.squareup.picasso.Picasso;

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

public class SingleActivityDetail extends AppCompatActivity {
    private static final String TAG = "SingleActivityDetail";
    private static final int REQUEST_STORAGE = 0;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_IMAGE_CAPTURE = REQUEST_STORAGE + 1;
    private static final int REQUEST_LOAD_IMAGE = REQUEST_IMAGE_CAPTURE + 1;
    private static final int PIC_CROP = REQUEST_LOAD_IMAGE + 1;
    ArrayList<ImageUploadModel> list;
    private RecyclerView recycler_view;
    private UploadedImageAdapter adapter;
    private Button addmore;
    private Uri originalImageUri;
    private Dialog dialog1;
    private Bitmap bitmapImage1;
    private String tempDimen = "", tempRemark = "";
    private String userChoosenTask;
    private String base64image;
    private String action;
    private ProgressHUD progressHD;
    private String media_option_id;
    private String media_option_name;
    private Button btnAddDoc;
    private int imgType = 1;
    private SingleActivityDetail context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_detail);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            btnAddDoc.setVisibility(View.VISIBLE);
        } else if (getIntent().getAction().equalsIgnoreCase("emedia")) {
            action = "emedia";
            if (getIntent().hasExtra("media_option_id") && getIntent().hasExtra("media_option_name")) {
                media_option_id = getIntent().getStringExtra("media_option_id");
                media_option_name = getIntent().getStringExtra("media_option_name");
            }
        }

        if (!media_option_name.equalsIgnoreCase(""))
            getSupportActionBar().setTitle(media_option_name);

        init();
//        Snackbar.make(findViewById(android.R.id.content), "Long tap on item, to see previews", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetDetailTask().execute();
    }

    private void init() {
        list = new ArrayList<ImageUploadModel>();
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        addmore = (Button) findViewById(R.id.takepicture);
        btnAddDoc = (Button) findViewById(R.id.btnAddDoc);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SingleActivityDetail.this);
        recycler_view.setLayoutManager(layoutManager);
        adapter = new UploadedImageAdapter(SingleActivityDetail.this, list, action);
        recycler_view.setAdapter(adapter);
        /*recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(SingleActivityDetail.this, recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showPreviewDialog(list.get(position));
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));*/
        addmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgType = 1;
                selectImage();
            }
        });
        btnAddDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgType = 2;
                selectImage();
            }
        });

        if (AppPreference.getSelectedProjectType(SingleActivityDetail.this).equalsIgnoreCase(AppConstants.PROJECT_TYPE.COMPLETED)) {
            ((LinearLayout) findViewById(R.id.llBottom)).setVisibility(View.GONE);
        }
    }

    @Override
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
                    outputFileUri = FileProvider.getUriForFile(SingleActivityDetail.this, BuildConfig.APPLICATION_ID + ".provider", croppedImageFile1);
                    originalFileUri = FileProvider.getUriForFile(SingleActivityDetail.this, BuildConfig.APPLICATION_ID + ".provider", croppedImageFile);
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
                if (imgType == 1)
                    addImageDialog();
                else if (imgType == 2)
                    addDocDialog();

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

    private void addDocDialog() {
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
                    tempDimen = "";
                    tempRemark = remarkText.getText().toString();
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


    private void addImageDialog() {
        dialog1 = new Dialog(SingleActivityDetail.this, R.style.image_preview_dialog);
        dialog1.setContentView(R.layout.image_setup_layout);
        Window window = dialog1.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog1.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog1.setCancelable(true);
        dialog1.setCanceledOnTouchOutside(false);

        ImageView imageView = (ImageView) dialog1.findViewById(R.id.img_circle);
        ScrollView container = (ScrollView) dialog1.findViewById(R.id.container);

        ImageView imgBack = (ImageView) dialog1.findViewById(R.id.imgBack);
        final Button btnSave = (Button) dialog1.findViewById(R.id.btnSave);
        final TextView tvDimenUnit = (TextView) dialog1.findViewById(R.id.tvDimenUnit);
        final EditText height = (EditText) dialog1.findViewById(R.id.dimensionTextheight);
        final EditText width = (EditText) dialog1.findViewById(R.id.dimensionTextwidth);
        final EditText remarkText = (EditText) dialog1.findViewById(R.id.remarkText);

        if (action.equalsIgnoreCase("wall"))
            tvDimenUnit.setText("Dimension(inches)");

        String croppedfilePath = Environment.getExternalStorageDirectory() + "/activity_image.jpg";
        bitmapImage1 = BitmapFactory.decodeFile(croppedfilePath);
        imageView.setImageBitmap(bitmapImage1);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (height.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(SingleActivityDetail.this, "Enter height", Toast.LENGTH_SHORT).show();
                else if (width.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(SingleActivityDetail.this, "Enter width", Toast.LENGTH_SHORT).show();
                else if (remarkText.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(SingleActivityDetail.this, "Enter remark", Toast.LENGTH_SHORT).show();
                else {
                    tempDimen = height.getText().toString() + "x" + width.getText().toString();
                    Log.e(TAG, "tempDimen: " + tempDimen);
                    tempRemark = remarkText.getText().toString();
                    dialog1.dismiss();
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
            Toast.makeText(SingleActivityDetail.this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }


    private void selectImage() {
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SingleActivityDetail.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = UtilClass.checkPermission(SingleActivityDetail.this);
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
            outputFileUri = FileProvider.getUriForFile(SingleActivityDetail.this,
                    BuildConfig.APPLICATION_ID + ".provider", newfile);
        } else {
            outputFileUri = Uri.fromFile(newfile);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_home:
                Intent intent = new Intent(SingleActivityDetail.this, DashBoardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgressDialog() {
        progressHD = ProgressHUD.show(SingleActivityDetail.this, "Processing...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void hideProgressDialog() {
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
            base64image = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.d(TAG, "base64image >> " + base64image);
            //api call
            JsonObject jsonObject = new JsonObject();
            if (action.equalsIgnoreCase("shop")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.PROJECTACTIVITYIMAGEUPLOAD);
                jsonObject.addProperty("user_id", AppPreference.getUserId(SingleActivityDetail.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SingleActivityDetail.this));
                jsonObject.addProperty("shop_id", AppPreference.getSelectedShopid(SingleActivityDetail.this));
                jsonObject.addProperty("activity_id", media_option_id);
                jsonObject.addProperty("image", base64image);
                jsonObject.addProperty("remark", tempRemark);
                jsonObject.addProperty("dimension", tempDimen);
            } else if (action.equalsIgnoreCase("transit")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.TRANSITPROJECTACTIIMGUPLOAD);
                jsonObject.addProperty("user_id", AppPreference.getUserId(SingleActivityDetail.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SingleActivityDetail.this));
                jsonObject.addProperty("vehicle_id", AppPreference.getSelectedVehicleId(SingleActivityDetail.this));
                jsonObject.addProperty("transit_id", media_option_id);
                jsonObject.addProperty("image", base64image);
                jsonObject.addProperty("remark", tempRemark);
                jsonObject.addProperty("dimension", tempDimen);
            } else if (action.equalsIgnoreCase("print")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.PROJECTPRINTIMAGEUPLOAD);
                jsonObject.addProperty("user_id", AppPreference.getUserId(SingleActivityDetail.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SingleActivityDetail.this));
                jsonObject.addProperty("print_id", AppPreference.getSelectedPMediaId(SingleActivityDetail.this));
                jsonObject.addProperty("media_option_id", media_option_id);
                jsonObject.addProperty("image", base64image);
                jsonObject.addProperty("remark", tempRemark);
                jsonObject.addProperty("dimension", tempDimen);
            } else if (action.equalsIgnoreCase("hoarding")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.PROJECTHOARDINGIMAGEUPLOAD);
                jsonObject.addProperty("user_id", AppPreference.getUserId(SingleActivityDetail.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SingleActivityDetail.this));
                jsonObject.addProperty("hoarding_id", AppPreference.getSelectedHoardingId(SingleActivityDetail.this));
                jsonObject.addProperty("media_option_id", media_option_id);
                jsonObject.addProperty("image", base64image);
                jsonObject.addProperty("remark", tempRemark);
                jsonObject.addProperty("dimension", tempDimen);
            } else if (action.equalsIgnoreCase("wall")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.PROJECTWALLMAGEUPLOAD);
                jsonObject.addProperty("user_id", AppPreference.getUserId(SingleActivityDetail.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SingleActivityDetail.this));
                jsonObject.addProperty("wall_id", AppPreference.getSelectedWallId(SingleActivityDetail.this));
                jsonObject.addProperty("media_option_id", media_option_id);
                jsonObject.addProperty("image", base64image);
                jsonObject.addProperty("remark", tempRemark);
                jsonObject.addProperty("dimension", tempDimen);
            } else if (action.equalsIgnoreCase("emedia")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.PROJECTEMEDIAIMAGEUPLOAD);
                jsonObject.addProperty("user_id", AppPreference.getUserId(SingleActivityDetail.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SingleActivityDetail.this));
                jsonObject.addProperty("emedia_id", AppPreference.getSelectedEMediaId(SingleActivityDetail.this));
                jsonObject.addProperty("media_option_id", media_option_id);
                jsonObject.addProperty("image", base64image);
                jsonObject.addProperty("remark", tempRemark);
                jsonObject.addProperty("dimension", tempDimen);
            }

            Log.e(TAG, "Request ADD IMAGE >> **" + action + "** >>" + jsonObject.toString());

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

                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            JSONArray jsonArray1 = jsonArray.getJSONArray(0);
                            JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
                            String image_url = jsonObject1.getString("image");
                            String dimention = jsonObject1.getString("dimention");
                            String remark = jsonObject1.getString("remark");
                            String datetime = jsonObject1.getString("datetime");
                            String update_id = jsonObject1.getString("update_id");
                            ImageUploadModel data = new ImageUploadModel(image_url, dimention, remark, datetime, update_id);
//                            list.add(0, data);
                            list.add(data);
                            publishProgress("200", "Added");
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
            adapter.notifyDataSetChanged();
            String status = values[0];
            String msg = values[1];
            Toast.makeText(SingleActivityDetail.this, msg, Toast.LENGTH_SHORT).show();
        }

    }

    class GetDetailTask extends AsyncTask<Void, String, Void> {

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
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.SHOPACTIVITYDETAIL);
                jsonObject.addProperty("user_id", AppPreference.getUserId(SingleActivityDetail.this)); //8
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SingleActivityDetail.this));
                jsonObject.addProperty("shop_id", AppPreference.getSelectedShopid(SingleActivityDetail.this));
                jsonObject.addProperty("activity_id", media_option_id);
            } else if (action.equalsIgnoreCase("transit")) {
                jsonObject = new JsonObject();
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.TRANSITACTIVITYDETAIL);
                jsonObject.addProperty("user_id", AppPreference.getUserId(SingleActivityDetail.this)); //8
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SingleActivityDetail.this));
                jsonObject.addProperty("vehicle_id", AppPreference.getSelectedVehicleId(SingleActivityDetail.this));
                jsonObject.addProperty("transit_id", media_option_id);
            } else if (action.equalsIgnoreCase("print")) {
                jsonObject = new JsonObject();
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.PRINTACTIVITYDETAIL);
                jsonObject.addProperty("user_id", AppPreference.getUserId(SingleActivityDetail.this)); //8
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SingleActivityDetail.this));
                jsonObject.addProperty("print_id", AppPreference.getSelectedPMediaId(SingleActivityDetail.this));
                jsonObject.addProperty("media_option_id", media_option_id);
            } else if (action.equalsIgnoreCase("hoarding")) {
                jsonObject = new JsonObject();
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.HOARDINGACTIVITYDETAIL);
                jsonObject.addProperty("user_id", AppPreference.getUserId(SingleActivityDetail.this)); //8
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SingleActivityDetail.this));
                jsonObject.addProperty("hoarding_id", AppPreference.getSelectedHoardingId(SingleActivityDetail.this));
                jsonObject.addProperty("media_option_id", media_option_id);
            } else if (action.equalsIgnoreCase("wall")) {
                jsonObject = new JsonObject();
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.WALLACTIVITYDETAIL);
                jsonObject.addProperty("user_id", AppPreference.getUserId(SingleActivityDetail.this)); //8
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SingleActivityDetail.this));
                jsonObject.addProperty("wall_id", AppPreference.getSelectedWallId(SingleActivityDetail.this));
                jsonObject.addProperty("media_option_id", media_option_id);
            } else if (action.equalsIgnoreCase("emedia")) {
                jsonObject = new JsonObject();
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.EMEDIAACTIVITYDETAIL);
                jsonObject.addProperty("user_id", AppPreference.getUserId(SingleActivityDetail.this)); //8
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SingleActivityDetail.this));
                jsonObject.addProperty("emedia_id", AppPreference.getSelectedEMediaId(SingleActivityDetail.this));
                jsonObject.addProperty("media_option_id", media_option_id);
            }
            Log.e(TAG, "Request ACTIVITY DETAIL >> " + jsonObject);

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response ACTIVITY DETAIL >> " + response.body().toString());
                    String str = response.body().toString();
                    try {
                        JSONObject jsonObject1 = new JSONObject(str);
                        list.clear();
                        if (jsonObject1.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject1.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                String image = jsonObject2.getString("image");
                                String remark = jsonObject2.getString("remark");
                                String datetime = jsonObject2.getString("datetime");
                                String dimention = jsonObject2.getString("dimention");
                                String update_id = jsonObject2.getString("update_id");
                                ImageUploadModel model = new ImageUploadModel(image, dimention, remark, datetime, update_id);
                                list.add(model);
                            }
                            adapter.notifyDataSetChanged();
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
            if (status.equalsIgnoreCase("400"))
                Toast.makeText(SingleActivityDetail.this, msg, Toast.LENGTH_SHORT).show();
        }

    }

}
