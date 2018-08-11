package com.example.bozhilun.android.b30;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchConstants;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.veepoo.protocol.model.datas.HalfHourSportData;
import com.veepoo.protocol.model.datas.TimeData;

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

    private List<HalfHourSportData> list;
    private B30StepDetailAdapter b30StepDetailAdapter;

    //步数数据
    List<BarEntry> b30ChartList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_step_detail_layout);
        ButterKnife.bind(this);

        initViews();
        initData();

    }

    private void initData() {
        b30ChartList = new ArrayList<>();
        List<HalfHourSportData> tmpList = WatchConstants.tmpSportList;
        if (tmpList != null) {
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



            Collections.sort(tmpList, new Comparator<HalfHourSportData>() {
                @Override
                public int compare(HalfHourSportData o1, HalfHourSportData o2) {
                    return o2.getTime().getColck().compareTo(o1.getTime().getColck());
                }
            });


            list.addAll(tmpList);
            b30StepDetailAdapter.notifyDataSetChanged();


        }

    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText("运动");
        commentB30ShareImg.setVisibility(View.VISIBLE);
        b30ChartTopRel.setVisibility(View.GONE);
        b30SportChartLin.setBackgroundColor(Color.parseColor("#2594EE"));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        b30StepDetailRecyclerView.setLayoutManager(layoutManager);
        b30StepDetailRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        list = new ArrayList<>();
        b30StepDetailAdapter = new B30StepDetailAdapter(this, list);
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

    @OnClick(R.id.commentB30BackImg)
    public void onViewClicked() {
        finish();
    }
}
