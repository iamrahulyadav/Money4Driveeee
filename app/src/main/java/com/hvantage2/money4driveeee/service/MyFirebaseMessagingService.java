package com.hvantage2.money4driveeee.service;

import android.annotation.SuppressLint;
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
import com.hvantage2.money4driveeee.activity.DashBoardActivity;
import com.hvantage2.money4driveeee.activity.ProjectDetailsActivity;
import com.hvantage2.money4driveeee.database.DBHelper;
import com.hvantage2.money4driveeee.model.MessageData;
import com.hvantage2.money4driveeee.model.ProjectModel;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMessagingService";
    private DBHelper mydb;

    @SuppressLint("LongLogTag")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("Notification Data : ", remoteMessage.getData().toString());
        JSONObject jsonObject = null;
        mydb = new DBHelper(MyFirebaseMessagingService.this);
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
            }

            if (remoteMessage.getData().size() > 0) {
                String NEW_PROJECT_ASSIGNED = remoteMessage.getData().get(AppConstants.NOTIFICATION_KEY.NEW_PROJECT_ASSIGNED);
                String PROJECT_STATUS_CHANGED = remoteMessage.getData().get(AppConstants.NOTIFICATION_KEY.PROJECT_STATUS_CHANGED);
                String NEW_MSG_ARRIVED = remoteMessage.getData().get(AppConstants.NOTIFICATION_KEY.NEW_MSG_ARRIVED);
                String MSG_DATA = remoteMessage.getData().get(AppConstants.NOTIFICATION_KEY.MSG_DATA);
                if (NEW_PROJECT_ASSIGNED != null) {
                    ProjectModel projectModel = new Gson().fromJson(NEW_PROJECT_ASSIGNED, ProjectModel.class);
                    sendNotification("New Project Assigned", projectModel.getProjectTitle(), projectModel.getProjectId());
                    Log.e(TAG, "onMessageReceived: projectModel >> " + projectModel);
                    if (!mydb.isProjectExist(projectModel.getProjectId())) {
                        mydb.saveProject(projectModel, AppConstants.PROJECT_TYPE_IDS.PENDING_ID);
                    } else
                        Log.e(TAG, "onMessageReceived: project already exists");
                    LocalBroadcastManager.getInstance(MyFirebaseMessagingService.this).sendBroadcast(new Intent("get_update"));
                } else if (PROJECT_STATUS_CHANGED != null) {
                    JSONObject jsonObject1 = null;
                    jsonObject1 = new JSONObject(PROJECT_STATUS_CHANGED);
                    String project_id = jsonObject1.getString("project_id");
                    String project_title = jsonObject1.getString("project_title");
                    String status_id = jsonObject1.getString("status");
                    if (status_id.equalsIgnoreCase("1"))
                        sendNotification("Status Changed: Pending", project_title, project_id);
                    else if (status_id.equalsIgnoreCase("2"))
                        sendNotification("Status Changed: Completed", project_title, project_id);
                    mydb.updateProjectStatus(project_id, status_id);
                } else if (MSG_DATA != null) {
                    JSONObject jsonObject1 = null;
                    jsonObject1 = new JSONObject(MSG_DATA);
                    MessageData data = new Gson().fromJson(jsonObject1.toString(), MessageData.class);
                    sendMsgNotification(data.getMsgSenderName() + ": " + data.getMsgText());
                    Intent intent = new Intent("get_msg");
                    intent.putExtra("new_msg", data);
                    LocalBroadcastManager.getInstance(MyFirebaseMessagingService.this).sendBroadcast(intent);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(String not_title, String not_body, String project_id) {
        try {
            AppPreference.setSelectedProjectId(MyFirebaseMessagingService.this, project_id);
            AppPreference.setSelectedProjectType(MyFirebaseMessagingService.this, AppConstants.PROJECT_TYPE.PENDING);

            Intent intent = new Intent(this, ProjectDetailsActivity.class);
            intent.putExtra("project_id", project_id);
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

    private void sendMsgNotification(String body) {
        try {

            Intent intent = new Intent(this, DashBoardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_notification_roboto)
                    .setContentTitle("New Message")
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                    .setSound(defaultSoundUri)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, notificationBuilder.build());
        } catch (Exception e) {
            Log.e("Notification Ex", e.getMessage());
        }
    }


}
