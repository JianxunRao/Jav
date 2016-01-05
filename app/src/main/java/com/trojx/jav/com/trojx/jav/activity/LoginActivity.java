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
import com.avos.avoscloud.LogInCallback;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.trojx.jav.R;

/**
 * Created by Administrator on 2016/1/1.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText et_account;
    private EditText et_password;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        finViews();

        ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        ab.setTitle("登录");



    }
    private void finViews(){

        et_account = (EditText) findViewById(R.id.et_login_account);
        et_password = (EditText) findViewById(R.id.et_login_password);
        toolbar = (Toolbar) findViewById(R.id.toolbar_login);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
    }
    public  void login(View view){
        final String account,password;
        account=et_account.getText().toString();
        password=et_password.getText().toString();
        if(account.isEmpty()||password.isEmpty()){
            Dialog dialog=new Dialog(this,"错误","用户名与密码不能为空！");
            dialog.show();
            dialog.getButtonAccept().setText("好的");
        }else {
            final ProgressDialog progressDialog=new ProgressDialog(this,"正在登录");
            progressDialog.show();
            AVUser.logInInBackground(account,password,new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if (e == null) {
                        Intent intent = new Intent();
                        intent.putExtra("account", account);
                        setResult(RESULT_OK, intent);
                        progressDialog.dismiss();
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Dialog dialog = new Dialog(LoginActivity.this, "登录失败", "请检查用户名与密码！");
                        dialog.show();
                        dialog.getButtonAccept().setText("好的");
                        Log.e("login error", e.toString());
                    }
                }
            });
        }
    }
    public  void goRegister(View view){
        Intent intent=new Intent(this,RegisterActivity.class);
        startActivity(intent);
        finish();
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
