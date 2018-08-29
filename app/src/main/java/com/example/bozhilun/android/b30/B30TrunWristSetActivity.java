package com.example.bozhilun.android.b30;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.aigestudio.wheelpicker.widgets.ProvincePick;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.INightTurnWristeDataListener;
import com.veepoo.protocol.model.datas.NightTurnWristeData;
import com.veepoo.protocol.model.datas.TimeData;
import com.veepoo.protocol.model.settings.NightTurnWristSetting;
import com.veepoo.protocol.operate.NightTurnWristOperate;
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
 * B30转腕亮屏设置
 */
public class B30TrunWristSetActivity extends WatchBaseActivity {
    private static final String TAG = "B30TrunWristSetActivity";

    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.b30TrunWristStartTv)
    TextView b30TrunWristStartTv;
    @BindView(R.id.b30TrunWristendTv)
    TextView b30TrunWristendTv;
    @BindView(R.id.b30TrunWristSeekBar)
    SeekBar b30TrunWristSeekBar;
    @BindView(R.id.showSeekBarValueTv)
    TextView showSeekBarValueTv;
    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;

    private ArrayList<String> hourList;
    private ArrayList<String> minuteList;
    private HashMap<String, ArrayList<String>> minuteMapList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_turn_wrist_set);
        ButterKnife.bind(this);

        initViews();
        initData();

    }

    private void initData() {
        hourList = new ArrayList<>();
        minuteList = new ArrayList<>();
        minuteMapList = new HashMap<>();
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

        MyApp.getVpOperateManager().readNightTurnWriste(iBleWriteResponse, new INightTurnWristeDataListener() {
            @Override
            public void onNightTurnWristeDataChange(NightTurnWristeData nightTurnWristeData) {
                Log.e(TAG, "-----nightTurnWristeData=" + nightTurnWristeData.toString());
                b30TrunWristStartTv.setText(nightTurnWristeData.getStartTime().getColck() + "");
                b30TrunWristendTv.setText(nightTurnWristeData.getEndTime().getColck() + "");
                b30TrunWristSeekBar.setProgress(nightTurnWristeData.getLevel());
            }
        });
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText("转腕亮屏");
        b30TrunWristSeekBar.setMax(9);
        b30TrunWristSeekBar.setProgress(6);
        b30TrunWristSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                showSeekBarValueTv.setText("(" + progress + ")");


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @OnClick({R.id.commentB30BackImg, R.id.b30WristStartRel, R.id.b30WristEndRel, R.id.b30TrunWristSaveBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.b30WristStartRel: //开始时间
                setChooseDate(0);
                break;
            case R.id.b30WristEndRel:   //结束时间
                setChooseDate(1);
                break;
            case R.id.b30TrunWristSaveBtn:  //保存
                //起始时间
                String startD = b30TrunWristStartTv.getText().toString().trim();
                int startHour = Integer.valueOf(StringUtils.substringBefore(startD, ":").trim());
                int startMine = Integer.valueOf(StringUtils.substringAfter(startD, ":").trim());
                TimeData startTime = new TimeData(startHour, startMine);
                String endD = b30TrunWristendTv.getText().toString().trim();
                int endHour = Integer.valueOf(StringUtils.substringBefore(endD, ":").trim());
                int endMine = Integer.valueOf(StringUtils.substringAfter(endD, ":").trim());
                TimeData endTime = new TimeData(endHour, endMine);


                MyApp.getVpOperateManager().settingNightTurnWriste(iBleWriteResponse, new INightTurnWristeDataListener() {
                    @Override
                    public void onNightTurnWristeDataChange(NightTurnWristeData nightTurnWristeData) {
                        Log.e(TAG,"-----翻腕-="+nightTurnWristeData.toString());
                        if(nightTurnWristeData.getOprateStauts() == NightTurnWristOperate.NTStatus.SUCCESS){
                            finish();
                        }
                    }
                }, new NightTurnWristSetting(true,startTime,endTime,b30TrunWristSeekBar.getProgress()));
                break;
        }
    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };

    private void setChooseDate(final int code) {
        ProvincePick starPopWin = new ProvincePick.Builder(B30TrunWristSetActivity.this, new ProvincePick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String province, String city, String dateDesc) {
                if (code == 0) {  //开始时间
                    b30TrunWristStartTv.setText(province + ":" + city);
                } else if (code == 1) {    //结束时间
                    b30TrunWristendTv.setText(province + ":" + city);
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
        starPopWin.showPopWin(B30TrunWristSetActivity.this);
    }
}
