package com.hvantage2.money4driveeee.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.activity.ProjectDetailsActivity;
import com.hvantage2.money4driveeee.adapter.ProjectListAdapter;

import com.hvantage2.money4driveeee.database.DBHelper;
import com.hvantage2.money4driveeee.model.ProjectModel;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.FragmentIntraction;
import com.hvantage2.money4driveeee.util.Functions;
import com.hvantage2.money4driveeee.util.ProgressHUD;
import com.hvantage2.money4driveeee.util.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Hvantage2 on 2018-02-21.
 */

public class HistoryCompletedFragment extends Fragment {

    private static final String TAG = "CompletedFragment";
    Context context;
    TextView tvEmpty;
    RecyclerView recyclerView;
    List<ProjectModel> list;
    ProjectListAdapter adapter;
    FragmentIntraction intraction;
    private View rootview;
    private ProgressHUD progressHD;
    private DBHelper db;
    private SwipeRefreshLayout refreshLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_complete, container, false);
        init(rootview);
        context = getActivity();
        if (intraction != null) {
            intraction.actionbarsetTitle("Project History");
        }
        db = new DBHelper(context);
        list = new ArrayList<ProjectModel>();
        init(rootview);
        if (db.getProjects(AppConstants.PROJECT_TYPE_IDS.COMPLETED_ID) != null) {
            list = db.getProjects(AppConstants.PROJECT_TYPE_IDS.COMPLETED_ID);
            Log.e(TAG, "onCreateView: list >> " + list);
            setAdapter();

        } else
            getProjectsFromServer();
        setAdapter();
        return rootview;
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

    private void init(View rootview) {
        context = getActivity();
        recyclerView = (RecyclerView) rootview.findViewById(R.id.recycler_view);
        tvEmpty = (TextView) rootview.findViewById(R.id.tvEmpty);
        refreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getProjectsFromServer();
            }
        });
    }

    private void setAdapter() {
        RecyclerView.LayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        adapter = new ProjectListAdapter(context, list);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ProjectModel data = list.get(position);
                Log.e(TAG, "onItemClick: project detail >> " + data);
                AppPreference.setSelectedProjectType(getActivity(), AppConstants.PROJECT_TYPE.COMPLETED);
                AppPreference.setSelectedProjectId(context, data.getProjectId());
                Intent intent = new Intent(context, ProjectDetailsActivity.class);
                intent.putExtra("project_id", data.getProjectId());
                context.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
    }

    private void getProjectsFromServer() {
        if (Functions.isConnectingToInternet(getActivity())) {
            Log.e(TAG, "getProjectsFromServer: deleteProjects() >> " + db.deleteProjects(AppConstants.PROJECT_TYPE_IDS.COMPLETED_ID));
            tvEmpty.setVisibility(View.GONE);
            new ServerTask().execute();
        } else {
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    private void showProgressDialog() {
        progressHD = ProgressHUD.show(context, "Processing...", true, false, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void hideProgressDialog() {
        progressHD.dismiss();
    }

    public class ServerTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            refreshLayout.setRefreshing(false);
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.COMPLETEPROJECTS);
            jsonObject.addProperty("user_id", AppPreference.getUserId(context)); //8
            jsonObject.addProperty(AppConstants.KEYS.LOGIN_TYPE_ID, AppPreference.getUserTypeId(context)); //8

            Log.e(TAG, "ServerTask: Request >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "ServerTask: Response >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        list.clear();
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Gson gson = new Gson();
                                ProjectModel data = gson.fromJson(jsonArray.getJSONObject(i).toString(), ProjectModel.class);
                                list.add(data);
                                // insert into database
                                db.saveProject(data, AppConstants.PROJECT_TYPE_IDS.COMPLETED_ID);
                            }
                            publishProgress("200", "");

                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            publishProgress("400", msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        publishProgress("400", getActivity().getResources().getString(R.string.api_error_msg));
                    }
                }

                @SuppressLint("LongLogTag")
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e(TAG, "error :- " + Log.getStackTraceString(t));
                    publishProgress("400", getActivity().getResources().getString(R.string.api_error_msg));
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            hideProgressDialog();
            adapter.notifyDataSetChanged();
            String status = values[0];
            String msg = values[1];
            if (status.equalsIgnoreCase("400")) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
            if (adapter.getItemCount() == 0)
                tvEmpty.setVisibility(View.VISIBLE);
            else
                tvEmpty.setVisibility(View.GONE);
        }
    }
}
