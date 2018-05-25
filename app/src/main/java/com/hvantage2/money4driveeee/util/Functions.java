package com.hvantage2.money4driveeee.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Hvantage2 on 2018-02-26.
 */

public class Functions {
    public static EditText EditTextPointer;
    public static String errorMessage;

    public static boolean isEmailValid(EditText tv) {
        //add your own logic
        if (TextUtils.isEmpty(tv.getText())) {
            EditTextPointer = tv;
            errorMessage = "This field can't be empty.!";
            return false;
        } else {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(tv.getText()).matches()) {
                return true;
            } else {
                EditTextPointer = tv;
                errorMessage = "Invalid Email-Id";
                return false;
            }
        }
    }
    public static final boolean isValidPhoneNumber(String mobno)
    {
        Boolean isValid=false;
        if(mobno.trim().length()<10)
        {
            isValid =false;
        }
        if(mobno.trim().length()==10)
        {
            Pattern pattern;
            Matcher matcher;
            final String MOBILE_PATTERN = "^[7-9][0-9]{9}$";
            pattern = Pattern.compile(MOBILE_PATTERN);
            matcher = pattern.matcher(mobno);
            boolean isMatch = matcher.matches();
            if (isMatch) {
                isValid=true;
            } else {
                isValid=false;
            }
        }
        return isValid;
    }
    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }

    public static String getCurrentTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        String strDate = sdf.format(c.getTime());
        return strDate;
    }
    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
        }
        return false;
    }

    public static String loadJSONFromAsset(Context context, String assetName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(assetName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
