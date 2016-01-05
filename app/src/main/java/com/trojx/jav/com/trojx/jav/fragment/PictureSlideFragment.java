package com.trojx.jav.com.trojx.jav.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.trojx.jav.R;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Administrator on 2016/1/2.
 */
public class PictureSlideFragment extends Fragment {


    private String url;
    private PhotoViewAttacher mAttacher;
    private ImageView imageView=null;
    private ProgressBarCircularIndeterminate progress;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_picture_slide,container,false);
        imageView = (ImageView) v.findViewById(R.id.iv_main_pic);
//        imageView.setImageResource(R.drawable.ic_search_black_48dp);//works!
//        Glide.with(getActivity()).load("http://pics.dmm.co.jp/mono/actjpgs/kamihata_itika.jpg").into(imageView);    //works!
//        Glide.with(getActivity()).load(R.drawable.ic_account_circle_black_48dp).into(imageView);//works!

        mAttacher = new PhotoViewAttacher(imageView);
//        Glide.with(getActivity()).load(R.drawable.ic_arrow_upward_white_48dp).fitCenter().crossFade().into(imageView);
//        mAttacher.update();
        progress = (ProgressBarCircularIndeterminate) v.findViewById(R.id.progressBarCircular);
        Glide.with(getActivity()).load(url).fitCenter().crossFade().into(new GlideDrawableImageViewTarget(imageView){
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                super.onResourceReady(resource, animation);
                mAttacher.update();
                progress.setVisibility(View.GONE);
            }
        });

        return v;
    }


    public static PictureSlideFragment newInstance(String url) {
        PictureSlideFragment f = new PictureSlideFragment();

        Bundle args = new Bundle();
        args.putString("url", url);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments() != null ? getArguments().getString("url") : "";
    }
}
