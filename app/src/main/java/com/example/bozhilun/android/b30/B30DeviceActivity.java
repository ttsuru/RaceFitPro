package com.example.bozhilun.android.b30;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/8/4.
 */

/**
 * 设备页面
 */
public class B30DeviceActivity extends WatchBaseActivity {

    @BindView(R.id.b30DeviceDisconnBtn)
    Button b30DeviceDisconnBtn;
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_device_layout);
        ButterKnife.bind(this);

        initViews();

    }

    private void initViews() {
        commentB30TitleTv.setText(getResources().getString(R.string.device));
        commentB30BackImg.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.b30DeviceDisconnBtn,R.id.commentB30BackImg})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.b30DeviceDisconnBtn:  //解绑
                MyCommandManager.DEVICENAME = null;
                SharedPreferencesUtils.saveObject(MyApp.getContext(), "mylanya", "");
                SharedPreferencesUtils.saveObject(MyApp.getContext(), "mylanmac", "");
                MyCommandManager.deviceAddress = null;
                MyApp.getVpOperateManager().disconnectWatch(new IBleWriteResponse() {
                    @Override
                    public void onResponse(int code) {
                        Log.e("DEVICE", "----code=" + code);
                        startActivity(NewSearchActivity.class);
                        finish();
                    }
                });
                break;
        }

    }
}
