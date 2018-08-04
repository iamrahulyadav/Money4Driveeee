package com.hvantage2.money4driveeee.service;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseInstanceService";
    private String refreshedToken = "";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e(TAG, "Refreshed token: " + refreshedToken);
        if (AppPreference.isLoggedIn(getApplicationContext()) == true) {
            updateFCM();
        } else {

        }
    }

    void updateFCM() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(AppConstants.KEYS.METHOD, AppConstants.FEILDEXECUTATIVE.UPDATE_FCM_TOKEN);
        jsonObject.addProperty(AppConstants.KEYS.USER_ID, AppPreference.getUserId(MyFirebaseInstanceIDService.this));
        jsonObject.addProperty(AppConstants.KEYS.FCM_ID, refreshedToken);
        Log.e(TAG, "Request UDPATE_FCM >> " + jsonObject.toString());

        MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
        Call<JsonObject> call = apiService.register_log_api(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e(TAG, "Response UDPATE_FCM >> " + response.body().toString());
                JsonObject jsonObject = response.body();
                if (jsonObject.get("status").getAsString().equals("200")) {
                } else {
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

}
