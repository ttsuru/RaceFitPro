package com.example.bozhilun.android.b30.b30homefragment;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.B30BloadDetailActivity;
import com.example.bozhilun.android.b30.B30HeartDetailActivity;
import com.example.bozhilun.android.b30.B30SleepDetailActivity;
import com.example.bozhilun.android.b30.B30StepDetailActivity;
import com.example.bozhilun.android.b30.ManualMeaureBloadActivity;
import com.example.bozhilun.android.b30.ManualMeaureHeartActivity;
import com.example.bozhilun.android.b30.b30view.B30CusBloadView;
import com.example.bozhilun.android.b30.b30view.B30CusHeartView;
import com.example.bozhilun.android.b30.b30view.B30CusSleepView;
import com.example.bozhilun.android.b30.bean.B30Bean;
import com.example.bozhilun.android.b30.service.ConnBleHelpService;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.siswatch.utils.WatchConstants;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.littlejie.circleprogress.circleprogress.WaveProgress;
import com.veepoo.protocol.model.datas.HalfHourBpData;
import com.veepoo.protocol.model.datas.HalfHourRateData;
import com.veepoo.protocol.model.datas.HalfHourSportData;
import com.veepoo.protocol.model.datas.OriginData;
import com.veepoo.protocol.model.datas.OriginHalfHourData;
import com.veepoo.protocol.model.datas.SleepData;
import com.veepoo.protocol.model.datas.SportData;
import com.veepoo.protocol.model.datas.TimeData;
import org.litepal.crud.DataSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by Administrator on 2018/7/20.
 */

public class B30HomeFragment extends LazyFragment implements ConnBleHelpService.ConnBleMsgDataListener,
        ConnBleHelpService.ConnBleHealthDataListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "B30HomeFragment";


    View b30HomeFragment;
    @BindView(R.id.b30ProgressBar)
    WaveProgress b30ProgressBar;
    Unbinder unbinder;
    //日期
    @BindView(R.id.b30_top_dateTv)
    TextView b30TopDateTv;
    //电量
    @BindView(R.id.batteryTopImg)
    ImageView batteryTopImg;
    @BindView(R.id.batteryPowerTv)
    TextView batteryPowerTv;
    @BindView(R.id.b30connectStateTv)
    TextView b30ConnectStateTv;

    //目标步数
    @BindView(R.id.b30GoalStepTv)
    TextView b30GoalStepTv;
    //运动步数的chart
    @BindView(R.id.b30BarChart)
    BarChart b30BarChart;
    //步数数据
    List<BarEntry> b30ChartList;
    //血压
    @BindView(R.id.bloadLastTimeTv)
    TextView bloadLastTimeTv;
    @BindView(R.id.b30BloadValueTv)
    TextView b30BloadValueTv;
    @BindView(R.id.b30HomeBloadChart)
    B30CusBloadView b30HomeBloadChart;
    //睡眠图表
    @BindView(R.id.b30CusSleepView)
    B30CusSleepView b30CusSleepView;
    @BindView(R.id.b30StartEndTimeTv)
    TextView b30StartEndTimeTv;
    @BindView(R.id.b30HomeSwipeRefreshLayout)
    SwipeRefreshLayout b30HomeSwipeRefreshLayout;
    @BindView(R.id.homeTodayTv)
    TextView homeTodayTv;
    @BindView(R.id.homeTodayImg)
    ImageView homeTodayImg;
    @BindView(R.id.homeYestTodayTv)
    TextView homeYestTodayTv;
    @BindView(R.id.homeYestdayImg)
    ImageView homeYestdayImg;
    @BindView(R.id.homeBeYestdayTv)
    TextView homeBeYestdayTv;
    @BindView(R.id.homeBeYestdayImg)
    ImageView homeBeYestdayImg;
    //日期的集合
    private ArrayList<String> b30BloadList;
    //高低血压集合
    private List<Map<Integer, Integer>> bloadListMap;

    private List<BarEntry> tmpB30StepList;
    @BindView(R.id.b30SportChartLin)
    LinearLayout b30SportChartLin;
    @BindView(R.id.b30ChartTopRel)
    RelativeLayout b30ChartTopRel;

    //心率图标
    @BindView(R.id.b30HomeHeartChart)
    B30CusHeartView b30CusHeartView;
    //最后一次时间
    @BindView(R.id.lastTimeTv)
    TextView lastTimeTv;
    //心率值
    @BindView(R.id.b30HeartValueTv)
    TextView b30HeartValueTv;
    //心率图标数据
    List<Integer> heartList;
    //最大步数
    @BindView(R.id.b30SportMaxNumTv)
    TextView b30SportMaxNumTv;
    //用于计算最大步数
    private List<Integer> tmpIntegerList;

   // private B30ConnStateReceiver b30ConnStateReceiver;
    private ConnBleHelpService connBleHelpService;

    //目标步数
    int goalStep;
    //默认步数
    int defaultSteps = 0;

    private List<Integer> sleepList;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG,"----hand----="+msg.what);
            switch (msg.what) {
                case 1001:  //b30HomeSwipeRefreshLayout 停止刷新
                    if (getActivity() != null && !getActivity().isFinishing() && b30HomeSwipeRefreshLayout != null) {
                        b30HomeSwipeRefreshLayout.setRefreshing(false);
                    }
                    break;
                case 1002:
                    if (getActivity() != null && b30HomeSwipeRefreshLayout != null) {
                        Log.e(TAG,"----刷新----");
                        b30HomeSwipeRefreshLayout.setRefreshing(false);
                    }
                    getBleMsgData();
                    break;
            }
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG,"----onCreate---");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WatchUtils.B30_CONNECTED_ACTION);
        intentFilter.addAction(WatchUtils.B30_DISCONNECTED_ACTION);
        getActivity().registerReceiver(broadcastReceiver,intentFilter);
       // b30ConnStateReceiver = new B30ConnStateReceiver();
        if(connBleHelpService == null){
            connBleHelpService = connBleHelpService.getConnBleHelpService();
        }

        connBleHelpService.setConnBleMsgDataListener(this);
        connBleHelpService.setConnBleHealthDataListener(this);
       // b30ConnStateReceiver.setB30ConnStateListener(this);
        //目标步数
        goalStep = (int) SharedPreferencesUtils.getParam(MyApp.getContext(), "b30Goal", 0);
        String saveDate = (String) SharedPreferencesUtils.getParam(getActivity(), "saveDate", "");
        if (WatchUtils.isEmpty(saveDate) || Long.valueOf(saveDate).equals("")) {
            SharedPreferencesUtils.setParam(getActivity(), "saveDate", System.currentTimeMillis() / 1000 + "");
        }
        B30Bean b30Beans = new B30Bean();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b30HomeFragment = inflater.inflate(R.layout.fragment_b30_home_layout, container, false);
        unbinder = ButterKnife.bind(this, b30HomeFragment);

        initViews();
        initData();

        return b30HomeFragment;
    }

    private void initData() {
        b30GoalStepTv.setText(getResources().getString(R.string.goal_step) + goalStep + getResources().getString(R.string.steps));

        //运动图表
        b30ChartList = new ArrayList<>();
        tmpB30StepList = new ArrayList<>();
        tmpIntegerList = new ArrayList<>();
        //心率图表
        heartList = new ArrayList<>();
        //血压图表
        b30BloadList = new ArrayList<>();
        bloadListMap = new ArrayList<>();
        sleepList = new ArrayList<>();

        clearDatyStyle(0);
    }


    private void initViews() {
        b30TopDateTv.setText(WatchUtils.getCurrentDate());
        b30ProgressBar.setMaxValue(goalStep);
        b30ProgressBar.setValue(defaultSteps);
        b30SportChartLin.setBackgroundColor(getResources().getColor(R.color.b30_sport));
        b30HomeSwipeRefreshLayout.setOnRefreshListener(this);


    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            if (connBleHelpService != null && MyCommandManager.DEVICENAME != null) {
                long currentTime = System.currentTimeMillis() / 1000;
                //保存的时间
                String tmpSaveTime = (String) SharedPreferencesUtils.getParam(getActivity(), "saveDate", "");
                long diffTime = (currentTime - Long.valueOf(tmpSaveTime)) / 60;
                Log.e(TAG, "----difftime-=" + diffTime + "--=" + diffTime);
                if (WatchConstants.isScanConn) {  //是搜索进来的
                    WatchConstants.isScanConn = false;
                    getBleMsgData();
                    connBleHelpService.getDeviceMsgData();
                    connBleHelpService.readHealthDaty();
                } else {  //不是搜索进来的
                    if (diffTime > 5) {
                        getBleMsgData();
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MyCommandManager.DEVICENAME != null && MyCommandManager.ADDRESS != null) {    //已连接
            b30ConnectStateTv.setText("connected");
        } else {  //未连接
            if (getActivity() != null && !getActivity().isFinishing()) {
                b30ConnectStateTv.setText("disconn");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG,"----onDestroyView---");
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if(broadcastReceiver != null){
                getActivity().unregisterReceiver(broadcastReceiver);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //电量
    @Override
    public void getBleBatteryData(int batteryLevel) {
        if (!getActivity().isFinishing()) {
            try {
                if (batteryLevel >= 0 && batteryLevel == 1) {
                    batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_two));
                } else if (batteryLevel == 2) {
                    batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_three));
                } else if (batteryLevel == 3) {
                    batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_four));
                } else if (batteryLevel == 4) {
                    batteryTopImg.setBackground(getResources().getDrawable(R.mipmap.image_battery_five));
                }
                batteryPowerTv.setText("" + batteryLevel * 25 + "%");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //步数
    @Override
    public void getBleSportData(SportData sportData) {
        B30Bean b30Bean = DataSupport.find(B30Bean.class, 1);
        if (b30Bean != null) {
            String sportStr = b30Bean.getSportDataStr();
            if (sportStr != null) {
                b30Bean.setSportDataStr(new Gson().toJson(sportData));
                b30Bean.update(0x01);
            }
        } else {
            b30Bean = new B30Bean();
            b30Bean.setId(0x01);
            b30Bean.setDate(new Date(System.currentTimeMillis() / 1000));
            b30Bean.setSportDataStr(new Gson().toJson(sportData));
            b30Bean.save();
        }

        defaultSteps = sportData.getStep();
        b30ProgressBar.setMaxValue(goalStep);
        b30ProgressBar.setValue(sportData.getStep());
    }

    //步数图表展示
    private void initBarChart(List<BarEntry> pointbar) {
        BarDataSet barDataSet = new BarDataSet(pointbar, "");
        barDataSet.setDrawValues(false);//是否显示柱子上面的数值
        barDataSet.setColor(Color.parseColor("#fa8072"));//设置第一组数据颜色

        Legend mLegend = b30BarChart.getLegend();
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(15.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色
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

        b30BarChart.getXAxis().setSpaceMax(0.5f);
        b30BarChart.animateXY(1000, 2000);//设置动画

    }

    //健康数据回调
    @Override
    public void getBleHealtyData(OriginHalfHourData originHalfHourData, List<OriginData> originDataList) {
        handler.sendEmptyMessage(1001);
        B30Bean b30Bean = DataSupport.find(B30Bean.class, 1);
        b30Bean.setOriginDataStr(new Gson().toJson(originDataList));
        b30Bean.setHalfHourDataStr(new Gson().toJson(originHalfHourData));
        b30Bean.update(0x01);

        connBleHelpService.readSleepData(3);
        //展示步数的图表
        showSportStepData(originHalfHourData.getHalfHourSportDatas());
        //展示心率图表
        showSportHeartData(originHalfHourData.getHalfHourRateDatas());
        //展示血压图表
        showBloadData(originHalfHourData.getHalfHourBps());
    }

    //睡眠回调
    @Override
    public void getBleSleepData(SleepData sleepData) {
        b30StartEndTimeTv.setText(sleepData.getSleepDown().getColck() + "-" + sleepData.getSleepUp().getColck());
        SharedPreferencesUtils.setParam(getActivity(), "sleepDataStr", new Gson().toJson(sleepData));

        sleepList.clear();
        String slleepLin = sleepData.getSleepLine();
        for (int i = 0; i < slleepLin.length(); i++) {
            if (i <= slleepLin.length() - 1) {
                int subStr = Integer.valueOf(slleepLin.substring(i, i + 1));
                sleepList.add(subStr);
            }

        }
        sleepList.add(0, 2);
        sleepList.add(slleepLin.length(), 2);
        Log.e(TAG, "----睡眠size=" + sleepList.size());
        if (sleepList != null && sleepList.size() > 0) {
            WatchConstants.tmpSleepList = new ArrayList<>();
            WatchConstants.tmpSleepList.clear();
            WatchConstants.tmpSleepList.addAll(sleepList);
            b30CusSleepView.setSleepList(sleepList);
        } else {
            b30CusSleepView.setSleepList(new ArrayList<Integer>());
        }


    }

    //展示血压图表
    @SuppressLint("SetTextI18n")
    private void showBloadData(List<HalfHourBpData> halfHourBps) {
        b30BloadList.clear();
        bloadListMap.clear();
        Log.e(TAG, "----血压=" + halfHourBps.size());
        if (halfHourBps.size() > 0) {
            //获取日期
            for (HalfHourBpData halfHourBpData : halfHourBps) {
                // Log.e(TAG,"---bload-="+halfHourBpData.toString());
                b30BloadList.add(halfHourBpData.getTime().getColck());
                Map<Integer, Integer> mp = new HashMap<>();
                mp.put(halfHourBpData.getLowValue(), halfHourBpData.getHighValue());
                bloadListMap.add(mp);
            }
            //最近一次的血压数据
            HalfHourBpData lastHalfHourBpData = halfHourBps.get(halfHourBps.size() - 1);
            if (lastHalfHourBpData != null) {
                //最近的时间
                bloadLastTimeTv.setText("最近" + lastHalfHourBpData.getTime().getColck());
                //最近时间的血压高低值
                b30BloadValueTv.setText(lastHalfHourBpData.getHighValue() + "/" + lastHalfHourBpData.getLowValue() + "mmhg");
            }

            b30HomeBloadChart.setTimeList(b30BloadList);
            b30HomeBloadChart.setMapList(bloadListMap);
            WatchConstants.tmpBloadList = new ArrayList<>();
            WatchConstants.tmpBloadList.clear();
            WatchConstants.tmpBloadList.addAll(halfHourBps);
            b30HomeBloadChart.setScal(false);
        } else {
            b30HomeBloadChart.setTimeList(b30BloadList);
            b30HomeBloadChart.setMapList(bloadListMap);
            b30HomeBloadChart.setScal(false);
        }

        WatchConstants.tmpListMap = new ArrayList<>();
        WatchConstants.tmpListMap.clear();
        WatchConstants.tmpListMap.addAll(bloadListMap);

    }

    //展示心率图表
    private void showSportHeartData(List<HalfHourRateData> halfHourRateDatas) {
        WatchConstants.tmpHeartList = new ArrayList<>();
        WatchConstants.tmpHeartList.clear();
        WatchConstants.tmpHeartList.addAll(halfHourRateDatas);
        heartList.clear();
        if (halfHourRateDatas.size() > 0) {
            List<Map<String, Integer>> listMap = new ArrayList<>();
            int k = 0;
            for (int i = 0; i < 48; i++) {
                Map<String, Integer> map = new HashMap<>();
                int time = i * 30;
                map.put("time", time);
                TimeData tmpDate = halfHourRateDatas.get(k).getTime();
                int tmpIntDate = tmpDate.getHMValue();

                if (tmpIntDate == time) {
                    map.put("val", halfHourRateDatas.get(k).getRateValue());
                    if (k < halfHourRateDatas.size() - 1) {
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
            HalfHourRateData lastHalfHourRateData = halfHourRateDatas.get(halfHourRateDatas.size() - 1);
            if (lastHalfHourRateData != null) {
                lastTimeTv.setText("最近 " + lastHalfHourRateData.getTime().getColck());
                b30HeartValueTv.setText(lastHalfHourRateData.getRateValue() + " bpm");
            }

            //圆点的半径
            b30CusHeartView.setPointRadio(5);
            b30CusHeartView.setRateDataList(heartList);
        }else{
            //圆点的半径
            b30CusHeartView.setPointRadio(5);
            b30CusHeartView.setRateDataList(heartList);
        }


    }

    //展示步数的图表
    private void showSportStepData(List<HalfHourSportData> halfHourSportDatas) {
        WatchConstants.tmpSportList = new ArrayList<>();
        WatchConstants.tmpSportList.clear();
        WatchConstants.tmpSportList.addAll(halfHourSportDatas);
        b30ChartList.clear();
        tmpIntegerList.clear();
        tmpB30StepList.clear();

        if(halfHourSportDatas.size()>0){
            List<Map<String, Integer>> listMap = new ArrayList<>();
            int k = 0;
            for (int i = 0; i < 48; i++) {
                Map<String, Integer> map = new HashMap<>();
                int time = i * 30;
                map.put("time", time);

                TimeData tmpDate = halfHourSportDatas.get(k).getTime();
                int tmpIntDate = tmpDate.getHMValue();
                if (tmpIntDate == time) {
                    map.put("val", halfHourSportDatas.get(k).getStepValue());
                    if (k < halfHourSportDatas.size() - 1) {
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
                tmpB30StepList.add(new BarEntry(i, tmpMap.get("val")));
                tmpIntegerList.add(tmpMap.get("val"));
            }
            b30ChartList.addAll(tmpB30StepList);
            b30SportMaxNumTv.setText(Collections.max(tmpIntegerList) + getResources().getString(R.string.steps));
            initBarChart(b30ChartList);
            b30BarChart.invalidate();
        }else{
            initBarChart(b30ChartList);
            b30BarChart.setNoDataTextColor(Color.WHITE);
            b30BarChart.invalidate();
        }


    }

    @OnClick({R.id.b30SportChartLin, R.id.b30CusHeartLin, R.id.b30CusBloadLin,
            R.id.b30MeaureHeartImg, R.id.b30MeaureBloadImg, R.id.b30SleepLin,
            R.id.homeTodayTv,R.id.homeYestTodayTv,R.id.homeBeYestdayTv,R.id.battery_watchRecordShareImg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.b30SportChartLin: //步数统计
                startActivity(new Intent(getActivity(), B30StepDetailActivity.class));
                break;
            case R.id.b30CusHeartLin:   //心率
                startActivity(new Intent(getActivity(), B30HeartDetailActivity.class));
                break;
            case R.id.b30CusBloadLin:   //血压
                startActivity(new Intent(getActivity(), B30BloadDetailActivity.class));
                break;
            case R.id.b30MeaureHeartImg:    //手动测量心率
                 startActivity(new Intent(getActivity(), ManualMeaureHeartActivity.class));
                break;
            case R.id.b30MeaureBloadImg:    //手动测量血压
                startActivity(new Intent(getActivity(), ManualMeaureBloadActivity.class));
                break;
            case R.id.b30SleepLin:      //睡眠详情
                startActivity(new Intent(getActivity(), B30SleepDetailActivity.class));
                break;
            case R.id.homeTodayTv:  //今天
                clearDatyStyle(0);
                break;
            case R.id.homeYestTodayTv:  //昨天
                clearDatyStyle(1);
                break;
            case R.id.homeBeYestdayTv:  //前天
                clearDatyStyle(2);
                break;
            case R.id.battery_watchRecordShareImg:  //分享
                WatchUtils.shareCommData(getActivity());
                break;

        }

    }


    //下拉刷新
    @Override
    public void onRefresh() {
        if (getActivity() != null && !getActivity().isFinishing() && !b30HomeSwipeRefreshLayout.isRefreshing()) {
            b30HomeSwipeRefreshLayout.setRefreshing(true);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(1001);
                }
            }, 3 * 1000);

//            handler.postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    handler.sendEmptyMessage(1002);
//                }
//            }, 3 * 1000);

        }
    }

    //获取手环的数据
    private void getBleMsgData() {
        SharedPreferencesUtils.setParam(getActivity(), "saveDate", System.currentTimeMillis() / 1000 + "");
        connBleHelpService.getDeviceMsgData();
        connBleHelpService.readHealthDaty();
    }


    private void clearDatyStyle(int code){
        homeTodayImg.setVisibility(View.INVISIBLE);
        homeYestdayImg.setVisibility(View.INVISIBLE);
        homeBeYestdayImg.setVisibility(View.INVISIBLE);
        switch (code){
            case 0: //今天
                homeTodayImg.setVisibility(View.VISIBLE);
                showHealthData(0);
                break;
            case 1: //昨天
                homeYestdayImg.setVisibility(View.VISIBLE);
                showHealthData(1);
                break;
            case 2: //前天
                homeBeYestdayImg.setVisibility(View.VISIBLE);
                showHealthData(2);
                break;
        }
    }


    private void showHealthData(int code){
        B30Bean b30Bean = DataSupport.find(B30Bean.class,1);
        if(b30Bean != null){
            Log.e(TAG,"----b30Bean="+b30Bean.toString());
            switch (code){
                case 0:
                    String sptStr = b30Bean.getSportDataStr();
                    if(sptStr != null){
                        SportData sportData = new Gson().fromJson(sptStr, SportData.class);
                        getBleSportData(sportData);
                    }

                    String healthData = b30Bean.getHalfHourDataStr();
                    String oriStr = b30Bean.getOriginDataStr();
                    if(healthData != null){
                        OriginHalfHourData originHalfHourData = new Gson().fromJson(healthData,OriginHalfHourData.class);
                        List<OriginData> lt = new Gson().fromJson(oriStr,new TypeToken<List<OriginData>>(){}.getType());
                        getBleHealtyData(originHalfHourData,lt);
                    }
                    break;
                case 1:
                    showEmptyData();
                    break;
                case 2:
                    showEmptyData();
                    break;
            }
        }

    }

    //展示空的数据
    private void showEmptyData(){
        b30ProgressBar.setMaxValue(goalStep);
        b30ProgressBar.setValue(0);

        initBarChart(new ArrayList<BarEntry>());
        b30BarChart.invalidate();


        //展示步数的图表
        showSportStepData(new ArrayList<HalfHourSportData>());
        //展示心率图表
        showSportHeartData(new ArrayList<HalfHourRateData>());
        //展示血压图表
        showBloadData(new ArrayList<HalfHourBpData>());
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG,"-----action="+action);
            if(action != null){
                if(action.equals(WatchUtils.B30_CONNECTED_ACTION)){ //连接
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        b30ConnectStateTv.setText("connected");
                        if (connBleHelpService != null && MyCommandManager.DEVICENAME != null) {
                            connBleHelpService.getDeviceMsgData();
                            connBleHelpService.readHealthDaty();
                        }
                    }
                }
                if(action.equals(WatchUtils.B30_DISCONNECTED_ACTION)){  //断开
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        b30ConnectStateTv.setText("disconn");
                    }
                }
            }
        }
    };

}
