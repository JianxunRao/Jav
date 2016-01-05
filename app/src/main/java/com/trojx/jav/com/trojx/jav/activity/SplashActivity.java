package com.trojx.jav.com.trojx.jav.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.gc.materialdesign.widgets.Dialog;
import com.trojx.jav.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/5.
 */
public class SplashActivity extends AppCompatActivity {

    private RelativeLayout rl_splash;
    private TextView tv_version;
    private TextView tv_unhide;
    private int unhideCount=0;
    private ArrayList<AVObject> actressList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        findViews();

        getActress();




    }
    private  void findViews(){
        rl_splash = (RelativeLayout) findViewById(R.id.rl_splash);
        tv_version = (TextView) findViewById(R.id.splash_version);
        tv_unhide = (TextView) findViewById(R.id.tv_unhide);

        PackageManager packageManager=getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(),0);
            String version=packageInfo.versionName;
            tv_version.setText("Version "+version);
        } catch (Exception e) {
            Log.e("about error", e.toString());
        }

        SharedPreferences sp=getSharedPreferences("hide",MODE_PRIVATE);
        boolean hide=sp.getBoolean("hide",false);
        if(hide){
            Dialog dialog= new Dialog(this,"错误","遇到了未知的错误.");
            dialog.show();
            dialog.getButtonAccept().setText("好的");
            rl_splash.setVisibility(View.GONE);
            tv_unhide.setVisibility(View.VISIBLE);
        }
    }
    public void unhide(View view){
        unhideCount +=1 ;
        if(unhideCount==3){
            SharedPreferences sp=getSharedPreferences("hide",MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putBoolean("hide",false);
            editor.commit();

            Intent intent=new Intent(SplashActivity.this,HomeActivity.class);
//            intent.putExtra("actressList", actressList);
            startActivity(intent);
            finish();
        }
    }
    private  void getActress(){
        if(getSharedPreferences("hide",MODE_PRIVATE).getBoolean("hide",false))
            return;

        AVQuery<AVObject> query=new AVQuery<>("actress");
        query.orderByDescending("hot");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e==null){
//                    actressList= (ArrayList<AVObject>) list;
                    Intent intent=new Intent(SplashActivity.this,HomeActivity.class);
//                    intent.putExtra("actressList",actressList);
                    try {
                        Thread.currentThread().sleep(500);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    startActivity(intent);
                    finish();
                }else {
                    Log.e("get actressList error",e.toString());
                    Dialog dialog=new Dialog(SplashActivity.this,"错误","连接网络失败，请检查你的网络设置并重试");
                    dialog.show();
                    dialog.getButtonAccept().setText("好的");
                    dialog.getButtonAccept().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                }
            }
        });









    }
}
