package com.example.bozhilun.android.b30.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Code;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.veepoo.protocol.listener.base.IABleConnectStatusListener;
import com.veepoo.protocol.listener.base.IConnectResponse;
import com.veepoo.protocol.listener.base.INotifyResponse;
import com.veepoo.protocol.listener.data.IFindPhonelistener;

import java.io.IOException;

public class B30ConnStateService extends Service {

    private static final String TAG = "B30ConnStateService";
    private static final int SEARCH_REQUEST_CODE = 1001;    //扫描
    private static final int AUTO_CONN_REQUEST_CODE = 1002; //非手动断开
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothClient bluetoothClient;
    private final IBinder mLocalBinder = new B30LoadBuilder();
    private ConnBleHelpService connBleHelpService;
    public B30ConnStateListener b30ConnStateListener;

    //震动
    private Vibrator mVibrator;
    private MediaPlayer mMediaPlayer;



    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case AUTO_CONN_REQUEST_CODE:  //非手动断开消息
                    handler.removeMessages(AUTO_CONN_REQUEST_CODE);
                    String  bleMacs = (String) SharedPreferencesUtils.readObject(MyApp.getContext(),"mylanmac");
                    Log.e(TAG,"-----读取的地址="+bleMacs+"--="+MyCommandManager.DEVICENAME);
                    //非手动断开
                    if(MyCommandManager.DEVICENAME != null&&MyCommandManager.deviceAddress != null
                            && !WatchUtils.isEmpty(bleMacs.trim())){
                        Log.e(TAG,"----非手动断开----");
                        connectAutoConn(true);
                    }else{  //手动断开
                        Log.e(TAG,"----手动断开----");
                    }

                    break;
                case SEARCH_REQUEST_CODE:  //搜索返回
                    handler.removeMessages(SEARCH_REQUEST_CODE);
                    String bleMacss = (String) SharedPreferencesUtils.readObject(MyApp.getContext(),"mylanmac");
                    SearchResult searchResult = (SearchResult) msg.obj;
                    Log.e(TAG,"----hand-msg="+searchResult.getAddress()+(searchResult.getAddress().equals(bleMacss.trim())));
                    if(searchResult.getAddress().equals(bleMacss.trim())){
                        Log.e(TAG,"----相等了----");
                        if(bluetoothClient != null){
                            bluetoothClient.stopSearch();
                        }
                        connB30ConnBle(bleMacss);
                    }
                    break;
            }

        }
    };



    @Override
    public void onCreate() {
        super.onCreate();
        registerConnState();
        bluetoothClient = new BluetoothClient(MyApp.getContext());
        BluetoothManager bluetoothManager = (BluetoothManager) MyApp.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager != null){
            bluetoothAdapter = bluetoothManager.getAdapter();
        }

    }

    //注册广播
    private void registerConnState() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WatchUtils.B30_CONNECTED_ACTION);
        intentFilter.addAction(WatchUtils.B30_DISCONNECTED_ACTION);
        registerReceiver(broadcastReceiver,intentFilter);
    }


    //连接
    public void connB30ConnBle(final String mac){
        MyApp.getVpOperateManager().registerConnectStatusListener(mac,iaBleConnectStatusListener);
        MyApp.getVpOperateManager().connectDevice(mac, new IConnectResponse() {
            @Override
            public void connectState(int i, BleGattProfile bleGattProfile, boolean b) {
                Log.e(TAG,"----connectState="+i);
                if(i == Code.REQUEST_SUCCESS){  //连接成功过
                    if(bluetoothClient != null){
                        bluetoothClient.stopSearch();
                    }
                }
            }
        }, new INotifyResponse() {
            @Override
            public void notifyState(int i) {
                Log.e(TAG,"----notifiy="+i);
                if(i == Code.REQUEST_SUCCESS){
                    if(connBleHelpService == null){
                        connBleHelpService = connBleHelpService.getConnBleHelpService();
                    }
                   // connBleHelpService = connBleHelpService.getConnBleHelpService();
                    connBleHelpService.setConnBleHelpListener(new ConnBleHelpService.ConnBleHelpListener() {
                        @Override
                        public void connSuccState() {
                            MyCommandManager.DEVICENAME = "B30";
                            MyCommandManager.ADDRESS = mac;
                            SharedPreferencesUtils.saveObject(MyApp.getContext(), "mylanya", "B30");
                            SharedPreferencesUtils.saveObject(MyApp.getContext(), "mylanmac", mac);
                            Intent intent = new Intent();
                            intent.setAction(WatchUtils.B30_CONNECTED_ACTION);
                            sendBroadcast(intent);

//
//                            if(b30ConnStateListener != null){
//                                b30ConnStateListener.onB30Connect();
//                            }
                        }
                    });
                    connBleHelpService.doConnOperater();
                }
            }
        });
    }

    //自动连接
    public void connectAutoConn(boolean isScan) {
        if(isScan){
            if(bluetoothAdapter != null && bluetoothAdapter.isEnabled()){
                //bluetoothAdapter.startLeScan(leScanCallback);
                SearchRequest request = (new SearchRequest.Builder()).searchBluetoothLeDevice(Integer.MAX_VALUE, 1).build();
                bluetoothClient.search(request, new SearchResponse() {
                    @Override
                    public void onSearchStarted() {
                        Log.e(TAG,"-----开始扫描----");
                    }

                    @Override
                    public void onDeviceFounded(SearchResult searchResult) {
                        Log.e(TAG,"----onDeviceFound="+searchResult.getName()+"-mac="+searchResult.getAddress());
                        Message message = handler.obtainMessage();
                        message.what = SEARCH_REQUEST_CODE;
                        message.obj = searchResult;
                        handler.sendMessage(message);

                    }

                    @Override
                    public void onSearchStopped() {
                        Log.e(TAG,"----扫描停止----");
                    }

                    @Override
                    public void onSearchCanceled() {
                        Log.e(TAG,"----扫描关闭----");
                    }
                });
            }
        }else{
            if(bluetoothClient != null){
                bluetoothClient.stopSearch();
            }
        }
    }


    //监听蓝牙连接或断开的状态
    private IABleConnectStatusListener iaBleConnectStatusListener = new IABleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String s, int state) {
            switch (state){
                case Constants.STATUS_CONNECTED:    //已连接
                    Log.e(TAG,"-----监听--conn");
                    findPhoneListenerData();

                    break;
                case Constants.STATUS_DISCONNECTED: //已断开
                    Log.e(TAG,"-----监听--disconn");
                    Intent intents = new Intent();
                    intents.setAction(WatchUtils.B30_DISCONNECTED_ACTION);
                    sendBroadcast(intents);
//                    if(b30ConnStateListener != null){
//                        b30ConnStateListener.onB30Disconn();
//                    }
                    handler.sendEmptyMessage(AUTO_CONN_REQUEST_CODE);
                    break;
            }
        }
    };

    //监听查找手机
    private void findPhoneListenerData() {
        MyApp.getVpOperateManager().settingFindPhoneListener(new IFindPhonelistener() {
            @Override
            public void findPhone() {
                try{
                    mVibrator = (Vibrator) MyApp.getContext().getSystemService(Service.VIBRATOR_SERVICE);
                    mMediaPlayer = new MediaPlayer();
                    AssetFileDescriptor file = MyApp.getContext().getResources().openRawResourceFd(R.raw.music);
                    try {
                        mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
                                file.getLength());
                        mMediaPlayer.prepare();
                        file.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mMediaPlayer.setVolume(0.5f, 0.5f);
                    mMediaPlayer.setLooping(false);
                    mMediaPlayer.start();
                    if (mVibrator.hasVibrator()) {
                        //想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
                        mVibrator.vibrate(new long[]{500, 1000, 500, 1000}, -1);//查找手机是调用系统震动
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    public void setB30ConnStateListener(B30ConnStateListener b30ConnStateListener) {
        this.b30ConnStateListener = b30ConnStateListener;
    }

    public interface B30ConnStateListener{
        void onB30Connect();
        void onB30Disconn();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            if(broadcastReceiver != null){
                unregisterReceiver(broadcastReceiver);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }


    public class B30LoadBuilder extends Binder{
        public  B30ConnStateService getB30Service(){
            return B30ConnStateService.this;
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };
}
