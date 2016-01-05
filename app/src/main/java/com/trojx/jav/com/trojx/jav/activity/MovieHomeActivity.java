package com.trojx.jav.com.trojx.jav.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.trojx.jav.R;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/1.
 */
public class MovieHomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout mdDrawerLayout;
    private NavigationView mnaNavigationView;
    private ListView lv_movie_home_movie;
    private myMovieAdapter adapter=new myMovieAdapter();
    private static  final int NEW_MOVIES=1;
    private static  final int HOT_MOVIES=2;
    private static  final int FAV_MOVIES=3;
    private  int currentMode=-1;
    private FloatingActionButton fab_move_top;
    private TextView tv_navi_account;
    private Date lastPressBack;
    private LinkedList<AVObject> movieObjectList=new LinkedList<AVObject>();
    private ActionBar ab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_home);

        toolbar = (Toolbar) findViewById(R.id.toolbar_movie);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);

        mdDrawerLayout= (DrawerLayout) findViewById(R.id.dl_movie_home);

        mnaNavigationView= (NavigationView) findViewById(R.id.navi_view);
        if(mnaNavigationView!=null){
            setupDrawercontent(mnaNavigationView);
        }
        lv_movie_home_movie = (ListView) findViewById(R.id.lv_movie_home_movie);
        lv_movie_home_movie.setAdapter(adapter);
        lv_movie_home_movie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AVQuery<AVObject> query=new AVQuery<AVObject>("Movie");
                query.whereEqualTo("code",movieObjectList.get(position).getString("title").split(" ")[0]);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null) {
                            AVObject item = list.get(0);
                            item.put("hot", item.getInt("hot") + 1);
                            item.saveInBackground();
                        }
                    }
                });
                Intent intent = new Intent(MovieHomeActivity.this, MovieMain.class);
                intent.putExtra("movieObject", movieObjectList.get(position).toString());
                startActivity(intent);
            }
        });
        lv_movie_home_movie.setOnScrollListener(new AbsListView.OnScrollListener() {
            int totalCount;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState){
                    case SCROLL_STATE_IDLE:
                        if(totalCount!=0)
                            fab_move_top.show();
                        break;
                    case SCROLL_STATE_FLING:
                        fab_move_top.hide();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                totalCount=totalItemCount;
            }
        });
        fab_move_top = (FloatingActionButton) findViewById(R.id.fab_movie_home_move_up);
        Intent intent=getIntent();
        int mode=intent.getIntExtra("mode",1);
        if(mode==NEW_MOVIES)
            getNewMovies();
        if(mode==HOT_MOVIES)
            getHotMovies();
        if(mode==FAV_MOVIES)
            getFavMovies();
    }



    private void setupDrawercontent(final NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navi_tag:
                        Intent intent = new Intent(MovieHomeActivity.this, TagActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.navi_exit:
                        finish();
                        break;
                    case R.id.navi_search:
                        Intent intent1 = new Intent(MovieHomeActivity.this, SearchActivity.class);
                        intent1.putExtra("actressName", "");
                        startActivity(intent1);
                        break;
                    case R.id.navi_hot:
                        if (currentMode != HOT_MOVIES)
                            getHotMovies();
                        break;
                    case R.id.navi_new:
                        if (currentMode != NEW_MOVIES)
                            getNewMovies();
                        break;
                    case R.id.navi_fav:
                        if(AVUser.getCurrentUser()==null){
                            Dialog dialog=new Dialog(MovieHomeActivity.this,"登录","收藏需要先登录.");
                            dialog.show();
                            dialog.getButtonAccept().setText("好的");
                            break;
                        }else {
                        if (currentMode != FAV_MOVIES)
                            getFavMovies();
                        break;
                        }
                    case  R.id.navi_actress:
                        Intent intent2=new Intent(MovieHomeActivity.this,HomeActivity.class);
                        startActivity(intent2);
                        finish();
                    case R.id.navi_login:
                        if (AVUser.getCurrentUser() == null) {
                            Intent intent3 = new Intent(MovieHomeActivity.this, LoginActivity.class);
                            startActivityForResult(intent3, 1);
                        } else {
                            final Dialog dialog = new Dialog(MovieHomeActivity.this, "退出", "退出登录吗？");
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
                    case R.id.navi_setting:
                        Intent intent3=new Intent(MovieHomeActivity.this,SettingActivity.class);
                        startActivity(intent3);
                        break;
                }
                mdDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void getHotMovies(){

        final ProgressDialog progressDialog=new ProgressDialog(this,"加载中");
        progressDialog.show();

        currentMode=HOT_MOVIES;
        ab.setTitle("高评价");

        if(movieObjectList!=null)
            movieObjectList.clear();
        AVQuery<AVObject> query=new AVQuery<>("Movie");
        query.setLimit(250);
        query.orderByDescending("hot");
//        query.setCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK); 不行，会在成瞬间加载好全部数据然后UI卡顿
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    movieObjectList= (LinkedList<AVObject>) list;
//                    for (int i = 0; i < list.size(); i++) {
//                        Movie mMovie = new Movie();
//                        AVObject mObject = list.get(i);
//                        mMovie.setTitle(mObject.getString("title"));
//                        mMovie.setCode(mMovie.getTitle().split(" ")[0]);
//                        mMovie.setIssueDate(mObject.getString("issuedate"));
//                        mMovie.setCatagory(mObject.getString("category"));
//                        mMovie.setActress(mObject.getString("actress"));
//                        mMovie.setCoverImg(mObject.getString("coverimg"));
//                        mMovie.setHot(mObject.getInt("hot"));
//                        movieList.add(mMovie);
//                    }
                    lv_movie_home_movie.smoothScrollToPosition(0);
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();

                    if (AVUser.getCurrentUser() != null) {
                        //在这里findview 不会空指针，不知道为什么
                        tv_navi_account = (TextView) findViewById(R.id.tv_navi_account);
                        tv_navi_account.setText(AVUser.getCurrentUser().getUsername());
                        //不能findview 要通过特定方法
                        mnaNavigationView.getMenu().getItem(6).getSubMenu().getItem(0).setTitle("注销");
                    }
                } else {
                    Log.e("getHotMovies error", e.toString());
                }
            }
        });
    }
    private void getNewMovies(){

        final ProgressDialog progressDialog=new ProgressDialog(this,"加载中");
        progressDialog.show();

        currentMode=NEW_MOVIES;
        ab.setTitle("新发布");

        if(movieObjectList!=null)
            movieObjectList.clear();
        AVQuery<AVObject> query=new AVQuery<>("Movie");
        query.setLimit(100);
        query.whereContains("issuedate", "2015-12");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    movieObjectList = (LinkedList<AVObject>) list;
//                    for (int i = 0; i < list.size(); i++) {
//                        Movie mMovie = new Movie();
//                        AVObject mObject = list.get(i);
//                        mMovie.setTitle(mObject.getString("title"));
//                        mMovie.setCode(mMovie.getTitle().split(" ")[0]);
//                        mMovie.setIssueDate(mObject.getString("issuedate"));
//                        mMovie.setCatagory(mObject.getString("category"));
//                        mMovie.setActress(mObject.getString("actress"));
//                        mMovie.setCoverImg(mObject.getString("coverimg"));
//                        mMovie.setHot(mObject.getInt("hot"));
//                        movieList.add(mMovie);
//                    }
                    list = null;
                    lv_movie_home_movie.smoothScrollToPosition(0);
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();

                    if (AVUser.getCurrentUser() != null) {
                        //在这里findview 不会空指针，不知道为什么
                        tv_navi_account = (TextView) findViewById(R.id.tv_navi_account);
                        tv_navi_account.setText(AVUser.getCurrentUser().getUsername());
                        //不能findview 要通过特定方法
                        mnaNavigationView.getMenu().getItem(6).getSubMenu().getItem(0).setTitle("注销");
                    }
                } else {
                    Log.e("getNewMovies error", e.toString());
                }

            }
        });
    }
    private void getFavMovies(){
        final ProgressDialog progressDialog=new ProgressDialog(this,"加载中");
        progressDialog.show();
        currentMode=FAV_MOVIES;
        ab.setTitle("我的收藏");
        if(movieObjectList!=null)
            movieObjectList.clear();


            if (AVUser.getCurrentUser() != null) {
                AVQuery<AVObject> query = new AVQuery<>("Favorite");
                query.whereEqualTo("user", AVUser.getCurrentUser());
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (e == null && list.size() > 0) {
                            Log.e("list size", list.size() + "");
                            for (int i = 0; i < list.size(); i++) {
                                AVObject mObject = (AVObject) list.get(i).get("movie");
                                mObject.fetchInBackground(new GetCallback<AVObject>() {
                                    @Override
                                    public void done(AVObject avObject, AVException e) {
                                        movieObjectList.add(avObject);
//                                        Movie mMovie=new Movie();
//                                        mMovie.setTitle(mObject.getString("title"));
//                                        mMovie.setCode(mMovie.getTitle().split(" ")[0]);
//                                        mMovie.setIssueDate(mObject.getString("issuedate"));
//                                        mMovie.setCatagory(mObject.getString("category"));
//                                        mMovie.setActress(mObject.getString("actress"));
//                                        mMovie.setCoverImg(mObject.getString("coverimg"));
//                                        mMovie.setHot(mObject.getInt("hot"));
//                                        movieList.add(mMovie);
                                        lv_movie_home_movie.smoothScrollToPosition(0);
                                        adapter.notifyDataSetChanged();
                                        progressDialog.dismiss();

                                    }
                                });
                            }
                            list = null;
                            if (AVUser.getCurrentUser() != null) {
                                //在这里findview 不会空指针，不知道为什么
                                tv_navi_account = (TextView) findViewById(R.id.tv_navi_account);
                                tv_navi_account.setText(AVUser.getCurrentUser().getUsername());
                                //不能findview 要通过特定方法
                                mnaNavigationView.getMenu().getItem(6).getSubMenu().getItem(0).setTitle("注销");
                            }
                        }else {
                            adapter.notifyDataSetChanged();
                            progressDialog.dismiss();
                        }
                    }
                });
            }

    }


    class myMovieAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return movieObjectList.size();
        }

        @Override
        public Object getItem(int position) {
            return movieObjectList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            Movie mMovie=movieList.get(position);
            AVObject movieObject=movieObjectList.get(position);
            View view;
            ViewHolder viewHolder;
            if(convertView==null){
                view= LayoutInflater.from(MovieHomeActivity.this).inflate(R.layout.search_list_item,null);
                viewHolder=new ViewHolder();
                viewHolder.iv_search_result= (ImageView) view.findViewById(R.id.iv_search_result);
                viewHolder.tv_search_result_actress= (TextView) view.findViewById(R.id.tv_search_result_actress);
                viewHolder.tv_search_result_name= (TextView) view.findViewById(R.id.tv_search_result_name);
                viewHolder.tv_search_result_category= (TextView) view.findViewById(R.id.tv_search_result_category);
                viewHolder.tv_search_result_like= (TextView) view.findViewById(R.id.tv_search_resullt_like);
                viewHolder.tv_search_result_code= (TextView) view.findViewById(R.id.tv_search_resullt_code);
                viewHolder.tv_search_result_time= (TextView) view.findViewById(R.id.tv_search_resullt_time);
                view.setTag(viewHolder);
            }else {
                view=convertView;
                viewHolder= (ViewHolder) view.getTag();
            }
            if(movieObject.getString("coverimg").contains("pl.jpg")) {
                Glide.with(MovieHomeActivity.this).load(movieObject.getString("coverimg").replace("pl.jpg", "ps.jpg")).crossFade().centerCrop().into(viewHolder.iv_search_result);
            }
            String title=movieObject.getString("title");
            title=title.substring(title.indexOf(" ")+1);
            viewHolder.tv_search_result_name.setText(title);
            String[] actress=movieObject.getString("actress").split("\\|");
            if(actress.length==1){
                viewHolder.tv_search_result_actress.setText(actress[0]);
            }else {
                viewHolder.tv_search_result_actress.setText(actress[0]+"等"+(actress.length-1)+"人");
            }
            viewHolder.tv_search_result_category.setText(movieObject.getString("category").replace("|"," "));
            viewHolder.tv_search_result_like.setText(movieObject.getInt("hot")+"");
            viewHolder.tv_search_result_code.setText(movieObject.getString("title").split(" ")[0]);
            viewHolder.tv_search_result_time.setText(movieObject.getString("issuedate"));
            return view;
        }
        class ViewHolder{
            ImageView iv_search_result;
            TextView tv_search_result_name;
            TextView tv_search_result_category;
            TextView tv_search_result_actress;
            TextView tv_search_result_like;
            TextView tv_search_result_code;
            TextView tv_search_result_time;
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
                if(currentMode==NEW_MOVIES)
                    getNewMovies();
                if(currentMode==HOT_MOVIES)
                    getHotMovies();
                if(currentMode==FAV_MOVIES)
                    getFavMovies();
                break;
            case android.R.id.home:
                mdDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }
    public void moveTop(View view){
        if(lv_movie_home_movie!=null)
            lv_movie_home_movie.smoothScrollToPosition(0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1&&resultCode==RESULT_OK){
            tv_navi_account= (TextView) findViewById(R.id.tv_navi_account);
            tv_navi_account.setText(AVUser.getCurrentUser().getUsername());
            mnaNavigationView.getMenu().getItem(6).getSubMenu().getItem(0).setTitle("注销");
        }
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
