package com.trojx.jav.com.trojx.jav.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.trojx.jav.R;
import com.trojx.jav.com.trojx.jav.domain.Magnet;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/4.
 */
public class MagnetActivity extends AppCompatActivity {

    private ArrayList<Magnet> magnetList;
    private Toolbar toolbar;
    private ListView lv_magnet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnet);

        Intent intent=getIntent();
        magnetList = (ArrayList<Magnet>) intent.getSerializableExtra("magnetList");
        if(magnetList.size()>1)//防止空值时越界
        magnetList.remove(magnetList.size() - 1);

        toolbar = (Toolbar) findViewById(R.id.toolbar_magnet);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        ab.setTitle("下载");
        lv_magnet = (ListView) findViewById(R.id.lv_magnet);
        lv_magnet.setAdapter(new magnetAdapter());
        lv_magnet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1=new Intent(Intent.ACTION_VIEW, Uri.parse("magnet:?xt=urn:btih:"+magnetList.get(position).getValue()));
                intent1.addCategory("android.intent.category.DEFAULT");
                startActivity(intent1);
            }
        });



    }
    private  class  magnetAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return magnetList.size();
        }

        @Override
        public Object getItem(int position) {
            return magnetList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v= LayoutInflater.from(MagnetActivity.this).inflate(R.layout.magnet_list_item,null);
            TextView tv_magnet_item= (TextView) v.findViewById(R.id.tv_magnet_list_item);
            tv_magnet_item.setText(magnetList.get(position).getName());
            return v;
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

    @Override
    public void onBackPressed() {
        finish();
        magnetList=null;
        lv_magnet=null;
        setContentView(R.layout.empty_layout);
    }
}
