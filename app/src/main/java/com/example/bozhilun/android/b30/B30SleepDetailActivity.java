package com.example.bozhilun.android.b30;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.b30view.B30CusSleepView;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchConstants;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.veepoo.protocol.model.datas.SleepData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/8/16.
 */

/**
 * 睡眠详情
 */
public class B30SleepDetailActivity extends WatchBaseActivity {

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;
    @BindView(R.id.detailSleepQuitRatingBar)
    RatingBar detailSleepQuitRatingBar;
    @BindView(R.id.detailCusSleepView)
    B30CusSleepView detailCusSleepView;
    @BindView(R.id.detailAllSleepTv)
    TextView detailAllSleepTv;
    @BindView(R.id.detailAwakeNumTv)
    TextView detailAwakeNumTv;
    @BindView(R.id.detailStartSleepTv)
    TextView detailStartSleepTv;
    @BindView(R.id.detailAwakeTimeTv)
    TextView detailAwakeTimeTv;
    @BindView(R.id.detailDeepTv)
    TextView detailDeepTv;
    @BindView(R.id.detailHightSleepTv)
    TextView detailHightSleepTv;
    @BindView(R.id.sleepCurrDateTv)
    TextView sleepCurrDateTv;

    private List<Integer> listValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_sleep_detail_layout);
        ButterKnife.bind(this);

        initViews();

        initData();


    }

    private void showSleepChartView(SleepData sleepData) {
        listValue.clear();
        String slleepLin = sleepData.getSleepLine();
        for (int i = 0; i < slleepLin.length(); i++) {
            if (i <= slleepLin.length() - 1) {
                int subStr = Integer.valueOf(slleepLin.substring(i, i + 1));
                listValue.add(subStr);
            }

        }
        listValue.add(0, 2);
        listValue.add(slleepLin.length() , 2);
        if(listValue.size()>0){
            detailCusSleepView.setSleepList(listValue);
        }else{
            detailCusSleepView.setSleepList(new ArrayList<Integer>());
        }
    }

    private void initData() {
        String sleepStr = (String) SharedPreferencesUtils.getParam(this,"sleepDataStr","");
        if(!WatchUtils.isEmpty(sleepStr)){
            SleepData sleepData = new Gson().fromJson(sleepStr,SleepData.class);
            if (sleepData != null) {
                showSleepChartView(sleepData);
                int sleepQulity = sleepData.getSleepQulity();
                detailSleepQuitRatingBar.setMax(5);
                detailSleepQuitRatingBar.setNumStars(sleepQulity);

                //睡眠时长
                detailAllSleepTv.setText((sleepData.getAllSleepTime() / 60) + "H" + (sleepData.getAllSleepTime() % 60) + "m");
                //苏醒次数
                detailAwakeNumTv.setText(sleepData.getWakeCount() + "");
                //入睡时间
                detailStartSleepTv.setText(sleepData.getSleepDown().getDateForSleepshow() + "");
                //苏醒时间
                detailAwakeTimeTv.setText(sleepData.getSleepUp().getDateForSleepshow() + "");
                //深度睡眠
                detailDeepTv.setText(sleepData.getDeepSleepTime() + "min");
                detailHightSleepTv.setText(sleepData.getLowSleepTime() + "min");

            }
        }

    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.sleep));
        commentB30ShareImg.setVisibility(View.VISIBLE);
        sleepCurrDateTv.setText(WatchUtils.getCurrentDate());
        detailSleepQuitRatingBar.setMax(5);
        detailSleepQuitRatingBar.setRating(100);
        listValue = new ArrayList<>();
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
