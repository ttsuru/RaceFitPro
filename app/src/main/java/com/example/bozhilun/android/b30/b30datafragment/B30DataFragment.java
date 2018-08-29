package com.example.bozhilun.android.b30.b30datafragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.service.B30DataServer;
import com.example.bozhilun.android.bean.AvgHeartRate;
import com.example.bozhilun.android.bean.NewsSleepBean;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.siswatch.bean.WatchDataDatyBean;
import com.example.bozhilun.android.siswatch.data.BarXFormartValue;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lecho.lib.hellocharts.model.ColumnChartData;


/**
 * B30数据界面
 */
public class B30DataFragment extends LazyFragment implements B30DataServer.B30DataServerListener {

    private static final String TAG = "B30DataFragment";

    View dataView;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    Unbinder unbinder;
    @BindView(R.id.b30DataWeekTv)
    TextView b30DataWeekTv;
    @BindView(R.id.b30DataMonthTv)
    TextView b30DataMonthTv;
    @BindView(R.id.b30DataYearTv)
    TextView b30DataYearTv;
    //步数的图表
    @BindView(R.id.stepDataChartView)
    BarChart stepDataChartView;

    private List<String> stepXList; //x轴数据
    //步数的相关
    List<WatchDataDatyBean> stepList;
    private B30DataServer b30DataServer;
    private Map<String, Integer> stepSumMap; //用于计算年的步数
    private List<String> tempList;  //用于计算年的步数的list
    //步数数值
    private List<Integer> mValues;
    List<BarEntry> pointbar;
    //睡眠图表
    @BindView(R.id.sleepDataChartView)
    BarChart sleepDataChartView;

    //睡眠相关
    private List<NewsSleepBean> sleepBeanList;  //数据源
    private List<Integer> sleepVlaues;  //睡眠的数值
    private List<String> sleepXList;    //睡眠X轴
    private Map<String, Integer> sumSleepMap;    //保存计算睡眠年的数据
    private List<String> tempSleepList; //
    private List<BarEntry> sleepBarEntryList;


    //心率图表
    @BindView(R.id.heartDataChartView)
    BarChart heartDataChartView;
    private List<AvgHeartRate> heartList;   //数据集合

    private List<Integer> heartValues;  //心率数值
    private List<String> heartXList;    //心率X轴数据
    private List<String> tempHeartList; //心率中间list
    private Map<String, Integer> heartMap;   //计算心率年的map
    private int ValueCount = 0;//计算年平均心率
    private Map<String, Integer> dayMap;
    private List<BarEntry> heartBarEntryList;





    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "----onCreate---");
        b30DataServer = MyApp.getB30DataServer();
        b30DataServer.setB30DataServerListener(this);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataView = inflater.inflate(R.layout.fragment_b30_data, container, false);
        unbinder = ButterKnife.bind(this, dataView);
        Log.e(TAG, "----onCreateView---");
        initViews();

        initData();

        setClearStyle(0);

        return dataView;


    }

    private void initData() {
        //步数的x轴数据
        stepXList = new ArrayList<>();
        //步数的数据
        pointbar = new ArrayList<>();

        //心率
        tempHeartList = new ArrayList<>();
        heartXList = new ArrayList<>();
        dayMap = new HashMap<>();
        heartValues = new ArrayList<>();
        heartBarEntryList = new ArrayList<>();

        //睡眠
        sleepVlaues = new ArrayList<>();
        sleepXList = new ArrayList<>();
        sleepBarEntryList = new ArrayList<>();

    }

    private void initViews() {
        commentB30TitleTv.setText(getResources().getString(R.string.data));
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        Log.e(TAG, "----isVisible---" + isVisible);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        Log.e(TAG, "---onDestroyView----");

    }

    @OnClick({R.id.b30DataWeekTv, R.id.b30DataMonthTv, R.id.b30DataYearTv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b30DataWeekTv:    //日
                setClearStyle(0);
                break;
            case R.id.b30DataMonthTv:   //月
                setClearStyle(1);
                break;
            case R.id.b30DataYearTv:    //年
                setClearStyle(2);
                break;
        }
    }


    //样式
    private void setClearStyle(int code) {
        if (b30DataWeekTv != null) {
            b30DataWeekTv.setBackgroundResource(R.drawable.newh9data_selecte_text_shap);
            b30DataWeekTv.setTextColor(Color.parseColor("#333333"));
        }
        if (b30DataMonthTv != null) {
            b30DataMonthTv.setBackgroundResource(R.drawable.newh9data_selecte_text_shap);
            b30DataMonthTv.setTextColor(Color.parseColor("#333333"));
        }
        if (b30DataYearTv != null) {
            b30DataYearTv.setBackgroundResource(R.drawable.newh9data_selecte_text_shap);
            b30DataYearTv.setTextColor(Color.parseColor("#333333"));
        }
        switch (code) {
            case 0:
                b30DataWeekTv.setTextColor(getResources().getColor(R.color.white));
                b30DataWeekTv.setBackgroundResource(R.drawable.newh9data_unselecte_text_shap_two);
                if (b30DataServer != null) {
                    b30DataServer.getHistoryData(7);
                }

                break;
            case 1:
                b30DataMonthTv.setTextColor(getResources().getColor(R.color.white));
                b30DataMonthTv.setBackgroundResource(R.drawable.newh9data_unselecte_text_shap_two);
                if (b30DataServer != null) {
                    b30DataServer.getHistoryData(30);
                }
                break;
            case 2:
                b30DataYearTv.setTextColor(getResources().getColor(R.color.white));
                b30DataYearTv.setBackgroundResource(R.drawable.newh9data_unselecte_text_shap_two);
                if (b30DataServer != null) {
                    b30DataServer.getHistoryData(365);
                }
                break;
        }
    }


    @Override
    public void showProDialog(int what) {
        showLoadingDialog("Loading...");
    }

    @Override
    public void closeProDialog(int what) {
        closeLoadingDialog();
    }

    //步数返回
    @Override
    public void b30StepData(String stepStr, int code) {
        JSONObject jsonObject;
        Log.e(TAG, "------step返回=" + stepStr);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        stepXList = new ArrayList<>();
        if (!WatchUtils.isEmpty(stepStr)) {
            try {
                jsonObject = new JSONObject(stepStr);
                if (jsonObject.getString("resultCode").equals("001")) {
                    String daydata = jsonObject.getString("day");
                    if (!WatchUtils.isEmpty(daydata) && !daydata.equals("[]")) {
                        stepList = new Gson().fromJson(daydata, new TypeToken<List<WatchDataDatyBean>>() {
                        }.getType());
                        mValues = new ArrayList<>();
                        if (code == 365) { //年
                            mValues.clear();
                            stepSumMap = new HashMap<>();
                            tempList = new ArrayList<>();
                            int sum = 0;
                            for (int i = 0; i < stepList.size(); i++) {
                                String dateStr = stepList.get(i).getRtc();
                                String strDate = dateStr.substring(2, 7);
                                if (stepSumMap.get(strDate) != null) {
                                    sum += stepList.get(i).getStepNumber();
                                } else {
                                    sum = stepList.get(i).getStepNumber();
                                }
                                stepSumMap.put(strDate, sum);
                            }

                            tempList.clear();
                            //遍历map
                            for (Map.Entry<String, Integer> entry : stepSumMap.entrySet()) {
                                tempList.add(entry.getKey().trim());
                            }
                            //升序排列
                            Collections.sort(tempList, new Comparator<String>() {
                                @Override
                                public int compare(String s, String t1) {
                                    return s.compareTo(t1);
                                }
                            });
                            for (int k = 0; k < tempList.size(); k++) {
                                mValues.add(stepSumMap.get(tempList.get(k)));
                                stepXList.add(tempList.get(k));
                            }
                            showStepsChat(mValues, stepXList);

                        } else {
                            mValues.clear();
                            //获取值
                            for (WatchDataDatyBean stepNumber : stepList) {
                                mValues.add(stepNumber.getStepNumber());    //步数的数值显示
                                String dateStr = stepNumber.getRtc();
                                String rct = dateStr.substring(5, dateStr.length());
                                stepXList.add(rct);
                            }
                            showStepsChat(mValues, stepXList);
                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //心率返回
    @Override
    public void b30HeartData(String heartStr, int code) {
        Log.e(TAG, "------heartStr=" + heartStr);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(heartStr);
            if(jsonObject.getString("resultCode").equals("001")){
                String heartData = jsonObject.getString("heartRate");
                if(!WatchUtils.isEmpty(heartData) && !heartData.equals("[]")){
                    heartList = new Gson().fromJson(heartData, new TypeToken<List<AvgHeartRate>>() {
                    }.getType());

                    if (code == 365) {    //年
                        heartMap = new HashMap<>();
                        heartMap.clear();
                        heartValues.clear();
                        int heartSum = 0;
                        int Day = 0;
                        dayMap.clear();
                        for (int i = 0; i < heartList.size(); i++) {
                            String strDate = heartList.get(i).getRtc().substring(2, 7);
                            if (heartMap.get(strDate) != null) {
                                heartSum += heartList.get(i).getAvgHeartRate();
                                if (heartList.get(i).getAvgHeartRate() > 0) {
                                    dayMap.put(strDate, Day++);
                                }
                            } else {
                                heartSum = heartList.get(i).getAvgHeartRate();
                                Day = 0;
                                if (heartList.get(i).getAvgHeartRate() > 0) {
                                    dayMap.put(strDate, Day++);
                                }
                            }
                            //W30BasicUtils.e(TAG, "----heartSum---" + heartSum);
                            if (heartSum > 0) {
                                ValueCount++;
                            }
                            heartMap.put(strDate, heartSum);
                        }
                        tempHeartList = new ArrayList<>();
                        tempHeartList.clear();
                        for (Map.Entry<String, Integer> maps : heartMap.entrySet()) {
                            tempHeartList.add(maps.getKey().trim());
                        }
                        //排序时间
                        Collections.sort(tempHeartList, new Comparator<String>() {
                            @Override
                            public int compare(String o1, String o2) {
                                return o1.compareTo(o2);
                            }
                        });
                        heartXList.clear();
                        //W30BasicUtils.e(TAG, "----heartSum---" + tempHeartList.size());
                        for (int i = 0; i < tempHeartList.size(); i++) {
                            heartValues.add(heartMap.get(tempHeartList.get(i)));
                            heartXList.add(tempHeartList.get(i));
                        }
                        showHeartChart(heartValues,heartXList);

                    } else {  //周或者月

                        heartValues.clear();
                        for (AvgHeartRate avgHeart : heartList) {
                            heartValues.add(avgHeart.getAvgHeartRate()); //2017-10-10
                            //Log.e(TAG, "----xxx----" + avgHeart.getRtc().substring(5, avgHeart.getRtc().length()));
                            heartXList.add(avgHeart.getRtc().substring(5, avgHeart.getRtc().length()));
                        }

                        showHeartChart(heartValues,heartXList);

                    }


                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //血压返回
    @Override
    public void b30BloadData(String bloadStr, int code) {
        Log.e(TAG, "------bloadStr=" + bloadStr);
    }

    //睡眠返回
    @Override
    public void b30SleepData(String sleepStr, int code) {
        Log.e(TAG, "------sleepStr=" + sleepStr);
        try {
            JSONObject sleepJson = new JSONObject(sleepStr);
            if (sleepJson.getString("resultCode").equals("001")) {
                String sleepData = sleepJson.getString("sleepData");
                if (sleepData != null && !sleepData.equals("[]")) {
                    sleepBeanList = new Gson().fromJson(sleepData, new TypeToken<List<NewsSleepBean>>() {
                    }.getType());
                    if (code == 365) {    //年
                        sumSleepMap = new HashMap<>();
                        int sleepSum = 0;
                        for (int i = 0; i < sleepBeanList.size(); i++) {
                            String strDate = sleepBeanList.get(i).getRtc().substring(2, 7);
                            if (sumSleepMap.get(strDate) != null) {
                                int shallowSleep = sleepBeanList.get(i).getShallowSleep();
                                int deepSleep = sleepBeanList.get(i).getDeepSleep();
                                sleepSum += (shallowSleep + deepSleep);

                            } else {
                                int shallowSleep = sleepBeanList.get(i).getShallowSleep();
                                int deepSleep = sleepBeanList.get(i).getDeepSleep();
                                sleepSum = (shallowSleep + deepSleep);
//                                            sleepSum = newsSleepBeanList.get(i).getSleepLen();
                            }

                            sumSleepMap.put(strDate, sleepSum);
                        }
                        tempSleepList = new ArrayList<>();
                        tempSleepList.clear();
                        //遍历map
                        for (Map.Entry<String, Integer> maps : sumSleepMap.entrySet()) {
                            tempSleepList.add(maps.getKey().trim());
                            //W30BasicUtils.e(TAG, "---睡眠MAP--=" + maps.getKey().trim());
                        }
                        //升序排列
                        Collections.sort(tempSleepList, new Comparator<String>() {
                            @Override
                            public int compare(String s, String t1) {
                                return s.compareTo(t1);
                            }
                        });
                        sleepXList.clear();
                        sleepVlaues.clear();
                        for (int k = 0; k < tempSleepList.size(); k++) {
                            sleepVlaues.add(sumSleepMap.get(tempSleepList.get(k)));
                            sleepXList.add(tempSleepList.get(k));
//                                Log.e(TAG, "---睡眠Value--=" + sumSleepMap.get(tempSleepList.get(k)) + "-==-睡眠Data-=" + tempSleepList.get(k));
                        }

                        showSleepChart(sleepVlaues,sleepXList);

                    } else {
                        sleepXList.clear();
                        sleepVlaues.clear();
                        for (NewsSleepBean sleepBean : sleepBeanList) {
                            int shallowSleep = sleepBean.getShallowSleep();
                            int deepSleep = sleepBean.getDeepSleep();
                            int sleepSum = (shallowSleep + deepSleep);
                            sleepVlaues.add(sleepSum); //2017-11-11
//                                        sleepVlaues.add(sleepBean.getSleepLen()); //2017-11-11
                            sleepXList.add(sleepBean.getRtc().substring(5, sleepBean.getRtc().length()));
                        }

                        showSleepChart(sleepVlaues,sleepXList);

                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    //展示睡眠图表
    private void showSleepChart(List<Integer> sleepVlaues, List<String> sleepXList) {
        Log.e(TAG,"----睡眠大小="+mValues.size());
        sleepBarEntryList.clear();
        for (int i = 0; i < sleepVlaues.size(); i++) {
            sleepBarEntryList.add(new BarEntry(i, sleepVlaues.get(i)));
        }

        BarDataSet barDataSet = new BarDataSet(sleepBarEntryList, "");
        barDataSet.setDrawValues(true);//是否显示柱子上面的数值
        barDataSet.setColor(Color.parseColor("#ffffff"));//设置第一组数据颜色

        barDataSet.setHighLightColor(Color.GREEN);

        Legend mLegend = sleepDataChartView.getLegend();
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(15.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
        mLegend.setEnabled(false);

        ArrayList<IBarDataSet> threebardata = new ArrayList<>();//IBarDataSet 接口很关键，
        // 是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        threebardata.add(barDataSet);

        BarData bardata = new BarData(threebardata);
        if (sleepVlaues.size() == 30) {
            bardata.setBarWidth(0.4f);  //设置柱子宽度
        } else {
            bardata.setBarWidth(0.1f);  //设置柱子宽度
        }


        sleepDataChartView.setData(bardata);
        sleepDataChartView.setDoubleTapToZoomEnabled(false);   //双击缩放
        sleepDataChartView.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//设置注解的位置在左上方
        sleepDataChartView.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状

        BarXFormartValue xFormartValue = new BarXFormartValue(sleepDataChartView, sleepXList);
        XAxis xAxis = sleepDataChartView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        xAxis.setDrawGridLines(false);//不显示网格
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setAxisLineWidth(2f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(xFormartValue);
        xAxis.setEnabled(true);
        sleepDataChartView.getDescription().setEnabled(false);

        sleepDataChartView.getAxisRight().setEnabled(false);//右侧不显示Y轴
        sleepDataChartView.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        sleepDataChartView.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
        sleepDataChartView.getAxisLeft().setEnabled(false);
        sleepDataChartView.getXAxis().setSpaceMax(0.5f);
        sleepDataChartView.animateXY(1000, 2000);//设置动画
    }

    //展示步数图表
    private void showStepsChat(List<Integer> mValues, List<String> xList) {
        Log.e(TAG,"----步数大小="+mValues.size());
        pointbar.clear();
        for (int i = 0; i < mValues.size(); i++) {
            pointbar.add(new BarEntry(i, mValues.get(i)));
        }

        BarDataSet barDataSet = new BarDataSet(pointbar, "");
        barDataSet.setDrawValues(true);//是否显示柱子上面的数值
        barDataSet.setColor(Color.parseColor("#ffffff"));//设置第一组数据颜色

        barDataSet.setHighLightColor(Color.GREEN);

        Legend mLegend = stepDataChartView.getLegend();
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(15.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
        mLegend.setEnabled(false);

        ArrayList<IBarDataSet> threebardata = new ArrayList<>();//IBarDataSet 接口很关键，
        // 是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        threebardata.add(barDataSet);

        BarData bardata = new BarData(threebardata);
        if (mValues.size() == 30) {
            bardata.setBarWidth(0.4f);  //设置柱子宽度
        } else {
            bardata.setBarWidth(0.1f);  //设置柱子宽度
        }


        stepDataChartView.setData(bardata);
        stepDataChartView.setDoubleTapToZoomEnabled(false);   //双击缩放
        stepDataChartView.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//设置注解的位置在左上方
        stepDataChartView.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状

        BarXFormartValue xFormartValue = new BarXFormartValue(stepDataChartView, xList);
        XAxis xAxis = stepDataChartView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        xAxis.setDrawGridLines(false);//不显示网格
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setAxisLineWidth(2f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(xFormartValue);
        xAxis.setEnabled(true);
        stepDataChartView.getDescription().setEnabled(false);

        stepDataChartView.getAxisRight().setEnabled(false);//右侧不显示Y轴
        stepDataChartView.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        stepDataChartView.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
        stepDataChartView.getAxisLeft().setEnabled(false);
        stepDataChartView.getXAxis().setSpaceMax(0.5f);
        stepDataChartView.animateXY(1000, 2000);//设置动画
    }



    //展示心率图表
    private void showHeartChart(List<Integer> heartList,List<String> xlt){
        Log.e(TAG,"----心率大小="+heartList.size());
        heartBarEntryList.clear();
        for (int i = 0; i < heartList.size(); i++) {
            heartBarEntryList.add(new BarEntry(i, heartList.get(i)));
        }

        BarDataSet barDataSet = new BarDataSet(heartBarEntryList, "");
        barDataSet.setDrawValues(true);//是否显示柱子上面的数值
        barDataSet.setColor(Color.parseColor("#ffffff"));//设置第一组数据颜色
        barDataSet.setHighLightColor(Color.GREEN);

        Legend mLegend = stepDataChartView.getLegend();
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(15.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
        mLegend.setEnabled(false);

        ArrayList<IBarDataSet> threebardata = new ArrayList<>();//IBarDataSet 接口很关键，
        // 是添加多组数据的关键结构，LineChart也是可以采用对应的接口类，也可以添加多组数据
        threebardata.add(barDataSet);

        BarData bardata = new BarData(threebardata);
        if (heartList.size() == 30) {
            bardata.setBarWidth(0.4f);  //设置柱子宽度
        } else {
            bardata.setBarWidth(0.1f);  //设置柱子宽度
        }


        heartDataChartView.setData(bardata);
        heartDataChartView.setDoubleTapToZoomEnabled(false);   //双击缩放
        heartDataChartView.getLegend().setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);//设置注解的位置在左上方
        heartDataChartView.getLegend().setForm(Legend.LegendForm.CIRCLE);//这是左边显示小图标的形状
        heartDataChartView.getLegend().setEnabled(false);

        BarXFormartValue xFormartValue = new BarXFormartValue(heartDataChartView, xlt);
        XAxis xAxis = heartDataChartView.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        xAxis.setDrawGridLines(false);//不显示网格
        xAxis.setAxisLineColor(Color.WHITE);
        xAxis.setAxisLineWidth(2f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(xFormartValue);
        xAxis.setEnabled(true);
        heartDataChartView.getDescription().setEnabled(false);

        heartDataChartView.getAxisRight().setEnabled(false);//右侧不显示Y轴
        heartDataChartView.getAxisLeft().setAxisMinValue(0.8f);//设置Y轴显示最小值，不然0下面会有空隙
        heartDataChartView.getAxisLeft().setDrawGridLines(false);//不设置Y轴网格
        heartDataChartView.getAxisLeft().setEnabled(false);
        heartDataChartView.getXAxis().setSpaceMax(0.5f);
        heartDataChartView.animateXY(1000, 2000);//设置动画
    }
}
