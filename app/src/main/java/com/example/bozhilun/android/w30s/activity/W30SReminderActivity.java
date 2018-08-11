package com.example.bozhilun.android.w30s.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferenceUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;

import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/16 11:55
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SReminderActivity extends WatchBaseActivity implements CompoundButton.OnCheckedChangeListener {

    private static final int REQD_MSG_CONTENT_CODE = 1001;  //读取短信内容权限code
    private static final int REQUEST_REQDPHONE_STATE_CODE = 1002;

    private static final String BOZLUN_PACKNAME_EN = "com.bozlun.bozhilun.android";

    @BindView(R.id.switch_Skype)
    Switch switchSkype;
    @BindView(R.id.switch_WhatsApp)
    Switch switchWhatsApp;
    @BindView(R.id.switch_Facebook)
    Switch switchFacebook;
    @BindView(R.id.switch_LinkendIn)
    Switch switchLinkendIn;
    @BindView(R.id.switch_Twitter)
    Switch switchTwitter;
    @BindView(R.id.switch_Viber)
    Switch switchViber;
    @BindView(R.id.switch_LINE)
    Switch switchLINE;
    @BindView(R.id.switch_WeChat)
    Switch switchWeChat;
    @BindView(R.id.switch_QQ)
    Switch switchQQ;
    @BindView(R.id.switch_Msg)
    Switch switchMsg;
    @BindView(R.id.switch_Phone)
    Switch switchPhone;
    @BindView(R.id.watch_msgOpenAccessBtn)
    RelativeLayout watch_msgOpenAccessBtn;
    @BindView(R.id.newSearchTitleTv)
    TextView newSearchTitleTv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w30s_reminder);
        ButterKnife.bind(this);

        newSearchTitleTv.setText(getResources().getString(R.string.string_application_reminding));
        watch_msgOpenAccessBtn.setVisibility(View.GONE);
        getSwitchState();
        initSwitch();
    }

    private void getSwitchState() {
        boolean w30sswitch_skype = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_Skype", false);
        boolean w30sswitch_whatsApp = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_WhatsApp", false);
        boolean w30sswitch_facebook = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_Facebook", false);
        boolean w30sswitch_linkendIn = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_LinkendIn", false);
        boolean w30sswitch_twitter = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_Twitter", false);
        boolean w30sswitch_viber = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_Viber", false);
        boolean w30sswitch_line = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_LINE", false);
        boolean w30sswitch_weChat = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_WeChat", false);
        boolean w30sswitch_qq = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_QQ", false);
        boolean w30sswitch_msg = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_Msg", false);
        boolean w30sswitch_Phone = (boolean) SharedPreferenceUtil.get(W30SReminderActivity.this, "w30sswitch_Phone", false);
        switchSkype.setChecked(w30sswitch_skype);
        switchWhatsApp.setChecked(w30sswitch_whatsApp);
        switchFacebook.setChecked(w30sswitch_facebook);
        switchLinkendIn.setChecked(w30sswitch_linkendIn);
        switchTwitter.setChecked(w30sswitch_twitter);
        switchViber.setChecked(w30sswitch_viber);
        switchLINE.setChecked(w30sswitch_line);
        switchWeChat.setChecked(w30sswitch_weChat);
        switchQQ.setChecked(w30sswitch_qq);
        switchMsg.setChecked(w30sswitch_msg);
        switchPhone.setChecked(w30sswitch_Phone);
    }

    private void initSwitch() {
        switchSkype.setOnCheckedChangeListener(this);
        switchWhatsApp.setOnCheckedChangeListener(this);
        switchFacebook.setOnCheckedChangeListener(this);
        switchLinkendIn.setOnCheckedChangeListener(this);
        switchTwitter.setOnCheckedChangeListener(this);
        switchViber.setOnCheckedChangeListener(this);
        switchLINE.setOnCheckedChangeListener(this);
        switchWeChat.setOnCheckedChangeListener(this);
        switchQQ.setOnCheckedChangeListener(this);
        switchMsg.setOnCheckedChangeListener(this);
        switchPhone.setOnCheckedChangeListener(this);
    }


    @OnClick({R.id.watch_msgOpenNitBtn,R.id.newSearchTitleLeft,
            R.id.watch_msgOpenAccessBtn,R.id.newSearchRightImg1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.newSearchTitleLeft:
                finish();
                break;
            case R.id.watch_msgOpenNitBtn:
                Intent intentr = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivityForResult(intentr, 101);
                break;
            case R.id.watch_msgOpenAccessBtn:
                Intent ints = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(ints, 102);
                break;
            case R.id.newSearchRightImg1:   //进入权限界面
                String appPackName = getPackageName();
                String contentTx = "Please open the required permissions";
                Log.e("包名","------appName="+appPackName);
                if(appPackName != null && appPackName.equals(BOZLUN_PACKNAME_EN)){
                    contentTx = "Please open the required permissions";
                }else{
                    contentTx = "请打开所需权限";
                }
                new MaterialDialog.Builder(this)
                        .title(getResources().getString(R.string.prompt))
                        .content(contentTx)
                        .positiveText(getResources().getString(R.string.confirm))
                        .negativeText(getResources().getString(R.string.cancle))
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        }).show();

                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_Skype:
                SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_Skype", isChecked);
                break;
            case R.id.switch_WhatsApp:
                SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_WhatsApp", isChecked);
                break;
            case R.id.switch_Facebook:
                SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_Facebook", isChecked);
                break;
            case R.id.switch_LinkendIn:
                SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_LinkendIn", isChecked);
                break;
            case R.id.switch_Twitter:
                SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_Twitter", isChecked);
                break;
            case R.id.switch_Viber:
                SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_Viber", isChecked);
                break;
            case R.id.switch_LINE:
                SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_LINE", isChecked);
                break;
            case R.id.switch_WeChat:    //微信
                SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_WeChat", isChecked);
                break;
            case R.id.switch_QQ:    //QQ
                SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_QQ", isChecked);
                break;
            case R.id.switch_Msg:   //短信
                if(!AndPermission.hasPermissions(W30SReminderActivity.this,new String[] {Manifest.permission.READ_SMS,Manifest.permission.READ_CONTACTS})){
                    AndPermission.with(W30SReminderActivity.this)
                            .runtime()
                            .permission(Manifest.permission.READ_SMS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS)
                            .start();
                }
                SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_Msg", isChecked);
                break;
            case R.id.switch_Phone:     //来电
                if(!AndPermission.hasPermissions(W30SReminderActivity.this,new String[]{Manifest.permission.CALL_PHONE,Manifest.permission.READ_PHONE_STATE})){
                    AndPermission.with(W30SReminderActivity.this)
                            .runtime()
                            .permission(Manifest.permission.CALL_PHONE,Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS)
                            .rationale(new Rationale<List<String>>() {
                                @Override
                                public void showRationale(Context context, List<String> data, RequestExecutor executor) {
                                   executor.execute();
                                }
                            })
                            .start();
                }
                MyApp.getmW30SBLEManage().SendAnddroidLanguage(0x01);
                SharedPreferenceUtil.put(W30SReminderActivity.this, "w30sswitch_Phone", isChecked);
                break;
        }
    }

    /**
     * 动态申请权限回调
     */
//    private PermissionListener permissionListener = new PermissionListener() {
//        @Override
//        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
//            switch (requestCode) {
//
//            }
//        }
//
//        @Override
//        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
//            switch (requestCode) {
//                case REQD_MSG_CONTENT_CODE:
//
//                    break;
//            }
//            AndPermission.hasAlwaysDeniedPermission(W30SReminderActivity.this, deniedPermissions);
//        }
//    };
}
