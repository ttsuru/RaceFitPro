package com.example.bozhilun.android.b30.b30minefragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.AboutActivity;
import com.example.bozhilun.android.activity.LoginActivity;
import com.example.bozhilun.android.activity.MyPersonalActivity;
import com.example.bozhilun.android.b30.B30DeviceActivity;
import com.example.bozhilun.android.b30.B30SysSettingActivity;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.veepoo.protocol.listener.base.IBleWriteResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/7/20.
 */

/**
 * B30 我的界面
 */
public class B30MineFragment extends LazyFragment implements RequestView {

    View b30MineView;
    Unbinder unbinder;

    @BindView(R.id.b30userImageHead)
    ImageView b30UserImageHead;
    @BindView(R.id.b30UserNameTv)
    TextView b30UserNameTv;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.b30MineDeviceTv)
    TextView b30MineDeviceTv;
    @BindView(R.id.b30MineUnitTv)
    TextView b30MineUnitTv;
    @BindView(R.id.b30MineSportGoalTv)
    TextView b30MineSportGoalTv;
    @BindView(R.id.b30MineSleepGoalTv)
    TextView b30MineSleepGoalTv;

    private RequestPressent requestPressent;

    private AlertDialog.Builder builder;
    //运动目标
    ArrayList<String> sportGoalList;
    //睡眠目标
    ArrayList<String> sleepGoalList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b30MineView = inflater.inflate(R.layout.fragment_b30_mine_layout, container, false);
        unbinder = ButterKnife.bind(this, b30MineView);

        initViews();

        initData();

        return b30MineView;
    }

    private void initData() {
        requestPressent = new RequestPressent();
        requestPressent.attach(this);
        getUserData();

        sportGoalList = new ArrayList<>();
        sleepGoalList = new ArrayList<>();
        for (int i = 1000; i <= 64000; i += 1000) {
            sleepGoalList.add(i + "");
        }

        for(int i = 1;i<48;i++){
            sleepGoalList.add(WatchUtils.mul(Double.valueOf(i),0.5)+"");
        }

        //显示运动目标和睡眠目标
        int b30SportGoal = (int) SharedPreferencesUtils.getParam(getActivity(),"b30Goal",0);
        b30MineSportGoalTv.setText(b30SportGoal+"");
        //睡眠
        String b30SleepGoal = (String) SharedPreferencesUtils.getParam(MyApp.getContext(),"b30SleepGoal","");
        if(!WatchUtils.isEmpty(b30SleepGoal)){
            b30MineSleepGoalTv.setText(b30SleepGoal+"");
        }
    }

    private void getUserData() {
        String url = URLs.HTTPs + URLs.getUserInfo; //查询用户信息
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestPressent.getRequestJSONObject(1, url, MyApp.getContext(), jsonObj.toString(), 1);
    }

    private void initViews() {
        commentB30TitleTv.setText(getResources().getString(R.string.menu_settings));

    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getActivity().isFinishing()) {
            if (MyCommandManager.DEVICENAME != null) {
                String bleMac = (String) SharedPreferencesUtils.readObject(MyApp.getContext(),"mylanmac");
                b30MineDeviceTv.setText("B30  "+bleMac);
            } else {
                b30MineDeviceTv.setText("未连接");
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (requestPressent != null) {
            requestPressent.detach();
        }
    }

    @OnClick({R.id.b30userImageHead, R.id.b30MineDeviceRel, R.id.b30MineSportRel,
            R.id.b30MineSleepRel, R.id.b30MineUnitRel, R.id.b30MineAboutRel, R.id.b30LogoutBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b30userImageHead: //头像
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.b30MineDeviceRel: //设备
                if (!getActivity().isFinishing()) {
                    if (MyCommandManager.DEVICENAME != null) {
                        startActivity(new Intent(getActivity(), B30DeviceActivity.class));
                    } else {
                        startActivity(new Intent(getActivity(),NewSearchActivity.class));
                        getActivity().finish();
                    }
                }

                break;
            case R.id.b30MineSportRel:  //运动目标
                setSportGoal(); //设置运动目标
                break;
            case R.id.b30MineSleepRel:  //睡眠目标
                setSleepGoal(); //设置睡眠目标
                break;
            case R.id.b30MineUnitRel:   //单位设置
                showUnitDialog();
                break;
            case R.id.b30MineAboutRel:  //关于
                startActivity(new Intent(getActivity(), B30SysSettingActivity.class));
                break;
            case R.id.b30LogoutBtn: //退出登录
                longOutApp();
                break;
        }
    }

    private void longOutApp() {
        new MaterialDialog.Builder(getActivity())
                .title(getResources().getString(R.string.prompt))
                .content("是否退出登录?")
                .positiveText(getResources().getString(R.string.confirm))
                .negativeText(getResources().getString(R.string.cancle))
                .onPositive(new com.afollestad.materialdialogs.MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if(MyCommandManager.DEVICENAME != null){
                            MyCommandManager.DEVICENAME = null;
                            MyCommandManager.ADDRESS = null;
                            MyApp.getVpOperateManager().disconnectWatch(new IBleWriteResponse() {
                                @Override
                                public void onResponse(int state) {
                                    if(state == -1){
                                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "mylanya", "");
                                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "mylanmac", "");
                                        SharedPreferencesUtils.saveObject(MyApp.getContext(), "userId", null);
                                        SharedPreferencesUtils.saveObject(MyApp.getContext(),"userInfo","");
                                        SharedPreferencesUtils.setParam(MyApp.getContext(), "isFirst", "");
                                        startActivity(new Intent(getActivity(),LoginActivity.class));
                                        getActivity().finish();
                                    }
                                }
                            });
                        }else{
                            MyCommandManager.DEVICENAME = null;
                            MyCommandManager.ADDRESS = null;
                            SharedPreferencesUtils.saveObject(MyApp.getContext(), "mylanya", "");
                            SharedPreferencesUtils.saveObject(MyApp.getContext(), "mylanmac", "");
                            SharedPreferencesUtils.saveObject(MyApp.getContext(), "userId", null);
                            SharedPreferencesUtils.saveObject(MyApp.getContext(),"userInfo","");
                            SharedPreferencesUtils.setParam(MyApp.getContext(), "isFirst", "");
                            startActivity(new Intent(getActivity(),LoginActivity.class));
                            getActivity().finish();

                        }


                    }
                }).show();


    }

    //设置运动目标
    private void setSportGoal() {
        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(getActivity() ,
                new ProfessionPick.OnProCityPickedListener() {
            @Override
            public void onProCityPickCompleted(String profession) {
                b30MineSportGoalTv.setText(profession);
                SharedPreferencesUtils.setParam(getActivity(),"b30Goal",Integer.valueOf(profession.trim()));
            }
        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(sleepGoalList) //min year in loop
                .dateChose("8000") // date chose when init popwindow
                .build();
        dailyumberofstepsPopWin.showPopWin(getActivity());
    }

    //设置睡眠目标
    private void setSleepGoal() {


        ProfessionPick dailyumberofstepsPopWin = new ProfessionPick.Builder(getActivity() ,
                new ProfessionPick.OnProCityPickedListener() {
                    @Override
                    public void onProCityPickCompleted(String profession) {
                        b30MineSleepGoalTv.setText(profession );
                        SharedPreferencesUtils.setParam(getActivity(),"b30SleepGoal",profession.trim());
                    }
                }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .setProvinceList(sleepGoalList) //min year in loop
                .dateChose("8.0") // date chose when init popwindow
                .build();
        dailyumberofstepsPopWin.showPopWin(getActivity());
    }

    //展示公英制
    private void showUnitDialog() {
        String runTypeString[] = new String[]{getResources().getString(R.string.setkm),
                getResources().getString(R.string.setmi)};
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.select_running_mode))
                .setItems(runTypeString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (i == 0) { //公制
                            b30MineUnitTv.setText(getResources().getString(R.string.setkm));
                        } else {  //英制
                            b30MineUnitTv.setText(getResources().getString(R.string.setmi));
                        }
                    }
                }).setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }


    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        if (!WatchUtils.isEmpty(object.toString())) {

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(object.toString());
                if (jsonObject.getString("resultCode").equals("001")) {
                    JSONObject myInfoJsonObject = jsonObject.getJSONObject("userInfo");
                    if (myInfoJsonObject != null) {
                        b30UserNameTv.setText("" + myInfoJsonObject.getString("nickName") + "");
                        String imgHead = myInfoJsonObject.getString("image");
                        if (!WatchUtils.isEmpty(imgHead)) {
                            RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true);
                            //头像
                            Glide.with(getActivity()).load(imgHead).apply(mRequestOptions).into(b30UserImageHead);    //头像
                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void failedData(int what, Throwable e) {

    }

    @Override
    public void closeLoadDialog(int what) {

    }

}
