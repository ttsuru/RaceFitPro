package com.example.bozhilun.android.b30;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.aigestudio.wheelpicker.widgets.ProvincePick;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.WatchDeviceActivity;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ILongSeatDataListener;
import com.veepoo.protocol.model.datas.LongSeatData;
import com.veepoo.protocol.model.settings.LongSeatSetting;
import com.veepoo.protocol.operate.LongSeatOperater;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/8/13.
 */

/**
 * 久坐提醒设置时间
 */
public class B30LongSitSetActivity extends WatchBaseActivity {


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.showB30LongSitStartTv)
    TextView showB30LongSitStartTv;
    @BindView(R.id.showB30LongSitEndTv)
    TextView showB30LongSitEndTv;
    @BindView(R.id.showB30LongSitTv)
    TextView showB30LongSitTv;


    private ArrayList<String> hourList;
    private ArrayList<String> minuteList;
    private HashMap<String, ArrayList<String>> minuteMapList;
    ArrayList<String> longTimeLit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_long_sitset);
        ButterKnife.bind(this);

        initViews();
        initData();
        //读取久坐提醒
        readLongSitData();

    }

    private void readLongSitData() {
        //读取久坐提醒
        MyApp.getVpOperateManager().readLongSeat(iBleWriteResponse, new ILongSeatDataListener() {
            @Override
            public void onLongSeatDataChange(LongSeatData longSeatData) {
                showB30LongSitStartTv.setText(longSeatData.getStartHour()+":"+longSeatData.getStartMinute());
                showB30LongSitEndTv.setText(longSeatData.getEndHour()+":"+longSeatData.getEndMinute());
                showB30LongSitTv.setText(longSeatData.getThreshold()+"min");
            }
        });
    }

    private void initData() {
        hourList = new ArrayList<>();
        minuteList = new ArrayList<>();
        minuteMapList = new HashMap<>();
        longTimeLit = new ArrayList<>();
        for(int i = 30;i<=240;i++){
            longTimeLit.add(i+"");
        }

        for (int i = 0; i < 60; i++) {
            if (i == 0) {
                minuteList.add("00");
            } else if (i < 10) {
                minuteList.add("0"+i);
            } else {
                minuteList.add(i + "");
            }
        }
        for (int i = 0; i < 24; i++) {
            if (i == 0) {
                hourList.add("00");
                minuteMapList.put("00", minuteList);
            } else if (i < 10) {
                hourList.add("0" + i + "");
                minuteMapList.put("0" + i + "", minuteList);
            } else {
                hourList.add(i + "");
                minuteMapList.put(i + "", minuteList);
            }
        }
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText("久坐设置");
    }

    @OnClick({R.id.commentB30BackImg, R.id.b30LongSitStartRel, R.id.b30LongSitEndRel,
            R.id.b30LongSitTimeRel, R.id.b30LongSitSaveBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.b30LongSitStartRel:   //开始时间
                chooseStartEndDate(0);
                break;
            case R.id.b30LongSitEndRel: //结束时间
                chooseStartEndDate(1);
                break;
            case R.id.b30LongSitTimeRel:    //时长
                chooseLongTime();
                break;
            case R.id.b30LongSitSaveBtn:    //保存
                saveLongSitData();
                break;
        }
    }

    private void saveLongSitData() {
        if(MyCommandManager.DEVICENAME != null){
            String startD = showB30LongSitStartTv.getText().toString().trim();
            int startHour = Integer.valueOf(StringUtils.substringBefore(startD,":").trim());
            int startMine = Integer.valueOf(StringUtils.substringAfter(startD,":").trim());
            String endD = showB30LongSitEndTv.getText().toString().trim();
            int endHour = Integer.valueOf(StringUtils.substringBefore(endD,":").trim());
            int endMine = Integer.valueOf(StringUtils.substringAfter(endD,":").trim());
            //时长
            String longD = showB30LongSitTv.getText().toString().trim();
            int longTime = Integer.valueOf(StringUtils.substringBefore(longD,"min").trim());
            MyApp.getVpOperateManager().settingLongSeat(iBleWriteResponse, new LongSeatSetting(startHour, startMine, endHour, endMine, longTime, true), new ILongSeatDataListener() {
                @Override
                public void onLongSeatDataChange(LongSeatData longSeatData) {
                    Log.e("久坐","----longSeatData="+longSeatData.toString());
                    if(longSeatData.getStatus() == LongSeatOperater.LSStatus.OPEN_SUCCESS){
                        finish();
                    }
                }
            });
        }
    }

    //设置时长
    private void chooseLongTime() {
        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(B30LongSitSetActivity.this, new ProfessionPick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String profession) {
                showB30LongSitTv.setText(profession+"min");
            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(longTimeLit) //min year in loop
                .dateChose("10000") // date chose when init popwindow
                .build();
        dailyumberofstepsPopWin.showPopWin(B30LongSitSetActivity.this);
    }

    //选择时间
    private void chooseStartEndDate(final int code) {
        ProvincePick starPopWin = new ProvincePick.Builder(B30LongSitSetActivity.this, new ProvincePick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String province, String city, String dateDesc) {
                if (code == 0) {  //开始时间
                    showB30LongSitStartTv.setText(province + ":" + city);
                } else if (code == 1) {    //结束时间
                    showB30LongSitEndTv.setText(province + ":" + city);
                }

            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(hourList) //min year in loop
                .setCityList(minuteMapList) // max year in loop
                .build();
        starPopWin.showPopWin(B30LongSitSetActivity.this);
    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };
}
