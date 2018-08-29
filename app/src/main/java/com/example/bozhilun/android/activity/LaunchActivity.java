package com.example.bozhilun.android.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.B30HomeActivity;
import com.example.bozhilun.android.h9.H9HomeActivity;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.WatchHomeActivity;
import com.example.bozhilun.android.siswatch.utils.SharedPreferenceUtil;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.w30s.W30SHomeActivity;
import com.yanzhenjie.permission.AndPermission;

/**
 * 启动页
 */
public class LaunchActivity extends WatchBaseActivity {

    private static final String TAG = "LaunchActivity";

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean isGuid = (boolean) msg.obj;
            Log.e(TAG,"---isGuid="+isGuid);
            if(isGuid){
                switchLoginUser();
            }else{
                startActivity(NewGuidActivity.class);
                finish();
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_layout);


        initData();


        final boolean isGuide = (boolean) SharedPreferencesUtils.getParam(LaunchActivity.this,"isGuide",false);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                message.what = 1001;
                message.obj = isGuide;
                handler.sendMessage(message);
            }
        }, 3 * 1000);

    }

    private void initData() {

        //B30目标步数 默认8000
        int goalStep = (int) SharedPreferencesUtils.getParam(MyApp.getContext(),"b30Goal",0);
        if(goalStep==0){
            SharedPreferencesUtils.setParam(MyApp.getContext(),"b30Goal",8000);
        }
        String b30SleepGoal = (String) SharedPreferencesUtils.getParam(MyApp.getContext(),"b30SleepGoal","");
        if(WatchUtils.isEmpty(b30SleepGoal)){
            SharedPreferencesUtils.setParam(MyApp.getContext(),"b30SleepGoal","8.0");
        }
        //B30的默认密码
        String b30Pwd = (String) SharedPreferencesUtils.getParam(MyApp.getContext(),"b30pwd","");
        if(WatchUtils.isEmpty(b30Pwd)){
            SharedPreferencesUtils.setParam(MyApp.getContext(),"b30pwd","0000");
        }

        com.suchengkeji.android.w30sblelibrary.utils.SharedPreferenceUtil.put(LaunchActivity.this, "w30sunit", true);
        //H8手表初始化消息提醒开关
        String isFirst = (String) SharedPreferencesUtils.getParam(LaunchActivity.this, "msgfirst", "");
        if (WatchUtils.isEmpty(isFirst)) {
            SharedPreferencesUtils.saveObject(LaunchActivity.this, "weixinmsg", "1");
            SharedPreferencesUtils.saveObject(LaunchActivity.this, "msg", "1");
            SharedPreferencesUtils.saveObject(LaunchActivity.this, "qqmsg", "1");
            SharedPreferencesUtils.saveObject(LaunchActivity.this, "Viber", "1");
            SharedPreferencesUtils.saveObject(LaunchActivity.this, "Twitteraa", "1");
            SharedPreferencesUtils.saveObject(LaunchActivity.this, "facebook", "1");
            SharedPreferencesUtils.saveObject(LaunchActivity.this, "Whatsapp", "1");
            SharedPreferencesUtils.saveObject(LaunchActivity.this, "Instagrambutton", "1");
            SharedPreferencesUtils.saveObject(LaunchActivity.this, "laidian", "0");
            SharedPreferencesUtils.setParam(MyApp.getApplication(), "laidianphone", "off");
        }else{
            //初始化H8消息提醒功能
            SharedPreferencesUtils.setParam(LaunchActivity.this, "msgfirst", "isfirst");
        }

        //申请读取通讯录的权限
        AndPermission.with(LaunchActivity.this)
                .runtime()
                .permission(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_SMS, Manifest.permission.WRITE_SETTINGS)
                .start();

    }

    //判断进入的页面
    private void switchLoginUser() {
        String userId = (String) SharedPreferencesUtils.readObject(LaunchActivity.this, "userId");
        Log.e(TAG,"----userId="+userId);
        //判断有没有登录
        if (null != SharedPreferencesUtils.readObject(LaunchActivity.this, "userId")) {
            Log.e("GuideActivity", "--------蓝牙---" + SharedPreferencesUtils.readObject(LaunchActivity.this, "mylanya"));
            String btooth = (String) SharedPreferencesUtils.readObject(LaunchActivity.this, "mylanya");
            String w30sbtooth = (String) SharedPreferenceUtil.get(LaunchActivity.this, "mylanya", "");
            if (!WatchUtils.isEmpty(btooth) || !WatchUtils.isEmpty(w30sbtooth)) {
                if ("bozlun".equals(btooth)) {    //H8 手表
                    startActivity(new Intent(LaunchActivity.this, WatchHomeActivity.class));
                } else if ("W06X".equals(btooth)) {   // H9 手表
                    startActivity(new Intent(LaunchActivity.this, H9HomeActivity.class));
                } else if ("W30".equals(w30sbtooth) || "w30".equals(w30sbtooth)) {
                    startActivity(new Intent(LaunchActivity.this, W30SHomeActivity.class));
                } else if ("B30".equals(btooth)) {
                    startActivity(new Intent(LaunchActivity.this, B30HomeActivity.class));
                } else {
                    startActivity(new Intent(LaunchActivity.this, NewSearchActivity.class));
                }
            } else {
                startActivity(new Intent(LaunchActivity.this, NewSearchActivity.class));
            }
            finish();
        } else {
            startActivity(new Intent(LaunchActivity.this, LoginActivity.class));
        }
    }
}
