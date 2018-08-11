package com.example.bozhilun.android.activity.wylactivity.wyl_util.service;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.activity.GuideActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.sdk.bluetooth.protocol.command.base.BaseCommand;
import com.sdk.bluetooth.protocol.command.push.MsgCountPush;
import com.sdk.bluetooth.protocol.command.push.PhoneNamePush;
import com.sdk.bluetooth.utils.BackgroundThread;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferenceUtil;

import java.util.ArrayList;

/**
 * Created by admin on 2017/5/14.\
 * 6.0 广播接收来电
 */

public class PhoneBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneBroadcastReceiver";
    private static final String H9_NAME_TAG = "W06X";    //H9手表


    OnCallPhoneListener onClallListener;

    public void setOnClallListener(OnCallPhoneListener onClallListener) {
        this.onClallListener = onClallListener;
    }


    String phoneNumber = "";
    private String bleName ;// = (String) SharedPreferencesUtils.readObject(MyApp.getApplication().getApplicationContext(), "mylanya");
    //private String w30SBleName = (String) SharedPreferenceUtil.get(MyApp.getApplication(), "mylanya", "");


    public PhoneBroadcastReceiver() {
        super();
        bleName = (String) SharedPreferencesUtils.readObject(MyApp.getApplication().getApplicationContext(), "mylanya");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG, "---------action---" + action);

        //呼入电话
        if (action.equals(GuideActivity.B_PHONE_STATE)) {
            doReceivePhone(context, intent);
        }
    }

    /**
     * 处理电话广播.
     *
     * @param context
     * @param intent
     */
    public void doReceivePhone(Context context, Intent intent) {
        phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

        Log.d(TAG, "---phoneNumber----" + phoneNumber);

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int state = telephony.getCallState();
        Log.d(TAG, "-----state-----" + state);
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING://"[Broadcast]等待接电话="
                Log.d(TAG, "------收到了来电广播---" + phoneNumber);
            if(!WatchUtils.isEmpty(phoneNumber)){
                if(onClallListener != null){
                    onClallListener.callPhoneAlert(phoneNumber);
                }
            }
                if (bleName != null && !bleName.equals("bozlun")) {
                    getPeople(phoneNumber, context);
                }

                //W30
                if(bleName != null &&(bleName.equals("w30") || bleName.equals("W30"))){
                    if(!WatchUtils.isEmpty(phoneNumber)){
                        boolean isOn = (boolean) SharedPreferenceUtil.get(MyApp.getApplication(),"w30sswitch_Phone",true);
                        if(isOn){
                            sendPhoneAlertData(phoneNumber);
                        }
                    }
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:// "[Broadcast]挂断电话
                Log.d(TAG, "------挂断电话--");
                if (bleName != null && !bleName.equals("bozlun")) {
                    missCallPhone();    //挂断电话
                }
                if(bleName != null &&(bleName.equals("w30") || bleName.equals("W30"))){
                    disW30Phone();
                }

                break;
            case TelephonyManager.CALL_STATE_OFFHOOK://"[Broadcast]通话中="
                Log.d(TAG, "------通话中--");
                if (bleName != null && !bleName.equals("bozlun")) {
                    missCallPhone();    //挂断电话
                }
                if(bleName != null &&(bleName.equals("w30") || bleName.equals("W30"))){
                    disW30Phone();
                }
                break;
        }
    }

    //发送电话号码
    private void sendPhoneAlertData(String phoneNumber) {
        getPhoneContacts(phoneNumber);
    }

    private void disW30Phone(){
        MyApp.getmW30SBLEManage().notifyMsgClose();
    }


    //挂断电话
    private void missCallPhone() {
//        if (w30SBleName != null && !TextUtils.isEmpty(w30SBleName) && w30SBleName.equals("W30")) {
//            if (MyCommandManager.DEVICENAME != null && MyCommandManager.DEVICENAME.equals("W30")) {
//                MyApp.getmW30SBLEManage().notifyMsgClose();
//            }
//        }

        if (bleName != null && !TextUtils.isEmpty(bleName) && bleName.equals("bozlun")) {
            if (H9_NAME_TAG.equals(bleName)) {   //H9挂掉电话
                sendPhoneCallCommands("", "", PhoneNamePush.HangUp_Call_type, MsgCountPush.HANGUP_CALL_TYPE, 0);
            }
        }
    }

    /**
     * 通过输入获取电话号码
     */
    public void getPeople(String nunber, Context context) {
        boolean w30sswitch_Phone = (boolean) SharedPreferenceUtil.get(MyApp.context, "w30sswitch_Phone", true);
        Log.d(TAG, "------收到了来电广播3---"  + "=====" + w30sswitch_Phone);
        try {
            Cursor cursor = context.getContentResolver().query(Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI, nunber), new String[]{
                    ContactsContract.PhoneLookup._ID,
                    ContactsContract.PhoneLookup.NUMBER,
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup.TYPE, ContactsContract.PhoneLookup.LABEL}, null, null, null);

            if (cursor != null && !WatchUtils.isEmpty("" + cursor.getCount() + "")) {
                if (cursor.getCount() == 0) {
//                    //没找到电话号码
//                    if (w30SBleName != null && !TextUtils.isEmpty(w30SBleName) && w30SBleName.equals("W30")) {
//                        if (w30sswitch_Phone) {
//                            Log.d(TAG, "------收到了来电广播4---" + w30SBleName + "=====" + w30sswitch_Phone + "===" + nunber);
//                            MyApp.getmW30SBLEManage().notifacePhone(nunber, 0x01);
//                        }
//                        return;
//                    }
                    if (bleName != null && !TextUtils.isEmpty(bleName) && bleName.equals("bozlun"))
                        phonesig(nunber, 1);
                } else if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    if (bleName != null && !TextUtils.isEmpty(bleName) && bleName.equals("bozlun"))
                        phonesig(cursor.getString(2), 2);
                }
            } else {
//                //直接发送电话号码，无名字
//                if (w30SBleName != null && !TextUtils.isEmpty(w30SBleName) && w30SBleName.equals("W30")) {
//                    if (w30sswitch_Phone) {
//                        Log.d(TAG, "------收到了来电广播5---" + w30SBleName + "=====" + w30sswitch_Phone + "===" + phoneNumber);
//                        MyApp.getmW30SBLEManage().notifacePhone(phoneNumber, 0x01);
//                    }
//                    return;
//                }
                if (bleName != null && !TextUtils.isEmpty(bleName) && bleName.equals("bozlun"))
                    phonesig(phoneNumber, 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //发送电话号码或者昵称的方法
    public void phonesig(String num, int id) {
        Log.d(TAG, "------num---" + num + "--id-" + id);
        if (!WatchUtils.isEmpty(bleName)) {
            try {
                if (H9_NAME_TAG.equals(bleName)) { //H9手表
                    Log.d(TAG, "-----h9--" + bleName);
                    if ((Boolean) SharedPreferencesUtils.readObject(MyApp.getApplication().getApplicationContext(), "INCOME_CALL")) {
                        if (id == 2) {    //有名字
                            Log.d(TAG, "---11--h9--" + bleName);
                            sendPhoneCallCommands(num, phoneNumber, PhoneNamePush.Incoming_Call_type, MsgCountPush.ICOMING_CALL_TYPE, 1);
                        } else {
                            Log.d(TAG, "---22--h9--" + SharedPreferencesUtils.readObject(MyApp.getApplication().getApplicationContext(), "mylanya"));
                            sendPhoneCallCommands(num, num, PhoneNamePush.Incoming_Call_type, MsgCountPush.ICOMING_CALL_TYPE, 1);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 推送来电
     *
     * @param name
     * @param incomingNumber
     * @param callType
     * @param countType
     */
    private void sendPhoneCallCommands(String name, String incomingNumber, byte callType, byte countType, long delayTime) {
        byte[] bName = new byte[0x00];
        if (incomingNumber != null) {
            bName = incomingNumber.getBytes();
            if (name != null) {
                bName = name.getBytes();
            }
        }
        PhoneNamePush phonePush = new PhoneNamePush(commandResultCallback, callType, bName);
        MsgCountPush countPush = new MsgCountPush(commandResultCallback, countType, (byte) 0x01);
        ArrayList<BaseCommand> sendList = new ArrayList<>();
        sendList.add(phonePush);
        sendList.add(countPush);
        if (delayTime > 0) {
            final ArrayList<BaseCommand> commands = sendList;
            BackgroundThread.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommands(commands);
                }
            }, delayTime);
        } else {
            AppsBluetoothManager.getInstance(MyApp.getContext()).sendCommands(sendList);
        }
    }

    private BaseCommand.CommandResultCallback commandResultCallback = new BaseCommand.CommandResultCallback() {
        @Override
        public void onSuccess(BaseCommand baseCommand) {

        }

        @Override
        public void onFail(BaseCommand baseCommand) {

        }
    };

    public interface OnCallPhoneListener {
        void callPhoneAlert(String phoneTag);
    }

    private static final String[] PHONES_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Photo.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID };

    /**联系人显示名称**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /**电话号码**/
    private static final int PHONES_NUMBER_INDEX = 1;

    /**头像ID**/
    private static final int PHONES_PHOTO_ID_INDEX = 2;

    /**联系人的ID**/
    private static final int PHONES_CONTACT_ID_INDEX = 3;
    boolean isPhone = true;
    /**得到手机通讯录联系人信息**/
    private void getPhoneContacts(String pName) {

        Log.e(TAG,"----pname-="+pName);
        ContentResolver resolver = MyApp.getApplication().getContentResolver();

        // 获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);

        if (phoneCursor != null) {
            Log.e(TAG,"---phoneCursor.size="+phoneCursor.getCount());
            while (phoneCursor.moveToNext()) {
                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                Log.e(TAG,"----ph--="+phoneNumber);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (!TextUtils.isEmpty(phoneNumber)){
                    phoneNumber = phoneNumber.replace("-","");
                    phoneNumber = phoneNumber.replace(" ","");
                    if(pName.equals(phoneNumber)){
                        isPhone = false;
                        String contactNames = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX)+"";
                        Log.e(TAG,"-----相等---="+contactNames);
                        MyApp.getmW30SBLEManage().notifacePhone(contactNames,0x01);
                        return;
                    }
                }
                Log.e(TAG,"-----phoneNum-="+phoneNumber);
                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                Log.e(TAG,"----contactName--="+contactName);

            }
            if(isPhone){
                MyApp.getmW30SBLEManage().notifacePhone(pName,0x01);
            }

            phoneCursor.close();
        }else{
            MyApp.getmW30SBLEManage().notifacePhone(pName,0x01);
        }
    }

}