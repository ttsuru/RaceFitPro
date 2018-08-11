package com.example.bozhilun.android.w30s;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.PhoneBroadcastReceiver;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.BlueAdapterUtils;
import com.example.bozhilun.android.siswatch.utils.PhoneStateListenerInterface;
import com.example.bozhilun.android.siswatch.utils.PhoneUtils;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.w30s.utils.NationalistinctionDUtils;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.FragmentAdapter;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.data.W30sNewsH9DataFragment;
import com.example.bozhilun.android.siswatch.run.W30sNewRunFragment;
import com.example.bozhilun.android.siswatch.utils.UpdateManager;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.fragment.W30SMineFragment;
import com.example.bozhilun.android.w30s.fragment.W30SRecordFragment;
import com.example.bozhilun.android.widget.NoScrollViewPager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.suchengkeji.android.w30sblelibrary.W30SBLEManage;
import com.suchengkeji.android.w30sblelibrary.W30SBLEServices;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferenceUtil;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/5 16:54
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SHomeActivity extends WatchBaseActivity implements PhoneStateListenerInterface{
    private final String TAG = "W30SHomeActivity";
    private List<Fragment> h18iFragmentList = new ArrayList<>();
    @BindView(R.id.h18i_view_pager)
    NoScrollViewPager h18iViewPager;
    @BindView(R.id.h18i_bottomBar)
    BottomBar h18iBottomBar;
    MyBroadcastReceiver myBroadcastReceivers = null;
    public BluetoothAdapter bluetoothAdapter;
    private String w30SBleName;
    private String phoneNumber;

    //已经连接过到蓝牙mac
    String mylanmac ;
    //更新
    UpdateManager updateManager;


    DisPhoneCallBack disPhoneCallBack = null;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "--------home-w30-=onCreate---");
        setContentView(R.layout.activity_w30s_home);
        ButterKnife.bind(this);
        mylanmac = (String) SharedPreferenceUtil.get(MyApp.getContext(), "mylanmac", "");
        w30SBleName = (String) SharedPreferenceUtil.get(MyApp.getApplication(), "mylanya", "");
        initViews();
        initBuleAdapter();

        myBroadcastReceivers = new MyBroadcastReceiver();
        disPhoneCallBack = new DisPhoneCallBack();
    }


    //判断蓝牙是否打开，未打开强制打开
    private void initBuleAdapter() {
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if(!bluetoothAdapter.enable()){
            BlueAdapterUtils.getBlueAdapterUtils(W30SHomeActivity.this).turnOnBlue(W30SHomeActivity.this,10000,1000);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            if (myBroadcastReceivers == null)
                myBroadcastReceivers = new MyBroadcastReceiver();
            //注册监听电话状态变化的监听
            MyApp.getCustomPhoneStateListener().setPhoneStateListenerInterface(this);
            //注册连接状态的广播
            registerReceiver(myBroadcastReceivers, makeGattUpdateIntentFilter());
        } catch (Exception e) {
            e.getMessage();
        }
    }

    /**
     * 广播过滤
     *
     * @return
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(W30SBLEServices.ACTION_FINDE_AVAILABLE_DEVICE);
        intentFilter.addAction(W30SBLEServices.ACTION_GATT_CONNECTED);
        intentFilter.addAction(W30SBLEServices.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "--------home-w30-=onstop---");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "--------home-w30-=ondestory---");
        if(updateManager != null){
            updateManager.destoryUpdateBroad();
        }
        try {
            if (myBroadcastReceivers != null) {
                unregisterReceiver(myBroadcastReceivers);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(MyCommandManager.DEVICENAME == null && !WatchUtils.isEmpty(mylanmac)){
            if(W30SBLEManage.mW30SBLEServices != null){
                W30SBLEManage.mW30SBLEServices.connectBle(mylanmac);
            }else{
                Log.e(TAG,"------server=null-了---");
                W30SBLEManage instance = W30SBLEManage.getInstance(MyApp.getContext());
                MyApp.setmW30SBLEManage(instance);
                MyApp.getmW30SBLEManage().openW30SBLEServices();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(111);
                    }
                }, 3 * 1000);
            }
        }else{
            MyApp.getmW30SBLEManage().SendAnddroidLanguage(0x01);
            MyApp.getmW30SBLEManage().disPhoneCallData(disPhoneCallBack);
        }

        try {
            //scanConn();
            if (MyApp.AppisOne) {
                String appInfo = NationalistinctionDUtils.getAppInfo(W30SHomeActivity.this);
                if (!WatchUtils.isEmpty(appInfo)&&!appInfo.equals("com.bozlun.bozhilun.android")) {
                    //检查更新
                    updateManager =
                            new UpdateManager(W30SHomeActivity.this, URLs.HTTPs + URLs.getvision);
                    updateManager.checkForUpdate(false);
                    MyApp.AppisOne = false;
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }
    /**
     * 初始化，添加Fragment界面
     */
    private void initViews() {
        h18iFragmentList.add(new W30SRecordFragment()); //记录
//        h18iFragmentList.add(new NewsH9DataFragment());   //数据
        h18iFragmentList.add(new W30sNewsH9DataFragment()); //数据
        h18iFragmentList.add(new W30sNewRunFragment());   //跑步
        //h18iFragmentList.add(new TmpRunFragment());   //跑步
//        h18iFragmentList.add(new W30SFrendensFragment());   //联系人
        h18iFragmentList.add(new W30SMineFragment());   //我的
        if (h18iFragmentList == null) return;
        FragmentStatePagerAdapter fragmentPagerAdapter = new FragmentAdapter(getSupportFragmentManager(), h18iFragmentList);
        h18iViewPager.setAdapter(fragmentPagerAdapter);
        h18iBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_home: //记录
                        h18iViewPager.setCurrentItem(0);
                        break;
                    case R.id.tab_set:  //开跑
                        h18iViewPager.setCurrentItem(2);
                        break;
                    case R.id.tab_data:     //数据
                        h18iViewPager.setCurrentItem(1);
                        break;
                    case R.id.tab_my:   //我的
                        h18iViewPager.setCurrentItem(3);//4
                        break;
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 过滤按键动作
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);

        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            moveTaskToBack(true);
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void callPhoneData(int flag, String phoneNumber) {
        Log.e(TAG,"----falgCallPhone="+flag+"--num="+phoneNumber+"--=w30SBleName="+w30SBleName);
        try {
            this.phoneNumber = phoneNumber;
            if (!W30SHomeActivity.this.isFinishing() && W30SBLEManage.mW30SBLEServices != null) {
                switch (flag) {
                    case 0: //挂断
                        isNo = true;
                        if (w30SBleName != null && w30SBleName.equals("W30")) {
                            missCallPhone();    //挂断电话
                        }

                        break;
                    case 2: //接通
                        isNo = true;
                        if (w30SBleName != null && w30SBleName.equals("W30")) {
                            missCallPhone();    //挂断电话
                        }
                        break;
                    case 1: //来电
                       // Log.e(TAG,"---case1来电--="+phoneNumber);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                              //  handler.sendEmptyMessage(555);
                            }
                        }, 1000);
                        break;

                }
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }


    boolean isNo = true;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 555:
                    //Log.e(TAG,"------hand555---="+phoneNumber);
                    if (w30SBleName != null && w30SBleName.equals("W30")) {
                        getPeople(phoneNumber, MyApp.getContext());
                    }
                    break;
                case 0x06:
                    handler.removeMessages(0x06);
                    bluetoothAdapter = W30SBLEManage.getmBluetoothAdapter();
                    if (bluetoothAdapter != null) {
                       // bluetoothAdapter.startLeScan(leScanCallback);
                    } else {
                        W30SBLEManage instance = W30SBLEManage.getInstance(MyApp.getContext());
                        MyApp.setmW30SBLEManage(instance);
                        bluetoothAdapter = W30SBLEManage.getmBluetoothAdapter();
                    }
                    break;
                case 111:
                    if(MyCommandManager.DEVICENAME == null && !WatchUtils.isEmpty(mylanmac)){
                        if(W30SBLEManage.mW30SBLEServices != null){
                            W30SBLEManage.mW30SBLEServices.connectBle(mylanmac);
                        }
                    }
                    MyApp.getmW30SBLEManage().disPhoneCallData(disPhoneCallBack);
                    break;
                case 122:
                    String pNum = (String) msg.obj;
                    //Log.e(TAG,"---hand122="+pNum);
                    MyApp.getmW30SBLEManage().SendAnddroidLanguage(0x01);
                    MyApp.getmW30SBLEManage().disPhoneCallData(disPhoneCallBack);
                    if(!WatchUtils.isEmpty(pNum)){
                        boolean isW30Phone = (boolean) SharedPreferenceUtil.get(MyApp.context, "w30sswitch_Phone", true);
                        if(isW30Phone){
                            getNameByPhone(pNum);
                        }
                    }
                    break;
            }
            return false;
        }
    });

    //挂断电话
    private void missCallPhone() {
        if (w30SBleName != null && !TextUtils.isEmpty(w30SBleName) && w30SBleName.equals("W30")) {
            if (MyCommandManager.DEVICENAME != null && MyCommandManager.DEVICENAME.equals("W30")) {
                MyApp.getmW30SBLEManage().notifyMsgClose();
                MyApp.getmW30SBLEManage().notifyMsgClose();
            }
        }
    }

    /**
     * 通过输入获取电话号码
     */
    public void getPeople(String nunber, Context context) {
        Message message = new Message();
        message.what = 122;
        message.obj = nunber;
        handler.sendMessage(message);

    }


    private class DisPhoneCallBack implements W30SBLEServices.DisPhoneCallListener{
        @Override
        public void disCallPhone(int disV) {
            Log.e(TAG,"---11--dev---"+disV);
            TelephonyManager tm = (TelephonyManager) W30SHomeActivity.this
                    .getSystemService(Service.TELEPHONY_SERVICE);
            PhoneUtils.endPhone(W30SHomeActivity.this,tm);
            PhoneUtils.dPhone();
            PhoneUtils.endCall(MyApp.getContext());
            PhoneUtils.endcall();
        }
    }

    private void getNameByPhone(String phNumber){
        //Log.e(TAG,"---getNameByPhone-=="+phNumber);
        boolean isPhoneNum = true; //判断通讯录中是否保存了联系人
        //得到ContentResolver对象
        ContentResolver cr = getContentResolver();
        //取得电话本中开始一项的光标
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if(cursor != null ){
            //向下移动光标
            while(cursor.moveToNext())
            {
                //取得联系人名字
                int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                String contact = cursor.getString(nameFieldColumnIndex);
                //Log.e(TAG,"---contact="+contact);
                //取得电话号码
                String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);
                if(phone != null ){
                    while(phone.moveToNext())
                    {
                        String PhoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if(!WatchUtils.isEmpty(PhoneNumber)){
                            //格式化手机号
                            PhoneNumber = PhoneNumber.replace("-","");
                            PhoneNumber = PhoneNumber.replace(" ","");
                            // Log.e(TAG,"---PhoneNumber="+PhoneNumber);
                            if(phNumber.equals(PhoneNumber)){
                                isPhoneNum = false;
                                int nameFieldColumnIndexs = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                                String contacts = cursor.getString(nameFieldColumnIndexs)+"";
                                MyApp.getmW30SBLEManage().notifacePhone(contacts+PhoneNumber,0x01);

                            }
                        }

                    }
                }
                //通讯录中无保存联系人时，直接发送电话号码
//                if(isPhoneNum){
//                    MyApp.getmW30SBLEManage().notifacePhone(phNumber,0x01);
//                }

            }
        }
        //通讯录为空时直接发送电话号码
        if(isPhoneNum){
            MyApp.getmW30SBLEManage().notifacePhone(phNumber,0x01);
        }
    }

}
