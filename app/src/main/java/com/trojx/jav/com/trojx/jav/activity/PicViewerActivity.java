package com.trojx.jav.com.trojx.jav.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.trojx.jav.R;
import com.trojx.jav.com.trojx.jav.fragment.PictureSlideFragment;

import java.util.ArrayList;

public class PicViewerActivity extends AppCompatActivity {

    private ViewPager mPager;
    private PictureSlidePagerAdapter mPagerAdapter;
    private ArrayList<String> urlList=new ArrayList<String>();
    private TextView tv_pic_view_title;
    private TextView tv_pic_view_indicator;
    private Toolbar toolbar;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_viewer);

        Intent intent=getIntent();
//        code = intent.getStringExtra("code");
        Bundle bundle=intent.getBundleExtra("urls");
        urlList=bundle.getStringArrayList("urls");
        title = bundle.getString("title");



        tv_pic_view_title = (TextView) findViewById(R.id.tv_pic_view_title);
        tv_pic_view_title.setText(title);

        tv_pic_view_indicator = (TextView) findViewById(R.id.tv_pic_view_indicator);
        toolbar = (Toolbar) findViewById(R.id.toolbar_pic_view);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
        toolbar.setTitle("");
        ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);



        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new PictureSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (urlList != null)
                    tv_pic_view_indicator.setText(String.valueOf(position + 1) + "/" + urlList.size());
//                mPagerAdapter.notifyDataSetChanged();//
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        final ProgressDialog progressDialog=new ProgressDialog(this,"加载中");
//        progressDialog.show();

//        AVQuery<AVObject> query=new AVQuery<>("Movie");
//        query.whereEqualTo("code", code);
//        query.findInBackground(new FindCallback<AVObject>() {
//            @Override
//            public void done(List<AVObject> list, AVException e) {
//                if (e == null && list.size() > 0) {
//                    AVObject item = list.get(0);
//                    Log.e("",item.get("sampleimgsbig").toString());//sampleimg【s】big
//                    Log.e("split", item.get("sampleimgsbig").toString().split("|").toString());
////                    String[] s = {"http://pics.dmm.co.jp/mono/actjpgs/kamihata_itika.jpg", "http://pics.dmm.co.jp/mono/actjpgs/kamihata_itika.jpg"};//works!
//                    String[] s=item.getString("sampleimgsbig").split("\\|");
//                    for (int i = 0; i < s.length; i++)
//                        urlList.add(s[i]);
//                    mPagerAdapter.notifyDataSetChanged();  //
//
//                    tv_pic_view_title.setText(item.getString("title"));
//                    progressDialog.dismiss();
//                } else {
//                    if (e != null)
//                        Log.e("get Urls error", e.toString());
//                }
//            }
//        });
    }


    private class PictureSlidePagerAdapter extends FragmentStatePagerAdapter{


        public PictureSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PictureSlideFragment.newInstance(urlList.get(position));
        }

        @Override
        public int getCount() {
            return urlList.size();//
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
