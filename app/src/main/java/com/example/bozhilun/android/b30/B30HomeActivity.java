package com.example.bozhilun.android.b30;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.KeyEvent;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.FragmentAdapter;
import com.example.bozhilun.android.b30.b30datafragment.B30DataFragment;
import com.example.bozhilun.android.b30.b30homefragment.B30HomeFragment;
import com.example.bozhilun.android.b30.b30minefragment.B30MineFragment;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.run.W30sNewRunFragment;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.widget.NoScrollViewPager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import org.litepal.tablemanager.Connector;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/7/20.
 */

public class B30HomeActivity extends WatchBaseActivity {


    @BindView(R.id.b30View_pager)
    NoScrollViewPager b30ViewPager;
    @BindView(R.id.b30BottomBar)
    BottomBar b30BottomBar;

    private List<Fragment> b30FragmentList = new ArrayList<>();


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1001:
                    if(MyApp.getB30ConnStateService() != null){
                        String bm = (String) SharedPreferencesUtils.readObject(B30HomeActivity.this,"mylanmac");
                        MyApp.getB30ConnStateService().connB30ConnBle(bm.trim());
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_home);
        ButterKnife.bind(this);
        SQLiteDatabase db = Connector.getDatabase();

        initViews();

        initData();
    }

    private void initData() {
    }


    private void initViews() {
        b30FragmentList.add(new B30HomeFragment());
        b30FragmentList.add(new B30DataFragment());
        b30FragmentList.add(new W30sNewRunFragment());
        b30FragmentList.add(new B30MineFragment());
        FragmentStatePagerAdapter fragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), b30FragmentList);
        b30ViewPager.setAdapter(fragmentPagerAdapter);
        b30BottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                switch (tabId){
                    case R.id.b30_tab_home: //首页
                        b30ViewPager.setCurrentItem(0);
                        break;
                    case R.id.b30_tab_data: //数据
                        b30ViewPager.setCurrentItem(1);
                        break;
                    case R.id.b30_tab_set:  //开跑
                        b30ViewPager.setCurrentItem(2);
                        break;
                    case R.id.b30_tab_my:   //我的
                        b30ViewPager.setCurrentItem(3);
                        break;
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MyCommandManager.DEVICENAME == null){    //未连接
            if(MyApp.getB30ConnStateService() != null){
                String bm = (String) SharedPreferencesUtils.readObject(B30HomeActivity.this,"mylanmac");
                if(!WatchUtils.isEmpty(bm)){
                    MyApp.getB30ConnStateService().connB30ConnBle(bm.trim());
                }

            }else{
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(1001);
                    }
                }, 3 * 1000);
            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 过滤按键动作
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);

        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            moveTaskToBack(true);
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }
}
