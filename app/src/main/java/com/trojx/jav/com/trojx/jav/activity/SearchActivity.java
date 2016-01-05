package com.trojx.jav.com.trojx.jav.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.trojx.jav.R;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.kaede.tagview.OnTagClickListener;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText et_search;
    private TagView hot_search;
    private ListView lv_search_result;
    private String[] hotTags=new String[5];
    private MyresultAdapter adapter;
    private ProgressDialog progressDialog;
    private LinearLayout ll_hot_tag;
    private FloatingActionButton fab_moveToUp;
    private String currentSearch;
    private FloatingActionButton fab_searchAll;
    private LinkedList<AVObject> movieObjectList=new LinkedList<AVObject>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        findViews();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        getHotTags();


        Intent intent=getIntent();
        currentSearch=intent.getStringExtra("actressName");
        Log.e("current search",currentSearch);
        if(!currentSearch.isEmpty()){
            et_search.setText(currentSearch);
            et_search.setSelection(currentSearch.length());
            search();
        }

        adapter=new MyresultAdapter();
        lv_search_result.setAdapter(adapter);
        lv_search_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AVQuery<AVObject> query=new AVQuery<AVObject>("Movie");
                query.whereEqualTo("code",movieObjectList.get(position).getString("title").split(" ")[0]);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if(e==null){
                            AVObject item=list.get(0);
                            item.put("hot",item.getInt("hot")+1);
                            item.saveInBackground();
                        }
                    }
                });
                Intent intent = new Intent(SearchActivity.this, MovieMain.class);
                intent.putExtra("movieObject", movieObjectList.get(position).toString());
                intent.putExtra("isFromSearch",true);
                startActivityForResult(intent,1);
            }
        });
        lv_search_result.setOnScrollListener(new AbsListView.OnScrollListener() {
            private  int totalCount;
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState){
                    case SCROLL_STATE_IDLE:
                        if(totalCount!=0)
                        fab_moveToUp.show();
                        break;
                    case SCROLL_STATE_FLING:
                        fab_moveToUp.hide();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                totalCount=totalItemCount;
                if (firstVisibleItem != 1) {
                    ll_hot_tag.setVisibility(View.GONE);//应优化为属性动画
                }
                if (firstVisibleItem == 0) {
                    ll_hot_tag.setVisibility(View.VISIBLE);
                }
                if(totalItemCount==100&&firstVisibleItem==totalItemCount-visibleItemCount){
                    fab_searchAll.show();
                }else {
                    fab_searchAll.hide();
                }
            }
        });



    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_search:
                movieObjectList.clear();//先清空所有数据
                search();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private  void findViews(){
        toolbar= (Toolbar) findViewById(R.id.search_toolbar);
        et_search= (EditText) findViewById(R.id.et_search);
        hot_search= (TagView) findViewById(R.id.tagview_hot);
        lv_search_result= (ListView) findViewById(R.id.lv_search_result);
        ll_hot_tag= (LinearLayout) findViewById(R.id.ll_hot_tag);
        fab_moveToUp= (FloatingActionButton) findViewById(R.id.fab_search_move_up);
        fab_searchAll= (FloatingActionButton) findViewById(R.id.fab_search_all);
        fab_searchAll.setVisibility(View.INVISIBLE);

        toolbar.setBackgroundColor(getResources().getColor(R.color.logoback));

        hot_search.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                et_search.setText(tag.text);
                et_search.setSelection(tag.text.length());//将光标显示到文本最后
            }
        });

        fab_moveToUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movieObjectList != null) {
                    lv_search_result.smoothScrollToPosition(0);
                }
            }
        });

    }
    private void getHotTags(){
        AVQuery<AVObject> query=new AVQuery<AVObject>("HotSearch");
        query.limit(5);
        query.orderByDescending("SearchCount");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    for (int i = 0; i < list.size(); i++) {
                        hotTags[i] = list.get(i).getString("Keyword");
                    }
                    hot_search.addTags(hotTags);
                } else {
                    Log.e("hotTags", e.toString());
                }
            }
        });
    }
    private void search(){
        if (et_search.getText().toString().isEmpty()){
//            Toast.makeText(this,"搜索条件不能为空!",Toast.LENGTH_SHORT).show();
            Dialog dialog=new Dialog(this,"错误","搜索关键词不能为空！");
            dialog.show();
            dialog.getButtonAccept().setText("好的");
            dialog.setCancelable(false);
        }else {
            currentSearch=et_search.getText().toString();
            showDialog();
            postTag();
            AVQuery<AVObject> queryCode=AVQuery.getQuery("Movie");
            queryCode.whereContains("code",currentSearch);
            AVQuery<AVObject> queryCategory=AVQuery.getQuery("Movie");
            queryCategory.whereContains("category",currentSearch);
            AVQuery<AVObject> queryPruducer=AVQuery.getQuery("Movie");
            queryPruducer.whereContains("producer",currentSearch);
            AVQuery<AVObject> queryPulisher=AVQuery.getQuery("Movie");
            queryPulisher.whereContains("publisher",currentSearch);
            AVQuery<AVObject> querySeries=AVQuery.getQuery("Movie");
            querySeries.whereContains("series",currentSearch);
            AVQuery<AVObject> queryDirector=AVQuery.getQuery("Movie");
            queryDirector.whereContains("director",currentSearch);
            AVQuery<AVObject> queryTitle=AVQuery.getQuery("Movie");
            queryTitle.whereContains("title", currentSearch);
            AVQuery<AVObject> queryActress=AVQuery.getQuery("Movie");
            queryActress.whereContains("actress", currentSearch);
            List<AVQuery<AVObject>> queries = new ArrayList<AVQuery<AVObject>>();
            queries.add(queryActress);
            queries.add(queryCategory);
            queries.add(queryCode);
            queries.add(queryDirector);
            queries.add(queryPruducer);
            queries.add(queryPulisher);
            queries.add(querySeries);
            queries.add(queryTitle);
            AVQuery<AVObject> mainQuery = AVQuery.or(queries);
            mainQuery.setLimit(100);
            mainQuery.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if(e==null){
                        Toast.makeText(SearchActivity.this,"search done",Toast.LENGTH_SHORT).show();
                        movieObjectList= (LinkedList<AVObject>) list;
//                        Log.e("movieObjectList size",movieObjectList.size()+"");
//                        Log.e("position 0",movieObjectList.get(0).toString());
//                        Log.e("position 1",movieObjectList.get(1).toString());
//                            mMovie.setActress(movieObject.getString("actress"));
////                            mMovie.setActressImg(movieObject.getString("actressimg"));
//                            mMovie.setCatagory(movieObject.getString("category"));
//                            mMovie.setCoverImg(movieObject.getString("coverimg"));
////                            mMovie.setDirector(movieObject.getString("director"));
//                            mMovie.setIssueDate(movieObject.getString("issuedate"));
////                            mMovie.setProducer(movieObject.getString("producer"));
////                            mMovie.setPublisher(movieObject.getString("publisher"));
////                            mMovie.setSampleImgs_big(movieObject.getString("sampleimgsbig"));
////                            mMovie.setSampleImgs_small(movieObject.getString("sampleimgssmall"));
////                            mMovie.setTimelong(movieObject.getString("timelong"));
////                            mMovie.setSeries(movieObject.getString("series"));
//                            mMovie.setTitle(movieObject.getString("title"));
////                            mMovie.setUrl(movieObject.getString("url"));
//                            mMovie.setCode(mMovie.getTitle().split(" ")[0]);
//                            mMovie.setHot(movieObject.getInt("hot"));
//
//                            movieList.add(mMovie);
//                            mMovie=null;
//                            movieObject=null;

//                        System.gc();
                        adapter.notifyDataSetChanged();
                        if(movieObjectList.size()>0) {
                            lv_search_result.setSelection(0);
                        }
//                        Toast.makeText(SearchActivity.this,"搜索完成！",Toast.LENGTH_SHORT).show();
                        dismissDialog();
                    }else {
                        Log.e("Search error", e.toString());
                    }
                }
            });
        }
    }
    public void searchAll(View view){
        showDialog();
        movieObjectList.clear();
        AVQuery<AVObject> queryCode=AVQuery.getQuery("Movie");
        queryCode.whereContains("code",currentSearch);
        AVQuery<AVObject> queryCategory=AVQuery.getQuery("Movie");
        queryCategory.whereContains("category",currentSearch);
        AVQuery<AVObject> queryPruducer=AVQuery.getQuery("Movie");
        queryPruducer.whereContains("producer",currentSearch);
        AVQuery<AVObject> queryPulisher=AVQuery.getQuery("Movie");
        queryPulisher.whereContains("publisher",currentSearch);
        AVQuery<AVObject> querySeries=AVQuery.getQuery("Movie");
        querySeries.whereContains("series",currentSearch);
        AVQuery<AVObject> queryDirector=AVQuery.getQuery("Movie");
        queryDirector.whereContains("director",currentSearch);
        AVQuery<AVObject> queryTitle=AVQuery.getQuery("Movie");
        queryTitle.whereContains("title", currentSearch);
        AVQuery<AVObject> queryActress=AVQuery.getQuery("Movie");
        queryActress.whereContains("actress", currentSearch);
        List<AVQuery<AVObject>> queries = new ArrayList<AVQuery<AVObject>>();
        queries.add(queryActress);
        queries.add(queryCategory);
        queries.add(queryCode);
        queries.add(queryDirector);
        queries.add(queryPruducer);
        queries.add(queryPulisher);
        queries.add(querySeries);
        queries.add(queryTitle);
        AVQuery<AVObject> mainQuery = AVQuery.or(queries);
        mainQuery.setLimit(1000);
        mainQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e==null){
                    Toast.makeText(SearchActivity.this,"search done",Toast.LENGTH_SHORT).show();
//                    movieObjectList= (ArrayList<AVObject>) list;
                    movieObjectList.addAll(movieObjectList.size(),list);

//                        mMovie.setActress(movieObject.getString("actress"));
////                        mMovie.setActressImg(movieObject.getString("actressimg"));
//                        mMovie.setCatagory(movieObject.getString("category"));
//                        mMovie.setCoverImg(movieObject.getString("coverimg"));
////                        mMovie.setDirector(movieObject.getString("director"));
//                        mMovie.setIssueDate(movieObject.getString("issuedate"));
////                        mMovie.setProducer(movieObject.getString("producer"));
////                        mMovie.setPublisher(movieObject.getString("publisher"));
////                        mMovie.setSampleImgs_big(movieObject.getString("sampleimgsbig"));
////                        mMovie.setSampleImgs_small(movieObject.getString("sampleimgssmall"));
////                        mMovie.setTimelong(movieObject.getString("timelong"));
////                        mMovie.setSeries(movieObject.getString("series"));
//                        mMovie.setTitle(movieObject.getString("title"));
////                        mMovie.setUrl(movieObject.getString("url"));
//                        mMovie.setCode(mMovie.getTitle().split(" ")[0]);
//                        mMovie.setHot(movieObject.getInt("hot"));
//
//                        movieList.add(mMovie);
//                        mMovie=null;
//                        movieObject=null;
                    adapter.notifyDataSetChanged();
//                        Toast.makeText(SearchActivity.this,"搜索完成！",Toast.LENGTH_SHORT).show();
                    dismissDialog();
                    fab_searchAll.hide();
                }else {
                    Log.e("Search error",e.toString());
                }
            }
        });
    }
    class MyresultAdapter extends BaseAdapter{

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
            AVObject movieObject=movieObjectList.get(position);
            View view;
            ViewHolder viewHolder;
            if(convertView==null){
                view= LayoutInflater.from(SearchActivity.this).inflate(R.layout.search_list_item,null);
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
            if(movieObject.getString("coverimg").contains("pl.jpg")){
                Glide.with(SearchActivity.this).load(movieObject.getString("coverimg").replace("pl.jpg","ps.jpg")).crossFade().centerCrop().into(viewHolder.iv_search_result);
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

    /**
     * 显示进度对话框
     */
    private void showDialog(){
        progressDialog=new ProgressDialog(this,"搜索中");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();
    }

    /**
     * 取消显示进度对话框
     */
    private  void dismissDialog(){
        progressDialog.dismiss();
    }

    /**
     * 向云端更新搜索关键词，存在则递增，否则新建
     */
    private  void postTag(){
        AVQuery<AVObject> query=new AVQuery<AVObject>("HotSearch");
        final String tag=et_search.getText().toString();
        query.whereEqualTo("Keyword",tag);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(list.size()==0){
                    AVObject post=new AVObject("HotSearch");
                    post.put("Keyword",tag);
                    post.put("SearchCount", 1);
                    post.saveInBackground();
                }else {
                    AVObject post1=list.get(0);
                    int count=post1.getInt("SearchCount");
                    post1.put("SearchCount",count+1);
                   post1.saveInBackground();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lv_search_result=null;
        adapter=null;
        lv_search_result=null;
        hotTags=null;
        movieObjectList=null;
        this.setContentView(R.layout.empty_layout);
        System.gc();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==RESULT_OK){
            et_search.setText(data.getStringExtra("searchKey"));
            search();
        }

    }
}
