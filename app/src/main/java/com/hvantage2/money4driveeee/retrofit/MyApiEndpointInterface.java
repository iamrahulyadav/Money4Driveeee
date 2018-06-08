package com.hvantage2.money4driveeee.retrofit;

import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.util.AppConstants;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Hvantage2 on 2018-02-26.
 */

public interface MyApiEndpointInterface {
    @POST(AppConstants.ENDPOINT.LOGIN)
    Call<JsonObject> user_login(@Body JsonObject jsonObject);

    @POST(AppConstants.ENDPOINT.DASHBOARD)
    Call<JsonObject> user_dashboard(@Body JsonObject jsonObject);

    @POST(AppConstants.ENDPOINT.PROJECTAPI)
    Call<JsonObject> project_api(@Body JsonObject jsonObject);

    @POST(AppConstants.ENDPOINT.PROJECT_ACTIVITY_API)
    Call<JsonObject> project_activity_api(@Body JsonObject jsonObject);

    @POST(AppConstants.ENDPOINT.PROJECT_TRANSIT_API)
    Call<JsonObject> project_transit_api(@Body JsonObject jsonObject);

    @POST(AppConstants.ENDPOINT.PROJECT_SHOP_DETAIL_API)
    Call<JsonObject> project_shop_detail_api(@Body JsonObject jsonObject);

    @POST(AppConstants.ENDPOINT.PROJECT_TRANSIT_DETAIL_API)
    Call<JsonObject> project_transit_detail_api(@Body JsonObject jsonObject);

    @POST(AppConstants.ENDPOINT.REGISTER_LOG_API)
    Call<JsonObject> register_log_api(@Body JsonObject jsonObject);

    @POST(AppConstants.ENDPOINT.PROJECT_PRINT_DETAIL_API)
    Call<JsonObject> project_print_detail_api(@Body JsonObject jsonObject);

    @POST(AppConstants.ENDPOINT.PROJECT_HOARDINGS_API)
    Call<JsonObject> project_hoardings_api(@Body JsonObject jsonObject);

    @POST(AppConstants.ENDPOINT.PROJECT_WALL_API)
    Call<JsonObject> project_wall_api(@Body JsonObject jsonObject);

    @POST(AppConstants.ENDPOINT.PROJECT_EMEDIA_API)
    Call<JsonObject> project_emedia_api(@Body JsonObject jsonObject);

    @POST(AppConstants.ENDPOINT.USER_CHAT_API)
    Call<JsonObject> user_chat_api(@Body JsonObject jsonObject);

    @POST(AppConstants.ENDPOINT.NEW_PROJECT_API)
    Call<JsonObject> new_project_api(@Body JsonObject jsonObject);


}
