package com.trojx.jav.com.trojx.jav.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.trojx.jav.R;
import com.trojx.jav.com.trojx.jav.domain.Actress;
import com.yalantis.flipviewpager.adapter.BaseFlipAdapter;
import com.yalantis.flipviewpager.utils.FlipSettings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 2015/12/28.
 */
public class HomeActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ListView lv_actress;
    private ArrayList<Actress> actressList=new ArrayList<Actress>();
    private ActressAdapter actressAdapter;
    private EditText et_home_search;
    private int skip=0;
    private  boolean isFirstLoad=true;
    private  TextView tv_navi_account;
    private  Date lastPressBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar_home);
        toolbar.setTitle("女优");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        final ActionBar ab=getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout= (DrawerLayout) findViewById(R.id.dl_home);

        navigationView= (NavigationView) findViewById(R.id.navi_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        et_home_search= (EditText) findViewById(R.id.et_home_search);
        et_home_search.setEnabled(false);
        et_home_search.setVisibility(View.GONE);

        lv_actress= (ListView) findViewById(R.id.lv_actress);
        FlipSettings flipSettings=new FlipSettings.Builder().build();
        actressAdapter=new ActressAdapter(this,actressList,flipSettings);
        lv_actress.setAdapter(actressAdapter);

        getActress();

        lv_actress.setOnScrollListener(new AbsListView.OnScrollListener() {
            int state;
            boolean hasAppend = true;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                state = scrollState;
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE)
                    hasAppend = false;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (isFirstLoad && !hasAppend && firstVisibleItem == (totalItemCount - visibleItemCount - 2) && (state == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL || state == AbsListView.OnScrollListener.SCROLL_STATE_FLING)) {
                    getActress();
                    hasAppend = true;
                }
            }
        });
        lv_actress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Actress actress = (Actress) lv_actress.getAdapter().getItem(position);
                AVQuery<AVObject> query = new AVQuery<AVObject>("actress");
                query.whereEqualTo("name", actress.getName());
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null) {
                            AVObject item = list.get(0);
                            int hot = item.getInt("hot");
                            item.put("hot", hot + 1);
                            item.saveInBackground();
                        }
                    }
                });
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                intent.putExtra("actressName", actress.getName().split("（")[0]);
                startActivity(intent);
            }
        });



//        setHotMovies();
    }


    class ActressAdapter extends BaseFlipAdapter<Actress>{


        public ActressAdapter(Context context, List<Actress> items, FlipSettings settings) {
            super(context, items, settings);
        }

        @Override
        public View getPage(int position, View convertView, ViewGroup parent, Actress item1, Actress item2) {
            final ActressHolder actressHolder;
            if(convertView==null){
                actressHolder=new ActressHolder();
                convertView=getLayoutInflater().inflate(R.layout.actress_merge_page,parent,false);
                actressHolder.leftAvatar= (ImageView) convertView.findViewById(R.id.iv_left_actress_avatar);
                actressHolder.rightAvatar= (ImageView) convertView.findViewById(R.id.iv_right_actress_avatar);
                actressHolder.infoPage=getLayoutInflater().inflate(R.layout.actress_info, parent, false);
                actressHolder.infoName= (TextView) actressHolder.infoPage.findViewById(R.id.tv_actress_info_name);
                actressHolder.infoBirth= (TextView) actressHolder.infoPage.findViewById(R.id.tv_actress_info_birth);
                actressHolder.infoBirthplace= (TextView) actressHolder.infoPage.findViewById(R.id.tv_actress_info_birthplace);
                actressHolder.infoAge= (TextView) actressHolder.infoPage.findViewById(R.id.tv_actress_info_age);
                actressHolder.infoHeight= (TextView) actressHolder.infoPage.findViewById(R.id.tv_actress_info_height);
                actressHolder.infoBWH= (TextView) actressHolder.infoPage.findViewById(R.id.tv_actress_info_bwh);
                actressHolder.infoCup= (TextView) actressHolder.infoPage.findViewById(R.id.tv_actress_info_cup);

                convertView.setTag(actressHolder);
            }else {
                actressHolder= (ActressHolder) convertView.getTag();
            }

            switch (position){
                case 1:
                    Glide.with(HomeActivity.this).load(item1.getAvatar()).into(actressHolder.leftAvatar);
                    if(item2!=null)
                        Glide.with(HomeActivity.this).load(item2.getAvatar()).into(actressHolder.rightAvatar);
                    break;
                default:
                    fillHolder(actressHolder, position == 0 ? item1 : item2);
                    actressHolder.infoPage.setTag(actressHolder);
                    return actressHolder.infoPage;
            }
            return convertView;
        }

        @Override
        public int getPagesCount() {
            return 3;
        }
        private void fillHolder(ActressHolder actressHolder,Actress actress){
            if(actress==null){return;}
            actressHolder.infoName.setText(actress.getName());
            if(actress.getBirth().length()==0){
                actressHolder.infoBirth.setVisibility(View.GONE);
            }else {
                actressHolder.infoBirth.setVisibility(View.VISIBLE);
                actressHolder.infoBirth.setText(actress.getBirth());
            }
            if(actress.getAge().length()==0){
                actressHolder.infoAge.setVisibility(View.GONE);
            }else {
                actressHolder.infoAge.setVisibility(View.VISIBLE);
                actressHolder.infoAge.setText(actress.getAge()+"岁");
            }
            if(actress.getBirthplace().length()==0){
                actressHolder.infoBirthplace.setVisibility(View.GONE);
            }else {
                actressHolder.infoBirthplace.setVisibility(View.VISIBLE);
                actressHolder.infoBirthplace.setText(actress.getBirthplace());
            }
            if(actress.getHeight().length()==0){
                actressHolder.infoHeight.setVisibility(View.GONE);
            }else {
                actressHolder.infoHeight.setVisibility(View.VISIBLE);
                actressHolder.infoHeight.setText(actress.getHeight());
            }
            if(actress.getWaist().length()==0){
                actressHolder.infoBWH.setVisibility(View.GONE);
            }else {
                actressHolder.infoBWH.setVisibility(View.VISIBLE);
                actressHolder.infoBWH.setText(actress.getBust()+" "+actress.getWaist()+" "+actress.getHips());
            }
            if (actress.getCup().length()==0){
                actressHolder.infoCup.setVisibility(View.GONE);
            }else {
                actressHolder.infoCup.setVisibility(View.VISIBLE);
                actressHolder.infoCup.setText(actress.getCup()+" 罩杯");
            }
            switch ((Math.round((float)(Math.random()*4)))){
                case 0:
                    actressHolder.infoPage.setBackgroundColor(getResources().getColor(R.color.saffron));
                    break;
                case 1:
                    actressHolder.infoPage.setBackgroundColor(getResources().getColor(R.color.orange));
                    break;
                case 2:
                    actressHolder.infoPage.setBackgroundColor(getResources().getColor(R.color.pink));
                    break;
                case 3:
                    actressHolder.infoPage.setBackgroundColor(getResources().getColor(R.color.purple));
                    break;
                case 4:
                    actressHolder.infoPage.setBackgroundColor(getResources().getColor(R.color.sienna));
            }

        }
        class ActressHolder{
            ImageView leftAvatar;
            ImageView rightAvatar;
            View infoPage;
            TextView infoName,infoBirth,infoAge,infoBirthplace,infoHeight,infoBWH,infoCup;
        }
    }


    private void getActress(){

        AVQuery<AVObject> query=new AVQuery<AVObject>("actress");
        query.setLimit(30);
        query.setSkip(skip);
        query.addDescendingOrder("hot");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e==null){
                    for (AVObject item: list) {
                        Actress actress=new Actress();
                        actress.setAvatar(item.getAVFile("avatarFile").getUrl());
                        actress.setName(item.getString("name"));
                        actress.setHips(item.getString("hips"));
                        actress.setWaist(item.getString("waist"));
                        actress.setAge(item.getString("age"));
                        actress.setBirth(item.getString("birth"));
                        actress.setBirthplace(item.getString("birthplace"));
                        actress.setCup(item.getString("cup"));
                        actress.setBust(item.getString("bust"));
                        actress.setHeight(item.getString("height"));
                        if(actressList!=null)
                        actressList.add(actress);
                    }
                    actressAdapter.notifyDataSetChanged();

//                        lv_actress.setSelection(4);//修正首次加载 图片显示问题
//                        lv_actress.setSelection(0);
//                    lv_actress.smoothScrollToPosition(10);//不起作用？
//                    lv_actress.smoothScrollToPosition(0);

//                    Toast.makeText(HomeActivity.this,"Load success",Toast.LENGTH_SHORT).show();

                    if(AVUser.getCurrentUser()!=null){
                        //在这里findview 不会空指针，不知道为什么
                        tv_navi_account= (TextView) findViewById(R.id.tv_navi_account);
                        tv_navi_account.setText(AVUser.getCurrentUser().getUsername());
                        //不能findview 要通过特定方法
                        navigationView.getMenu().getItem(6).getSubMenu().getItem(0).setTitle("注销");
                    }

                }else {
                    Log.e("getActress", e.toString());
                }
            }
        });
        skip+=30;
    }



    private void setupDrawerContent(final NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navi_tag:
                        Intent intent = new Intent(HomeActivity.this, TagActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.navi_exit:
                        finish();
                        break;
                    case R.id.navi_search:
                        Intent intent1 = new Intent(HomeActivity.this, SearchActivity.class);
                        intent1.putExtra("actressName", "");
                        startActivity(intent1);
                        break;
                    case R.id.navi_hot:
                        Intent intent2 = new Intent(HomeActivity.this, MovieHomeActivity.class);
                        intent2.putExtra("mode", 2);
                        startActivity(intent2);
                        finish();
                        break;
                    case R.id.navi_new:
                        Intent intent3 = new Intent(HomeActivity.this, MovieHomeActivity.class);
                        intent3.putExtra("mode", 1);
                        startActivity(intent3);
                        finish();
                        break;
                    case R.id.navi_login:
                        if (AVUser.getCurrentUser() == null) {
                            Intent intent4 = new Intent(HomeActivity.this, LoginActivity.class);
                            startActivityForResult(intent4, 1);
                        } else {
                            final Dialog dialog = new Dialog(HomeActivity.this, "退出", "退出登录吗？");
                            dialog.show();
                            dialog.getButtonAccept().setText("是的");
                            dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AVUser.logOut();             //清除缓存用户对象
                                    TextView tv_navi_account = (TextView) findViewById(R.id.tv_navi_account);
                                    tv_navi_account.setText("未登录");
                                    navigationView.getMenu().getItem(6).getSubMenu().getItem(0).setTitle("登录");
                                    dialog.dismiss();
                                }
                            });
                        }
                        break;
                    case R.id.navi_fav:
                        if (AVUser.getCurrentUser() == null) {
                            Dialog dialog = new Dialog(HomeActivity.this, "登录", "收藏需要先登录.");
                            dialog.show();
                            dialog.getButtonAccept().setText("好的");
                            break;
                        } else {
                            Intent intent5 = new Intent(HomeActivity.this, MovieHomeActivity.class);
                            intent5.putExtra("mode", 3);
                            startActivity(intent5);
                            finish();
                            break;
                        }
                    case R.id.navi_setting:
                        Intent intent6=new Intent(HomeActivity.this,SettingActivity.class);
                        startActivity(intent6);
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);//按下home按钮显示抽屉菜单
                return true;
            case R.id.actress_search:
                if(et_home_search.isEnabled()){
                    et_home_search.invalidate();
                    search();
                }else {
                    et_home_search.setEnabled(true);
                    et_home_search.setVisibility(View.VISIBLE);
                }
                return true;
            case  R.id.actress_display_hot:
                actressList.clear();
                isFirstLoad=true;
                skip=0;
                getActress();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(navigationView!=null&&navigationView.isShown()){
            mDrawerLayout.closeDrawers();//如果抽屉菜单打开则先关闭
        }else {
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

    private void search(){
        String key=et_home_search.getText().toString();
        if(key.length()>0){
            isFirstLoad=false;
            actressList.clear();
            final ProgressDialog progressDialog=new ProgressDialog(HomeActivity.this,"搜索中...");
            progressDialog.show();
            AVQuery<AVObject> query=new AVQuery<AVObject>("actress");
            query.whereContains("name",key);
            query.addAscendingOrder("hot");
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (e == null) {
                        for (AVObject item : list) {
                            Actress actress = new Actress();
                            actress.setAvatar(item.getAVFile("avatarFile").getUrl());
                            actress.setName(item.getString("name"));
                            actress.setHips(item.getString("hips"));
                            actress.setWaist(item.getString("waist"));
                            actress.setAge(item.getString("age"));
                            actress.setBirth(item.getString("birth"));
                            actress.setBirthplace(item.getString("birthplace"));
                            actress.setCup(item.getString("cup"));
                            actress.setBust(item.getString("bust"));
                            actress.setHeight(item.getString("height"));
                            actressList.add(actress);
                        }
                        actressAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    } else {
                        Log.e("search error", e.toString());
                    }
                }
            });
        }
    }
    private void savePictToLeancloud(){
        File file=new File("sdcard/actressAvatar/actressAvatar");
        File[] fileList=file.listFiles();
        for (File file2:fileList) {
            try {
                final AVFile avFile=AVFile.withFile(file2.getName().replace(".jpg",""),file2);
                avFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if(e==null){
                            AVQuery<AVObject> query=new AVQuery<AVObject>("actress");
                            query.whereEqualTo("url",avFile.getOriginalName());
                            query.findInBackground(new FindCallback<AVObject>() {
                                @Override
                                public void done(List<AVObject> list, AVException e) {
                                    AVObject item=list.get(0);
                                    item.put("avatarFile",avFile);
                                    item.saveInBackground();
                                }
                            });
                        }else {
                            Log.e("save error",e.toString());
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lv_actress=null;
        actressList=null;
        actressAdapter=null;
        this.setContentView(R.layout.empty_layout);
        System.gc();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1&&resultCode==RESULT_OK){
            tv_navi_account= (TextView) findViewById(R.id.tv_navi_account);
            tv_navi_account.setText(AVUser.getCurrentUser().getUsername());
            navigationView.getMenu().getItem(6).getSubMenu().getItem(0).setTitle("注销");
        }
    }
//    private  void setHotMovies(){
//        try{
//            ArrayList<String> movies=new ArrayList<>();
//            BufferedReader br=new BufferedReader(new FileReader(new File("sdcard/hotMovie.txt")));
//            String line=null;
//            while((line=br.readLine())!=null){
//                movies.add(line);
//            }
//            Log.e("movies size",movies.size()+"");
//            for (int i=0;i<500;i++) {
//                AVQuery<AVObject> query=new AVQuery<>("Movie");
//                query.whereEqualTo("code", movies.get(i));
//                final int j=520-i;
//                query.getFirstInBackground(new GetCallback<AVObject>() {
//                    @Override
//                    public void done(AVObject avObject, AVException e) {
//                        if(e==null){
//                            if(avObject!=null){
//                                avObject.put("hot",j);
//                                avObject.saveInBackground();
//                                Log.e("now saving",String.valueOf(520-j));
//                            }
//                        }else {
//                            Log.e("save hot error",e.toString());
//                        }
//                    }
//                });
//            }
//        }catch (IOException e){
//            e.printStackTrace();
//            Log.e("error",e.toString());
//        }
//    }







}
