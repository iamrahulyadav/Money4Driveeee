package com.hvantage2.money4driveeee.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.activity.ProjectDetailsActivity;
import com.hvantage2.money4driveeee.adapter.ProjectHistoryAdapter;
import com.hvantage2.money4driveeee.model.ProjectModel;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.FragmentIntraction;
import com.hvantage2.money4driveeee.util.Functions;
import com.hvantage2.money4driveeee.util.RecyclerItemClickListener;
import com.hvantage2.money4driveeee.customview.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingFragment extends Fragment {
    private static final String TAG = "PendingFragment";
    private View rootview;
    Context context;
    ProgressDialog dialog;
    CustomTextView tvEmpty;
    RecyclerView recyclerView;
    List<ProjectModel> projectModelList;
    ProjectHistoryAdapter historyAdapter;
    FragmentIntraction intraction;
    private ShimmerFrameLayout container1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_complete, container, false);
        init(rootview);
        context = getActivity();
        if (intraction != null) {
            intraction.actionbarsetTitle("Pending Projects");
        }

        setAdapter();
        getAllPending();

        return rootview;
    }

    private void init(View rootview) {
        context = getActivity();
        recyclerView = (RecyclerView) rootview.findViewById(R.id.recycler_view);
        tvEmpty = (CustomTextView) rootview.findViewById(R.id.tvEmpty);
        container1 = (ShimmerFrameLayout)rootview. findViewById(R.id.shimmer_view_container);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void startAnimation() {
        container1.setVisibility(View.VISIBLE);
        container1.startShimmerAnimation();
    }


    private void stopAnimation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                container1.stopShimmerAnimation();
                container1.setVisibility(View.GONE);
            }
        }, 2000);

    }

    private void setAdapter() {
        projectModelList = new ArrayList<ProjectModel>();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        historyAdapter = new ProjectHistoryAdapter(context, projectModelList);
        recyclerView.setAdapter(historyAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ProjectModel messageModel = projectModelList.get(position);
                Log.e(TAG, "onItemClick: project detail >> "+messageModel );
                AppPreference.setSelectedProjectId(context, messageModel.getProject_id());
                AppPreference.setSelectedProjectType(getActivity(),AppConstants.PROJECT_TYPE.PENDING);
                Intent intent =new Intent(context,ProjectDetailsActivity.class);
                intent.putExtra("messageModal", messageModel);
                context.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

    }

    private void getAllPending() {
        if (Functions.isConnectingToInternet(getActivity())) {
            tvEmpty.setVisibility(View.GONE);
            new getAllPendingTask().execute();
        } else {
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    public class getAllPendingTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startAnimation();
            //showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.PENDINGPROJECTS);
            jsonObject.addProperty("user_id", AppPreference.getUserId(context)); //8
            jsonObject.addProperty(AppConstants.KEYS.LOGIN_TYPE_ID, AppPreference.getUserTypeId(context));
            Log.e(TAG, "Request GET ALL PENDING >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.project_api(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response GET ALL PENDING >> " + response.body().toString());
                    String resp = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        projectModelList.clear();
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ProjectModel projectModel = new ProjectModel();
                                JSONObject object = jsonArray.getJSONObject(i);
                                projectModel.setProject_id(object.getString("project_id"));
                                projectModel.setProjectTittle(object.getString("project_title"));
                                projectModel.setProjectCity(object.getString("city"));
                                projectModel.setProjectDate(object.getString("created_date"));
                                projectModel.setProject_desc(object.getString("project_desc"));
                                projectModelList.add(projectModel);
                            }
                            publishProgress("200", "");

                        } else if (jsonObject.getString("status").equalsIgnoreCase("400")) {
                            String msg = jsonObject.getJSONArray("result").getJSONObject(0).getString("msg");
                            projectModelList.clear();
                            publishProgress("400", msg);
                        }
                        historyAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        projectModelList.clear();
                        historyAdapter.notifyDataSetChanged();
                        publishProgress("400", getActivity().getResources().getString(R.string.api_error_msg));
                    }
                }

                @SuppressLint("LongLogTag")
                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e(TAG, "error :- " + Log.getStackTraceString(t));
                    projectModelList.clear();
                    historyAdapter.notifyDataSetChanged();
                    publishProgress("400", getActivity().getResources().getString(R.string.api_error_msg));
                }
            });
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //hideProgressDialog();
            stopAnimation();
            String status=values[0];
            String msg=values[1];
            if(status.equalsIgnoreCase("400"))
            {
            }
            if (historyAdapter != null) {
                if (historyAdapter.getItemCount() == 0)
                    tvEmpty.setVisibility(View.VISIBLE);
                else
                    tvEmpty.setVisibility(View.GONE);
            }
        }



        /*private void publishProgress(int status, String msg) {
            hideProgressDialog();
            if (status == 200) {

            } else if (status == 400) {
                //Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
            if (historyAdapter != null) {
                if (historyAdapter.getItemCount() == 0)
                    tvNoOrders.setVisibility(View.VISIBLE);
                else
                    tvNoOrders.setVisibility(View.GONE);
            }
        }*/
    }

    private void hideProgressDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    private void showProgressDialog() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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



}

