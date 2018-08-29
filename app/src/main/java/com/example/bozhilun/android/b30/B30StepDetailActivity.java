package com.example.bozhilun.android.b30;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.adapter.B30StepDetailAdapter;
import com.example.bozhilun.android.b30.bean.B30Bean;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchConstants;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.veepoo.protocol.model.datas.HalfHourSportData;
import com.veepoo.protocol.model.datas.OriginData;
import com.veepoo.protocol.model.datas.SportData;
import com.veepoo.protocol.model.datas.TimeData;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/8/4.
 */

public class B30StepDetailActivity extends WatchBaseActivity {

    private static final String TAG = "B30StepDetailActivity";


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;

    @BindView(R.id.b30ChartTopRel)
    RelativeLayout b30ChartTopRel;
    @BindView(R.id.b30BarChart)
    BarChart b30BarChart;
    @BindView(R.id.b30SportChartLin)
    LinearLayout b30SportChartLin;
    @BindView(R.id.b30StepDetailRecyclerView)
    RecyclerView b30StepDetailRecyclerView;
    @BindView(R.id.countStepTv)
    TextView countStepTv;
    @BindView(R.id.countDisTv)
    TextView countDisTv;
    @BindView(R.id.countKcalTv)
    TextView countKcalTv;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv2)
    TextView tv2;
    @BindView(R.id.tv3)
    TextView tv3;
    @BindView(R.id.stepCurrDateTv)
    TextView stepCurrDateTv;

    private List<OriginData> list;
    private B30StepDetailAdapter b30StepDetailAdapter;

    //步数数据
    List<BarEntry> b30ChartList;

    B30Bean b30Bean;
    private int flagCode = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_step_detail_layout);
        ButterKnife.bind(this);

        initViews();
        //显示汇总数据
        b30Bean = DataSupport.find(B30Bean.class, 1);
        initData();

        showData();

    }

    private void showData() {

        Log.e(TAG, "----bean=" + b30Bean.toString());
        if (b30Bean != null) {
            String sportStr = b30Bean.getSportDataStr();
            Log.e(TAG, "----sportStr=" + sportStr);
            if (sportStr != null) {
                SportData sportData = new Gson().fromJson(sportStr, SportData.class);
                Log.e(TAG, "-----sportData=" + sportData.toString());
                if (sportData != null) {
                    countStepTv.setText(sportData.getStep() + "");
                    countDisTv.setText(sportData.getDis() + "");
                    countKcalTv.setText(sportData.getKcal() + "");
                }
            }

        }
        clearStyleData(0);
        showSportDetail(1);

    }

    private void showSportDetail(int code) {
        if (b30Bean != null) {
            String originDataStr = b30Bean.getOriginDataStr();
            Log.e(TAG, "-----originDataStr=" + originDataStr);
            if (originDataStr != null) {
                List<OriginData> tmpList = new Gson().fromJson(originDataStr, new TypeToken<List<OriginData>>() {
                }.getType());
                if (tmpList != null && tmpList.size() > 0) {
                    list.clear();
                    switch (code) {
                        case 1: //步数
                            flagCode = 1;
                            break;
                        case 2: //里程
                            flagCode = 2;
                            break;
                        case 3: //卡路里
                            flagCode = 3;
                            break;
                        default:
                            flagCode = 1;
                            break;
                    }
                    list.clear();
                    list.addAll(tmpList);
                    b30StepDetailAdapter.notifyDataSetChanged();
                }
            }
        }

    }

    private void initData() {
        b30ChartList = new ArrayList<>();
        List<HalfHourSportData> tmpList = WatchConstants.tmpSportList;
        if (tmpList != null && tmpList.size() > 0) {
            List<Map<String, Integer>> listMap = new ArrayList<>();
            int k = 0;
            for (int i = 0; i < 48; i++) {
                Map<String, Integer> map = new HashMap<>();
                int time = i * 30;
                map.put("time", time);

                TimeData tmpDate = tmpList.get(k).getTime();
                int tmpIntDate = tmpDate.getHMValue();
                if (tmpIntDate == time) {
                    map.put("val", tmpList.get(k).getStepValue());
                    if (k < tmpList.size() - 1) {
                        k++;
                    }
                } else {
                    map.put("val", 0);
                }
                listMap.add(map);
                //Log.e(TAG, "----time=" + map.get("time") + "val:" + map.get("val"));
            }

            for (int i = 0; i < listMap.size(); i++) {
                Map<String, Integer> tmpMap = listMap.get(i);
                b30ChartList.add(new BarEntry(i, tmpMap.get("val")));
            }
            initBarChart(b30ChartList);
            b30BarChart.invalidate();
        }

    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText("运动");
        commentB30ShareImg.setVisibility(View.VISIBLE);
        b30ChartTopRel.setVisibility(View.GONE);
        b30SportChartLin.setBackgroundColor(Color.parseColor("#2594EE"));
        stepCurrDateTv.setText(WatchUtils.getCurrentDate());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        b30StepDetailRecyclerView.setLayoutManager(layoutManager);
        b30StepDetailRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        list = new ArrayList<>();
        b30StepDetailAdapter = new B30StepDetailAdapter(this, list, flagCode);
        b30StepDetailRecyclerView.setAdapter(b30StepDetailAdapter);


    }


    //步数图表展示
    private void initBarChart(List<BarEntry> pointbar) {
        BarDataSet barDataSet = new BarDataSet(pointbar, "");
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        barDataSet.setColor(Color.WHITE);//设置第一组数据颜色

        Legend mLegend = b30BarChart.getLegend();
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(15.0f);// 字体
        mLegend.setTextColor(Color.WHITE);// 颜色
        mLegend.setEnabled(false);

        ArrayList<IBarDataSet> threebardata = new ArrayList<>();//IBarDataSet 接口很关键，是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        threebardata.add(barDataSet);

        BarData bardata = new BarData(threebardata);
        bardata.setBarWidth(0.5f);  //设置柱子宽度

        b30BarChart.setData(bardata);
        b30BarChart.setDoubleTapToZoomEnabled(false);   //双击缩放
        b30BarChart.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//设置注解的位置在左上方
        b30BarChart.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状

        b30BarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        b30BarChart.getXAxis().setDrawGridLines(false);//不显示网格
        b30BarChart.getXAxis().setEnabled(false);

        b30BarChart.getDescription().setEnabled(false);

        b30BarChart.getAxisRight().setEnabled(false);//右侧不显示Y轴
        b30BarChart.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        b30BarChart.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
        b30BarChart.getAxisLeft().setEnabled(false);
//        barchart.setDescriptionPosition(100,20);//数据描述的位置
//        barchart.setDescriptionColor(Color.GRAY);//数据的颜色
//        barchart.setDescriptionTextSize(40);//数据字体大小
        // barchart.setDescription("No Deal");//设置描述
        //  barchart.setDescriptionTextSize(20.f);//设置描述字体
//        barchart.getXAxis().setSpaceBetweenLabels(50);
        b30BarChart.getXAxis().setSpaceMax(0.5f);
        b30BarChart.animateXY(1000, 2000);//设置动画

    }


    @OnClick({R.id.commentB30BackImg, R.id.stepDetailStepLin,
            R.id.stepDetailDisLin, R.id.stepDetailKcalLin,R.id.commentB30ShareImg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.stepDetailStepLin:
                clearStyleData(0);
                showSportDetail(1);
                break;
            case R.id.stepDetailDisLin:
                clearStyleData(1);
                showSportDetail(2);
                break;
            case R.id.stepDetailKcalLin:
                clearStyleData(2);
                showSportDetail(3);
                break;
            case R.id.commentB30ShareImg:   //分享
                WatchUtils.shareCommData(B30StepDetailActivity.this);
                break;


        }
    }

    private void clearStyleData(int txtCode) {
        tv1.setTextColor(ContextCompat.getColor(this, R.color.txt_color));
        tv2.setTextColor(ContextCompat.getColor(this, R.color.txt_color));
        tv3.setTextColor(ContextCompat.getColor(this, R.color.txt_color));
        countStepTv.setTextColor(ContextCompat.getColor(this, R.color.txt_color));
        countDisTv.setTextColor(ContextCompat.getColor(this, R.color.txt_color));
        countKcalTv.setTextColor(ContextCompat.getColor(this, R.color.txt_color));
        switch (txtCode) {
            case 0:
                tv1.setTextColor(ContextCompat.getColor(this, R.color.new_deep_colorAccent));
                countStepTv.setTextColor(ContextCompat.getColor(this, R.color.new_deep_colorAccent));
                break;
            case 1:
                tv2.setTextColor(ContextCompat.getColor(this, R.color.new_deep_colorAccent));
                countDisTv.setTextColor(ContextCompat.getColor(this, R.color.new_deep_colorAccent));
                break;
            case 2:
                tv3.setTextColor(ContextCompat.getColor(this, R.color.new_deep_colorAccent));
                countKcalTv.setTextColor(ContextCompat.getColor(this, R.color.new_deep_colorAccent));
                break;
        }
    }
}
