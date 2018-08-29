package com.example.bozhilun.android.b30;


import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.WatchDeviceActivity;
import com.example.bozhilun.android.siswatch.bleus.WatchBluetoothService;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.w30s.carema.W30sCameraActivity;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IBPSettingDataListener;
import com.veepoo.protocol.listener.data.ICameraDataListener;
import com.veepoo.protocol.listener.data.ILongSeatDataListener;
import com.veepoo.protocol.listener.data.INightTurnWristeDataListener;
import com.veepoo.protocol.model.datas.BpSettingData;
import com.veepoo.protocol.model.datas.LongSeatData;
import com.veepoo.protocol.model.datas.NightTurnWristeData;
import com.veepoo.protocol.model.enums.EBPDetectModel;
import com.veepoo.protocol.model.settings.BpSetting;
import com.veepoo.protocol.model.settings.LongSeatSetting;
import com.veepoo.protocol.operate.CameraOperater;
import com.veepoo.protocol.operate.LongSeatOperater;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/8/4.
 */

/**
 * 设备页面
 */
public class B30DeviceActivity extends WatchBaseActivity {

    private static final String TAG = "B30DeviceActivity";

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.longSitToggleBtn)
    ToggleButton longSitToggleBtn;
    @BindView(R.id.turnWristToggleBtn)
    ToggleButton turnWristToggleBtn;
    @BindView(R.id.privateBloadToggleBtn)
    ToggleButton privateBloadToggleBtn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_device_layout);
        ButterKnife.bind(this);

        initViews();

        initData();

    }

    private void initData() {
        //读取是否转腕亮屏
        if(MyCommandManager.DEVICENAME != null){
            //读取久坐提醒
            MyApp.getVpOperateManager().readLongSeat(iBleWriteResponse, new ILongSeatDataListener() {
                @Override
                public void onLongSeatDataChange(LongSeatData longSeatData) {
                    Log.e(TAG,"----久坐提醒="+longSeatData.toString());
                    longSitToggleBtn.setChecked(longSeatData.isOpen());
                }
            });
            //读取转腕亮屏
            MyApp.getVpOperateManager().readNightTurnWriste(iBleWriteResponse, new INightTurnWristeDataListener() {
                @Override
                public void onNightTurnWristeDataChange(NightTurnWristeData nightTurnWristeData) {
                    Log.e(TAG,"----转腕亮屏="+nightTurnWristeData.toString());
                    turnWristToggleBtn.setChecked(nightTurnWristeData.isNightTureWirsteStatusOpen());
                }
            });

            //读取私人血压
            MyApp.getVpOperateManager().readDetectBP(iBleWriteResponse, new IBPSettingDataListener() {
                @Override
                public void onDataChange(BpSettingData bpSettingData) {
                    Log.e(TAG,"---读取私人血压="+bpSettingData.toString());
                    if(bpSettingData.getModel() == EBPDetectModel.DETECT_MODEL_PRIVATE){
                        privateBloadToggleBtn.setChecked(true);
                    }else{
                        privateBloadToggleBtn.setChecked(false);
                    }
                }
            });
        }
    }

    private void initViews() {
        commentB30TitleTv.setText(getResources().getString(R.string.device));
        commentB30BackImg.setVisibility(View.VISIBLE);

        longSitToggleBtn.setOnCheckedChangeListener(new ToggleClickListener());
        turnWristToggleBtn.setOnCheckedChangeListener(new ToggleClickListener());
        privateBloadToggleBtn.setOnCheckedChangeListener(new ToggleClickListener());
    }


    @OnClick({R.id.commentB30BackImg, R.id.b30DeviceMsgRel, R.id.b30DeviceAlarmRel,
            R.id.b30DeviceLongSitRel, R.id.b30DeviceWristRel, R.id.b30DevicePrivateBloadRel,
            R.id.b30DeviceSwitchRel, R.id.b30DevicePtoRel, R.id.b30DeviceCounDownRel, R.id.b30DeviceResetRel,
            R.id.b30DeviceStyleRel, R.id.b30DevicedfuRel, R.id.b30DeviceClearDataRel,R.id.b30DisConnBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.b30DeviceMsgRel:  //消息提醒
                startActivity(B30MessAlertActivity.class);
                break;
            case R.id.b30DeviceAlarmRel:    //闹钟设置

                break;
            case R.id.b30DeviceLongSitRel:  //久坐提醒
                startActivity(B30LongSitSetActivity.class);
                break;
            case R.id.b30DeviceWristRel:    //翻腕亮屏
                startActivity(B30TrunWristSetActivity.class);
                break;
            case R.id.b30DevicePrivateBloadRel: //血压私人模式
                startActivity(PrivateBloadActivity.class);
                break;
            case R.id.b30DeviceSwitchRel:   //开关设置
                startActivity(B30SwitchSetActivity.class);
                break;
            case R.id.b30DevicePtoRel:  //拍照
                if(AndPermission.hasPermissions(B30DeviceActivity.this, Manifest.permission.CAMERA)){
                    startActivity(W30sCameraActivity.class);
                }else{
                    AndPermission.with(B30DeviceActivity.this)
                            .runtime()
                            .permission(Manifest.permission.CAMERA)
                            .onGranted(new Action<List<String>>() {
                                @Override
                                public void onAction(List<String> data) {
                                    startActivity(W30sCameraActivity.class);
                                }
                            })
                            .rationale(new Rationale<List<String>>() {
                                @Override
                                public void showRationale(Context context, List<String> data, RequestExecutor executor) {

                                }

                            }).start();
                }

                break;
            case R.id.b30DeviceCounDownRel: //倒计时

                break;
            case R.id.b30DeviceResetRel:    //重置设备密码
                startActivity(B30ResetActivity.class);
                break;
            case R.id.b30DeviceStyleRel:    //主题风格
                startActivity(B30ScreenStyleActivity.class);
                break;
            case R.id.b30DevicedfuRel:  //固件升级
                startActivity(B30DufActivity.class);
                break;
            case R.id.b30DeviceClearDataRel:    //清除数据
                new MaterialDialog.Builder(this)
                        .title(getResources().getString(R.string.prompt))
                        .content("是否确认清除数据?")
                        .positiveText(getResources().getString(R.string.confirm))
                        .negativeText(getResources().getString(R.string.cancle))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                               MyApp.getVpOperateManager().clearDeviceData(iBleWriteResponse);
                            }
                        }).show();
                break;
            case R.id.b30DisConnBtn:    //断开连接
                disB30Conn();
                break;
        }
    }

    //断开连接
    private void disB30Conn() {
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.prompt))
                .content("是否断开连接?")
                .positiveText(getResources().getString(R.string.confirm))
                .negativeText(getResources().getString(R.string.cancle))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if(MyCommandManager.DEVICENAME != null){
                            MyCommandManager.DEVICENAME = null;
                            MyCommandManager.ADDRESS = null;
                            MyApp.getVpOperateManager().disconnectWatch(new IBleWriteResponse() {
                                @Override
                                public void onResponse(int state) {
                                    Log.e(TAG,"----state="+state);
                                    if(state == -1){
                                        SharedPreferencesUtils.saveObject(B30DeviceActivity.this, "mylanya", "");
                                        SharedPreferencesUtils.saveObject(B30DeviceActivity.this, "mylanmac", "");
                                        startActivity(NewSearchActivity.class);
                                        finish();
                                        return;
                                    }
                                }
                            });
                        }else{
                            MyCommandManager.DEVICENAME = null;
                            MyCommandManager.ADDRESS = null;
                            SharedPreferencesUtils.saveObject(B30DeviceActivity.this, "mylanya", "");
                            SharedPreferencesUtils.saveObject(B30DeviceActivity.this, "mylanmac", "");
                            startActivity(NewSearchActivity.class);
                            finish();
                        }


                    }
                }).show();
    }

    //开关按钮点击监听
    private class ToggleClickListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(MyCommandManager.DEVICENAME != null){
                switch (buttonView.getId()){
                    case R.id.longSitToggleBtn: //久坐设置
                        setLongSit(isChecked);
                        break;
                    case R.id.turnWristToggleBtn:   //转腕亮屏
                        setNightTurnWriste(isChecked);
                        break;
                    case R.id.privateBloadToggleBtn:    //血压私人模式
                        boolean isPrivateBload = isChecked;
                        boolean isAngioAdjuste = false;
                        BpSetting bpSetting = new BpSetting(isPrivateBload, 111, 88);
                        bpSetting.setAngioAdjuste(isAngioAdjuste);

                        MyApp.getVpOperateManager().settingDetectBP(iBleWriteResponse, new IBPSettingDataListener() {
                            @Override
                            public void onDataChange(BpSettingData bpSettingData) {
                                Log.e(TAG,"----设置私人血压模式="+bpSettingData.toString());
                            }
                        }, bpSetting);

                        break;

                }
            }

        }
    }

    //设置转腕亮屏
    private void setNightTurnWriste(boolean isOn){
        if(MyCommandManager.DEVICENAME != null){
            MyApp.getVpOperateManager().settingNightTurnWriste(iBleWriteResponse, new INightTurnWristeDataListener() {
                @Override
                public void onNightTurnWristeDataChange(NightTurnWristeData nightTurnWristeData) {

                }
            }, isOn);
        }
    }

    //设置久坐提醒
    private void setLongSit(boolean isOpen){
        MyApp.getVpOperateManager().settingLongSeat(iBleWriteResponse, new LongSeatSetting(8, 0, 19, 0, 60, isOpen),
                new ILongSeatDataListener() {
            @Override
            public void onLongSeatDataChange(LongSeatData longSeatData) {
                Log.e(TAG,"-----设置久坐="+longSeatData.toString());
            }
        });
    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };

}
