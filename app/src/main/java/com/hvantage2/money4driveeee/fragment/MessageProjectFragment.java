package com.hvantage2.money4driveeee.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.activity.ChatActivity;
import com.hvantage2.money4driveeee.activity.SelectProjectForChatActivity;
import com.hvantage2.money4driveeee.activity.SelectUserForChatActivity;
import com.hvantage2.money4driveeee.adapter.SelectProjectAdapter;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageProjectFragment extends Fragment {
    private static final String TAG = "MessageProjectFragment";
    Context context;
    RecyclerView recyclerView;
    FragmentIntraction intraction;
    private View rootview;
    private RelativeLayout create_new;
    private FloatingActionMenu floatingActionMenu;
    private ArrayList<ProjectModel> list;
    private SelectProjectAdapter adapter;
    private ProgressHUD progressHD;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_messages, container, false);
        setHasOptionsMenu(true);
        if (intraction != null) {
            intraction.actionbarsetTitle("Messages");
        }

        init(rootview);
        setFloatingButton();
        setAdapter();
        getAllProject();
        return rootview;
    }

    private void setAdapter() {
        list = new ArrayList<ProjectModel>();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        adapter = new SelectProjectAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("project_title", list.get(position).getProjectTitle());
                intent.putExtra("project_subtitle", list.get(position).getProjectDesc());
                intent.putExtra("project_id", list.get(position).getProjectId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));
    }

    private void getAllProject() {
        if (Functions.isConnectingToInternet(getActivity())) {
//            tvEmpty.setVisibility(View.GONE);
            new getAllProjectTask().execute();
        } else {
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_LONG).show();
        }
    }

    private void showProgressDialog() {
        progressHD = ProgressHUD.show(getActivity(), "Processing...", true, false, new DialogInterface.OnCancelListener() {
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

    private void setFloatingButton() {
        new FloatingButton().showFloatingButton(rootview, context);
        new FloatingButton().setFloatingButtonControls(rootview);
    }

    private void createCustomAnimation() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(floatingActionMenu.getMenuIconView(), "scaleX", 1.0f, 0.2f);
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(floatingActionMenu.getMenuIconView(), "scaleY", 1.0f, 0.2f);
        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(floatingActionMenu.getMenuIconView(), "scaleX", 0.2f, 1.0f);
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(floatingActionMenu.getMenuIconView(), "scaleY", 0.2f, 1.0f);

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                floatingActionMenu.getMenuIconView().setImageResource(floatingActionMenu.isOpened()
                        ? R.drawable.fab_back : R.drawable.fab_plus);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        floatingActionMenu.setIconToggleAnimatorSet(set);

    }

    private void init(View rootview) {
        context = getActivity();
        recyclerView = (RecyclerView) rootview.findViewById(R.id.recycler_view);
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

    public class getAllProjectTask extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.USERALLPROJECTLISTDATA);
            jsonObject.addProperty("user_id", AppPreference.getUserId(getActivity())); //8
            jsonObject.addProperty("user_type", AppPreference.getUserTypeId(getActivity()));
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
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                ProjectModel model = new Gson().fromJson(jsonObject1.toString(), ProjectModel.class);



                                /*ProjectModel model = new ProjectModel();
                                model.setProject_id(jsonObject1.getString("project_id"));
                                model.setProjectTittle(jsonObject1.getString("project_title"));*/

                                String survey_user_name = jsonObject1.getString("survey_user_name");
                                String execution_user_name = jsonObject1.getString("execution_user_name");
                                String data_user_name = jsonObject1.getString("data_user_name");
                                String sales_user_name = jsonObject1.getString("sales_user_name");
                                ArrayList<String> group_list = new ArrayList<String>();
                                if (!survey_user_name.equalsIgnoreCase(""))
                                    group_list.add(survey_user_name);
                                if (!execution_user_name.equalsIgnoreCase(""))
                                    group_list.add(execution_user_name);
                                if (!data_user_name.equalsIgnoreCase(""))
                                    group_list.add(data_user_name);
                                if (!sales_user_name.equalsIgnoreCase(""))
                                    group_list.add(sales_user_name);

                                String groupnames = TextUtils.join(", ", group_list);
                                Log.e(TAG, "onResponse: " + groupnames);
                                model.setProjectDesc(groupnames);
                                list.add(model);
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
            if (status.equalsIgnoreCase("400")) {
            }
            /*if (adapter != null) {
                if (adapter.getItemCount() == 0)
                    tvEmpty.setVisibility(View.VISIBLE);
                else
                    tvEmpty.setVisibility(View.GONE);
            }*/
        }
    }

    public class FloatingButton {
        FrameLayout bckgroundDimmer;
        com.github.clans.fab.FloatingActionButton button1, button2;

        public void showFloatingButton(final View activity, final Context mContext) {
            floatingActionMenu = (FloatingActionMenu) activity.findViewById(R.id.material_design_android_floating_action_menu);
            button1 = (com.github.clans.fab.FloatingActionButton) activity.findViewById(R.id.material_design_floating_action_menu_item1);
            button2 = (com.github.clans.fab.FloatingActionButton) activity.findViewById(R.id.material_design_floating_action_menu_item2);
            createCustomAnimation();

            button1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    floatingActionMenu.close(true);
                    startActivity(new Intent(getActivity(), SelectUserForChatActivity.class));
                }
            });
            button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    floatingActionMenu.close(true);
                    startActivity(new Intent(getActivity(), SelectProjectForChatActivity.class));
                }
            });
        }

        public void setFloatingButtonControls(View activity) {
            bckgroundDimmer = (FrameLayout) activity.findViewById(R.id.background_dimmer);
            floatingActionMenu = (FloatingActionMenu) activity.findViewById(R.id.material_design_android_floating_action_menu);
            floatingActionMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
                @Override
                public void onMenuToggle(boolean opened) {
                    if (opened) {
                        bckgroundDimmer.setVisibility(View.VISIBLE);
                    } else {
                        bckgroundDimmer.setVisibility(View.GONE);
                    }
                }
            });
            bckgroundDimmer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (floatingActionMenu.isOpened()) {
                        floatingActionMenu.close(true);
                        bckgroundDimmer.setVisibility(View.GONE);
                        //menu opened
                    }
                }
            });
        }
    }
}
