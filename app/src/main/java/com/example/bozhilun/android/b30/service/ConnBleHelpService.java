package com.example.bozhilun.android.b30.service;


import android.util.Log;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.bean.UserInfoBean;
import com.example.bozhilun.android.imagepicker.TempActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.google.gson.Gson;
import com.veepoo.protocol.VPOperateManager;
import com.veepoo.protocol.listener.base.IBleWriteResponse;
import com.veepoo.protocol.listener.data.IBatteryDataListener;
import com.veepoo.protocol.listener.data.IDeviceFuctionDataListener;
import com.veepoo.protocol.listener.data.ILanguageDataListener;
import com.veepoo.protocol.listener.data.IOriginDataListener;
import com.veepoo.protocol.listener.data.IPersonInfoDataListener;
import com.veepoo.protocol.listener.data.IPwdDataListener;
import com.veepoo.protocol.listener.data.ISleepDataListener;
import com.veepoo.protocol.listener.data.ISocialMsgDataListener;
import com.veepoo.protocol.listener.data.ISportDataListener;
import com.veepoo.protocol.model.datas.BatteryData;
import com.veepoo.protocol.model.datas.FunctionDeviceSupportData;
import com.veepoo.protocol.model.datas.FunctionSocailMsgData;
import com.veepoo.protocol.model.datas.LanguageData;
import com.veepoo.protocol.model.datas.OriginData;
import com.veepoo.protocol.model.datas.OriginHalfHourData;
import com.veepoo.protocol.model.datas.PersonInfoData;
import com.veepoo.protocol.model.datas.PwdData;
import com.veepoo.protocol.model.datas.SleepData;
import com.veepoo.protocol.model.datas.SportData;
import com.veepoo.protocol.model.enums.ELanguage;
import com.veepoo.protocol.model.enums.EOprateStauts;
import com.veepoo.protocol.model.enums.ESex;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/26.
 */

public class ConnBleHelpService {

    private static final String TAG = "ConnBleHelpService";


    //总数
    int tmpAllData = 0;
    //当前
    int tmpCurrentData = 0;

    //验证密码
    public ConnBleHelpListener connBleHelpListener;

    //设备数据
    public ConnBleMsgDataListener connBleMsgDataListener;

    //健康数据返回
    public ConnBleHealthDataListener connBleHealthDataListener;

    private List<OriginData> originDataList = new ArrayList<>();


    public ConnBleHelpService() {
    }


    public void doConnOperater(){
        //验证设备密码
        VPOperateManager.getMangerInstance(MyApp.getContext()).confirmDevicePwd(new IBleWriteResponse() {
            @Override
            public void onResponse(int i) {
                Log.e(TAG,"-----密码="+i);
            }
        }, new IPwdDataListener() {
            @Override
            public void onPwdDataChange(PwdData pwdData) {
                Log.e(TAG,"-----pwdData="+pwdData.toString());
            }
        }, new IDeviceFuctionDataListener() {
            @Override
            public void onFunctionSupportDataChange(FunctionDeviceSupportData functionDeviceSupportData) {
                Log.e(TAG,"-----functionDeviceSupportData--="+functionDeviceSupportData.toString());
                Log.e(TAG,"-----contactMsgLength="+functionDeviceSupportData.getContactMsgLength()+"--all="+functionDeviceSupportData.getAllMsgLength());
            }
        }, new ISocialMsgDataListener() {
            @Override
            public void onSocialMsgSupportDataChange(FunctionSocailMsgData functionSocailMsgData) {
                Log.e(TAG,"-----functionSocailMsgData-="+functionSocailMsgData);
            }
        }, "0000",true );

        //同步用户信息
        String userData = (String) SharedPreferencesUtils.readObject(MyApp.getContext(),"saveuserinfodata");
        Log.e(TAG,"----userData="+userData);
        if(!WatchUtils.isEmpty(userData)){
            UserInfoBean userInfoBean = new Gson().fromJson(userData,UserInfoBean.class);
            //体重
            String tmpWeight = userInfoBean.getWeight();
            int userWeight ;
            if(tmpWeight.contains("kg")){
                userWeight = Integer.valueOf(StringUtils.substringBefore(tmpWeight,"kg").trim());
            }else{
                userWeight = Integer.valueOf(tmpWeight.trim());
            }

            //身高
            String tmpHeight = userInfoBean.getHeight();
            int userHeight;
            if(tmpHeight.contains("cm")){
                userHeight = Integer.valueOf(StringUtils.substringBefore(tmpHeight,"cm").trim());
            }else{
                userHeight = Integer.valueOf(tmpHeight.trim());
            }
            String tmpSex = userInfoBean.getSex();
            //性别
            ESex eSex = tmpSex.equals("M")?ESex.valueOf("MAN"):ESex.valueOf("MAN");
            Log.e(TAG,"----性别-="+eSex.toString());
            //年龄
            String userBirthday = userInfoBean.getBirthday();
            int userAge = WatchUtils.getAgeFromBirthTime(userBirthday);
            Log.e(TAG,"----年龄="+userAge);
            int sportGoal = (int) SharedPreferencesUtils.getParam(MyApp.getContext(),"b30Goal",0);
            MyApp.getVpOperateManager().syncPersonInfo(new IBleWriteResponse() {
                @Override
                public void onResponse(int i) {
                    Log.e(TAG,"----i="+i);
                }
            }, new IPersonInfoDataListener() {
                @Override
                public void OnPersoninfoDataChange(EOprateStauts eOprateStauts) {
                    Log.e(TAG,"----eOprateStauts-="+eOprateStauts);
                    //同步用户信息成功
                    if(eOprateStauts == EOprateStauts.OPRATE_SUCCESS){
                        if(connBleHelpListener != null){
                            connBleHelpListener.connSuccState();
                        }
                    }
                }
            },new PersonInfoData(eSex,userHeight,userWeight,userAge,sportGoal));
        }

    }


    public void getDeviceMsgData(){
        //设置语言
        MyApp.getVpOperateManager().settingDeviceLanguage(bleWriteResponse, new ILanguageDataListener() {
            @Override
            public void onLanguageDataChange(LanguageData languageData) {

            }
        }, ELanguage.CHINA);
        //获取步数
        MyApp.getVpOperateManager().readSportStep(bleWriteResponse, new ISportDataListener() {
            @Override
            public void onSportDataChange(SportData sportData) {
                Log.e(TAG,"------步数="+sportData.toString());
                if(connBleMsgDataListener != null){
                    connBleMsgDataListener.getBleSportData(sportData);
                }
            }
        });
        //电量
        MyApp.getVpOperateManager().readBattery(bleWriteResponse, new IBatteryDataListener() {
            @Override
            public void onDataChange(BatteryData batteryData) {
                Log.e(TAG,"----电量-="+batteryData.toString());
                if(connBleMsgDataListener != null){
                    connBleMsgDataListener.getBleBatteryData(batteryData.getBatteryLevel());
                }
            }
        });


    }

    //读取健康数据
    public void readHealthDaty(){
        originDataList.clear();
        VPOperateManager.getMangerInstance(MyApp.getContext()).readOriginData(bleWriteResponse, new IOriginDataListener() {
            @Override
            public void onOringinFiveMinuteDataChange(OriginData originData) {
                String message = "健康数据-返回:" + originData.toString();
                Log.e(TAG,"----111="+message);
                originDataList.add(originData);
            }

            @Override
            public void onReadOriginProgress(float progress) {
                String message = "健康数据[5分钟]-读取进度:" + progress;
                Log.e(TAG,"-----222="+message);

            }

            @Override
            public void onReadOriginProgressDetail(int date, String dates, int all, int num) {
                Log.e(TAG,"-----sss="+date+"--dates="+dates+"--all="+all+"--num="+num);
                tmpAllData = all;
                tmpCurrentData = num;
            }

            @Override
            public void onOringinHalfHourDataChange(OriginHalfHourData originHalfHourData) {
                String message = "健康数据[30分钟]-返回:" + originHalfHourData.toString();
                Log.e(TAG,"-----333="+message);
                Log.e(TAG,"---是否相等="+tmpAllData+"-="+tmpCurrentData);
                if(tmpAllData == tmpCurrentData){   //读取完了
                    Log.e(TAG,"----完了="+new Gson().toJson(originHalfHourData));
                    if(connBleHealthDataListener != null){
                        connBleHealthDataListener.getBleHealtyData(originHalfHourData,originDataList);
                    }
                    readSleepData(3);
                }
            }

            @Override
            public void onReadOriginComplete() {
                String message = "健康数据-读取结束";

            }
        }, 3);



    }

    //读取睡眠数据
    public void readSleepData(int day){
        //读取睡眠数据
        MyApp.getVpOperateManager().readSleepData(bleWriteResponse, new ISleepDataListener() {
            @Override
            public void onSleepDataChange(SleepData sleepData) {
                Log.e(TAG,"----睡眠-sleepData11="+sleepData.toString());
                if(connBleHealthDataListener != null){
                    connBleHealthDataListener.getBleSleepData(sleepData);
                }
            }

            @Override
            public void onSleepProgress(float v) {
                Log.e(TAG,"----睡眠-onSleepProgress="+v);
            }

            @Override
            public void onSleepProgressDetail(String s, int i) {
                Log.e(TAG,"----睡眠-onSleepProgressDetail="+s+"---i="+i);
            }

            @Override
            public void onReadSleepComplete() {
                Log.e(TAG,"----睡眠-onReadSleepComplete");
            }
        },day );
    }

    //设置密码回调
   public interface ConnBleHelpListener{
        void connSuccState();
    }

    //步数，电量，睡眠回调
    public interface ConnBleMsgDataListener{
        //电量
        void getBleBatteryData(int batteryLevel);
        //步数数据
        void getBleSportData(SportData sportData);

    }

    //健康数据返回回调
    public interface ConnBleHealthDataListener{
        void getBleHealtyData(OriginHalfHourData originHalfHourData,List<OriginData> originData);
        void getBleSleepData(SleepData sleepData);
    }

    public void setConnBleMsgDataListener(ConnBleMsgDataListener connBleMsgDataListener) {
        this.connBleMsgDataListener = connBleMsgDataListener;
    }

    public void setConnBleHelpListener(ConnBleHelpListener connBleHelpListener) {
        this.connBleHelpListener = connBleHelpListener;
    }

    public void setConnBleHealthDataListener(ConnBleHealthDataListener connBleHealthDataListener) {
        this.connBleHealthDataListener = connBleHealthDataListener;
    }

    private IBleWriteResponse bleWriteResponse = new IBleWriteResponse() {
        @Override
        public void onResponse(int i) {
            Log.e(TAG,"------bleWriteResponse="+i);
        }
    };
}
