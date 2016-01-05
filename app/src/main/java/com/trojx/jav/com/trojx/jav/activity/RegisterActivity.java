package com.trojx.jav.com.trojx.jav.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.trojx.jav.R;

/**
 * Created by Administrator on 2016/1/1.
 */
public class RegisterActivity extends AppCompatActivity{

    private Toolbar toolbar;
    private EditText et_account;
    private EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViews();
        ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        ab.setTitle("注册");


    }
    private  void findViews(){

        toolbar = (Toolbar) findViewById(R.id.toolbar_register);
        et_account = (EditText) findViewById(R.id.et_register_account);
        et_password = (EditText) findViewById(R.id.et_register_password);
        setSupportActionBar(toolbar);
        toolbar.setTitle("注册");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

    }
    public  void register(View view){
        final String account=et_account.getText().toString();
        final String password=et_password.getText().toString();
        if(account.isEmpty()||password.isEmpty()){
            Dialog dialog=new Dialog(this, "错误","用户名与密码不能为空！");
            dialog.show();
            dialog.getButtonAccept().setText("好的");
        }else {
            final ProgressDialog progressDialog=new ProgressDialog(this,"正在登录");
            progressDialog.show();
            AVUser avUser=new AVUser();
            avUser.setUsername(account);
            avUser.setPassword(password);
            avUser.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        Intent intent = new Intent();
                        intent.putExtra("account", account);
                        setResult(RESULT_OK, intent);
                        progressDialog.dismiss();
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Dialog dialog = new Dialog(RegisterActivity.this, "注册失败", "请检查用户名与密码！");
                        dialog.show();
                        dialog.getButtonAccept().setText("好的");
                        Log.e("login error", e.toString());
                    }
                }
            });
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return true;
    }
}
