package com.hvantage2.money4driveeee.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.activity.ProjectDetailsActivity;
import com.hvantage2.money4driveeee.model.MessageData;
import com.hvantage2.money4driveeee.model.ProjectModel;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("Notification Data : ", remoteMessage.getData().toString());
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(remoteMessage.getData().toString());
            if (jsonObject.has("notification_data")) {
                JSONObject jsonObject1 = jsonObject.getJSONObject("notification_data");
                String not_body = jsonObject1.getString("not_body");
                String not_title = jsonObject1.getString("not_title");
                String project_id = jsonObject1.getString("project_id");
                sendNotification(not_title, not_body, project_id);
                //send to dashboard screen
                LocalBroadcastManager.getInstance(MyFirebaseMessagingService.this).sendBroadcast(new Intent("get_update"));
            } else if (jsonObject.has("msg_data")) {
                JSONObject jsonObject1 = jsonObject.getJSONObject("msg_data");
                MessageData data = new Gson().fromJson(jsonObject1.toString(), MessageData.class);
                Intent intent = new Intent("get_msg");
                intent.putExtra("new_msg", data);
                LocalBroadcastManager.getInstance(MyFirebaseMessagingService.this).sendBroadcast(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(String not_title, String not_body, String project_id) {
        try {
            AppPreference.setSelectedProjectId(MyFirebaseMessagingService.this, project_id);
            AppPreference.setSelectedProjectType(MyFirebaseMessagingService.this, AppConstants.PROJECT_TYPE.PENDING);

            ProjectModel modal = new ProjectModel();
            modal.setProject_id(project_id);
            Intent intent = new Intent(this, ProjectDetailsActivity.class);
            intent.putExtra("messageModal", modal);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_notification_roboto)
                    .setContentTitle(not_title)
                    .setContentText(not_body)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setSound(defaultSoundUri)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Integer.parseInt(project_id), notificationBuilder.build());
        } catch (Exception e) {
            Log.e("Notification Ex", e.getMessage());
        }
    }


}
