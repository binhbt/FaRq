package com.vn.fa.ui;

import android.support.v7.app.AppCompatActivity;

import com.vn.fa.net.RequestLoader;

/**
 * Created by binhbt on 6/22/2016.
 */
public abstract class RxActivity extends AppCompatActivity {
    protected void onDestroy() {
        RequestLoader.getDefault().cancelAll(this);
        super.onDestroy();
    }
}
