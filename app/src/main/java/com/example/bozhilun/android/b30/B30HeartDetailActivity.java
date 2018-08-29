package com.example.bozhilun.android.b30;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.adapter.B30HeartDetailAdapter;
import com.example.bozhilun.android.b30.b30view.B30CusHeartView;
import com.example.bozhilun.android.b30.bean.B30Bean;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.google.gson.Gson;
import com.veepoo.protocol.model.datas.HalfHourRateData;
import com.veepoo.protocol.model.datas.OriginHalfHourData;
import com.veepoo.protocol.model.datas.TimeData;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/8/6.
 */

/**
 * B30心率详情界面
 */
public class B30HeartDetailActivity extends WatchBaseActivity {

    private static final String TAG = "B30HeartDetailActivity";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;
    @BindView(R.id.b30HeartDetailView)
    B30CusHeartView b30HeartDetailView;
    @BindView(R.id.b30HeartDetailRecyclerView)
    RecyclerView b30HeartDetailRecyclerView;
    @BindView(R.id.bloadCurrdateTv)
    TextView bloadCurrdateTv;
    private List<HalfHourRateData> halfHourRateDatasList;
    private B30HeartDetailAdapter b30HeartDetailAdapter;


    //心率图标数据
    List<Integer> heartList;
    private B30Bean b30Bean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_heart_detail_layout);
        ButterKnife.bind(this);

        initViews();
        initData();


    }

    private void initData() {
        b30Bean = DataSupport.find(B30Bean.class, 1);
        if (b30Bean != null) {
            String halfHourDataStr = b30Bean.getHalfHourDataStr();
            Log.e(TAG, "----halfHourDataStr=" + halfHourDataStr);
            if (halfHourDataStr != null) {
                OriginHalfHourData originHalfHourData = new Gson().fromJson(halfHourDataStr, OriginHalfHourData.class);
                List<HalfHourRateData> tmpLt = originHalfHourData.getHalfHourRateDatas();
                if (tmpLt != null && tmpLt.size() > 0) {
                    heartList.clear();
                    List<Map<String, Integer>> listMap = new ArrayList<>();
                    int k = 0;
                    for (int i = 0; i < 48; i++) {
                        Map<String, Integer> map = new HashMap<>();
                        int time = i * 30;
                        map.put("time", time);
                        TimeData tmpDate = tmpLt.get(k).getTime();
                        int tmpIntDate = tmpDate.getHMValue();

                        if (tmpIntDate == time) {
                            map.put("val", tmpLt.get(k).getRateValue());
                            if (k < tmpLt.size() - 1) {
                                k++;
                            }
                        } else {
                            map.put("val", 0);
                        }
                        listMap.add(map);
                    }
                    for (int i = 0; i < listMap.size(); i++) {
                        Map<String, Integer> map = listMap.get(i);
                        heartList.add(map.get("val"));
                    }

                    //圆点的半径
                    b30HeartDetailView.setPointRadio(5);
                    b30HeartDetailView.setRateDataList(heartList);
                    Collections.sort(tmpLt, new Comparator<HalfHourRateData>() {
                        @Override
                        public int compare(HalfHourRateData o1, HalfHourRateData o2) {
                            return o2.getTime().getColck().compareTo(o1.getTime().getColck());
                        }
                    });
                    halfHourRateDatasList.addAll(tmpLt);
                    b30HeartDetailAdapter.notifyDataSetChanged();
                }
            }
        }


    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText("心率");
        bloadCurrdateTv.setText(WatchUtils.getCurrentDate());
        commentB30ShareImg.setVisibility(View.VISIBLE);

        heartList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        b30HeartDetailRecyclerView.setLayoutManager(layoutManager);
        b30HeartDetailRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        halfHourRateDatasList = new ArrayList<>();
        b30HeartDetailAdapter = new B30HeartDetailAdapter(halfHourRateDatasList, this);
        b30HeartDetailRecyclerView.setAdapter(b30HeartDetailAdapter);
    }

    @OnClick({R.id.commentB30BackImg, R.id.commentB30ShareImg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg: //返回
                finish();
                break;
            case R.id.commentB30ShareImg:
                WatchUtils.shareCommData(B30HeartDetailActivity.this);
                break;
        }
    }
}
