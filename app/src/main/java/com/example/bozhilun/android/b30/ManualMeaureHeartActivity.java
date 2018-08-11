package com.example.bozhilun.android.b30;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/8/6.
 */

/**
 * 手动测量心率
 */
public class ManualMeaureHeartActivity extends WatchBaseActivity {

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_meaure_heart);
        ButterKnife.bind(this);

        initViews();


    }

    private void initViews() {
        commentB30TitleTv.setText("手动测量心率");
    }

    @OnClick({R.id.commentB30BackImg, R.id.commentB30ShareImg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.commentB30ShareImg:

                break;
        }
    }
}
