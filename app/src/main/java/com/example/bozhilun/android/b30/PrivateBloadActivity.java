package com.example.bozhilun.android.b30;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.view.ScrollPickerView;
import com.example.bozhilun.android.b30.view.StringScrollPicker;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IBPSettingDataListener;
import com.veepoo.protocol.model.datas.BpSettingData;
import com.veepoo.protocol.model.settings.BpSetting;
import com.veepoo.protocol.operate.BPModelOprate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 私人血压
 */
public class PrivateBloadActivity extends WatchBaseActivity implements ScrollPickerView.OnSelectedListener{

    private static final String TAG = "PrivateBloadActivity";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.hightBloadView)
    StringScrollPicker hightBloadView;
    @BindView(R.id.lowBloadView)
    StringScrollPicker lowBloadView;


    //血压数据
    private List<String> hightBloadList;
    //低压
    private List<String> lowBloadList;

    private int highBload ;
    private int lowBload ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_bload_b30);
        ButterKnife.bind(this);


        initViews();

        initData();

        readBloadState();


    }

    //读取私人血压
    private void readBloadState() {
        if(MyCommandManager.DEVICENAME != null){
            MyApp.getVpOperateManager().readDetectBP(iBleWriteResponse, new IBPSettingDataListener() {
                @Override
                public void onDataChange(BpSettingData bpSettingData) {
                    Log.e(TAG,"----bpSettingData="+bpSettingData.toString());
                }
            });
        }
    }

    private void initData() {
        hightBloadList = new ArrayList<>();
        lowBloadList = new ArrayList<>();
        for(int i = 80;i<=209;i++){
            hightBloadList.add(i+1+"");
        }
        for(int k = 46;k<=179;k++){
            lowBloadList.add(k+1+"");
        }

        hightBloadView.setData(hightBloadList);
        hightBloadView.setSelectedPosition(40);
        highBload = Integer.valueOf(hightBloadList.get(40));


        lowBloadView.setData(lowBloadList);
        lowBloadView.setSelectedPosition(34);
        lowBload = Integer.valueOf(lowBloadList.get(34));

        hightBloadView.setOnSelectedListener(this);
        lowBloadView.setOnSelectedListener(this);

    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText("血压私人模式设置");


    }

    @OnClick({R.id.commentB30BackImg, R.id.b30SetPrivateBloadBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.b30SetPrivateBloadBtn:    //保存
                if(MyCommandManager.DEVICENAME != null){
                    BpSetting bpSetting = new BpSetting(true,highBload,lowBload);
                    bpSetting.setAngioAdjuste(false);
                    MyApp.getVpOperateManager().settingDetectBP(iBleWriteResponse, new IBPSettingDataListener() {
                        @Override
                        public void onDataChange(BpSettingData bpSettingData) {
                            Log.e(TAG,"-----设置="+bpSettingData.toString());
                            if(bpSettingData.getStatus() == BPModelOprate.BPStatus.SETTING_PRIVATE_SUCCESS){
                                finish();
                            }

                        }
                    }, bpSetting);
                }

                break;
        }
    }

    @Override
    public void onSelected(ScrollPickerView scrollPickerView, int position) {
       // Log.e(TAG,"---view="+scrollPickerView.getData().toString()+"---posiiton="+position);
        switch (scrollPickerView.getId()){
            case R.id.hightBloadView:   //高压
                highBload = Integer.valueOf(hightBloadList.get(position));
                Log.e(TAG,"--1-view="+highBload);
                break;
            case R.id.lowBloadView: //低压
                lowBload = Integer.valueOf(lowBloadList.get(position));
                Log.e(TAG,"--22-lowBload="+lowBload);
                break;
        }
    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };
}
