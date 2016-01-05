package com.trojx.jav.com.trojx.jav.application;

import android.app.Application;
import android.content.res.Configuration;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.trojx.jav.com.trojx.jav.activity.WebviewActivity;

/**
 * Created by Administrator on 2015/12/26.
 */
public class MyApp extends Application {
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, "JYge9u5xTMBtHLsHARnSCMcv-gzGzoHsz", "xrGOQ8eg5DssCS6HcAF2lUjw");
        AVOSCloud.setLastModifyEnabled(true);//该功能现在正处于 beta 阶段，请谨慎使用。

        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
//                    String installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                    // 关联  installationId 到用户表等操作……
                } else {
                    // 保存失败，输出错误信息
                }
            }
        });

        PushService.setDefaultPushCallback(this, WebviewActivity.class);//调用以下代码启动推送服务，同时设置默认打开的 Activity
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}
