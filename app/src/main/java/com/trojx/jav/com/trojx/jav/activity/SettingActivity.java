package com.trojx.jav.com.trojx.jav.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;
import com.trojx.jav.R;

/**
 * Created by Administrator on 2016/1/4.
 */
public class SettingActivity extends AppCompatActivity {

    private ButtonFlat bt_update;
    private ButtonFlat bt_about;
    private ButtonFlat bt_terms;
    private ButtonFlat bt_hide;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        findViews();

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setBackgroundColor(getResources().getColor(R.color.logoback));
        ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        ab.setTitle("设置");




    }
    private void findViews(){
        bt_update = (ButtonFlat) findViewById(R.id.bt_update);
        bt_about = (ButtonFlat) findViewById(R.id.bt_about);
        bt_terms = (ButtonFlat) findViewById(R.id.bt_terms);
        bt_hide = (ButtonFlat) findViewById(R.id.bt_hide);
        toolbar = (Toolbar) findViewById(R.id.toolbar_setting);
    }
    public void update(View view){
        PackageManager packageManager=getPackageManager();
        try{
            PackageInfo packageInfo=packageManager.getPackageInfo(getPackageName(),0);
            final String version=packageInfo.versionName;
            AVQuery<AVObject> query=new AVQuery<>("Version");
            query.addDescendingOrder("createdAt");
            query.getFirstInBackground(new GetCallback<AVObject>() {
                @Override
                public void done(final AVObject avObject, AVException e) {
                    if (!avObject.getString("version").equals(version)) {
                        final Dialog dialog = new Dialog(SettingActivity.this, "新版本", "当前版本：" + version + "\r\n" + "最新版本：" + avObject.getString("version"));
                        dialog.show();
                        dialog.getButtonAccept().setText("立即更新");
                        dialog.getButtonCancel().setText("暂不更新");
                        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(avObject.getAVFile("file").getUrl()));
                                intent.addCategory("android.intent.category.DEFAULT");
                                startActivity(intent);
                                dialog.dismiss();//?
                            }
                        });
                    } else {
                        Dialog dialog = new Dialog(SettingActivity.this, "更新", "已经是最新版本");
                        dialog.show();
                        dialog.getButtonAccept().setText("好的");
                    }
                }
            });
        }catch (Exception e){
            Log.e("getPackageInfo error",e.toString());
        }
    }
    public  void about(View view){
        PackageManager packageManager=getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            String version=packageInfo.versionName;
            Dialog dialog=new Dialog(SettingActivity.this,"关于","JAV"+"\r\n"+"当前版本："+version+"\r\n"+"trojx123@gmail.com");
            dialog.show();
            dialog.getButtonAccept().setText("好的");
        } catch (Exception e) {
            Log.e("about error",e.toString());
        }
    }
    public  void terms(View view){
        Dialog dialog=new Dialog(SettingActivity.this,"使用须知","本软件所有内容均来自互联网，禁止未成年人使用。");
        dialog.show();
        dialog.getButtonAccept().setText("确定");
    }
    public void hide(View view){
        final Dialog dialog=new Dialog(SettingActivity.this,"绅士模式","当你一段时间内暂不需要使用本应用时，为了避免隐私泄露问题与重复装卸软件的麻烦，你可以开启“绅士模式”。\r\n" +
                "开启后，下一次进入本应用将直接进入空白界面。\r\n只有连续三次点击屏幕最右上角空白处，才能取消该模式，恢复正常使用。");
        dialog.show();
        dialog.getButtonAccept().setText("开启");
        dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences("hide", MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("hide", true);
                editor.commit();
                dialog.dismiss();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
