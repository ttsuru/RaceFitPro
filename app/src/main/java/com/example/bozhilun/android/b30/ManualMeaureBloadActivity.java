package com.example.bozhilun.android.b30;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.b30view.CustomCircleProgressBar;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IBPDetectDataListener;
import com.veepoo.protocol.model.datas.BpData;
import com.veepoo.protocol.model.enums.EBPDetectModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 测量血压界面
 */
public class ManualMeaureBloadActivity extends WatchBaseActivity {

    private static final String TAG = "ManualMeaureBloadActivi";


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;
    @BindView(R.id.b30MeaureBloadProgressView)
    CustomCircleProgressBar b30MeaureBloadProgressView;
    @BindView(R.id.b30MeaureStartImg)
    ImageView b30MeaureStartImg;
    @BindView(R.id.b30MeaurePlaceHolderImg)
    ImageView b30MeaurePlaceHolderImg;
    @BindView(R.id.showStateTv)
    TextView showStateTv;

    //开始或者停止测量的标识
    private boolean isStart = false;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BpData meaureBpData = (BpData) msg.obj;
            if (meaureBpData != null) {
                if (meaureBpData.getProgress() == 100) {  //测量结束
                    stopMeaureBoload();
                    Log.e(TAG,"----测量结果="+meaureBpData.getHighPressure() + "/" + meaureBpData.getLowPressure());
                    if (b30MeaureBloadProgressView != null) {
                        b30MeaureBloadProgressView.setTmpTxt(meaureBpData.getHighPressure() + "/" + meaureBpData.getLowPressure());//.setProgressText(meaureBpData.getHighPressure() + "/" + meaureBpData.getLowPressure());
                        showStateTv.setText("正常");
                    }
                }
            }else{
                stopMeaureBoload();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_meaure_bload);
        ButterKnife.bind(this);

        initViews();

        initData();

    }

    private void initData() {
        b30MeaureBloadProgressView.setMaxProgress(100);
    }

    private void initViews() {
        commentB30TitleTv.setText("血压手动测试");
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30ShareImg.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.commentB30BackImg,R.id.commentB30ShareImg,R.id.b30MeaureStartImg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:    //返回
                finish();
                break;
            case R.id.commentB30ShareImg:   //分享
                WatchUtils.shareCommData(ManualMeaureBloadActivity.this);
                break;
            case R.id.b30MeaureStartImg:    //开始或者停止测量
                b30MeaurePlaceHolderImg.setVisibility(View.GONE);
                b30MeaureBloadProgressView.setVisibility(View.VISIBLE);
                if (MyCommandManager.DEVICENAME != null) {
                    if (!isStart) {
                        isStart = true;
                        b30MeaureStartImg.setImageResource(R.drawable.detect_bp_pause);
                        b30MeaureBloadProgressView.setProgress(100);
                        if (MyCommandManager.DEVICENAME != null) {
                            MyApp.getVpOperateManager().startDetectBP(bleWriteResponse, new IBPDetectDataListener() {
                                @Override
                                public void onDataChange(BpData bpData) {
                                    Log.e(TAG, "----bpData=" + bpData.toString());
                                    Message message = handler.obtainMessage();
                                    message.what = 1001;
                                    message.obj = bpData;
                                    handler.sendMessage(message);
                                }
                            }, EBPDetectModel.DETECT_MODEL_PUBLIC);
                        }
                    } else {
                        stopMeaureBoload();
                    }
                } else {
                    showStateTv.setText("未连接手环");
                }

                break;
        }
    }

    //停止测量
    private void stopMeaureBoload() {
        isStart = false;
        b30MeaureStartImg.setImageResource(R.drawable.detect_bp_start);
        b30MeaureBloadProgressView.stopAnim();
        MyApp.getVpOperateManager().stopDetectBP(bleWriteResponse, EBPDetectModel.DETECT_MODEL_PUBLIC);
    }

    private IBleWriteResponse bleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {
            Log.e(TAG, "------bleWriteResponse=" + i);
        }
    };
}
