package com.example.bozhilun.android.b30;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IPwdDataListener;
import com.veepoo.protocol.model.datas.PwdData;
import com.veepoo.protocol.model.enums.EPwdStatus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 重置设备密码
 */
public class B30ResetActivity extends WatchBaseActivity {


    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.b30OldPwdEdit)
    EditText b30OldPwdEdit;
    @BindView(R.id.b30NewPwdEdit)
    EditText b30NewPwdEdit;
    @BindView(R.id.b30AgainNewPwdEdit)
    EditText b30AgainNewPwdEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_reset);
        ButterKnife.bind(this);

        initViews();


    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText("重置设备密码");

    }

    @OnClick({R.id.commentB30BackImg, R.id.b30ResetPwdBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:
                finish();
                break;
            case R.id.b30ResetPwdBtn:
                resetBlePwd();
                break;
        }
    }

    //重置设备密码
    private void resetBlePwd() {
        String oldPwd = b30OldPwdEdit.getText().toString().trim();
        String newPwd = b30NewPwdEdit.getText().toString().trim();
        String againNewPwd = b30AgainNewPwdEdit.getText().toString().trim();
        //密码
        String b30Pwd = (String) SharedPreferencesUtils.getParam(MyApp.getContext(),"b30pwd","");
        if(WatchUtils.isEmpty(oldPwd) || WatchUtils.isEmpty(newPwd) || WatchUtils.isEmpty(againNewPwd)){
            return;
        }
        if(!oldPwd.equals(b30Pwd)){
            ToastUtil.showShort(B30ResetActivity.this,"旧密码不正确!");
            return;
        }
        if(!oldPwd.equals(againNewPwd)){
            ToastUtil.showShort(B30ResetActivity.this,"两次密码不相同!");
            return;
        }

        if(MyCommandManager.DEVICENAME != null){
            MyApp.getVpOperateManager().modifyDevicePwd(iBleWriteResponse, new IPwdDataListener() {
                @Override
                public void onPwdDataChange(PwdData pwdData) {
                    Log.e("密码","-----pwdData="+pwdData.toString());
                    if(pwdData.getmStatus() == EPwdStatus.SETTING_SUCCESS){
                        ToastUtil.showShort(B30ResetActivity.this,"重置成功");
                        finish();
                    }
                }
            }, againNewPwd);
        }

    }

    private IBleWriteResponse iBleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {

        }
    };
}
