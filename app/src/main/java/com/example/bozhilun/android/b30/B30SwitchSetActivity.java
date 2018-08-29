package com.example.bozhilun.android.b30;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ICheckWearDataListener;
import com.veepoo.protocol.listener.data.ICustomSettingDataListener;
import com.veepoo.protocol.listener.data.IFindDeviceDatalistener;
import com.veepoo.protocol.model.datas.CheckWearData;
import com.veepoo.protocol.model.datas.FindDeviceData;
import com.veepoo.protocol.model.enums.ECheckWear;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.veepoo.protocol.model.settings.CheckWearSetting;
import com.veepoo.protocol.model.settings.CustomSetting;
import com.veepoo.protocol.model.settings.CustomSettingData;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/8/13.
 */

/**
 * B30的开关设置
 */
public class B30SwitchSetActivity extends WatchBaseActivity {

    private static final String TAG = "B30SwitchSetActivity";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.b30CheckWearToggleBtn)
    ToggleButton b30CheckWearToggleBtn;
    @BindView(R.id.b30AutoHeartToggleBtn)
    ToggleButton b30AutoHeartToggleBtn;
    @BindView(R.id.b30AutoBloadToggleBtn)
    ToggleButton b30AutoBloadToggleBtn;
    @BindView(R.id.b30SwitchFindPhoneToggleBtn)
    ToggleButton b30SwitchFindPhoneToggleBtn;
    @BindView(R.id.b30SecondToggleBtn)
    ToggleButton b30SecondToggleBtn;
    @BindView(R.id.b30SwitchDisAlertTogg)
    ToggleButton b30SwitchDisAlertTogg;

    boolean isHaveMetricSystem = true;
    boolean isMetric = true;
    boolean is24Hour = true;    //是否是24小时
    boolean isOpenAutoHeartDetect = true;
    boolean isOpenAutoBpDetect = true;  //血压
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_switch_set);
        ButterKnife.bind(this);

        initViews();
        readSwitchData();

    }

    private void readSwitchData() {
        boolean isWearCheck = (boolean) SharedPreferencesUtils.getParam(B30SwitchSetActivity.this, "b30wearcheck", false);
        boolean isdisAlert = (boolean) SharedPreferencesUtils.getParam(B30SwitchSetActivity.this, "b30disAlert", false);
        b30CheckWearToggleBtn.setChecked(isWearCheck);
        b30SwitchDisAlertTogg.setChecked(isdisAlert);


        if (MyCommandManager.DEVICENAME != null) {
            MyApp.getVpOperateManager().readCustomSetting(iBleWriteResponse, new ICustomSettingDataListener() {
                @Override
                public void OnSettingDataChange(CustomSettingData customSettingData) {
                    Log.e(TAG, "---自定义设置--=" + customSettingData.toString());
                    if (customSettingData.getAutoHeartDetect() == EFunctionStatus.SUPPORT_OPEN) {
                        b30AutoHeartToggleBtn.setChecked(true);
                    }
                    if (customSettingData.getAutoBpDetect() == EFunctionStatus.SUPPORT_OPEN) {
                        b30AutoBloadToggleBtn.setChecked(true);
                    }
                    if (customSettingData.getFindPhoneUi() == EFunctionStatus.SUPPORT_OPEN) {
                        b30SwitchFindPhoneToggleBtn.setChecked(true);
                    }
                    if (customSettingData.getSecondsWatch() == EFunctionStatus.SUPPORT_OPEN) {
                        b30SecondToggleBtn.setChecked(true);
                    }

                }
            });

        }
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText("开关设置");
        b30CheckWearToggleBtn.setOnCheckedChangeListener(new SwitchClickListener());
        b30AutoHeartToggleBtn.setOnCheckedChangeListener(new SwitchClickListener());
        b30AutoBloadToggleBtn.setOnCheckedChangeListener(new SwitchClickListener());
        b30SwitchFindPhoneToggleBtn.setOnCheckedChangeListener(new SwitchClickListener());
        b30SecondToggleBtn.setOnCheckedChangeListener(new SwitchClickListener());
        b30SwitchDisAlertTogg.setOnCheckedChangeListener(new SwitchClickListener());

    }

    @OnClick(R.id.commentB30BackImg)
    public void onViewClicked() {
        finish();

    }

    private class SwitchClickListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (MyCommandManager.DEVICENAME != null) {
                switch (buttonView.getId()) {
                    case R.id.b30CheckWearToggleBtn:    //佩戴检测
                        SharedPreferencesUtils.setParam(B30SwitchSetActivity.this, "b30wearcheck", isChecked);
                        CheckWearSetting checkWearSetting = new CheckWearSetting();
                        checkWearSetting.setOpen(isChecked);
                        MyApp.getVpOperateManager().setttingCheckWear(iBleWriteResponse, new ICheckWearDataListener() {
                            @Override
                            public void onCheckWearDataChange(CheckWearData checkWearData) {
                                Log.e(TAG, "------佩戴检测=" + checkWearData.getCheckWearState());
                                if (checkWearData.getCheckWearState() == ECheckWear.OPEN_SUCCESS) {
                                    SharedPreferencesUtils.setParam(B30SwitchSetActivity.this, "b30wearcheck", true);
                                }
                            }
                        }, checkWearSetting);
                        break;
                    case R.id.b30AutoHeartToggleBtn:    //心率自动检测
                        b30AutoHeartToggleBtn.setChecked(isChecked);
                        isOpenAutoHeartDetect = isChecked;
                        break;
                    case R.id.b30AutoBloadToggleBtn:    //血压自动检测
                        b30AutoBloadToggleBtn.setChecked(isChecked);
                        isOpenAutoBpDetect = isChecked;  //血压
                        break;
                    case R.id.b30SwitchFindPhoneToggleBtn:  //查找手机
                        checkViberPerminess();
                        b30SwitchFindPhoneToggleBtn.setChecked(isChecked);
                        MyApp.getVpOperateManager().settingFindDevice(iBleWriteResponse, new IFindDeviceDatalistener() {
                            @Override
                            public void onFindDevice(FindDeviceData findDeviceData) {
                                Log.e(TAG, "----查找手机=" + findDeviceData.toString());
                            }
                        }, isChecked);
                        break;
                    case R.id.b30SecondToggleBtn:   //秒表功能
                        b30SecondToggleBtn.setChecked(isChecked);
                        break;
                    case R.id.b30SwitchDisAlertTogg:    //断连提醒
                        b30SwitchDisAlertTogg.setChecked(isChecked);
                        SharedPreferencesUtils.setParam(B30SwitchSetActivity.this, "b30disAlert", isChecked);
                        break;
                }

                MyApp.getVpOperateManager().changeCustomSetting(iBleWriteResponse, new ICustomSettingDataListener() {
                    @Override
                    public void OnSettingDataChange(CustomSettingData customSettingData) {
                        Log.e(TAG, "----OnSettingDataChange=" + customSettingData.toString());
                    }
                }, new CustomSetting(isHaveMetricSystem, isMetric, is24Hour, isOpenAutoHeartDetect, isOpenAutoBpDetect));
            }
        }
    }

    //检查是否有震动的权限
    private void checkViberPerminess() {
        if (!AndPermission.hasPermissions(B30SwitchSetActivity.this, Manifest.permission.VIBRATE)) {
            AndPermission.with(B30SwitchSetActivity.this)
                    .runtime()
                    .permission(Manifest.permission.VIBRATE)
                    .rationale(new Rationale<List<String>>() {
                        @Override
                        public void showRationale(Context context, List<String> data, RequestExecutor executor) {

                        }
                    }).start();
        }
    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };
}
