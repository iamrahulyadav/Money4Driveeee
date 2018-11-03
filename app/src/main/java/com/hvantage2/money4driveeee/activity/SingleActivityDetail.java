package com.hvantage2.money4driveeee.activity;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.hvantage2.money4driveeee.util.Functions;
import com.hvantage2.money4driveeee.util.ProgressHUD;
import com.hvantage2.money4driveeee.util.TouchImageView;
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

public class SingleActivityDetail extends AppCompatActivity {
    private static final String TAG = "SingleActivityDetail";
    private static final int REQUEST_STORAGE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = REQUEST_STORAGE + 1;
    private static final int REQUEST_LOAD_IMAGE = REQUEST_IMAGE_CAPTURE + 1;
    ArrayList<ImageUploadModel> list;
    private RecyclerView recycler_view;
    private UploadedImageAdapter adapter;
    private Button addmore;
    private Dialog dialog1;
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
    private double square_meter = 0.0;
    private double square_feet = 0.0;

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
        } else if (getIntent().getAction().equalsIgnoreCase("transit")) {
            action = "transit";
            btnAddDoc.setVisibility(View.GONE);
        } else if (getIntent().getAction().equalsIgnoreCase("print")) {
            action = "print";
        } else if (getIntent().getAction().equalsIgnoreCase("hoarding")) {
            action = "hoarding";
        } else if (getIntent().getAction().equalsIgnoreCase("wall")) {
            action = "wall";
        } else if (getIntent().getAction().equalsIgnoreCase("emedia")) {
            action = "emedia";
        }

        if (getIntent().hasExtra("media_option_id") && getIntent().hasExtra("media_option_name")) {
            media_option_id = getIntent().getStringExtra("media_option_id");
            media_option_name = getIntent().getStringExtra("media_option_name");
        }

        if (!media_option_name.equalsIgnoreCase(""))
            getSupportActionBar().setTitle(media_option_name);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetDataTask().execute();
    }

    private void init() {
        list = new ArrayList<ImageUploadModel>();
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        addmore = (Button) findViewById(R.id.takepicture);
        btnAddDoc = (Button) findViewById(R.id.btnAddDoc);
        LinearLayoutManager layoutManager = new LinearLayoutManager(SingleActivityDetail.this);
        recycler_view.setLayoutManager(layoutManager);

        adapter = new UploadedImageAdapter(context, list, action, new UploadedImageAdapter.MyAdapterListener() {
            @Override
            public void onStartUploading(Bitmap bitmap, int position) {
                Log.e(TAG, "onStartUploading ");
                new ImageTask(position).execute(bitmap);
            }

            @Override
            public void onDelete(View view, final int position) {
                AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete?");
                builder.setTitle("Delete");
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new DeleteTask(position).execute(list.get(position).getUpdate_id());
                    }
                });
                builder.show();
            }

            @Override
            public void onEdit(View view, int position) {
                Intent intent = new Intent(context, EditPhotoActivity.class);
                intent.putExtra("image_url", list.get(position).getImage_url());
                intent.putExtra("dimen", list.get(position).getDimension());
                intent.putExtra("remark", list.get(position).getRemark());
                intent.putExtra("update_id", list.get(position).getUpdate_id());
                intent.putExtra("action", action);
                context.startActivity(intent);
            }

            @Override
            public void onView(View view, int position) {
                showPreviewDialog(list.get(position));
            }
        });

        recycler_view.setAdapter(adapter);
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

    private void deleteDialog(final int position) {

    }

    private class DeleteTask extends AsyncTask<String, String, Void> {
        int position = 0;

        public DeleteTask(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(String... strings) {
            JsonObject jsonObject = new JsonObject();
            if (action.equalsIgnoreCase("shop")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.DELETEACTIVITYDETAIL);
                jsonObject.addProperty("update_id", strings[0]);
            } else if (action.equalsIgnoreCase("transit")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.DELETETRANSITACTDET);
                jsonObject.addProperty("update_id", strings[0]);
            } else if (action.equalsIgnoreCase("print")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.DELETEPRINTDETAIL);
                jsonObject.addProperty("update_id", strings[0]);
            } else if (action.equalsIgnoreCase("hoarding")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.DELETEHOARDINGIDETAIL);
                jsonObject.addProperty("update_id", strings[0]);
            } else if (action.equalsIgnoreCase("wall")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.DELETEWALLDETAIL);
                jsonObject.addProperty("update_id", strings[0]);
            } else if (action.equalsIgnoreCase("emedia")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.DELETEEMEDIADETAIL);
                jsonObject.addProperty("update_id", strings[0]);
            }
            Log.e("Adapter", "Request DELETE IMAGE >> **" + action + "** >> " + jsonObject);
            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_activity_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response DELETE IMAGE >> " + response.body().toString());
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
                        publishProgress("400", context.getResources().getString(R.string.api_error_msg));
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    publishProgress("400", context.getResources().getString(R.string.api_error_msg));
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
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                list.remove(position);
                adapter.notifyDataSetChanged();
            } else if (status.equalsIgnoreCase("400"))
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo");
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


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case UtilClass.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Camera"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Gallery"))
                        galleryIntent();
                } else {
                }
                break;
        }
    }

    private void addDocDialog(final Bitmap bitmap) {
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
                    tempDimen = "";
                    tempRemark = remarkText.getText().toString();
                    dialog1.dismiss();
                    ImageUploadModel data = new ImageUploadModel(bitmap, tempDimen, tempRemark);
                    list.add(data);
                    adapter.notifyDataSetChanged();
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

    private void addImageDialog(final Bitmap bitmap) {
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

        if (action.equalsIgnoreCase("wall") || action.equalsIgnoreCase("shop"))
            tvDimenUnit.setText("Dimension (inches)");

        Bitmap imgThumb = Bitmap.createScaledBitmap(bitmap, 640, 480, false);
        imageView.setImageBitmap(imgThumb);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (height.getText().toString().length() > 0 && width.getText().toString().length() > 0)
                    tempDimen = height.getText().toString() + "x" + width.getText().toString();
                else
                    tempDimen = "";
                Log.e(TAG, "tempDimen: " + tempDimen);
                tempRemark = remarkText.getText().toString();
                dialog1.dismiss();
                ImageUploadModel data = new ImageUploadModel(bitmap, tempDimen, tempRemark);
                list.add(data);
                adapter.notifyDataSetChanged();
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

    private void addImageDialogTransit(final Bitmap bitmap) {
        dialog1 = new Dialog(SingleActivityDetail.this, R.style.image_preview_dialog);
        dialog1.setContentView(R.layout.image_setup_layout_transit);
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
        final EditText remarkText = (EditText) dialog1.findViewById(R.id.remarkText);

        Bitmap imgThumb = Bitmap.createScaledBitmap(bitmap, 640, 480, false);
        imageView.setImageBitmap(imgThumb);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempDimen = "";
                tempRemark = remarkText.getText().toString();
                dialog1.dismiss();
                ImageUploadModel data = new ImageUploadModel(bitmap, tempDimen, tempRemark);
                list.add(data);
                adapter.notifyDataSetChanged();
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

    private void addImageDialogWall(final Bitmap bitmap) {
        final Dialog dialog = new Dialog(context, R.style.image_preview_dialog);
        dialog.setContentView(R.layout.image_setup_layout_wall);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);


        LinearLayout llDimen = (LinearLayout) dialog.findViewById(R.id.llDimen);
        ImageView imageView = (ImageView) dialog.findViewById(R.id.img_circle);
        ScrollView container = (ScrollView) dialog.findViewById(R.id.container);

        ImageView imgBack = (ImageView) dialog.findViewById(R.id.imgBack);
        Button btnSave = (Button) dialog.findViewById(R.id.btnSave);
        final TextView tvDimenUnit = (TextView) dialog.findViewById(R.id.tvDimenUnit);
        final TextView tvSquareFeet = (TextView) dialog.findViewById(R.id.tvSquareFeet);
        final TextView tvSquareMeter = (TextView) dialog.findViewById(R.id.tvSquareMeter);
        final EditText height = (EditText) dialog.findViewById(R.id.dimensionTextheight);
        final EditText width = (EditText) dialog.findViewById(R.id.dimensionTextwidth);
        final EditText remarkText = (EditText) dialog.findViewById(R.id.remarkText);
        Bitmap imgThumb = Bitmap.createScaledBitmap(bitmap, 480, 640, false);
        imageView.setImageBitmap(imgThumb);


        square_feet = 0.0;
        square_meter = 0.0;
        width.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    if (!TextUtils.isEmpty(height.getText().toString()) && !TextUtils.isEmpty(height.getText().toString())) {
                        square_feet = Functions.roundTwoDecimals((Double.parseDouble(height.getText().toString()) / 12) * (Double.parseDouble(width.getText().toString()) / 12));
                        square_meter = Functions.roundTwoDecimals(square_feet / 10.764);
                        tvSquareFeet.setText("Square foot : " + square_feet);
                        tvSquareMeter.setText("Square meter : " + square_meter);
                    } else {
                        Toast.makeText(context, "Height or width is empty", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (height.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(context, "Enter height", Toast.LENGTH_SHORT).show();
                else if (width.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(context, "Enter width", Toast.LENGTH_SHORT).show();
                else {
                    tempDimen = height.getText().toString() + "x" + width.getText().toString();
                    Log.e(TAG, "tempDimen: " + tempDimen);
                    tempRemark = remarkText.getText().toString();
                    dialog.dismiss();
                    ImageUploadModel data = new ImageUploadModel(bitmap, tempDimen, tempRemark);
                    list.add(data);
                    adapter.notifyDataSetChanged();
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
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                    if (imgType == 1)
                        selectPreviewDialog(bitmap);
                    else
                        addDocDialog(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), outputFileUri);
                    if (imgType == 1)
                        selectPreviewDialog(bitmap);
                    else
                        addDocDialog(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void selectPreviewDialog(Bitmap bitmap) {
        switch (action) {
            case "shop":
                addImageDialog(bitmap);
                break;
            case "transit":
                addImageDialogTransit(bitmap);
                break;
            case "hoarding":
                addImageDialog(bitmap);
                break;
            case "wall":
                addImageDialogWall(bitmap);
                break;
            case "print":
                addImageDialog(bitmap);
                break;
            case "emedia":
                addImageDialog(bitmap);
                break;
            default:
                addImageDialog(bitmap);
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
                /*  .setBorderCornerThickness(5)
                  .setBorderCornerColor(getResources().getColor(R.color.colorAccent))
                  .setBorderLineColor(getResources().getColor(R.color.white))
                  .setBorderLineThickness(3)*/
                .start(this);
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
        int position = 0;

        public ImageTask(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Bitmap... bitmaps) {
            Bitmap bitmapImage = bitmaps[0];
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
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
                jsonObject.addProperty("remark", tempRemark);
                jsonObject.addProperty("dimension", tempDimen);
                jsonObject.addProperty("image", base64image);
            } else if (action.equalsIgnoreCase("transit")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.TRANSITPROJECTACTIIMGUPLOAD);
                jsonObject.addProperty("user_id", AppPreference.getUserId(SingleActivityDetail.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SingleActivityDetail.this));
                jsonObject.addProperty("vehicle_id", AppPreference.getSelectedVehicleId(SingleActivityDetail.this));
                jsonObject.addProperty("transit_id", media_option_id);
                jsonObject.addProperty("remark", tempRemark);
                jsonObject.addProperty("dimension", tempDimen);
                jsonObject.addProperty("image", base64image);
            } else if (action.equalsIgnoreCase("print")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.PROJECTPRINTIMAGEUPLOAD);
                jsonObject.addProperty("user_id", AppPreference.getUserId(SingleActivityDetail.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SingleActivityDetail.this));
                jsonObject.addProperty("print_id", AppPreference.getSelectedPMediaId(SingleActivityDetail.this));
                jsonObject.addProperty("media_option_id", media_option_id);
                jsonObject.addProperty("remark", tempRemark);
                jsonObject.addProperty("dimension", tempDimen);
                jsonObject.addProperty("image", base64image);
            } else if (action.equalsIgnoreCase("hoarding")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.PROJECTHOARDINGIMAGEUPLOAD);
                jsonObject.addProperty("user_id", AppPreference.getUserId(SingleActivityDetail.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SingleActivityDetail.this));
                jsonObject.addProperty("hoarding_id", AppPreference.getSelectedHoardingId(SingleActivityDetail.this));
                jsonObject.addProperty("media_option_id", media_option_id);
                jsonObject.addProperty("remark", tempRemark);
                jsonObject.addProperty("dimension", tempDimen);
                jsonObject.addProperty("image", base64image);

            } else if (action.equalsIgnoreCase("wall")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.PROJECTWALLMAGEUPLOAD);
                jsonObject.addProperty("user_id", AppPreference.getUserId(SingleActivityDetail.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SingleActivityDetail.this));
                jsonObject.addProperty("wall_id", AppPreference.getSelectedWallId(SingleActivityDetail.this));
                jsonObject.addProperty("media_option_id", media_option_id);
                jsonObject.addProperty("dimension", tempDimen);
                jsonObject.addProperty("remark", tempRemark);
                jsonObject.addProperty("square_feet", "" + square_feet);
                jsonObject.addProperty("square_meter", "" + square_meter);
                jsonObject.addProperty("image", base64image);
            } else if (action.equalsIgnoreCase("emedia")) {
                jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.PROJECTEMEDIAIMAGEUPLOAD);
                jsonObject.addProperty("user_id", AppPreference.getUserId(SingleActivityDetail.this));
                jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(SingleActivityDetail.this));
                jsonObject.addProperty("emedia_id", AppPreference.getSelectedEMediaId(SingleActivityDetail.this));
                jsonObject.addProperty("media_option_id", media_option_id);
                jsonObject.addProperty("remark", tempRemark);
                jsonObject.addProperty("dimension", tempDimen);
                jsonObject.addProperty("image", base64image);
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
                            list.set(position, data);
                            adapter.notifyDataSetChanged();
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
            String status = values[0];
            String msg = values[1];
            Toast.makeText(SingleActivityDetail.this, msg, Toast.LENGTH_SHORT).show();
        }

    }

    class GetDataTask extends AsyncTask<Void, String, Void> {

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
            if (status.equalsIgnoreCase("400")) {
                if (!msg.equalsIgnoreCase("data not found"))
                    Toast.makeText(SingleActivityDetail.this, msg, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void showPreviewDialog(ImageUploadModel modal) {
        Dialog dialog1 = new Dialog(context, R.style.image_preview_dialog);
        dialog1.setContentView(R.layout.image_preview_layout);
        Window window = dialog1.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog1.setCancelable(true);
        dialog1.setCanceledOnTouchOutside(true);

        TouchImageView imgPreview = (TouchImageView) dialog1.findViewById(R.id.imgPreview);
        TextView tvDimen = (TextView) dialog1.findViewById(R.id.tvDimen);
        TextView tvRemark = (TextView) dialog1.findViewById(R.id.tvRemark);
        imgPreview.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if (!modal.getImage_url().equalsIgnoreCase(""))
            Picasso.with(context)
                    .load(modal.getImage_url())
                    .placeholder(R.drawable.no_image_placeholder)
                    .into(imgPreview);
        else if (modal.getImage() != null)
            imgPreview.setImageBitmap(modal.getImage());


        if (!modal.getDimension().equalsIgnoreCase(""))
            tvDimen.setText("Dimensions : " + modal.getDimension());
        tvRemark.setText("Remark : " + modal.getRemark());
        dialog1.show();
    }
}
