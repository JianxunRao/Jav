package com.trojx.jav.com.trojx.jav.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.Dialog;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.trojx.jav.R;
import com.trojx.jav.com.trojx.jav.domain.Actress;
import com.trojx.jav.com.trojx.jav.domain.Magnet;
import com.trojx.jav.com.trojx.jav.domain.Movie;

import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.kaede.tagview.OnTagClickListener;
import me.kaede.tagview.Tag;
import me.kaede.tagview.TagView;

public class MovieMain extends AppCompatActivity {


    private ImageView iv_backdrop;
    private Toolbar toolbar;
    private CardView cv_movie_info,cv_actress,cv_sampleimgs;
    private TextView tv_code,tv_issuetime,tv_timelong,tv_producer,
    tv_publisher,tv_series,tv_director;
    private TagView tagView;
    private  ListView lv_actress;
    private  GridView gv_sampleimgs;
    private ButtonRectangle bt_download;
    private FloatingActionButton fab;
    private CollapsingToolbarLayout collapsingToolbar;
    boolean isFav=false;  //标记本movie是否为当前user喜欢的
    private Movie mMovie=new Movie();
    private String[] tags;
    private List<Actress> acctressList=new ArrayList<Actress>();
    private String[] sampleImgsSmall;
    private ProgressDialog progressDialog;
    private AVObject movieObject;
    private Date lastPressBack;
    private boolean isFromSearch=false;
    private String content="";
    private  ArrayList<Magnet> magnetList=new ArrayList<Magnet>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_main);
        findViews();
//        Error:Execution failed for task ':app:transformClassesWithJarMergingForDebug'.
//                > com.android.build.api.transform.TransformException: java.util.zip.ZipException: duplicate entry: org/htmlparser/util/SimpleNodeIterator.class

        Intent intent=getIntent();
//        final String code=intent.getStringExtra("code");
        try {
            movieObject=AVObject.parseAVObject(intent.getStringExtra("movieObject"));
            isFromSearch = intent.getBooleanExtra("isFromSearch",false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        progressDialog=new ProgressDialog(this,"载入中");
        progressDialog.show();
        //AVObject...
        getData();
        getBtupResponse();//从BTUP下载磁力链接信息


        tagView.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                //打开搜索activity搜索对应tag关键词

            }
        });

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar= (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        fab.setBackgroundColor(Color.CYAN);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFav) {

                    if (movieObject != null) {
                        if (AVUser.getCurrentUser() != null) {
                            AVObject favRec = new AVObject("Favorite");
                            favRec.put("user", AVUser.getCurrentUser());
                            favRec.put("movie", movieObject);
                            favRec.saveInBackground();
                            fab.setImageResource(R.drawable.ic_favorite_white_48dp);
                            isFav = true;
                            Toast.makeText(MovieMain.this, "收藏成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Dialog dialog = new Dialog(MovieMain.this, "未登录", "收藏请先登录.");
                            dialog.show();
                            dialog.getButtonAccept().setText("好的");
                        }
                    }
                    //...
                } else {

                    //...
                    if (movieObject != null) {
                        if (AVUser.getCurrentUser() != null) {
                            AVQuery<AVObject> query = new AVQuery<AVObject>("Favorite");
                            query.whereEqualTo("user", AVUser.getCurrentUser());
                            query.whereEqualTo("movie", movieObject);
                            query.findInBackground(new FindCallback<AVObject>() {
                                @Override
                                public void done(List<AVObject> list, AVException e) {
                                    if (e == null && list.size() != 0) {
                                        for (AVObject item : list) {
                                            Log.e("list length", list.size() + "");
                                            Log.e("item toString", item.toString());
                                            item.deleteInBackground();
                                        }
                                        fab.setImageResource(R.drawable.ic_favorite_border_white_48dp);
                                        isFav = false;
                                        Toast.makeText(MovieMain.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (e != null)
                                            Log.e("delete favorite error", e.toString());
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
        gv_sampleimgs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MovieMain.this, PicViewerActivity.class);
                Bundle bundle = new Bundle();
                ArrayList<String> urls = new ArrayList<String>();
                Collections.addAll(urls, mMovie.getSampleImgs_big().split("\\|"));
                bundle.putStringArrayList("urls", urls);
                bundle.putString("title", mMovie.getTitle());
                intent.putExtra("urls", bundle);
                startActivity(intent);
            }
        });
        lv_actress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isFromSearch) {
                    Intent intent1 = new Intent();
                    intent1.putExtra("searchKey", acctressList.get(position).getName());
                    Log.e("actress list", acctressList.get(position).getName());
                    setResult(RESULT_OK, intent1);//如果从MovieHome启动呢？
                    finish();
                } else {
                    Intent intent2 = new Intent(MovieMain.this, SearchActivity.class);
                    intent2.putExtra("actressName", acctressList.get(position).getName());//若从MovieHome启动
                    Log.e("actress list", acctressList.get(position).getName());
                    startActivity(intent2);
                }
            }
        });
        tagView.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                if (isFromSearch) {
                    Intent intent1 = new Intent();
                    intent1.putExtra("searchKey", tag.text);
                    setResult(RESULT_OK, intent1);//如果从MovieHome启动呢？
                    finish();
                } else {
                    Intent intent2 = new Intent(MovieMain.this, SearchActivity.class);
                    intent2.putExtra("actressName", tag.text);//若从MovieHome启动
                    startActivity(intent2);
                }
            }
        });
        bt_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieMain.this, MagnetActivity.class);
                intent.putExtra("magnetList", magnetList);
                startActivity(intent);
                movieObject.put("magnetList", magnetList);
                movieObject.saveInBackground();
            }
        });
        iv_backdrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> list=new ArrayList<String>();
                list.add(movieObject.getString("coverimg").split("\\|")[0]);
                Bundle bundle=new Bundle();
                bundle.putStringArrayList("urls", list);
                bundle.putString("title", movieObject.getString("title"));
                Intent intent1=new Intent(MovieMain.this,PicViewerActivity.class);
                intent1.putExtra("urls",bundle);
                startActivity(intent1);
            }
        });
    }
    private void findViews(){
        iv_backdrop= (ImageView) findViewById(R.id.backdrop);
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        cv_movie_info= (CardView) findViewById(R.id.cv_movie_info);
        cv_actress= (CardView) findViewById(R.id.cv_actress);
        cv_sampleimgs= (CardView) findViewById(R.id.cv_sampleimgs);
        tv_code= (TextView) findViewById(R.id.tv_code);
        tv_issuetime= (TextView) findViewById(R.id.tv_issuetime);
        tv_timelong= (TextView) findViewById(R.id.tv_timelong);
        tv_producer= (TextView) findViewById(R.id.tv_producer);
        tv_publisher= (TextView) findViewById(R.id.tv_publisher);
        tv_series= (TextView) findViewById(R.id.tv_series);
        tv_director= (TextView) findViewById(R.id.tv_director);
        tagView= (TagView) findViewById(R.id.tagview);
        lv_actress= (ListView) findViewById(R.id.lv_actress);
        gv_sampleimgs= (GridView) findViewById(R.id.gv_sampleimgs);
        bt_download= (ButtonRectangle) findViewById(R.id.bt_download);
        fab= (FloatingActionButton) findViewById(R.id.fab);
        collapsingToolbar= (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return true;
        }
    }

    private void getData(){


                    mMovie.setActress(movieObject.getString("actress"));
                    mMovie.setActressImg(movieObject.getString("actressimg"));
                    mMovie.setCatagory(movieObject.getString("category"));
                    mMovie.setCoverImg(movieObject.getString("coverimg"));
                    mMovie.setDirector(movieObject.getString("director"));
                    mMovie.setIssueDate(movieObject.getString("issuedate"));
                    mMovie.setProducer(movieObject.getString("producer"));
                    mMovie.setPublisher(movieObject.getString("publisher"));
                    mMovie.setSampleImgs_big(movieObject.getString("sampleimgsbig"));
                    mMovie.setSampleImgs_small(movieObject.getString("sampleimgssmall"));
                    mMovie.setTimelong(movieObject.getString("timelong"));
                    mMovie.setSeries(movieObject.getString("series"));
                    mMovie.setTitle(movieObject.getString("title"));
                    mMovie.setUrl(movieObject.getString("url"));
                    mMovie.setCode(mMovie.getTitle().split(" ")[0]);
                    Log.w("movieObject", movieObject.toString());

                    Glide.with(MovieMain.this).load(mMovie.getCoverImg()).crossFade().into(iv_backdrop);

                    tv_code.setText("标识码：" + mMovie.getCode());
                    if (mMovie.getDirector().isEmpty()) {
                        tv_director.setVisibility(View.GONE);
                    } else {
                        tv_director.setText("导演：" + mMovie.getDirector());
                    }
                    if (mMovie.getSeries().isEmpty() || mMovie.getSeries().contentEquals(mMovie.getCatagory())) {
                        tv_series.setVisibility(View.GONE);
                    } else {
                        tv_series.setText("系列：" + mMovie.getSeries());
                    }
                    if (mMovie.getPublisher().isEmpty()) {
                        tv_publisher.setVisibility(View.GONE);
                    } else {
                        tv_publisher.setText("发行商：" + mMovie.getPublisher());
                    }
                    if (mMovie.getProducer().isEmpty()) {
                        tv_producer.setVisibility(View.GONE);
                    } else {
                        tv_producer.setText("制作商：" + mMovie.getProducer());
                    }
                    if (mMovie.getIssueDate().isEmpty()) {
                        tv_issuetime.setVisibility(View.GONE);
                    } else {
                        tv_issuetime.setText("发行时间：" + mMovie.getIssueDate());
                    }
                    if (mMovie.getTimelong().isEmpty()) {
                        tv_timelong.setVisibility(View.GONE);
                    } else {
                        tv_timelong.setText("时长：" + mMovie.getTimelong());
                    }

                    tags = mMovie.getCatagory().split("\\|");
                    tagView.addTags(tags);


                    collapsingToolbar.setTitle(mMovie.getTitle().substring(mMovie.getTitle().indexOf(" ")));

                    String[] actress = mMovie.getActress().split("\\|");
                    String[] actressimg = mMovie.getActressImg().split("\\|");
                    for (int i = 0; i < actress.length; i++) {
                        Actress mActress = new Actress();
                        mActress.setName(actress[i]);
                        mActress.setAvatar(actressimg[i]);
                        acctressList.add(mActress);
                    }
                    ActressAdapter adapter = new ActressAdapter();
                    lv_actress.setAdapter(adapter);
                    View view = adapter.getView(0, null, lv_actress);
                    view.measure(0, 0);
                    ViewGroup.LayoutParams params = lv_actress.getLayoutParams();
                    params.height = view.getMeasuredHeight() * acctressList.size() + lv_actress.getDividerHeight() * (acctressList.size() - 1);
                    lv_actress.setLayoutParams(params);

                    sampleImgsSmall = mMovie.getSampleImgs_small().split("\\|");//
                    Log.w("sampleimgs", sampleImgsSmall.length + "");
                    SampleImgAdapter adapter1 = new SampleImgAdapter();
                    gv_sampleimgs.setAdapter(adapter1);

                    setGridViewHeightBasedOnChildren(gv_sampleimgs);

                    //获取当前user是否收藏此movie
                    if (movieObject != null) {
                        if (AVUser.getCurrentUser() != null) {
                            Log.e("获取当前user是否收藏此movie", "");
                            AVQuery<AVObject> query1 = new AVQuery<AVObject>("Favorite");
                            query1.whereEqualTo("user", AVUser.getCurrentUser());
                            query1.whereEqualTo("movie", movieObject);
                            query1.findInBackground(new FindCallback<AVObject>() {
                                @Override
                                public void done(List<AVObject> list, AVException e) {
                                    if (e == null && list.size() != 0) {
                                        Log.e("find success", list.size() + "");
                                        fab.setImageResource(R.drawable.ic_favorite_white_48dp);
                                        isFav = true;
                                    } else {
                                        Log.e("get isfavorite error", "");
                                    }
                                }
                            });
                        }
                    }
                    progressDialog.dismiss();
    }
    private class ActressAdapter extends BaseAdapter{



        @Override
        public int getCount() {
            return acctressList.size();
        }

        @Override
        public Object getItem(int position) {
            return acctressList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Actress mActress= (Actress) getItem(position);
            View view;
            ViewHolder viewHolder;
            if (convertView==null){
                view= LayoutInflater.from(MovieMain.this).inflate(R.layout.actress_list_item,null);
                viewHolder=new ViewHolder();
                viewHolder.civ_actressImg= (CircleImageView) view.findViewById(R.id.civ_actress_img);
                viewHolder.tv_actressName= (TextView) view.findViewById(R.id.tv_actress_name);
                view.setTag(viewHolder);
            }else {
                view=convertView;
                viewHolder= (ViewHolder) view.getTag();
            }
            Glide.with(MovieMain.this).load(mActress.getAvatar()).crossFade().into(viewHolder.civ_actressImg);
            viewHolder.tv_actressName.setText(mActress.getName());
            return  view;
        }
        class  ViewHolder{
            CircleImageView civ_actressImg;
            TextView tv_actressName;
        }
    }
    private  class SampleImgAdapter extends  BaseAdapter{

        @Override
        public int getCount() {
            return sampleImgsSmall.length;
        }

        @Override
        public Object getItem(int position) {
            return sampleImgsSmall[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            view=LayoutInflater.from(MovieMain.this).inflate(R.layout.sample_img_grid_item,null);
            ImageView iv= (ImageView) view.findViewById(R.id.iv_sample_small);
            Glide.with(MovieMain.this).load(sampleImgsSmall[position]).crossFade().into(iv);
            return view;
        }
    }
//    public  void download(View view){
//        Intent intent=new Intent(MovieMain.this,MagnetActivity.class);
//        intent.putExtra("magnetList",magnetList);
//        startActivity(intent);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        acctressList=null;
        lv_actress=null;
        gv_sampleimgs=null;
        sampleImgsSmall=null;
        tags=null;
        tagView=null;
        System.gc();
    }


    //计算gridview高度
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


    private void getBtupResponse(){

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        StringRequest stringRequest=new StringRequest("http://www.btup.net/s/" + movieObject.getString("title").split(" ")[0] + "/1/", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                content=response.substring(1);
                magnetList=parseMagnet();
                Log.e("magnetList size",magnetList.size()+"");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                content="";
            }
        });
        requestQueue.add(stringRequest);
    }


    private ArrayList<Magnet> parseMagnet(){
        ArrayList<Magnet> magnetList=new ArrayList<>();
        if(!content.isEmpty()){
            Log.e("content",content.substring(0,100));
            try{
                Parser parser=new Parser(content);
                TagNameFilter filter=new TagNameFilter("a");
                NodeList nodes=parser.extractAllNodesThatMatch(filter);
                for(int i=0;i<nodes.size();i++) {
                    String value = nodes.elementAt(i).getText();
                    if (value.contains("www.btup.net/info/")) {
                        Magnet magnet = new Magnet();
                        value = value.substring(33, 73);
                        magnet.setValue(value);
//					System.out.println(value);
                        String name = nodes.elementAt(i).toPlainTextString();
//					System.out.println(i+name);
                        magnet.setName(name);
                        magnetList.add(magnet);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
            return  magnetList;
    }
}

