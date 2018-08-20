package com.example.bozhilun.android.b30;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.ISocialMsgDataListener;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.enums.EFunctionStatus;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/8/13.
 */

/**
 * B30消息提醒页面
 */
public class B30MessAlertActivity extends WatchBaseActivity {

    private static final String TAG = "B30MessAlertActivity";

    @BindView(R.id.b30SkypeTogg)
    ToggleButton b30SkypeTogg;
    @BindView(R.id.b30WhatsAppTogg)
    ToggleButton b30WhatsAppTogg;
    @BindView(R.id.b30FacebookTogg)
    ToggleButton b30FacebookTogg;
    @BindView(R.id.b30LinkedTogg)
    ToggleButton b30LinkedTogg;
    @BindView(R.id.b30TwitterTogg)
    ToggleButton b30TwitterTogg;
    @BindView(R.id.b30LineTogg)
    ToggleButton b30LineTogg;
    @BindView(R.id.b30WechatTogg)
    ToggleButton b30WechatTogg;
    @BindView(R.id.b30QQTogg)
    ToggleButton b30QQTogg;
    @BindView(R.id.b30MessageTogg)
    ToggleButton b30MessageTogg;
    @BindView(R.id.b30PhoneTogg)
    ToggleButton b30PhoneTogg;
    @BindView(R.id.newSearchTitleTv)
    TextView newSearchTitleTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_msgalert);
        ButterKnife.bind(this);

        initViews();
        //读取社交消息设置
        readSocialMsg();

    }

    private void readSocialMsg() {
        if (MyCommandManager.DEVICENAME != null) {
            MyApp.getVpOperateManager().readSocialMsg(iBleWriteResponse, new ISocialMsgDataListener() {
                @Override
                public void onSocialMsgSupportDataChange(FunctionSocailMsgData functionSocailMsgData) {
                    Log.e(TAG, "----读取=" + functionSocailMsgData.toString());

                    if (functionSocailMsgData.getSkype() == EFunctionStatus.SUPPORT_OPEN) {
                        b30SkypeTogg.setChecked(true);
                    } else {
                        b30SkypeTogg.setChecked(false);
                    }

                    if (functionSocailMsgData.getWhats() == EFunctionStatus.SUPPORT_OPEN) {
                        b30WhatsAppTogg.setChecked(true);
                    } else {
                        b30WhatsAppTogg.setChecked(false);
                    }

                    if (functionSocailMsgData.getFacebook() == EFunctionStatus.SUPPORT_OPEN) {
                        b30FacebookTogg.setChecked(true);
                    } else {
                        b30FacebookTogg.setChecked(false);
                    }
                    if (functionSocailMsgData.getLinkin() == EFunctionStatus.SUPPORT_OPEN) {
                        b30LinkedTogg.setChecked(true);
                    } else {
                        b30LinkedTogg.setChecked(false);
                    }
                    if (functionSocailMsgData.getTwitter() == EFunctionStatus.SUPPORT_OPEN) {
                        b30TwitterTogg.setChecked(true);
                    } else {
                        b30TwitterTogg.setChecked(false);
                    }
                    //viber


                    if (functionSocailMsgData.getLine() == EFunctionStatus.SUPPORT_OPEN) {
                        b30LineTogg.setChecked(true);
                    } else {
                        b30LineTogg.setChecked(false);
                    }
                    if (functionSocailMsgData.getWechat() == EFunctionStatus.SUPPORT_OPEN) {
                        b30WechatTogg.setChecked(true);
                    } else {
                        b30WechatTogg.setChecked(false);
                    }
                    if (functionSocailMsgData.getQq() == EFunctionStatus.SUPPORT_OPEN) {
                        b30QQTogg.setChecked(true);
                    } else {
                        b30QQTogg.setChecked(false);
                    }
                    if (functionSocailMsgData.getMsg() == EFunctionStatus.SUPPORT_OPEN) {
                        b30MessageTogg.setChecked(true);
                    } else {
                        b30MessageTogg.setChecked(false);
                    }
                    if (functionSocailMsgData.getPhone() == EFunctionStatus.SUPPORT_OPEN) {
                        b30PhoneTogg.setChecked(true);
                    } else {
                        b30PhoneTogg.setChecked(false);
                    }
                }
            });
        }
    }



    private void initViews() {
        newSearchTitleTv.setText("社交消息");
        b30SkypeTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30WhatsAppTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30FacebookTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30LinkedTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30TwitterTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30LineTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30WechatTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30QQTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30MessageTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());
        b30PhoneTogg.setOnCheckedChangeListener(new ToggCheckChanageListener());


    }

    @OnClick({R.id.newSearchTitleLeft, R.id.newSearchRightImg1,R.id.msgOpenNitBtn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.newSearchTitleLeft:
                finish();
                break;
            case R.id.newSearchRightImg1:

                break;
            case R.id.msgOpenNitBtn:    //打开通知
                Intent intentr = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivityForResult(intentr, 1001);
                break;
        }
    }

    //监听
    private class ToggCheckChanageListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            FunctionSocailMsgData socailMsgData = new FunctionSocailMsgData();
            switch (buttonView.getId()) {
                case R.id.b30SkypeTogg: //skype
                    if (isChecked) {
                        socailMsgData.setSkype(EFunctionStatus.SUPPORT_OPEN);
                    } else {
                        socailMsgData.setSkype(EFunctionStatus.SUPPORT_CLOSE);
                    }
                    break;
                case R.id.b30WhatsAppTogg:  //whatspp
                    if (isChecked) {
                        socailMsgData.setWhats(EFunctionStatus.SUPPORT_OPEN);
                    } else {
                        socailMsgData.setWhats(EFunctionStatus.SUPPORT_CLOSE);
                    }
                    break;
                case R.id.b30FacebookTogg:  //facebook
                    if (isChecked) {
                        socailMsgData.setFacebook(EFunctionStatus.SUPPORT_OPEN);
                    } else {
                        socailMsgData.setFacebook(EFunctionStatus.SUPPORT_CLOSE);
                    }
                    break;
                case R.id.b30LinkedTogg:    //linked
                    if (isChecked) {
                        socailMsgData.setLinkin(EFunctionStatus.SUPPORT_OPEN);
                    } else {
                        socailMsgData.setLinkin(EFunctionStatus.SUPPORT_CLOSE);
                    }
                    break;
                case R.id.b30TwitterTogg:   //twitter
                    if (isChecked) {
                        socailMsgData.setTwitter(EFunctionStatus.SUPPORT_OPEN);
                    } else {
                        socailMsgData.setTwitter(EFunctionStatus.SUPPORT_CLOSE);
                    }
                    break;
                case R.id.b30LineTogg:  //line
                    if (isChecked) {
                        socailMsgData.setLine(EFunctionStatus.SUPPORT_OPEN);
                    } else {
                        socailMsgData.setLine(EFunctionStatus.SUPPORT_CLOSE);
                    }
                    break;
                case R.id.b30WechatTogg:    //wechat
                    if (isChecked) {
                        socailMsgData.setWechat(EFunctionStatus.SUPPORT_OPEN);
                    } else {
                        socailMsgData.setWechat(EFunctionStatus.SUPPORT_CLOSE);
                    }
                    break;
                case R.id.b30QQTogg:    //qq
                    if (isChecked) {
                        socailMsgData.setQq(EFunctionStatus.SUPPORT_OPEN);
                    } else {
                        socailMsgData.setQq(EFunctionStatus.SUPPORT_CLOSE);
                    }
                    break;
                case R.id.b30MessageTogg:   //msg
                    if (!AndPermission.hasPermissions(B30MessAlertActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS})) {
                        AndPermission.with(B30MessAlertActivity.this)
                                .runtime()
                                .permission(Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS)
                                .start();
                    }
                    //SharedPreferencesUtils.setParam(B30MessAlertActivity.this,"b30msg",isChecked);
                    if (isChecked) {
                        socailMsgData.setMsg(EFunctionStatus.SUPPORT_OPEN);
                    } else {
                        socailMsgData.setMsg(EFunctionStatus.SUPPORT_CLOSE);
                    }
                    break;
                case R.id.b30PhoneTogg: //phone
                    if (!AndPermission.hasPermissions(B30MessAlertActivity.this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE})) {
                        AndPermission.with(B30MessAlertActivity.this)
                                .runtime()
                                .permission(Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_CONTACTS)
                                .rationale(new Rationale<List<String>>() {
                                    @Override
                                    public void showRationale(Context context, List<String> data, RequestExecutor executor) {
                                        executor.execute();
                                    }
                                })
                                .start();
                        // SharedPreferencesUtils.setParam(B30MessAlertActivity.this,"b30phone",isChecked);
                        if (isChecked) {
                            socailMsgData.setPhone(EFunctionStatus.SUPPORT_OPEN);
                        } else {
                            socailMsgData.setPhone(EFunctionStatus.SUPPORT_CLOSE);
                        }
                    }
                    break;
            }

            MyApp.getVpOperateManager().settingSocialMsg(iBleWriteResponse, new ISocialMsgDataListener() {
                @Override
                public void onSocialMsgSupportDataChange(FunctionSocailMsgData functionSocailMsgData) {

                }
            }, socailMsgData);
        }
    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };

    public boolean isSupportOpen(EFunctionStatus data, FunctionSocailMsgData functionSocailMsgData) {
        List<EFunctionStatus> list = new ArrayList<>();
        list.add(functionSocailMsgData.getPhone());
        if (list.contains(data) && data == EFunctionStatus.SUPPORT_OPEN) {
            return true;
        } else {
            return false;
        }

    }

}
