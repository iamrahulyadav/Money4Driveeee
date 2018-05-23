package com.hvantage2.money4driveeee.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.adapter.SingleMessageAdapter;
import com.hvantage2.money4driveeee.customview.CustomEditText;
import com.hvantage2.money4driveeee.model.MessageData;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.Functions;
import com.hvantage2.money4driveeee.util.ProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private RecyclerView recyclerView;
    private SingleMessageAdapter adapter;
    private ArrayList<MessageData> list;
    private ProgressHUD progressHD;
    private String project_id = "";
    private CustomEditText etNewMsg;
    private ImageView btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setSubtitle("Rk, Ashu");
        init();
        setMessageRecycler();
        LocalBroadcastManager.getInstance(this).registerReceiver(new MsgReciever(), new IntentFilter("get_msg"));
        if (getIntent().hasExtra("project_title"))
            getSupportActionBar().setTitle(getIntent().getStringExtra("project_title"));
        if (getIntent().hasExtra("project_subtitle"))
            getSupportActionBar().setSubtitle(getIntent().getStringExtra("project_subtitle"));
        if (getIntent().hasExtra("project_id")) {
            project_id = getIntent().getStringExtra("project_id");
            getAllChat();
        }
    }

    private void init() {
        etNewMsg = (CustomEditText) findViewById(R.id.etNewMsg);
        btnSend = (ImageView) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNewMsg();
            }
        });
    }

    private void setMessageRecycler() {
        list = new ArrayList<MessageData>();
        recyclerView = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        adapter = new SingleMessageAdapter(this, list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.chat_menus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAllChat() {
        if (Functions.isConnectingToInternet(this)) {
//            tvEmpty.setVisibility(View.GONE);
            new getAllChatTask().execute();
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
        }
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

    private void sendNewMsg() {
        if (!Functions.isConnectingToInternet(this))
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show();
        else if (TextUtils.isEmpty(etNewMsg.getText().toString()))
            Toast.makeText(this, "Write your message", Toast.LENGTH_LONG).show();
        else
            new sendNewMsgTask().execute(etNewMsg.getText().toString());
    }

    class MsgReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MessageData new_msg = (MessageData) intent.getSerializableExtra("new_msg");
            Log.e(TAG, "onReceive: new_msg >> " + new_msg);
            if (list != null && adapter != null) {
                list.add(new_msg);
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
            }
        }
    }

    public class getAllChatTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.GETPROJECTMSGS);
            jsonObject.addProperty("user_id", AppPreference.getUserId(ChatActivity.this)); //8
            jsonObject.addProperty("project_id", project_id);
            Log.e(TAG, "Request GET ALL PROJECTS >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.user_chat_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response GET ALL PROJECTS >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                MessageData data = new Gson().fromJson(jsonArray.getJSONObject(i).toString(), MessageData.class);
                                list.add(data);
                            }
                            publishProgress("200", "");

                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            list.clear();
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            publishProgress("400", msg);
                        }
                    } catch (JSONException e) {
                        list.clear();
                        e.printStackTrace();
                        publishProgress("400", getResources().getString(R.string.api_error_msg));
                    }
                }

                @SuppressLint("LongLogTag")
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    list.clear();
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
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
            if (status.equalsIgnoreCase("400")) {
            }
          /*  if (adapter != null) {
                if (adapter.getItemCount() == 0)
                    tvEmpty.setVisibility(View.VISIBLE);
                else
                    tvEmpty.setVisibility(View.GONE);
            }*/
        }
    }

    public class sendNewMsgTask extends AsyncTask<String, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(String... values) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.SENDMSGTOPROJECT);
            jsonObject.addProperty("msg_sender_id", AppPreference.getUserId(ChatActivity.this)); //8
            jsonObject.addProperty("project_id", project_id);
            jsonObject.addProperty("msg_text", values[0]);
            Log.e(TAG, "Request SEND_MSG >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.user_chat_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response SEND_MSG >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            etNewMsg.setText("");
                            JSONObject jsonObject1 = jsonObject.getJSONArray("result").getJSONObject(0);
                            MessageData data = new Gson().fromJson(jsonObject1.toString(), MessageData.class);
                            list.add(data);
                            publishProgress("200", "");

                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            publishProgress("400", msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        publishProgress("400", getResources().getString(R.string.api_error_msg));
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
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
            }
          /*  if (adapter != null) {
                if (adapter.getItemCount() == 0)
                    tvEmpty.setVisibility(View.VISIBLE);
                else
                    tvEmpty.setVisibility(View.GONE);
            }*/
        }
    }

}
