package com.hvantage2.money4driveeee.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Hvantage2 on 2018-02-21.
 */

public class AppPreference {
    private static final String INSTALLED_STATUS ="installed_status" ;
    private static final String LOGGED_IN ="logged_in" ;
    private static final String USER_TYPE = "user_type";
    private static final String USER_TYPE_ID ="user_type_id" ;
    private static final String VEHICLE_NAME = "VEHICLE_NAME";
    private static final String MANAGER_CONTACT_NO = "manager_contact_no";
    public static Context appContext;
    private static String LOGIN_PREFERENCE;
    public static final String USERDETAILS = "userdetails";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String PROJECT_ID = "selected_project_id";
    public static final String SHOP_ID = "selected_shop_id";
    public static final String SHOP_NAME = "selected_shop_name";
    private static final String ALLOCATION_MEDIA_ID ="selected_allocation_media_id" ;
    private static final String VEHICLE_ID ="selected_vehicle_id" ;
    private static final String PMEDIA_ID ="selected_pmedia_id" ;
    private static final String PMEDIA_NAME ="selected_pmedia_name" ;
    private static final String EMEDIA_ID ="selected_emedia_id" ;
    private static final String EMEDIA_NAME ="selected_emedia_name" ;
    private static final String HOARDING_ID ="selected_hoarding_id" ;
    private static final String HOARDING_NAME ="selected_hoarding_name" ;
    private static final String WALL_ID ="selected_wall_id" ;
    private static final String WALL_NAME ="selected_wall_name" ;
    private static final String PROJECT_TYPE ="selected_project_type" ;


    public static void setStringPreference(Context context, String name, String value) {
        appContext = context;
        SharedPreferences settings = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public static String getStringPreferences(Context context, String user) {
        SharedPreferences settings = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return settings.getString(user, "");
    }

    public static void setInstalled(Context context, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(INSTALLED_STATUS, value);
        editor.commit();
    }

    public static boolean isInstalled(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getBoolean(INSTALLED_STATUS, false);
    }

    public static void setLoggedIn(Context context, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(LOGGED_IN, value);
        editor.commit();
    }

    public static boolean isLoggedIn(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getBoolean(LOGGED_IN, false);
    }

    public static void clearPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public static void setUserData(Context context, String userdata) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USERDETAILS, userdata);
        editor.commit();

    }

    public static String getUserData(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(USERDETAILS, "");
    }

    public static void setUserType(Context context, String userdata) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_TYPE, userdata);
        editor.commit();

    }

    public static String getUserType(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(USER_TYPE, "");
    }

    public static void setUserTypeId(Context context, String userdata) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_TYPE_ID, userdata);
        editor.commit();

    }

    public static String getUserTypeId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(USER_TYPE_ID, "");
    }

    public static void setUserId(Context context, String userid) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_ID, userid);
        editor.commit();

    }

    public static String getUserId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(USER_ID, "");
    }

    public static void setUserName(Context context, String userid) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(USER_NAME, userid);
        editor.commit();

    }

    public static String getUserName(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(USER_NAME, "");
    }

    public static void setSelectedProjectId(Context context, String projectId) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PROJECT_ID, projectId);
        editor.commit();

    }

    public static String getSelectedProjectId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(PROJECT_ID, "");
    }

    public static void setSelectedShopId(Context context, String shopid) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SHOP_ID, shopid);
        editor.commit();

    }

    public static String getSelectedShopid(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(SHOP_ID, "");
    }

    public static void setSelectedShopName(Context context, String shopid) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SHOP_NAME, shopid);
        editor.commit();

    }

    public static String getSelectedShopName(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(SHOP_NAME, "");
    }

    public static void setSelectedAlloMediaId(Context context, String shopid) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ALLOCATION_MEDIA_ID, shopid);
        editor.commit();

    }

    public static String getSelectedAlloMediaId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(ALLOCATION_MEDIA_ID, "");
    }

    public static void setSelectedVehicleId(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(VEHICLE_ID, value);
        editor.commit();

    }

    public static String getSelectedVehicleId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(VEHICLE_ID, "");
    }

    public static void setSelectedVehicleName(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(VEHICLE_NAME, value);
        editor.commit();

    }

    public static String getSelectedVehicleName(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(VEHICLE_NAME, "");
    }

    public static void setSelectedPMediaId(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PMEDIA_ID, value);
        editor.commit();

    }

    public static String getSelectedPMediaId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(PMEDIA_ID, "");
    }

    public static void setSelectedPMediaName(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PMEDIA_NAME, value);
        editor.commit();

    }

    public static String getSelectedPMediaName(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(PMEDIA_NAME, "");
    }

    public static void setSelectedEMediaId(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(EMEDIA_ID, value);
        editor.commit();

    }

    public static String getSelectedEMediaId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(EMEDIA_ID, "");
    }

    public static void setSelectedEMediaName(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(EMEDIA_NAME, value);
        editor.commit();

    }

    public static String getSelectedEMediaName(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(EMEDIA_NAME, "");
    }

    public static void setSelectedHoardingId(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(HOARDING_ID, value);
        editor.commit();
    }

    public static String getSelectedHoardingId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(HOARDING_ID, "");
    }

    public static void setSelectedHoardingName(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(HOARDING_NAME, value);
        editor.commit();
    }

    public static String getSelectedHoardingName(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(HOARDING_NAME, "");
    }

    public static void setSelectedWallId(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(WALL_ID, value);
        editor.commit();
    }

    public static String getSelectedWallId(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(WALL_ID, "");
    }

    public static void setSelectedWallName(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(WALL_NAME, value);
        editor.commit();
    }

    public static String getSelectedWallName(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(WALL_NAME, "");
    }

    public static void setManagerContactNo(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MANAGER_CONTACT_NO, value);
        editor.commit();

    }

    public static String getManagerContactNo(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(MANAGER_CONTACT_NO, "");
    }

    public static void setSelectedProjectType(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PROJECT_TYPE, value);
        editor.commit();
    }

    public static String getSelectedProjectType(Context context) {
        SharedPreferences pereference = context.getSharedPreferences(LOGIN_PREFERENCE, 0);
        return pereference.getString(PROJECT_TYPE, "");
    }

}
