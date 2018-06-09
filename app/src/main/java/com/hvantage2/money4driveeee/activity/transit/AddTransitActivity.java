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
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.hvantage2.money4driveeee.adapter.VehicleSearchResultAdapter;
import com.hvantage2.money4driveeee.model.StateCityModel;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.ProgressHUD;
import com.hvantage2.money4driveeee.util.RecyclerItemClickListener;
import com.hvantage2.money4driveeee.util.UtilClass;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTransitActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_STORAGE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = REQUEST_STORAGE + 1;
    private static final int REQUEST_LOAD_IMAGE = REQUEST_IMAGE_CAPTURE + 1;
    private static final String TAG = "AddTransitActivity";
    EditText etDriverName, etDriverContact, etVehicle, etRegNo, etDriverAddress, etStartDate, etEndDate;
    Button btnConfirm, btnCancel;
    ArrayList<StateCityModel> listState = new ArrayList<StateCityModel>();
    ArrayList<StateCityModel> listCity = new ArrayList<StateCityModel>();
    ArrayList<String> listGift = new ArrayList<String>();
    ArrayList<String> listResult = new ArrayList<String>();
    private String media_option_id = "";
    private ProgressHUD progressHD;
    private Context context;
    private ImageView imgDoc1, imgDoc2;
    private String userChoosenTask;
    private int imgCounter = 1;
    private String base64image1 = "", base64image2 = "";
    private TextView tvImgDoc1Remark, tvImgDoc2Remark;
    private TextView tvRequestOtp;
    private ProgressBar progressBar;
    private Spinner spinnerGift;
    private EditText etSelectGift;
    private int total_days = 0;
    private String vehicle_id = "0";
    private String verify_status = "0";
    private Spinner spinnerState, spinnerCity;
    private StateCityAdapter adapterCity, adapterState;
    private String selectedStateId = "0", selectedCityId = "0";
    private VehicleSearchResultAdapter adapterResult;
    private NestedScrollView nsvResult;
    private LinearLayout llVNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transit);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        new GetStateTask().execute();
        showSearchDialog();
        if (getIntent().hasExtra("media_option_id"))
            media_option_id = getIntent().getStringExtra("media_option_id");
        if (media_option_id == null || media_option_id.equalsIgnoreCase("")) {
            Toast.makeText(this, "Select Media Option", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (getIntent().hasExtra("total_days"))
            total_days = getIntent().getIntExtra("total_days", 0);
        Log.e(TAG, "onCreate: total_days >> " + total_days);
        if (total_days != 0)
            calculateDate(total_days);
    }


    private void calculateDate(int days) {
        Calendar c = Calendar.getInstance();
        Log.e(TAG, "calculateDate: c.getTime() >> " + c.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = sdf.format(c.getTime());
        c.add(Calendar.DATE, days);
        String endDate = sdf.format(c.getTime());
        Log.e(TAG, "calculateDate: startDate >> " + startDate);
        Log.e(TAG, "calculateDate: endDate >> " + endDate);
        etStartDate.setText(startDate);
        etEndDate.setText(endDate);
    }

    private void init() {
        etDriverName = (EditText) findViewById(R.id.etDriverName);
        etDriverContact = (EditText) findViewById(R.id.etDriverContact);
        etVehicle = (EditText) findViewById(R.id.etVehicle);
        etRegNo = (EditText) findViewById(R.id.etRegNo);
        etDriverAddress = (EditText) findViewById(R.id.etDriverAddress);
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

        spinnerGift = (Spinner) findViewById(R.id.spinnerGift);
        etSelectGift = (EditText) findViewById(R.id.etSelectGift);

        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);


        imgDoc1.setOnClickListener(this);
        imgDoc2.setOnClickListener(this);

        tvRequestOtp.setOnClickListener(this);
        etSelectGift.setOnClickListener(this);

        ((ScrollView) findViewById(R.id.container)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSoftKeyboard(view);
                return false;
            }
        });

        setStateAdapter();
        setGiftAdapter();
    }

    private void setGiftAdapter() {
        listGift.add("Select Gift");
        listGift.add("Pressure Cooker");
        listGift.add("Tea Mug Set");
        listGift.add("Pending");
        ArrayAdapter<String> adapterGift = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listGift);
        spinnerGift.setAdapter(adapterGift);

        spinnerGift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                etSelectGift.setText((String) spinnerGift.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setStateAdapter() {
        spinnerState = (Spinner) findViewById(R.id.spinnerState);
        adapterState = new StateCityAdapter(AddTransitActivity.this, R.layout.state_city_item_layout, R.id.tvTitle, listState);
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
        adapterCity = new StateCityAdapter(AddTransitActivity.this, R.layout.state_city_item_layout, R.id.tvTitle, listCity);
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

    private void showSearchDialog() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Search Vehicle No.");
        dialog.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.input_vehicle_no_layout, null);
        dialog.setView(dialogView);

        final AppCompatAutoCompleteTextView atvSearch = (AppCompatAutoCompleteTextView) dialogView.findViewById(R.id.atvSearch);
        final RecyclerView recycler_view = (RecyclerView) dialogView.findViewById(R.id.recycler_view);
        nsvResult = (NestedScrollView) dialogView.findViewById(R.id.nsvResult);
        llVNo = (LinearLayout) dialogView.findViewById(R.id.llTop);
        final EditText etState = (EditText) dialogView.findViewById(R.id.etState);
        final EditText etDistrictCode = (EditText) dialogView.findViewById(R.id.etDistrictCode);
        final EditText etSeries = (EditText) dialogView.findViewById(R.id.etSeries);
        final EditText etVehNo = (EditText) dialogView.findViewById(R.id.etVehNo);
        final ImageView imgRight = (ImageView) dialogView.findViewById(R.id.imgRight);

        recycler_view.setLayoutManager(new LinearLayoutManager(AddTransitActivity.this));
        adapterResult = new VehicleSearchResultAdapter(AddTransitActivity.this, listResult);
        recycler_view.setAdapter(adapterResult);
        recycler_view.addOnItemTouchListener(new RecyclerItemClickListener(AddTransitActivity.this, recycler_view, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                nsvResult.setVisibility(View.GONE);
                llVNo.setVisibility(View.VISIBLE);
                String tempVno = listResult.get(position);
                atvSearch.setText("");
                String[] strarray = tempVno.split("-");
                if (strarray.length == 4) {
                    etState.setText(strarray[0]);
                    etDistrictCode.setText(strarray[1]);
                    etSeries.setText(strarray[2]);
                    etVehNo.setText(strarray[3]);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));


        dialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String vehicle_no = etState.getText().toString() + "-" + etDistrictCode.getText().toString() + "-" + etSeries.getText().toString() + "-" + etVehNo.getText().toString();
//                String vehicle_no = etState.getText().toString() + etDistrictCode.getText().toString() + etSeries.getText().toString() + etVehNo.getText().toString();
                Log.e(TAG, "showSearchDialog: onClick: Search: vehicle_no >> " + vehicle_no);
                dialog.dismiss();
                etRegNo.setText(vehicle_no);
                new CheckVehicleNoTask().execute(vehicle_no);
            }
        });
        dialog.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();

        etState.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (etState.length() == 2) {
                    etState.clearFocus();
                    etDistrictCode.requestFocus();
                    etDistrictCode.setCursorVisible(true);
                    etDistrictCode.setSelection(etDistrictCode.length());

                    if (etVehNo.length() == 4 && etDistrictCode.length() == 2 && etSeries.length() == 2) {
                        imgRight.setVisibility(View.VISIBLE);
                        alertDialog.getButton(AlertDialog.BUTTON1).setEnabled(true);
                    } else {
                        imgRight.setVisibility(View.GONE);
                        alertDialog.getButton(AlertDialog.BUTTON1).setEnabled(false);
                    }

                } else {
                    imgRight.setVisibility(View.GONE);
                    alertDialog.getButton(AlertDialog.BUTTON1).setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        etDistrictCode.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (etDistrictCode.length() == 2) {
                    etDistrictCode.clearFocus();
                    etSeries.requestFocus();
                    etSeries.setCursorVisible(true);
                    etVehNo.setCursorVisible(true);

                    if (etState.length() == 2 && etVehNo.length() == 4 && etSeries.length() == 2) {
                        imgRight.setVisibility(View.VISIBLE);
                        alertDialog.getButton(AlertDialog.BUTTON1).setEnabled(true);
                    } else {
                        imgRight.setVisibility(View.GONE);
                        alertDialog.getButton(AlertDialog.BUTTON1).setEnabled(false);
                    }

                } else if (etDistrictCode.length() == 0) {
                    etDistrictCode.clearFocus();
                    etState.requestFocus();
                    etState.setCursorVisible(true);
                    etState.setSelection(etState.length());
                } else {
                    imgRight.setVisibility(View.GONE);
                    alertDialog.getButton(AlertDialog.BUTTON1).setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        etSeries.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (etSeries.length() == 2) {
                    etSeries.clearFocus();
                    etVehNo.requestFocus();
                    etVehNo.setCursorVisible(true);
                    if (etState.length() == 2 && etDistrictCode.length() == 2 && etVehNo.length() == 4) {
                        imgRight.setVisibility(View.VISIBLE);
                        alertDialog.getButton(AlertDialog.BUTTON1).setEnabled(true);
                    } else {
                        imgRight.setVisibility(View.GONE);
                        alertDialog.getButton(AlertDialog.BUTTON1).setEnabled(false);
                    }

                } else if (etSeries.length() == 0) {
                    etSeries.clearFocus();
                    etDistrictCode.requestFocus();
                    etDistrictCode.setCursorVisible(true);
                    etDistrictCode.setSelection(etDistrictCode.length());

                } else {
                    imgRight.setVisibility(View.GONE);
                    alertDialog.getButton(AlertDialog.BUTTON1).setEnabled(false);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        etVehNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (etVehNo.length() == 0) {
                    imgRight.setVisibility(View.GONE);
                    alertDialog.getButton(AlertDialog.BUTTON1).setEnabled(false);
                    etVehNo.clearFocus();
                    etSeries.requestFocus();
                    etSeries.setCursorVisible(true);
                } else if (etVehNo.length() == 4) {
                    if (etState.length() == 2 && etDistrictCode.length() == 2 && etSeries.length() == 2) {
                        imgRight.setVisibility(View.VISIBLE);
                        alertDialog.getButton(AlertDialog.BUTTON1).setEnabled(true);
                    } else {
                        imgRight.setVisibility(View.GONE);
                        alertDialog.getButton(AlertDialog.BUTTON1).setEnabled(false);
                    }
                } else {
                    imgRight.setVisibility(View.GONE);
                    alertDialog.getButton(AlertDialog.BUTTON1).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        atvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int count) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                Log.e(TAG, "onTextChanged: count >> " + count);
                if (atvSearch.getText().toString().length() > 2) {
                    new SearchTask().execute(atvSearch.getText().toString());
                } else {
                    nsvResult.setVisibility(View.GONE);
                    llVNo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void showOtpDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enter OTP Here");
        dialog.setMessage("Please enter the OTP sent to your number (+91" + etDriverContact.getText().toString() + ")");
        dialog.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.input_otp_layout, null);
        dialog.setView(dialogView);

        final EditText etNo1 = (EditText) dialogView.findViewById(R.id.etNo1);
        final EditText etNo2 = (EditText) dialogView.findViewById(R.id.etNo2);
        final EditText etNo3 = (EditText) dialogView.findViewById(R.id.etNo3);
        final EditText etNo4 = (EditText) dialogView.findViewById(R.id.etNo4);

        dialog.setPositiveButton("Send OTP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String otp = etNo1.getText().toString() + etNo2.getText().toString() + etNo3.getText().toString() + etNo4.getText().toString();
                Log.e(TAG, "showSearchDialog: onClick: Search: otp >> " + otp);
                tvRequestOtp.setText("");
                new OTPVerifyTask().execute(otp);
            }
        });
        dialog.setNegativeButton("Later", new DialogInterface.OnClickListener() {
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

    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnConfirm:
                addTransit();
                break;
            case R.id.tvRequestOtp:
                if (TextUtils.isEmpty(etDriverName.getText().toString()))
                    etDriverName.setError("Enter driver name");
                else if (TextUtils.isEmpty(etDriverContact.getText().toString()))
                    etDriverContact.setError("Enter driver contact no.");
                else if (TextUtils.isEmpty(etVehicle.getText().toString()))
                    etVehicle.setError("Enter vehicle name");
                else if (TextUtils.isEmpty(etRegNo.getText().toString()))
                    etRegNo.setError("Enter vehicle no.");
                else {
                    etDriverContact.clearFocus();
                    hideSoftKeyboard(view);
                    progressBar.setVisibility(View.VISIBLE);
                    tvRequestOtp.setText("Sending");
                    new AddTransitVerifyTask().execute();
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
            case R.id.etSelectGift:
                spinnerGift.performClick();
                break;
            case R.id.btnCancel:
                onBackPressed();
                break;
        }
    }

    //    private void showSearchDialog() {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        dialog.setTitle("Enter Vehicle No.");
//        dialog.setCancelable(false);
//        LayoutInflater inflater = this.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.input_vehicle_no_layout, null);
//        dialog.setView(dialogView);
//
//        final EditText etState = (EditText) dialogView.findViewById(R.id.etState);
//        final EditText etDistrictCode = (EditText) dialogView.findViewById(R.id.etDistrictCode);
//        final EditText etSeries = (EditText) dialogView.findViewById(R.id.etSeries);
//        final EditText etVehNo = (EditText) dialogView.findViewById(R.id.etVehNo);
//        final ImageView imgRight = (ImageView) dialogView.findViewById(R.id.imgRight);
//
//        dialog.setPositiveButton("Search", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String vehicle_no = etState.getText().toString() + "-" + etDistrictCode.getText().toString() + "-" + etSeries.getText().toString() + "-" + etVehNo.getText().toString();
////                String vehicle_no = etState.getText().toString() + etDistrictCode.getText().toString() + etSeries.getText().toString() + etVehNo.getText().toString();
//                Log.e(TAG, "showSearchDialog: onClick: Search: vehicle_no >> " + vehicle_no);
//                dialog.dismiss();
//                etRegNo.setText(vehicle_no);
//                new CheckVehicleNoTask().execute(vehicle_no);
//            }
//        });
//        dialog.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//            }
//        });
//
//        AlertDialog alertDialog = dialog.create();
//        alertDialog.show();
//
//        etState.addTextChangedListener(new TextWatcher() {
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // TODO Auto-generated method stub
//                if (etState.length() == 2) {
//                    etState.clearFocus();
//                    etDistrictCode.requestFocus();
//                    etDistrictCode.setCursorVisible(true);
//                    etDistrictCode.setSelection(etDistrictCode.length());
//
//                    if (etVehNo.length() == 4 && etDistrictCode.length() == 2 && etSeries.length() == 2) {
//                        imgRight.setVisibility(View.VISIBLE);
//                    } else
//                        imgRight.setVisibility(View.GONE);
//
//                } else {
//                    imgRight.setVisibility(View.GONE);
//                }
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//            }
//
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        etDistrictCode.addTextChangedListener(new TextWatcher() {
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // TODO Auto-generated method stub
//                if (etDistrictCode.length() == 2) {
//                    etDistrictCode.clearFocus();
//                    etSeries.requestFocus();
//                    etSeries.setCursorVisible(true);
//                    etVehNo.setCursorVisible(true);
//
//                    if (etState.length() == 2 && etVehNo.length() == 4 && etSeries.length() == 2) {
//                        imgRight.setVisibility(View.VISIBLE);
//                    } else
//                        imgRight.setVisibility(View.GONE);
//
//                } else if (etDistrictCode.length() == 0) {
//                    etDistrictCode.clearFocus();
//                    etState.requestFocus();
//                    etState.setCursorVisible(true);
//                    etState.setSelection(etState.length());
//                } else {
//                    imgRight.setVisibility(View.GONE);
//                }
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        etSeries.addTextChangedListener(new TextWatcher() {
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // TODO Auto-generated method stub
//                if (etSeries.length() == 2) {
//                    etSeries.clearFocus();
//                    etVehNo.requestFocus();
//                    etVehNo.setCursorVisible(true);
//                    if (etState.length() == 2 && etDistrictCode.length() == 2 && etVehNo.length() == 4) {
//                        imgRight.setVisibility(View.VISIBLE);
//                    } else
//                        imgRight.setVisibility(View.GONE);
//
//                } else if (etSeries.length() == 0) {
//                    etSeries.clearFocus();
//                    etDistrictCode.requestFocus();
//                    etDistrictCode.setCursorVisible(true);
//                    etDistrictCode.setSelection(etDistrictCode.length());
//
//                } else {
//                    imgRight.setVisibility(View.GONE);
//                }
//            }
//
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//            }
//
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        etVehNo.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (etVehNo.length() == 0) {
//                    imgRight.setVisibility(View.GONE);
//                    etVehNo.clearFocus();
//                    etSeries.requestFocus();
//                    etSeries.setCursorVisible(true);
//                } else if (etVehNo.length() == 4) {
//                    if (etState.length() == 2 && etDistrictCode.length() == 2 && etSeries.length() == 2) {
//                        imgRight.setVisibility(View.VISIBLE);
//                    } else
//                        imgRight.setVisibility(View.GONE);
//                } else {
//                    imgRight.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//    }

    private void showErrorDialog400(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(AddTransitActivity.this);
        dialog.setTitle("Message");
        dialog.setMessage(msg);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        dialog.setCancelable(false);
        dialog.show();
    }

    private void showErrorDialog300(String msg) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(AddTransitActivity.this);
        dialog.setTitle("Message");
        dialog.setMessage(msg);
        dialog.setNeutralButton("Repaste", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        dialog.setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        dialog.setPositiveButton("View Details", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent(AddTransitActivity.this, ConfirmTransitActivity.class);
                intent.setAction("view");
                intent.putExtra("media_option_id", media_option_id);
                intent.putExtra("vehicle_id", vehicle_id);
                startActivity(intent);
                finish();
            }
        });

        dialog.setCancelable(false);
        dialog.show();
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

    private void addTransit() {
        if (TextUtils.isEmpty(etDriverName.getText().toString()))
            etDriverName.setError("Enter driver name");
        else if (TextUtils.isEmpty(etDriverContact.getText().toString()))
            etDriverContact.setError("Enter driver contact no.");
        else if (TextUtils.isEmpty(etVehicle.getText().toString()))
            etVehicle.setError("Enter vehicle name");
        else if (TextUtils.isEmpty(etRegNo.getText().toString()))
            etRegNo.setError("Enter vehicle no.");
        else if (selectedStateId.equalsIgnoreCase("0"))
            Toast.makeText(context, "Select state", Toast.LENGTH_SHORT).show();
        else if (selectedCityId.equalsIgnoreCase("0"))
            Toast.makeText(context, "Select city", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(etDriverAddress.getText().toString()))
            etDriverAddress.setError("Enter address");
        else if (TextUtils.isEmpty(media_option_id))
            Toast.makeText(this, "Please select transit media", Toast.LENGTH_SHORT).show();
        else
            new AddTransitTask().execute();

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
                }
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
            outputFileUri = FileProvider.getUriForFile(AddTransitActivity.this,
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
                    outputFileUri = FileProvider.getUriForFile(AddTransitActivity.this, BuildConfig.APPLICATION_ID + ".provider", croppedImageFile1);
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
                .setScaleType(CropImageView.ScaleType.CENTER_INSIDE)
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

    private class GetStateTask extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.GETPROJECTSTATES);
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(AddTransitActivity.this));
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
                    adapterState.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (status.equalsIgnoreCase("400")) {
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
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
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(AddTransitActivity.this));
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

            } else if (status.equalsIgnoreCase("400")) {
            }
        }
    }

    class SearchTask extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.GETCHECKREGISRATIONNUMBER);
            jsonObject.addProperty("reg_number", strings[0]);

            Log.e(TAG, "SearchTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.new_project_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "SearchTask: Response >>" + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        listResult.clear();
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                listResult.add(jsonObject1.getString("reg_number"));
                            }
                            publishProgress("200", "");
                        } else {
                            listResult.clear();
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            publishProgress("400", msg);
                        }
                    } catch (JSONException e) {
                        listResult.clear();
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
            adapterResult.notifyDataSetChanged();
            if (adapterResult.getItemCount() > 0) {
                llVNo.setVisibility(View.GONE);
                nsvResult.setVisibility(View.VISIBLE);
            } else {
                nsvResult.setVisibility(View.GONE);
                llVNo.setVisibility(View.VISIBLE);
            }
        }
    }

    private class CheckVehicleNoTask extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(String... strings) {
            String vehicle_no = strings[0];
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.CHECKREGISTERATIONNUMBER);
            jsonObject.addProperty("user_id", AppPreference.getUserId(AddTransitActivity.this));
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(AddTransitActivity.this));
            jsonObject.addProperty("vehicle_regis_number", vehicle_no);

            Log.e(TAG, "Request CHECK VEHICLE NUMBER >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_transit_detail_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response CHECK VEHICLE NUMBER >>" + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            publishProgress("200", resp);
                        } else if (jsonObject.getString("status").equalsIgnoreCase("300")) {
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            vehicle_id = jsonObject.getJSONArray("result").getJSONObject(0).getString("vehicle_id");
                            publishProgress("300", msg);
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
            JSONObject jsonObject = null;
            if (status.equalsIgnoreCase("200")) {
                try {
                    jsonObject = new JSONObject(msg);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    JSONObject object = jsonArray.getJSONObject(0);
                    etDriverName.setText(object.getString("driver_name"));
                    etDriverContact.setText(object.getString("driver_contact_no"));
                    etVehicle.setText(object.getString("vehicle_model"));
                    etRegNo.setText(object.getString("vehicle_regis_number"));
//                    spinnerCity.setText(object.getString("city"));
                    etDriverAddress.setText(object.getString("address"));
//                    spinnerState.setText(object.getString("state"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (status.equalsIgnoreCase("300")) {
                showErrorDialog300(msg);
            } else if (status.equalsIgnoreCase("400")) {
                showErrorDialog400(msg);
            }
        }
    }


    private class AddTransitTask extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(String... strings) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.ADDTRANSITDETAIL);
            jsonObject.addProperty("user_id", AppPreference.getUserId(AddTransitActivity.this));
            jsonObject.addProperty("project_id", AppPreference.getSelectedProjectId(AddTransitActivity.this));
            jsonObject.addProperty("driver_name", etDriverName.getText().toString());
            jsonObject.addProperty("driver_contact_no", etDriverContact.getText().toString());
            jsonObject.addProperty("branding_id", AppPreference.getSelectedAlloMediaId(AddTransitActivity.this));
            jsonObject.addProperty("media_option_id", media_option_id);
            jsonObject.addProperty("vehicle_model", etVehicle.getText().toString());
            jsonObject.addProperty("vehicle_regis_number", etRegNo.getText().toString());
            jsonObject.addProperty("state", selectedStateId);
            jsonObject.addProperty("city", selectedCityId);
            jsonObject.addProperty("address", etDriverAddress.getText().toString());
            jsonObject.addProperty("vehicle_id", vehicle_id);
            jsonObject.addProperty("verify_status", verify_status);
            jsonObject.addProperty("start_date", etStartDate.getText().toString());
            jsonObject.addProperty("end_date", etEndDate.getText().toString());
            jsonObject.addProperty("doc_img1", base64image1);
            jsonObject.addProperty("doc_img2", base64image2);
            jsonObject.addProperty("img1_remark", tvImgDoc1Remark.getText().toString());
            jsonObject.addProperty("img2_remark", tvImgDoc2Remark.getText().toString());
            Log.e(TAG, "AddTransitTask: Request >> " + jsonObject.toString());
            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_transit_detail_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "AddTransitTask: Response >>" + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONObject jsonObject1 = jsonObject.getJSONArray("result").getJSONObject(0);
                            Log.e(TAG, "onResponse: inserted vehicle_id >> " + jsonObject1.getString("id"));
                            AppPreference.setSelectedVehicleId(AddTransitActivity.this, jsonObject1.getString("id"));
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
                AppPreference.setSelectedVehicleName(AddTransitActivity.this, etVehicle.getText().toString());
                Intent intent = new Intent(AddTransitActivity.this, PerformTransitActivity.class);
                intent.putExtra("media_option_id", media_option_id);
                startActivity(intent);
                finish();
            } else if (status.equalsIgnoreCase("400")) {
                showErrorDialog400(msg);
            }
        }
    }

    private class AddTransitVerifyTask extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.ADD_VEHICLE_WITH_VERIFY);
            jsonObject.addProperty("user_id", AppPreference.getUserId(AddTransitActivity.this));
            jsonObject.addProperty("driver_name", etDriverName.getText().toString());
            jsonObject.addProperty("driver_contact_no", etDriverContact.getText().toString());
            jsonObject.addProperty("vehicle_model", etVehicle.getText().toString());
            jsonObject.addProperty("vehicle_regis_number", etRegNo.getText().toString());
            Log.e(TAG, "AddTransitVerifyTask: Request >> " + jsonObject.toString());
            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_transit_detail_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "AddTransitVerifyTask: Response >>" + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONObject jsonObject1 = jsonObject.getJSONArray("result").getJSONObject(0);
                            vehicle_id = jsonObject1.getString("vehicle_id");
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
            String status = values[0];
            String msg = values[1];
            progressBar.setVisibility(View.GONE);
            if (status.equalsIgnoreCase("200")) {
                showOtpDialog();
                tvRequestOtp.setText("");
            } else if (status.equalsIgnoreCase("400")) {
                tvRequestOtp.setText("Send OTP");
                showErrorDialog400(msg);
            }
        }
    }

    private class OTPVerifyTask extends AsyncTask<String, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(String... strings) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.VEHICLE_VERIFY_OTP);
            jsonObject.addProperty("driver_contact_no", etDriverContact.getText().toString());
            jsonObject.addProperty("user_id", AppPreference.getUserId(context));
            jsonObject.addProperty("otp", strings[0]);
            Log.e(TAG, "OTPVerifyTask: Request >> " + jsonObject.toString());
            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_transit_detail_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "OTPVerifyTask: Response >>" + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONObject jsonObject1 = jsonObject.getJSONArray("result").getJSONObject(0);
                            verify_status = jsonObject1.getString("verify_status");
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
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("200")) {
                progressBar.setVisibility(View.GONE);
                tvRequestOtp.setText("");
            } else if (status.equalsIgnoreCase("400")) {
                tvRequestOtp.setText("Try Again");
                final AlertDialog.Builder dialog = new AlertDialog.Builder(AddTransitActivity.this);
                dialog.setTitle("Message");
                dialog.setMessage(msg);
                dialog.setPositiveButton("Re-Enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showOtpDialog();
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
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

}
