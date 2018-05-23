package com.hvantage2.money4driveeee.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.adapter.MessageAdapter;
import com.hvantage2.money4driveeee.customview.CustomButton;
import com.hvantage2.money4driveeee.customview.CustomTextView;
import com.hvantage2.money4driveeee.model.MessageModel;
import com.hvantage2.money4driveeee.retrofit.ApiClient;
import com.hvantage2.money4driveeee.retrofit.MyApiEndpointInterface;
import com.hvantage2.money4driveeee.util.AppConstants;
import com.hvantage2.money4driveeee.util.AppPreference;
import com.hvantage2.money4driveeee.util.FragmentIntraction;
import com.hvantage2.money4driveeee.util.Functions;
import com.hvantage2.money4driveeee.util.ProgressHUD;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "HomeFragment";
    Context context;
    RecyclerView recyclerView;
    List<MessageModel> msgList;
    MessageAdapter messageAdapter;
    ScrollView scroll;
    CustomTextView totalProjCount, newProjCount, completeProjCount, pendingProjectCount;
    FragmentIntraction intraction;
    private View rootView;
    private ProgressDialog dialog;
    private ProgressHUD progressHD;
    private CustomButton callmanager;
    private CustomButton btnMessage;
    private FloatingActionMenu floatingActionMenu;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = container.getContext();
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        if (intraction != null) {
            if (!AppPreference.getUserName(getActivity()).equalsIgnoreCase(""))
                intraction.actionbarsetTitle("Dashboard - " + AppPreference.getUserName(getActivity()));
            else
                intraction.actionbarsetTitle("Dashboard");
        }
        init();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new MyReciever(), new IntentFilter("get_update"));
        getDashboard();
        setFloatingButton();
        setMessageRecycler();
        return rootView;
    }

    private void getDashboard() {
        if (Functions.isConnectingToInternet(context))
            new getAllProjects().execute();
        else {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "No internet connection", Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDashboard();
                }
            }).show();
        }
    }

    private void setMessageRecycler() {
        msgList = new ArrayList<MessageModel>();
        for (int i = 0; i < 2; i++) {
            MessageModel projectModel = new MessageModel("01", "Hi.. how r u?", "James Hopess", Functions.getCurrentDate());
            msgList.add(projectModel);
        }

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        messageAdapter = new MessageAdapter(context, msgList);
        recyclerView.setAdapter(messageAdapter);
    }

    private void setFloatingButton() {
        new FloatingButton().showFloatingButton(rootView, context);
        new FloatingButton().setFloatingButtonControls(rootView);


    }

    private void init() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        scroll = (ScrollView) rootView.findViewById(R.id.scrollView);
        totalProjCount = (CustomTextView) rootView.findViewById(R.id.totalProjCount);
        newProjCount = (CustomTextView) rootView.findViewById(R.id.newProjCount);
        completeProjCount = (CustomTextView) rootView.findViewById(R.id.completeProjCount);
        pendingProjectCount = (CustomTextView) rootView.findViewById(R.id.pendingProjectCount);
        callmanager = (CustomButton) rootView.findViewById(R.id.callmanager);
        btnMessage = (CustomButton) rootView.findViewById(R.id.btnMessage);

        callmanager.setOnClickListener(this);
        btnMessage.setOnClickListener(this);
        ((RelativeLayout) rootView.findViewById(R.id.totalProjects)).setOnClickListener(this);
        ((RelativeLayout) rootView.findViewById(R.id.competedProjects)).setOnClickListener(this);
        ((RelativeLayout) rootView.findViewById(R.id.newAssignedProjects)).setOnClickListener(this);
        ((RelativeLayout) rootView.findViewById(R.id.pendingProjects)).setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.totalProjects:
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ProjectHistoryFragment llf = new ProjectHistoryFragment();
                ft.replace(R.id.main_frame, llf);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.competedProjects:
                FragmentManager fm4 = getFragmentManager();
                FragmentTransaction ft4 = fm4.beginTransaction();
                CompletedFragment llf4 = new CompletedFragment();
                ft4.replace(R.id.main_frame, llf4);
                ft4.addToBackStack(null);
                ft4.commit();
                break;
            case R.id.newAssignedProjects:
                FragmentManager fm1 = getFragmentManager();
                FragmentTransaction ft1 = fm1.beginTransaction();
                Pending2Fragment llf1 = new Pending2Fragment();
                ft1.replace(R.id.main_frame, llf1);
                ft1.addToBackStack(null);
                ft1.commit();
                break;
            case R.id.pendingProjects:
                FragmentManager fm5 = getFragmentManager();
                FragmentTransaction ft5 = fm5.beginTransaction();
                PendingFragment llf5 = new PendingFragment();
                ft5.replace(R.id.main_frame, llf5);
                ft5.addToBackStack(null);
                ft5.commit();
                break;
            case R.id.callmanager:
                Intent intent = new Intent(Intent.ACTION_CALL);
                Log.e(TAG, "onClick: manager_contact_no >> " + AppPreference.getManagerContactNo(getActivity()));
                if (!AppPreference.getManagerContactNo(getActivity()).equalsIgnoreCase("")) {
                    intent.setData(Uri.parse("tel:" + AppPreference.getManagerContactNo(getActivity())));
                    startActivity(intent);
                } else {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Manager's is not availble", BaseTransientBottomBar.LENGTH_INDEFINITE).setAction("DISMISS", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    }).show();
                }
                break;
            case R.id.btnMessage:
                FragmentManager fm6 = getFragmentManager();
                FragmentTransaction ft6 = fm6.beginTransaction();
                MessageFragment llf6 = new MessageFragment();
                ft6.replace(R.id.main_frame, llf6);
                ft6.addToBackStack(null);
                ft6.commit();
                break;

            default:
                break;
        }


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

    class MyReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            getDashboard();
        }
    }

    public class getAllProjects extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("method", AppConstants.FEILDEXECUTATIVE.USERDASHBOARD);
            jsonObject.addProperty("user_id", AppPreference.getUserId(context));
            jsonObject.addProperty(AppConstants.KEYS.LOGIN_TYPE_ID, AppPreference.getUserTypeId(context));
            Log.e(TAG, "Request DASHBOARD >> " + jsonObject.toString());

            MyApiEndpointInterface apiService = ApiClient.getClient().create(MyApiEndpointInterface.class);
            Call<JsonObject> call = apiService.user_dashboard(jsonObject);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    Log.e(TAG, "Response DASHBOARD >> " + response.body().toString());
                    JsonObject jsonObject = response.body();
                    if (jsonObject.get("status").getAsString().equals("200")) {
                        JsonArray jsonArray = jsonObject.getAsJsonArray("result");
                        JsonObject result = jsonArray.get(0).getAsJsonObject();
                        totalProjCount.setText(result.get("total_project").getAsString());
                        newProjCount.setText(result.get("newaggign_project").getAsString());
                        completeProjCount.setText(result.get("complete_project").getAsString());
                        pendingProjectCount.setText(result.get("pending_project").getAsString());
                        publishProgress("200", "");
                    } else {
                        String msg = jsonObject.getAsJsonArray("result").get(0).getAsJsonObject().get("msg").getAsString();
                        publishProgress("400", msg);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    publishProgress("400", getActivity().getResources().getString(R.string.api_error_msg));
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
            } else {
                Snackbar.make(getActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getDashboard();
                    }
                }).show();
            }
        }
    }

    public class FloatingButton {
        FrameLayout bckgroundDimmer;
        FloatingActionButton button1, button2;

        public void showFloatingButton(final View activity, final Context mContext) {

            floatingActionMenu = (FloatingActionMenu) activity.findViewById(R.id.material_design_android_floating_action_menu);
            button1 = (FloatingActionButton) activity.findViewById(R.id.material_design_floating_action_menu_item1);
            button2 = (FloatingActionButton) activity.findViewById(R.id.material_design_floating_action_menu_item2);
            createCustomAnimation();

            button1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Fragment fragment = new ProjectHistoryFragment();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.main_frame, fragment, fragment.getTag());
                    fragmentTransaction.commitAllowingStateLoss();
                    fragmentTransaction.addToBackStack(null);
                    floatingActionMenu.close(true);
                }
            });
            button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Fragment fragment = new MyProfileFragment();
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.main_frame, fragment, fragment.getTag());
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commitAllowingStateLoss();
                    floatingActionMenu.close(true);
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
