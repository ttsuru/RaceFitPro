package com.example.bozhilun.android;


import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.afa.tourism.greendao.gen.DaoMaster;
import com.afa.tourism.greendao.gen.DaoSession;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.bozhilun.android.B18I.b18idb.DBManager;
import com.example.bozhilun.android.B18I.b18iutils.B18iUtils;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.service.AlertService;
import com.example.bozhilun.android.b30.service.B30ConnStateService;
import com.example.bozhilun.android.b30.service.B30DataServer;
import com.example.bozhilun.android.bleutil.BluetoothLeService;
import com.example.bozhilun.android.exection.CrashHandler;
import com.example.bozhilun.android.siswatch.bleus.WatchBluetoothService;
import com.example.bozhilun.android.siswatch.utils.CustomPhoneStateListener;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.sdk.bluetooth.app.BluetoothApplicationContext;
import com.sdk.bluetooth.interfaces.BluetoothManagerDeviceConnectListener;
import com.sdk.bluetooth.manage.AppsBluetoothManager;
import com.suchengkeji.android.w30sblelibrary.W30SBLEManage;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferenceUtil;
import com.veepoo.protocol.VPOperateManager;

import org.litepal.LitePalApplication;
import java.util.ArrayList;
import java.util.List;
import cn.sharesdk.framework.ShareSDK;


/**
 * Created by thinkpad on 2016/7/20.
 */

public class MyApp extends LitePalApplication {

    private static MyApp application;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    //用于退出activity
    private List<AppCompatActivity> activities;

    public static Context context;


    //sis watch service
    private static WatchBluetoothService watchBluetoothService;

    public static WatchBluetoothService getWatchBluetoothService() {
        if (watchBluetoothService == null) {
            initWatchBlueTooth();
        }
        return watchBluetoothService;
    }


    public MyApp() {
        application = this;
    }

    public static MyApp getInstance() {

        return application;
    }


    public static OnH9ConnListener h9ConnListener;

    public static void setH9ConnListener(OnH9ConnListener h9ConnListener) {
        MyApp.h9ConnListener = h9ConnListener;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    static RequestQueue requestQueue;
    //B18I数据库管理
    private static DBManager dbManager;

    public static boolean isOne = true;
    public static boolean AppisOne = false;
    public static boolean AppisOneStar = false;


    //监听来电
    private static CustomPhoneStateListener customPhoneStateListener;
    CrashHandler crashHandler;
    //B30手环
    private static VPOperateManager vpOperateManager;

    //B30手环的服务
    private static B30ConnStateService b30ConnStateService;




    @Override
    public void onCreate() {
        super.onCreate();
        AppisOne = true;
        AppisOneStar = true;
        //LeakCanary.install(this);
        application = this;
        context = this;
        activities = new ArrayList<>();
        //初始化异常收集
        crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        ShareSDK.initSDK(this);
        //启动w30服务
        startW30SSerever();
        //启动B30的服务
        startB30Server();
        bindAlertServices();    //绑定通知的服务
        setDatabase();
        dbManager = DBManager.getInstance(context);
        //initBlueTooth();
        //启动sis watch手表的服务
        initWatchBlueTooth();

       //初始化H9手表SDK
        BluetoothApplicationContext.getInstance().init(application);
        //设置H9手表的连接监听回调
        AppsBluetoothManager.getInstance(MyApp.getContext()).addBluetoothManagerDeviceConnectListener(bluetoothManagerDeviceConnectListener);
        registerPhoneStateListener(); //注册H8手表的ianh监听

    }



    public static B30DataServer getB30DataServer(){
        return B30DataServer.getB30DataServer();
    }

    //B30的服务
    public static B30ConnStateService getB30ConnStateService(){
        if(b30ConnStateService == null){
            startB30Server();
        }
        return b30ConnStateService;
    }

    //启动
    private static void startB30Server() {
        Intent ints = new Intent(application.getApplicationContext(), B30ConnStateService.class);
        application.bindService(ints, b30ServerConn, BIND_AUTO_CREATE);
    }

    //B30
    private static ServiceConnection b30ServerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if(service != null){
                b30ConnStateService = ((B30ConnStateService.B30LoadBuilder) service).getB30Service();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            b30ConnStateService = null;
        }
    };

    public static VPOperateManager getVpOperateManager(){

        if(vpOperateManager == null){
            vpOperateManager = VPOperateManager.getMangerInstance(application);

        }
        return vpOperateManager;
    }


    private void startW30SSerever() {
        try {
            //启动W30S手表服务
            mW30SBLEManage = W30SBLEManage.getInstance(getApplicationContext());
            //上传数据时间间隔
            SharedPreferenceUtil.put(context, "upSportTime", "2017-11-02 15:00:00");
            SharedPreferenceUtil.put(context, "upSleepTime", "2013-11-02 15:00:00");
            SharedPreferenceUtil.put(context, "upHeartTimetwo", "2017-11-02 15:00:00");
            SharedPreferenceUtil.put(context, "upHeartTimeone", B18iUtils.getSystemDataStart());
            SharedPreferencesUtils.saveObject(context, "type", "0");
        } catch (Exception e) {
            e.getMessage();
        }
    }


    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        super.onTrimMemory(level);
    }


    static W30SBLEManage mW30SBLEManage;

    public static W30SBLEManage getmW30SBLEManage() {
        if (mW30SBLEManage == null) {
            return W30SBLEManage.getInstance(context);
        } else {
            return mW30SBLEManage;
        }
    }

    public static void setmW30SBLEManage(W30SBLEManage mW30SBLEManage) {
        MyApp.mW30SBLEManage = mW30SBLEManage;
    }

    /**
     * 定时读取手环数据
     */
    private Handler mHandler = new Handler();
    public static final String RefreshBroad = "com.example.bozhilun.android.RefreshBroad";

    /**
     * 自动同步数据
     */
    //private RefreshBroadcastReceivers refreshBroadcastReceiver;
    private void timingDown() {

//        //动态注册广播
////        H9RecordFragment.RefreshBroadcastReceiver refreshBroadcastReceiver = new H9RecordFragment.RefreshBroadcastReceiver();
//        refreshBroadcastReceiver = new RefreshBroadcastReceivers();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(RefreshBroad);
//        registerReceiver(refreshBroadcastReceiver, intentFilter);


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {

                    //上传数据时间间隔
                    SharedPreferenceUtil.put(context, "upSportTime", "2017-11-02 15:00:00");
                    SharedPreferenceUtil.put(context, "upSleepTime", "2015-11-02 15:00:00");
                    SharedPreferenceUtil.put(context, "upHeartTime", "2017-11-02 15:00:00");
                    Log.e("--------", "----rrrrrrrrrrrrrrrrr---z----自动同步数据重-");
                    Intent intent = new Intent();
                    intent.setAction(RefreshBroad);
//                intent.putExtra("refresh", "YES");
                    sendBroadcast(intent);
                    // 循环调用实现定时刷新界面
                    mHandler.postDelayed(this, 15 * (1000 * 60));//300000ms = 5min  600000ms = 10 min
                } catch (Exception e) {
                    e.getMessage();
                }

            }
        };
        mHandler.removeCallbacks(runnable);
        mHandler.postDelayed(runnable, 10 * (1000 * 60));
    }

    //监听来电
    public static CustomPhoneStateListener getCustomPhoneStateListener() {
        if (customPhoneStateListener == null) {
            synchronized (CustomPhoneStateListener.class) {
                if (customPhoneStateListener == null) {
                    customPhoneStateListener = new CustomPhoneStateListener(application);
                }
            }
        }
        return customPhoneStateListener;
    }


    //H8手表注册监听来电的监听
    private void registerPhoneStateListener() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(getCustomPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
        }
    }


    /**
     * H9 手表的连接监听回调
     */
    public BluetoothManagerDeviceConnectListener bluetoothManagerDeviceConnectListener = new BluetoothManagerDeviceConnectListener() {
        @Override
        public void onConnected(BluetoothDevice bluetoothDevice) {
            Log.e("全局App", "------h9--onConnected--");
            if (h9ConnListener != null) {
                Log.e("全局App", "----111--h9--onConnected--");
                h9ConnListener.h9connect(bluetoothDevice);
            }
        }

        @Override
        public void onConnectFailed() {
//            closeLoadingDialog();
            if (h9ConnListener != null) {
                h9ConnListener.h9connectFailed();
            }
        }

        @Override
        public void onEnableToSendComand(BluetoothDevice bluetoothDevice) {
            Log.e("全局App", "------h9--onConnected--");

            if (h9ConnListener != null) {
                h9ConnListener.h9onEnableToSendComand(bluetoothDevice);
            }
        }

        @Override
        public void onConnectDeviceTimeOut() {  //连接超时
            Log.e("全局App", "------h9--onConnected--");
//            closeLoadingDialog();
            if (h9ConnListener != null) {
                h9ConnListener.h9contimeout();
            }
        }
    };

    private void bindAlertServices() {
        Intent ints = new Intent(application.getApplicationContext(), AlertService.class);
        bindService(ints, alertConn, BIND_AUTO_CREATE);
    }

    private ServiceConnection alertConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("MyApp", "-----conn---");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("MyApp", "-----disconn---");
        }
    };


    public static MyApp getApplication() {
        return application;
    }


    /**
     * 设置greenDao
     */

    private void setDatabase() {
        // 通过DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为greenDAO 已经帮你做了。
        // 注意：默认的DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();

    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public static DBManager getDBManager() {
        return dbManager;
    }


    //开启手表的bluetoothservie
    public static void initWatchBlueTooth() {
        Intent gattServiceIntent = new Intent(getApplication(), WatchBluetoothService.class);
        getApplication().bindService(gattServiceIntent, mWatchServiceConnection, BIND_AUTO_CREATE);
    }

    //解除H8的服务
    public static void unBindWatchBlueTooth(){
        if(watchBluetoothService != null ){
            watchBluetoothService.unbindService(mWatchServiceConnection);
        }
    }


    // 管理服务的生命周期
    private static final ServiceConnection mWatchServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            watchBluetoothService = ((WatchBluetoothService.LocalBinder) service).getService();
            if (!watchBluetoothService.initialize()) {
                // Log.e(TAG, "Unable to initialize Bluetooth");

            }
            // Automatically connects to the device upon successful start-up initialization.
            // watchBluetoothService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            watchBluetoothService = null;
        }
    };


    /**
     * 添加Activity
     */
    public void addActivity(AppCompatActivity activity) {
        // 判断当前集合中不存在该Activity
        if (!activities.contains(activity)) {
            activities.add(activity);//把当前Activity添加到集合中
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeALLActivity() {
        //通过循环，把集合中的所有Activity销毁
        for (AppCompatActivity activity : activities) {
            //unregisterReceiver(refreshBroadcastReceiver);
            activity.finish();
        }
    }

    // 返回
    public static Context getContextObject() {
        return context;
    }

    public static RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(application);
        }
        return requestQueue;
    }

    public interface OnH9ConnListener {
        void h9connect(BluetoothDevice bluetoothDevice);

        void h9connectFailed();

        void h9onEnableToSendComand(BluetoothDevice bluetoothDevice);

        void h9contimeout();
    }

}
