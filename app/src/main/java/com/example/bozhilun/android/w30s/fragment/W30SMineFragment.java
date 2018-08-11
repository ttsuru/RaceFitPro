package com.example.bozhilun.android.w30s.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.activity.wylactivity.WenxinBandActivity;
import com.example.bozhilun.android.w30s.BaseFragment;
import com.example.bozhilun.android.w30s.SharePeClear;
import com.example.bozhilun.android.w30s.activity.W30SSettingActivity;
import com.bumptech.glide.Glide;
import com.example.bozhilun.android.B18I.b18isystemic.B18IAppSettingActivity;
import com.example.bozhilun.android.B18I.b18isystemic.B18IRankingListActivity;
import com.example.bozhilun.android.B18I.b18isystemic.FindFriendActivity;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.MyPersonalActivity;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.NewSearchActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.suchengkeji.android.w30sblelibrary.W30SBLEManage;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * @aboutContent: 联系人界面
 * @author： An
 * @crateTime: 2018/3/5 17:04
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class W30SMineFragment extends BaseFragment {

    private static final String TAG = "W30SMineFragment";

    Unbinder unbinder;

    //头像显示ImageView
    @BindView(R.id.userImageHead)
    ImageView userImageHead;
    //用户名称显示TextView
    @BindView(R.id.userName)
    TextView userName;
    //总公里数显示TextView
    @BindView(R.id.totalKilometers)
    TextView totalKilometers;
    //日均步数显示TextView
    @BindView(R.id.equalStepNumber)
    TextView equalStepNumber;
    //达标天数显示TextView
    @BindView(R.id.standardDay)
    TextView standardDay;
    @BindView(R.id.mine_mac_id)
    TextView mineMacId;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.privatemode_cardview)
    CardView privatemodeCardview;//排行榜

    private CommonSubscriber commonSubscriber, commonSubscriber2;
    private SubscriberOnNextListener subscriberOnNextListener, subscriberOnNextListener2;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b18i_mine, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (MyCommandManager.DEVICENAME != null) {
            String mylanmac = (String) SharedPreferenceUtil.get(getContext(), "mylanmac", "");
            if (mylanmac != null && !WatchUtils.isEmpty(mylanmac)) {
                String substring = mylanmac.substring((mylanmac.length() - 5), mylanmac.length());
                if (mineMacId != null) mineMacId.setText(substring);
            }
        }


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (MyCommandManager.DEVICENAME != null) {
            String mylanmac = (String) SharedPreferenceUtil.get(getContext(), "mylanmac", "");
            if (mylanmac != null && !WatchUtils.isEmpty(mylanmac)) {
                String substring = mylanmac.substring((mylanmac.length() - 5), mylanmac.length());
                if (mineMacId != null) mineMacId.setText(substring);
            }
        }
        try {
            //获取用户信息
            //getUserInfoData();
            syncUserInfoData();
            //获取数据展示
            getUserSportData();
            if (MyCommandManager.DEVICENAME != null) {
                String mylanmac = (String) SharedPreferenceUtil.get(getContext(), "mylanmac", "");
                if (mylanmac != null && !WatchUtils.isEmpty(mylanmac)) {
                    String substring = mylanmac.substring((mylanmac.length() - 5), mylanmac.length());
                    if (mineMacId != null) mineMacId.setText(substring);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MyCommandManager.DEVICENAME != null) {
            String mylanmac = (String) SharedPreferenceUtil.get(getContext(), "mylanmac", "");
            if (mylanmac != null && !WatchUtils.isEmpty(mylanmac)) {
                String substring = mylanmac.substring((mylanmac.length() - 5), mylanmac.length());
                if (mineMacId != null) mineMacId.setText(substring);
            }
        }
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        if (isVisible) {
            try {
                //            Log.d("========mmm===========", "isVisible");
                //获取用户信息
                //getUserInfoData();
                syncUserInfoData();
                //获取数据展示
                getUserSportData();
                if (MyCommandManager.DEVICENAME != null) {
                    String mylanmac = (String) SharedPreferenceUtil.get(getContext(), "mylanmac", "");
                    if (mylanmac != null && !WatchUtils.isEmpty(mylanmac)) {
                        String substring = mylanmac.substring((mylanmac.length() - 5), mylanmac.length());
                        if (mineMacId != null) mineMacId.setText(substring);
                    }
                }
            } catch (Exception e) {
                e.getMessage();
            }


        } else {
//            Log.d("=========mmm==========", "No isVisible");
        }
    }

    @Override
    protected void onFragmentFirstVisible() {
//        Log.d("==========mmm=========", "onFragmentFirstVisible");
        initData();
    }


    private void initData() {
        if (mineMacId != null) {
            if (MyCommandManager.DEVICENAME != null) {
                String mylanmac = (String) SharedPreferenceUtil.get(getContext(), "mylanmac", "");
                if (mylanmac != null && !WatchUtils.isEmpty(mylanmac)) mineMacId.setText(mylanmac);
            }
        }
        syncUserInfoData();

        //显示数据返回
        //数据返回
        subscriberOnNextListener2 = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) { //{"myInfo":{"distance":48.3,"count":2,"stepNumber":1582},"resultCode":"001"}
                if (!WatchUtils.isEmpty(result)) {
                    try {
                        Log.e("mine", "------result----" + result);
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.getInt("resultCode") == 001) {
                            JSONObject myInfoJsonObject = jsonObject.getJSONObject("myInfo");
                            if (myInfoJsonObject != null) {
                                String distances = myInfoJsonObject.getString("distance");
                                if (!WatchUtils.isEmpty(distances)) {
                                    //总公里数
                                    boolean w30sunit = (boolean) SharedPreferenceUtil.get(getContext(), "w30sunit", true);
                                    if (w30sunit) {
                                        if (textView3 != null)
                                            textView3.setText(getResources().getString(R.string.totaldistance));
                                        if (totalKilometers != null)
                                            totalKilometers.setText("" + WatchUtils.div(Double.valueOf(distances), 1000, 2) + " km");
                                    } else {
                                        if (textView3 != null)
                                            textView3.setText(getResources().getString(R.string.string_w30s_alldis));
                                        int round = (int) Math.round(Double.valueOf(distances) * 3.28);
                                        if (totalKilometers != null)
                                            totalKilometers.setText(String.valueOf(WatchUtils.div(round, 1, 2)).split("[.]")[0] + " ft");
                                    }
                                }
                                String counts = myInfoJsonObject.getString("count");
                                if (!WatchUtils.isEmpty(myInfoJsonObject.getString("count"))) {
                                    //达标天数
                                    if (standardDay != null)
                                        standardDay.setText("" + myInfoJsonObject.getString("count") + " day");
                                }
                                String stepNums = myInfoJsonObject.getString("stepNumber");
                                if (!WatchUtils.isEmpty(stepNums)) {
                                    //平均步数
                                    if (equalStepNumber != null)
                                        equalStepNumber.setText("" + myInfoJsonObject.getString("stepNumber") + " step");
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }


    //同步用户信息
    private void syncUserInfoData() {

        String userData = (String) SharedPreferencesUtils.readObject(getActivity(), "saveuserinfodata");
        //Log.d("-----用户资料-----AAA----", "--------" + userData);
        if (!WatchUtils.isEmpty(userData)) {
            try {
                //int weight;
                JSONObject jsonO = new JSONObject(userData);
                String imgHead = jsonO.getString("image");    //头像
                String nickName = jsonO.getString("nickName");    //名字
                //Log.d("-----用户资料-----CCC---", nickName + "===" + imgHead);
                if (userName != null) userName.setText("" + nickName + "");
                if (!WatchUtils.isEmpty(imgHead)) {
                    if (userImageHead != null) {
                        RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true);
                        //头像
                        Glide.with(getActivity()).
                                load(imgHead).apply(mRequestOptions).into(userImageHead);    //头像
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    //获取显示的数据
    private void getUserSportData() {
        String myInfoUrl = URLs.HTTPs + URLs.myInfo;
        JSONObject js = new JSONObject();
        try {
            js.put("userId", SharedPreferencesUtils.readObject(getActivity(), "userId"));
            js.put("deviceCode", (String) SharedPreferenceUtil.get(getContext(), "mylanmac", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        commonSubscriber2 = new CommonSubscriber(subscriberOnNextListener2, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber2, myInfoUrl, js.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.privatemode_cardview,
            R.id.personalData, R.id.smartAlert, R.id.findFriends, R.id.mineSetting, R.id.userImageHead})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.userImageHead:    //点击头像
                //startActivity(new Intent(getActivity(), WenxinBandActivity.class));
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.privatemode_cardview://排行榜
                startActivity(new Intent(getActivity(), B18IRankingListActivity.class).putExtra("is18i", "W30S"));
                break;
            case R.id.personalData://个人资料
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.smartAlert://功能设置-----改----》mine_dev_image
                if (MyCommandManager.DEVICENAME != null) {    //已连接
                    SharePeClear.sendCmdDatas(getContext());
                    startActivity(new Intent(getActivity(), W30SSettingActivity.class).putExtra("is18i", "W30S"));
                } else {
                    try {
                        if(MyApp.getmW30SBLEManage() != null){
                            Log.e(TAG,"-----server--!=null----");
                            MyApp.getmW30SBLEManage().getmW30SBLEServices().disconnectBle();
                            MyApp.getmW30SBLEManage().getmW30SBLEServices().disClearData();
                        }
                        startActivity(new Intent(getContext(), NewSearchActivity.class));
                        getActivity().finish();
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
                break;
            case R.id.findFriends://查找朋友
                startActivity(new Intent(getActivity(), FindFriendActivity.class).putExtra("is18i", "W30S"));
                break;
            case R.id.mineSetting://设置
                startActivity(new Intent(getActivity(), B18IAppSettingActivity.class).putExtra("is18i", "W30S"));
                break;
        }
    }
}
