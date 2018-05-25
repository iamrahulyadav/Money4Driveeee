package com.hvantage2.money4driveeee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.hvantage2.money4driveeee.R;

public class SelectLanguageActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();


    }

    private void init() {
//        ((Button) findViewById(R.id.tvNext)).setOnClickListener(this);
        ((LinearLayout) findViewById(R.id.llEnglish)).setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        /*if(view.getId() == R.id.tvNext){
            Intent intent = new Intent(SelectLanguageActivity.this, LoginActivity.class);
            startActivity(intent);
        }*/
        if(view.getId() == R.id.llEnglish
                ){
            Intent intent = new Intent(SelectLanguageActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
