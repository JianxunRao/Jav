package com.trojx.jav.com.trojx.jav.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.thefinestartist.finestwebview.FinestWebView;

/**
 * Created by Administrator on 2016/1/5.
 */
public class WebviewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new FinestWebView.Builder(WebviewActivity.this).show("http://www.zhagame.com/jav");

    }
}
