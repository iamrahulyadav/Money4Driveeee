package com.hvantage2.money4driveeee.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.model.UserModal;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.FragmentIntraction;
import com.hvantage2.money4driveeee.util.Functions;
import com.hvantage2.money4driveeee.customview.CustomButton;
import com.hvantage2.money4driveeee.customview.CustomEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hvantage2 on 2018-02-20.
 */

public class MyProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MyProfileFragment";
    Context context;
    String userid;
    CustomEditText etName, etEmailid, etMobile, etaddress;
    private View rootView;
    private ProgressDialog dialog;
    FragmentIntraction intraction;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_profile, container, false);
        ((RelativeLayout) rootView.findViewById(R.id.mainLayout)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return false;
            }
        });

        setHasOptionsMenu(true);
        if (intraction != null) {
            intraction.actionbarsetTitle("My Profile");
        }
        context = getActivity();
        init(rootView);
        String userDetail = AppPreference.getUserData(context);
        UserModal user = new Gson().fromJson(userDetail, UserModal.class);
        etName.setText(user.getName());
        etEmailid.setText(user.getEmail());
        etMobile.setText(user.getContactNumber());
        etaddress.setText(user.getAddress());
        userid = user.getUserId();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentIntraction) {
            intraction = (FragmentIntraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        intraction = null;
    }

    private void init(View rootView) {
        etName = (CustomEditText) rootView.findViewById(R.id.etname);
        etEmailid = (CustomEditText) rootView.findViewById(R.id.etemail);
        etMobile = (CustomEditText) rootView.findViewById(R.id.etmobileno);
        etaddress = (CustomEditText) rootView.findViewById(R.id.etaddress);
        ((CustomButton) rootView.findViewById(R.id.btnEdit)).setOnClickListener(this);

        etEmailid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                etEmailid.setEnabled(false);
                Toast.makeText(context, "Can't change email-id", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }




    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.product_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_home) {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.main_frame, new HomeFragment());
            ft.commitAllowingStateLoss();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnEdit) {
            if (etName.getText().toString().trim().isEmpty()) {
                etName.setError("Enter your name");
            } else if (etaddress.getText().toString().trim().isEmpty()) {
                etaddress.setError("Enter your address.");
            } else if (etMobile.getText().toString().trim().isEmpty()) {
                etMobile.setError("Enter your mobile no");
            } else if (!Functions.isValidPhoneNumber(etMobile.getText().toString().trim())) {
                etMobile.setError("Enter valid mobile no");
            } else {
                new UpdateProfile().execute();
            }
        }
    }

    private void showProgressDialog() {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void hideProgressDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }


    class UpdateProfile extends AsyncTask<Void,String, Void> {

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.UPDATE_PROFILE);
            jsonObject.addProperty("user_id", userid);
            jsonObject.addProperty("name", etName.getText().toString());
            jsonObject.addProperty("contact", etMobile.getText().toString());
            jsonObject.addProperty("address", etaddress.getText().toString());
            jsonObject.addProperty("FCM_ID", FirebaseInstanceId.getInstance().getToken());
            Log.e(TAG, "Request UPDATE PROFILE >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.user_login(jsonObject);
            Log.e(TAG, "updatePROFILE response :- " + apiService.user_login(jsonObject));
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response UPDATE PROFILE >> " + response.body().toString());
                    JsonObject jsonObject = response.body();
                    Log.d("status", String.valueOf(jsonObject));
                    if (jsonObject.get("status").getAsString().equals("200")) {
                        JsonArray jsonArray = jsonObject.getAsJsonArray("result");
                        JsonObject result = jsonArray.get(0).getAsJsonObject();
                        UserModal user = new Gson().fromJson(result, UserModal.class);
                        AppPreference.setUserData(context, result.toString());
                        AppPreference.setUserName(getActivity(),user.getName());
                        Log.e(TAG, "onResponse: >> "+user.toString() );
                        publishProgress("200", "");
                    } else {
                        JsonArray jsonArray = jsonObject.getAsJsonArray("result");
                        JsonObject result = jsonArray.get(0).getAsJsonObject();
                        publishProgress("400", result.get("msg").getAsString());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    publishProgress("400", getActivity().getResources().getString(R.string.api_error_msg));
                }
            });
            return null;
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            hideProgressDialog();
            String status=values[0];
            String msg=values[1];
            if(status.equalsIgnoreCase("200"))
                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

        }
/*
        private void publishProgress(int status, String msg) {
            hideProgressDialog();
            if (status == 200)
                Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
            else if (status == 400)
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }*/
    }

}
