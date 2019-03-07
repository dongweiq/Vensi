package com.honghe.vensitest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int smallWidth = getResources().getConfiguration().smallestScreenWidthDp;
        Log.e("whh", "smallestScreenWidthDp" + smallWidth);
        VensiUtil.INSTANCE.initHostApi();
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                VensiUtil.INSTANCE.connect();
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                VensiUtil.INSTANCE.login();
                break;
            case R.id.btnGetDevices:
                VensiUtil.INSTANCE.getDevices();
                break;
            case R.id.btnRefreshDevices:
                VensiUtil.INSTANCE.getStates();
                break;
            case R.id.btnOpen:
                VensiUtil.INSTANCE.open();
                break;
            case R.id.btnClose:
                VensiUtil.INSTANCE.close();
                break;
        }
    }
}
