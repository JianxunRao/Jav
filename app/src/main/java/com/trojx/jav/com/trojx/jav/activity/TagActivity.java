package com.trojx.jav.com.trojx.jav.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.trojx.jav.R;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/31.
 */
public class TagActivity extends AppCompatActivity {

    private  Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationview;
    private TextView tv_navi_account;
    private LinkedList<AVObject> tagList=new LinkedList<>();
    private GridView gv_tag;
    private MyGridAdapter adapter;
    private Date lastPressBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
//        tagview= (TagView) findViewById(R.id.tagview_tag_activity);
        gv_tag = (GridView) findViewById(R.id.gv_tag);
        toolbar= (Toolbar) findViewById(R.id.toolbar_tag);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setTitle("类别");

        setSupportActionBar(toolbar);
        ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);

        mDrawerLayout= (DrawerLayout) findViewById(R.id.dl_tag);
        mNavigationview= (NavigationView) findViewById(R.id.navi_view_tag);
        setupNavigationMenu();


//        gv_tag.setOnTagClickListener(new OnTagClickListener() {
//            @Override
//            public void onTagClick(Tag tag, int position) {
//                Intent intent=new Intent(TagActivity.this,SearchActivity.class);
//                intent.putExtra("actressName",tag.text);
//                startActivity(intent);
//            }
//        });
        adapter = new MyGridAdapter();
        getTags();
        gv_tag.setAdapter(adapter);
        gv_tag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tagList.get(position).put("hot",tagList.get(position).getInt("hot")+1);
                tagList.get(position).saveInBackground();
                Intent intent=new Intent(TagActivity.this,SearchActivity.class);
                intent.putExtra("actressName",tagList.get(position).getString("name"));
                startActivity(intent);
            }
        });


    }
    private void getTags(){

        final ProgressDialog progressDialog=new ProgressDialog(this,"载入中");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
//        tagview.removeAllTags();
        AVQuery<AVObject> query=new AVQuery<AVObject>("Tag");
//        query.setCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.orderByDescending("hot");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list!=null){
                        tagList= (LinkedList<AVObject>) list;
                        adapter.notifyDataSetChanged();
                        setGridViewHeightBasedOnChildren(gv_tag);
                    }
                    progressDialog.dismiss();

                    if (AVUser.getCurrentUser() != null) {
                        //在这里findview 不会空指针，不知道为什么
                        tv_navi_account = (TextView) findViewById(R.id.tv_navi_account);
                        tv_navi_account.setText(AVUser.getCurrentUser().getUsername());
                        //不能findview 要通过特定方法
                        mNavigationview.getMenu().getItem(6).getSubMenu().getItem(0).setTitle("注销");
                    }

                } else {
                    Log.e("get tag error", e.toString());
                }
            }
        });
    }
    private  class MyGridAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return tagList.size();
        }

        @Override
        public Object getItem(int position) {
            return tagList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            view= LayoutInflater.from(TagActivity.this).inflate(R.layout.tag_grid_item,null);
            TextView tv_tag_item= (TextView) view.findViewById(R.id.tv_tag);
            tv_tag_item.setText(tagList.get(position).getString("name"));

            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tag, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_refresh_tag:
                getTags();
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    private void setupNavigationMenu(){
        if(mNavigationview!=null){
            mNavigationview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.navi_actress:
                            Intent intent=new Intent(TagActivity.this,HomeActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        case R.id.navi_exit:
                            finish();
                        case R.id.navi_search:
                            Intent intent1=new Intent(TagActivity.this,SearchActivity.class);
                            intent1.putExtra("actressName","");
                            startActivity(intent1);
                            break;
                        case R.id.navi_hot:
                            Intent intent2=new Intent(TagActivity.this,MovieHomeActivity.class);
                            intent2.putExtra("mode",2);
                            startActivity(intent2);
                            finish();
                            break;
                        case R.id.navi_new:
                            Intent intent3=new Intent(TagActivity.this,MovieHomeActivity.class);
                            intent3.putExtra("mode",1);
                            startActivity(intent3);
                            finish();
                            break;
                        case R.id.navi_login:
                            if (AVUser.getCurrentUser() == null) {
                                Intent intent4 = new Intent(TagActivity.this, LoginActivity.class);
                                startActivityForResult(intent4, 1);
                            } else {
                                final Dialog dialog = new Dialog(TagActivity.this, "退出", "退出登录吗？");
                                dialog.show();
                                dialog.getButtonAccept().setText("是的");
                                dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        AVUser.logOut();             //清除缓存用户对象
                                        TextView tv_navi_account = (TextView) findViewById(R.id.tv_navi_account);
                                        tv_navi_account.setText("未登录");
                                        mNavigationview.getMenu().getItem(6).getSubMenu().getItem(0).setTitle("登录");
                                        dialog.dismiss();
                                    }
                                });
                            }
                            break;
                        case R.id.navi_fav:
                            if (AVUser.getCurrentUser() == null) {
                                Dialog dialog = new Dialog(TagActivity.this, "登录", "收藏需要先登录.");
                                dialog.show();
                                dialog.getButtonAccept().setText("好的");
                                break;
                            } else {
                                Intent intent5 = new Intent(TagActivity.this, MovieHomeActivity.class);
                                intent5.putExtra("mode", 3);
                                startActivity(intent5);
                                finish();
                                break;
                            }
                        case R.id.navi_setting:
                            Intent intent6=new Intent(TagActivity.this,SettingActivity.class);
                            startActivity(intent6);
                            break;
                    }
                    mDrawerLayout.closeDrawers();
                    return true;
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1&&resultCode==RESULT_OK){
            tv_navi_account= (TextView) findViewById(R.id.tv_navi_account);
            tv_navi_account.setText(AVUser.getCurrentUser().getUsername());
            mNavigationview.getMenu().getItem(6).getSubMenu().getItem(0).setTitle("注销");
        }
    }

    public static void setGridViewHeightBasedOnChildren(GridView gridView) {
        // 获取GridView对应的Adapter
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int rows;
        int columns=0;
        int horizontalBorderHeight=0;
        Class<?> clazz=gridView.getClass();
        try {
            //利用反射，取得每行显示的个数
            Field column=clazz.getDeclaredField("mRequestedNumColumns");
            column.setAccessible(true);
            columns=(Integer)column.get(gridView);
            //利用反射，取得横向分割线高度
            Field horizontalSpacing=clazz.getDeclaredField("mRequestedHorizontalSpacing");
            horizontalSpacing.setAccessible(true);
            horizontalBorderHeight=(Integer)horizontalSpacing.get(gridView);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        //判断数据总数除以每行个数是否整除。不能整除代表有多余，需要加一行
        if(listAdapter.getCount()%columns>0){
            rows=listAdapter.getCount()/columns+1;
        }else {
            rows=listAdapter.getCount()/columns;
        }
        int totalHeight = 0;
        for (int i = 0; i < rows; i++) { //只计算每项高度*行数
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight+horizontalBorderHeight*(rows-1);//最后加上分割线总高度
        gridView.setLayoutParams(params);
    }
    @Override
    public void onBackPressed() {
        if(lastPressBack==null){
            lastPressBack=new Date();
            Toast.makeText(this, "再按返回退出", Toast.LENGTH_SHORT).show();
        }
        else {
            Date nowPressBack=new Date();
            if((nowPressBack.getTime()- lastPressBack.getTime())<2000) {
                super.onBackPressed();
            }else {
                lastPressBack =nowPressBack;
                Toast.makeText(this,"再按返回退出",Toast.LENGTH_SHORT).show();
            }
        }
    }


}
