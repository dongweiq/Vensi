package com.honghe.vensitest;


import android.widget.Toast;

public class VensiUtil {


    public static void showNotice(String msg) {
        WHHLog.e(msg);
        Toast.makeText(MyApp.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
