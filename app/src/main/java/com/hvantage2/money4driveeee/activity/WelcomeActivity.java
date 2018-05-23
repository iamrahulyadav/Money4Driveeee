package com.hvantage2.money4driveeee.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hvantage2.money4driveeee.adapter.CustomSwipeAdapter;
import com.hvantage2.money4driveeee.R;
import com.hvantage2.money4driveeee.customview.CustomTextView;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    ViewPager viewPager;
    CustomSwipeAdapter adapter;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//            requestWindowFeature(Window.FEATURE_NO_TITLE);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_welcome);
            initCommponent();
    }

    private void initCommponent() {
        ((CustomTextView) findViewById(R.id.tvNext)).setOnClickListener(this);
        ((CustomTextView) findViewById(R.id.tvSkip)).setOnClickListener(this);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        adapter = new CustomSwipeAdapter(this);
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager, true);
        viewPager.setPageMargin((int) getResources().getDimension(R.dimen._8sdp));


    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvNext) {
            startActivity(new Intent(WelcomeActivity.this, SelectLanguageActivity.class));
        } else if (view.getId() == R.id.tvSkip) {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        }
    }
}

