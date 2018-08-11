package com.example.bozhilun.android.activity;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.aigestudio.wheelpicker.widgets.DatePick;
import com.aigestudio.wheelpicker.widgets.ProfessionPick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.base.BaseActivity;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.imagepicker.PickerBuilder;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.DialogSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.siswatch.utils.Base64BitmapUtil;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.ImageTool;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.google.gson.Gson;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferenceUtil;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RequestExecutor;
import org.apache.commons.lang.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import static com.example.bozhilun.android.util.Common.userInfo;

/**
 * Created by thinkpad on 2017/3/8.
 * 个人信息
 */

public class MyPersonalActivity extends BaseActivity implements RequestView {

    private static final String TAG = "MyPersonalActivity";

    private static final int GET_CAMERA_REQUEST_CODE = 1001;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.mine_logo_iv_personal)
    CircleImageView mineLogoIv;
    @BindView(R.id.nickname_tv)
    TextView nicknameTv;
    @BindView(R.id.sex_tv)
    TextView sexTv;
    @BindView(R.id.height_tv)
    TextView heightTv;
    @BindView(R.id.weight_tv)
    TextView weightTv;
    @BindView(R.id.birthday_tv)
    TextView birthdayTv;
    @BindView(R.id.bottomsheet)
    BottomSheetLayout bottomSheetLayout;
    @BindView(R.id.personal_avatar_relayout)
    RelativeLayout personalAvatarRelayout;
    @BindView(R.id.nickname_relayout_personal)
    RelativeLayout nicknameRelayoutPersonal;
    @BindView(R.id.sex_relayout)
    RelativeLayout sexRelayout;
    @BindView(R.id.height_relayout)
    RelativeLayout heightRelayout;
    @BindView(R.id.weight_relayout)
    RelativeLayout weightRelayout;
    @BindView(R.id.birthday_relayout)
    RelativeLayout birthdayRelayout;
    //单位设置的RadioGroup
    @BindView(R.id.radioGroup_unti)
    RadioGroup radioGroupUnti;
    //单位设置的布局
    @BindView(R.id.personal_UnitLin)
    LinearLayout personalUnitLin;
    @BindView(R.id.radio_km)
    RadioButton radioKm;
    @BindView(R.id.radio_mi)
    RadioButton radioMi;
    private String nickName, sex, height, weight, birthday, flag;
    private DialogSubscriber dialogSubscriber;
    private boolean isSubmit;

    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;
    private ArrayList<String> heightList;
    private ArrayList<String> weightList;

    private int userSex = 1;
    private int userHeight = 170;
    private int userWeitht = 60;


    boolean w30sunit = true;
    private String bleMac;


    private RequestQueue requestQueue;
    private RequestPressent requestPressent;


    private Uri mCutUri;


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        requestPressent.detach();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {
    }


    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences share = getSharedPreferences("nickName", 0);
        String name = share.getString("name", "");
        if(!WatchUtils.isEmpty(name)){
            nicknameTv.setText(name);
        }


    }

    private void setListData() {
        heightList.clear();
        weightList.clear();
        w30sunit = (boolean) SharedPreferenceUtil.get(MyPersonalActivity.this, "w30sunit", true);
        if (w30sunit) {     //公制
            for (int i = 120; i < 231; i++) {
                heightList.add(i + " cm");
            }
            for (int i = 20; i < 200; i++) {
                weightList.add(i + " kg");
            }
        } else {  //英制
            for (int i = 44; i < 100; i++) {
                heightList.add(i + " in");
            }
            for (int i = 20; i < 220; i++) {
                weightList.add(i + " lb");
            }
        }
    }

    @Override
    protected void initViews() {
        tvTitle.setText(R.string.personal_info);
        heightList = new ArrayList<>();
        weightList = new ArrayList<>();
        bleMac = (String) SharedPreferencesUtils.readObject(MyPersonalActivity.this, "mylanya");
        if (!WatchUtils.isEmpty(bleMac) && bleMac.equals("bozlun")) {    //H8手表
            personalUnitLin.setVisibility(View.VISIBLE);
        } else {
            personalUnitLin.setVisibility(View.GONE);
        }
        radioGroupUnti.setOnCheckedChangeListener(new MyCheckChangeListener());
        w30sunit = (boolean) SharedPreferenceUtil.get(MyPersonalActivity.this, "w30sunit", true);
        if (w30sunit) {
            radioKm.setChecked(true);
            radioMi.setChecked(false);
        } else {
            radioKm.setChecked(false);
            radioMi.setChecked(true);
        }

        EventBus.getDefault().register(this);


        //设置选择列表
        setListData();


        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String resultCode = jsonObject.getString("resultCode");
                    System.out.print("resultCode" + resultCode);
                    Log.e("MyPerson", "----resultCode--" + resultCode + "-isSubmit----" + isSubmit);
                    if ("001".equals(resultCode)) {

                    } else {
                        ToastUtil.showShort(MyPersonalActivity.this, getString(R.string.submit_fail));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

    }

    private void getUserInfoData() {
        String url = URLs.HTTPs + URLs.getUserInfo;
        if (requestPressent != null) {
            Gson gson = new Gson();
            HashMap<String, String> map = new HashMap<>();
            map.put("userId", (String) SharedPreferencesUtils.readObject(MyPersonalActivity.this, "userId"));
            String mapJson = gson.toJson(map);
            requestPressent.getRequestJSONObject(1, url, this, mapJson, 11);
        }
    }

    /**
     * 刷新所以得数据（名字和头像）
     */
    public void shuaxin() {
        isSubmit = false;
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", (String) SharedPreferencesUtils.readObject(MyPersonalActivity.this, "userId"));
        String mapjson = gson.toJson(map);
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, this);
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs + URLs.getUserInfo, mapjson);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_personal_info;
    }

    @OnClick({R.id.personal_avatar_relayout, R.id.nickname_relayout_personal, R.id.sex_relayout, R.id.height_relayout, R.id.weight_relayout, R.id.birthday_relayout})
    public void onClick(View view) {
        String userId = (String) SharedPreferencesUtils.readObject(MyPersonalActivity.this, "userId");
        SharedPreferences share = getSharedPreferences("Login_id", 0);
        int isoff = share.getInt("id", 0);
        if (!WatchUtils.isEmpty(userId)) {
            if (userId.equals("9278cc399ab147d0ad3ef164ca156bf0")) {  //判断是否是游客身份，如果是游客身份无权限修改信息
                ToastUtil.showToast(MyPersonalActivity.this, MyPersonalActivity.this.getResources().getString(R.string.noright));
            } else {
                switch (view.getId()) {
                    case R.id.personal_avatar_relayout:
                        if (AndPermission.hasAlwaysDeniedPermission(MyPersonalActivity.this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            chooseImgForUserHead(); //选择图片来源
                        } else {
                            AndPermission.with(MyPersonalActivity.this)
                                    .runtime()
                                    .permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    .onGranted(new Action<List<String>>() {
                                        @Override
                                        public void onAction(List<String> data) {

                                        }
                                    })
                                    .onDenied(new Action<List<String>>() {
                                        @Override
                                        public void onAction(List<String> data) {

                                        }
                                    }).start();

                        }
                        break;
                    case R.id.nickname_relayout_personal:
                        startActivity(new Intent(MyPersonalActivity.this, ModifyNickNameActivity.class));
                        break;
                    case R.id.sex_relayout:
                        showSexDialog();
                        break;
                    case R.id.height_relayout:      //身高
                        if (w30sunit) { //公制
                            ProfessionPick professionPopWin = new ProfessionPick.Builder(MyPersonalActivity.this, new ProfessionPick.OnProCityPickedListener() {
                                @Override
                                public void onProCityPickCompleted(String profession) {
                                    flag = "height";
                                    heightTv.setText(profession);
                                    height = profession.substring(0, 3);
                                    modifyPersonData(height);
                                }
                            }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                                    .textCancel(getResources().getString(R.string.cancle))
                                    .btnTextSize(16) // button text size
                                    .viewTextSize(25) // pick view text size
                                    .colorCancel(Color.parseColor("#999999")) //color of cancel button
                                    .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                                    .setProvinceList(heightList) //min year in loop
                                    .dateChose("170 cm") // date chose when init popwindow
                                    .build();
                            professionPopWin.showPopWin(MyPersonalActivity.this);
                        } else {      //英制
                            ProfessionPick professionPopWin = new ProfessionPick.Builder(MyPersonalActivity.this, new ProfessionPick.OnProCityPickedListener() {
                                @Override
                                public void onProCityPickCompleted(String profession) {
                                    heightTv.setText(profession+"");
                                    String tmpHeight = StringUtils.substringBefore(profession, "in").trim();
                                    Log.e(TAG, "---tmpHeight--" + tmpHeight);
                                    flag = "height";
                                    //height = profession.substring(0, 3);
                                    //1,英寸转cm
                                    double tmpCal = WatchUtils.mul(Double.valueOf(tmpHeight), 2.5);
                                    //截取小数点前的数据
                                    int beforeTmpCal = Integer.valueOf(StringUtils.substringBefore(String.valueOf(tmpCal), ".").trim());
                                    //截取小数点后的数据
                                    String afterTmpCal = StringUtils.substringAfter(String.valueOf(tmpCal), ".").trim();
                                    //判断小数点后一位是否》=5
                                    int lastAterTmpCal = Integer.valueOf(afterTmpCal.length() >= 1 ? afterTmpCal.substring(0, 1) : "0");
                                    Log.e(TAG, "----lastAterTmpCal--=" + lastAterTmpCal);
                                    if (lastAterTmpCal >= 5) {
                                        height = (beforeTmpCal + 1) + "";
                                    } else {
                                        height = beforeTmpCal + "";
                                    }
                                    Log.e(TAG, "---tmpHeight-height-" + height);
                                    modifyPersonData(height);
                                }
                            }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                                    .textCancel(getResources().getString(R.string.cancle))
                                    .btnTextSize(16) // button text size
                                    .viewTextSize(25) // pick view text size
                                    .colorCancel(Color.parseColor("#999999")) //color of cancel button
                                    .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                                    .setProvinceList(heightList) //min year in loop
                                    .dateChose("67 in") // date chose when init popwindow
                                    .build();
                            professionPopWin.showPopWin(MyPersonalActivity.this);
                        }

                        break;
                    case R.id.weight_relayout:  //体重
                        if (w30sunit) { //公制
                            ProfessionPick weightPopWin = new ProfessionPick.Builder(MyPersonalActivity.this, new ProfessionPick.OnProCityPickedListener() {
                                @Override
                                public void onProCityPickCompleted(String profession) {
                                    weightTv.setText(profession+"");
                                    flag = "weight";
                                    weight = profession.substring(0, 3);
                                    modifyPersonData(weight);
                                }
                            }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                                    .textCancel(getResources().getString(R.string.cancle))
                                    .btnTextSize(16) // button text size
                                    .viewTextSize(25) // pick view text size
                                    .colorCancel(Color.parseColor("#999999")) //color of cancel button
                                    .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                                    .setProvinceList(weightList) //min year in loop
                                    .dateChose("60 kg") // date chose when init popwindow
                                    .build();
                            weightPopWin.showPopWin(MyPersonalActivity.this);
                        } else {
                            //英制体重
                            ProfessionPick weightPopWin = new ProfessionPick.Builder(MyPersonalActivity.this, new ProfessionPick.OnProCityPickedListener() {
                                @Override
                                public void onProCityPickCompleted(String profession) {
                                    weightTv.setText(profession+"");
                                    flag = "weight";
                                    String tmpWeid = StringUtils.substringBefore(profession, "lb").trim();
                                    double calWeid = WatchUtils.mul(Double.valueOf(tmpWeid), 0.454);
                                    //截取小数点前的数据
                                    String beforeCalWeid = StringUtils.substringBefore(String.valueOf(calWeid), ".");
                                    //截取后小数点后的数据
                                    String afterCalWeid = StringUtils.substringAfter(String.valueOf(calWeid), ".");
                                    int lastNum = Integer.valueOf(afterCalWeid.length() >= 1 ? afterCalWeid.substring(0, 1) : "0");
                                    Log.e(TAG, "----lastNum=" + lastNum);
                                    //判断小数点后一位是否大于5
                                    if (lastNum >= 5) {
                                        weight = String.valueOf(Integer.valueOf(beforeCalWeid.trim()) + 1);
                                    } else {
                                        weight = beforeCalWeid.trim();
                                    }
                                    // weight = profession.substring(0, 3);
                                    modifyPersonData(weight);
                                }
                            }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                                    .textCancel(getResources().getString(R.string.cancle))
                                    .btnTextSize(16) // button text size
                                    .viewTextSize(25) // pick view text size
                                    .colorCancel(Color.parseColor("#999999")) //color of cancel button
                                    .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                                    .setProvinceList(weightList) //min year in loop
                                    .dateChose("132 lb") // date chose when init popwindow
                                    .build();
                            weightPopWin.showPopWin(MyPersonalActivity.this);
                        }

                        break;
                    case R.id.birthday_relayout:    //生日

                        DatePick pickerPopWin = new DatePick.Builder(MyPersonalActivity.this, new DatePick.OnDatePickedListener() {
                            @Override
                            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                                birthdayTv.setText(dateDesc+"");
                                flag = "birthday";
                                birthday = dateDesc;
                                modifyPersonData(dateDesc);//
                            }
                        }).textConfirm(getResources().getString(R.string.confirm)) //text of confirm button
                                .textCancel(getResources().getString(R.string.cancle)) //text of cancel button
                                .btnTextSize(16) // button text size
                                .viewTextSize(25) // pick view text size
                                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                                .minYear(1950) //min year in loop
                                .maxYear(2020) // max year in loop
                                .dateChose("2000-06-15") // date chose when init popwindow
                                .build();
                        pickerPopWin.showPopWin(MyPersonalActivity.this);
                        break;
                }

            }
        }

    }

    //选择图片
    private void chooseImgForUserHead() {
        MenuSheetView menuSheetView =
                new MenuSheetView(MyPersonalActivity.this, MenuSheetView.MenuType.LIST, R.string.select_photo, new MenuSheetView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (bottomSheetLayout.isSheetShowing()) {
                            bottomSheetLayout.dismissSheet();
                        }
                        switch (item.getItemId()) {
                            case R.id.take_camera:
                                cameraPic();
                                break;
                            case R.id.take_Album:   //相册
                                getImage(PickerBuilder.SELECT_FROM_GALLERY);
//                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                intent.setType("image/*");
//                                startActivityForResult(intent,120);
                                break;
                            case R.id.cancle:
                                break;
                        }
                        return true;
                    }
                });
        menuSheetView.inflateMenu(R.menu.menu_takepictures);
        bottomSheetLayout.showWithSheetView(menuSheetView);
    }

    private void showSexDialog() {
        new MaterialDialog.Builder(MyPersonalActivity.this)
                .title(R.string.select_sex)
                .items(R.array.select_sex)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        //0表示男,1表示女
                        if (which == 0) {
                            sex = "M";
                            sexTv.setText(getResources().getString(R.string.sex_nan));
                        } else {
                            sex = "F";
                            sexTv.setText(getResources().getString(R.string.sex_nv));
                        }
                        flag = "sex";
                        modifyPersonData(sex);
                        return true;
                    }
                })
                .positiveText(R.string.select)
                .show();
    }

    //相册选择
    private void getImage(int type) {
        new PickerBuilder(MyPersonalActivity.this, type)
                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                    @Override
                    public void onImageReceived(Uri imageUri) {
                        //设置头像
                        RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true);
                        Glide.with(MyPersonalActivity.this).
                                load(imageUri).apply(mRequestOptions).into(mineLogoIv);
                        uploadPic(ImageTool.getRealFilePath(MyPersonalActivity.this, imageUri),1);
                    }
                })
                .setImageName("headImg")
                .setImageFolderName("NewBluetoothStrap")
                .setCropScreenColor(Color.CYAN)
                .setOnPermissionRefusedListener(new PickerBuilder.onPermissionRefusedListener() {
                    @Override
                    public void onPermissionRefused() {
                    }
                })
                .start();
    }

    //上传头像图片
    private void uploadPic(String filePath,int flag) {
        Log.e(TAG,"----上传图片="+filePath);
        isSubmit = false;
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", Common.customer_id);
        if(flag == 0){
            map.put("image", filePath);
        }else{
            map.put("image", ImageTool.GetImageStr(filePath));
        }
        String mapjson = gson.toJson(map);
        Log.e(TAG,"----上传图片mapjson="+mapjson);
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, MyPersonalActivity.this);
        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.ziliaotouxiang, mapjson);
    }

    //完善用户资料
    private void modifyPersonData(String val) {
        isSubmit = true;
        Gson gson = new Gson();
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", Common.customer_id);
        map.put(flag, val);
        String mapjson = gson.toJson(map);
        Log.e(TAG, "-----mapJson=" + mapjson);
        dialogSubscriber = new DialogSubscriber(subscriberOnNextListener, MyPersonalActivity.this);
        OkHttpObservable.getInstance().getData(dialogSubscriber, URLs.HTTPs + URLs.yonghuziliao, mapjson);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);


        requestQueue = NoHttp.newRequestQueue(1);
        requestPressent = new RequestPressent();
        requestPressent.attach(this);


        AndPermission.with(MyPersonalActivity.this)
                .runtime()
                .permission(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {

                    }
                }).start();


        //获取用户数据
        getUserInfoData();
    }

    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        Log.e(TAG, "----obj=" + object.toString());
        if (what == 1) {
            showUserInfo(object.toString());
        }
    }


    @Override
    public void failedData(int what, Throwable e) {
        Log.e(TAG, "----fail=" + e.getMessage());
    }

    @Override
    public void closeLoadDialog(int what) {

    }

    class MyCheckChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (group.getId()) {
                case R.id.radioGroup_unti:
                    if (checkedId == R.id.radio_km) {
                        //  MyApp.getmW30SBLEManage().setUnit(1);// 0=英制，1=公制
                        SharedPreferenceUtil.put(MyPersonalActivity.this, "w30sunit", true);
                        setListData();
                    } else if (checkedId == R.id.radio_mi) {
                        // MyApp.getmW30SBLEManage().setUnit(0);// 0=英制，1=公制
                        SharedPreferenceUtil.put(MyPersonalActivity.this, "w30sunit", false);
                        setListData();
                    }
                    break;
            }
        }
    }


    //显示用户信息
    private void showUserInfo(String userData) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(userData);
            String jsonObjectb = jsonObject.getString("userInfo");
            Log.e(TAG, "------else---" + jsonObjectb);
            //保存用户信息
            SharedPreferencesUtils.saveObject(MyPersonalActivity.this, "saveuserinfodata", jsonObjectb);
            JSONObject jsonObjectbV = new JSONObject(jsonObjectb);
            sex = jsonObjectbV.getString("sex").toString();
            userInfo.setSex(sex);
            if ("M".equals(sex)) {
                userSex = 1;
                sexTv.setText(getResources().getString(R.string.sex_nan));
            } else if ("F".equals(sex)) {
                sexTv.setText(getResources().getString(R.string.sex_nv));
                userSex = 2;
            }
            birthday = jsonObjectbV.getString("birthday").toString();
            userInfo.setBirthday(birthday);
            birthdayTv.setText(birthday);


            height = jsonObjectbV.getString("height").toString();
            userInfo.setHeight(height);
            if (w30sunit) { //公制
                if(height.contains("cm")){
                    heightTv.setText(height);
                }else{
                    heightTv.setText(height+"cm");
                }

            } else {
                int tmpuserHeight;
                if (height.contains("cm")) {
                    tmpuserHeight = Integer.valueOf(height.substring(0, height.length() - 2).trim());
                } else {
                    tmpuserHeight = Integer.valueOf(height.trim());
                }
                double showTmpHe = WatchUtils.mul(Double.valueOf(tmpuserHeight), 0.4);
                //截取的小数点前部分
                int tmpBeforeHe = Integer.valueOf(StringUtils.substringBefore(String.valueOf(showTmpHe), "."));
                String afterTmpH = StringUtils.substringAfter(String.valueOf(showTmpHe), ".").trim();
                //截取的小数点后部分
                int tmpAftereHe = Integer.valueOf(afterTmpH.length() >= 1 ? afterTmpH.substring(0, 1) : "0");
                //判断截取小数点后一位是否大于5
                if (tmpAftereHe >= 5) {
                    heightTv.setText(StringUtils.substringBefore(String.valueOf(tmpBeforeHe + 1), ".")+"in");
                } else {
                    heightTv.setText(StringUtils.substringBefore(String.valueOf(showTmpHe), ".")+"in");
                }
            }

            SharedPreferencesUtils.setParam(MyPersonalActivity.this, "userheight", StringUtils.substringBefore(height, "cm"));

            weight = jsonObjectbV.getString("weight").toString();
            userInfo.setHeight(weight);
            if (w30sunit) { //公制
                if(weight.contains("kg")){
                    weightTv.setText(weight);
                }else{
                    weightTv.setText(weight+"kg");
                }

            } else {
                int tmpWid;
                //体重
                if (weight.contains("kg")) {
                    tmpWid = Integer.valueOf(weight.trim().substring(0, weight.length() - 2).trim());
                } else {
                    tmpWid = Integer.valueOf(weight.trim());
                }
                double showWid = WatchUtils.mul(Double.valueOf(tmpWid), 2.2);
                //截取小数点前的数据
                String beforeShowWid = StringUtils.substringBefore(String.valueOf(showWid), ".");

                //截取小数点后的数据
                String afterShowWid = StringUtils.substringAfter(String.valueOf(showWid), ".");
                //小数点后一位
                int lastWidNum = Integer.valueOf(afterShowWid.length() >= 1 ? afterShowWid.substring(0, 1) : "0");
                //判断小数点后一位是否》=5
                if (lastWidNum >= 5) {
                    weightTv.setText((Integer.valueOf(beforeShowWid) + 1) + "lb");
                } else {
                    weightTv.setText("" + beforeShowWid + "lb");
                }
            }

            nickName = jsonObjectbV.getString("nickName").toString();
            userInfo.setNickName(nickName);
            nicknameTv.setText(nickName);

            //年龄
            int age = WatchUtils.getAgeFromBirthTime(birthday);  //年龄
            //身高
            if (height.contains("cm")) {
                userHeight = Integer.valueOf(height.substring(0, height.length() - 2).trim());
            } else {
                userHeight = Integer.valueOf(height.trim());
            }
            //体重
            if (weight.contains("kg")) {
                userWeitht = Integer.valueOf(weight.trim().substring(0, weight.length() - 2).trim());
            } else {
                userWeitht = Integer.valueOf(weight.trim());
            }
            String imageUrl = jsonObjectbV.getString("image");
            if (!WatchUtils.isEmpty(imageUrl)) {
                SharedPreferencesUtils.saveObject(MyPersonalActivity.this, "Inmageuil", imageUrl);
                userInfo.setImage(imageUrl);
                //设置头像
                RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(true);
                Glide.with(MyPersonalActivity.this).load(imageUrl).
                        apply(mRequestOptions).into(mineLogoIv);
            }

            /**
             * 设置用户资料
             *
             * @param isMale 1:男性 ; 2:女性
             * @param age    年龄
             * @param hight  身高cm
             * @param weight 体重kg
             */
            SharedPreferenceUtil.put(MyPersonalActivity.this, "user_height", userHeight);
            SharedPreferenceUtil.put(MyPersonalActivity.this, "user_weight", userWeitht);
            /**
             * 设置用户资料
             *
             * @param isMale 1:男性 ; 2:女性
             * @param age    年龄
             * @param hight  身高cm
             * @param weight 体重kg
             */
            MyApp.getmW30SBLEManage().setUserProfile(userSex, age, userHeight, userWeitht);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG,"-----result-="+requestCode+"--resu="+resultCode);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case 120: //从相册图片后返回的uri
                    //启动裁剪
                    if(data != null){
                        handlerImageOnKitKat(data);
                    }

                    //startActivityForResult(CutForPhoto(data.getData()),111);
                    break;
                case 1001: //相机返回的 uri
                    //启动裁剪
                    String path = getExternalCacheDir().getPath();
                    Log.e(TAG,"----裁剪path="+path);
                    String name = "output.png";
                    startActivityForResult(CutForCamera(path,name),111);
                    break;
                case 111:
                    try {
                        //获取裁剪后的图片，并显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(
                                this.getContentResolver().openInputStream(mCutUri));
                        //showImg.setImageBitmap(bitmap);
                        mineLogoIv.setImageBitmap(bitmap);

//                        RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
//                                .skipMemoryCache(true);
//                        Glide.with(MyPersonalActivity.this).
//                                load(mCutUri).apply(mRequestOptions).into(mineLogoIv);
                        //uploadPic(ImageTool.getRealFilePath(MyPersonalActivity.this, mCutUri));
                        uploadPic(Base64BitmapUtil.bitmapToBase64(bitmap),0);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

    }
    /**
     * 打开相机
     */
    private void cameraPic() {
        //创建一个file，用来存储拍照后的照片
        File outputfile = new File(getExternalCacheDir().getPath(),"output.png");
        try {
            if (outputfile.exists()){
                outputfile.delete();//删除
            }
            outputfile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Uri imageuri ;
        if (Build.VERSION.SDK_INT >= 24){
            imageuri = FileProvider.getUriForFile(MyPersonalActivity.this,
                    "com.example.bozhilun.android.fileprovider", //可以是任意字符串
                    outputfile);
        }else{
            imageuri = Uri.fromFile(outputfile);
        }
        //启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageuri);
        startActivityForResult(intent,1001);
    }


    /**
     * 拍照之后，启动裁剪
     * @param camerapath 路径
     * @param imgname img 的名字
     * @return
     */

    private Intent CutForCamera(String camerapath,String imgname) {
        try {
            //设置裁剪之后的图片路径文件
            File cutfile = new File(getExternalCacheDir().getPath(),
                    "cutcamera.png"); //随便命名一个
            if (cutfile.exists()){ //如果已经存在，则先删除,这里应该是上传到服务器，然后再删除本地的，没服务器，只能这样了
                cutfile.delete();
            }
            cutfile.createNewFile();
            //初始化 uri
            Uri imageUri = null; //返回来的 uri
            Uri outputUri = null; //真实的 uri
            Intent intent = new Intent("com.android.camera.action.CROP");
            //拍照留下的图片
            File camerafile = new File(camerapath,imgname);
            if (Build.VERSION.SDK_INT >= 24) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                imageUri = FileProvider.getUriForFile(MyPersonalActivity.this,
                        "com.example.bozhilun.android.fileprovider",
                        camerafile);
            } else {
                imageUri = Uri.fromFile(camerafile);
            }
            outputUri = Uri.fromFile(cutfile);
            //把这个 uri 提供出去，就可以解析成 bitmap了
            mCutUri = outputUri;
            // crop为true是设置在开启的intent中设置显示的view可以剪裁
            intent.putExtra("crop",true);
            // aspectX,aspectY 是宽高的比例，这里设置正方形
            intent.putExtra("aspectX",1);
            intent.putExtra("aspectY",1);
            //设置要裁剪的宽高
            intent.putExtra("outputX", 150);
            intent.putExtra("outputY",150);
            intent.putExtra("scale",true);
            //如果图片过大，会导致oom，这里设置为false
            intent.putExtra("return-data",false);
            if (imageUri != null) {
                intent.setDataAndType(imageUri, "image/*");
            }
            if (outputUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            }
            intent.putExtra("noFaceDetection", true);
            //压缩图片
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            return intent;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 图片裁剪
     * @param uri
     * @return
     */
    private Intent CutForPhoto(Uri uri) {
        Log.e(TAG,"-----相册选择uri="+uri);
        try {
            //直接裁剪
            Intent intent = new Intent("com.android.camera.action.CROP");
            //设置裁剪之后的图片路径文件
            File cutfile = new File(getExternalCacheDir().getPath(),
                    "cutcamera.png"); //随便命名一个
            if (cutfile.exists()){ //如果已经存在，则先删除,这里应该是上传到服务器，然后再删除本地的，没服务器，只能这样了
                cutfile.delete();
            }
            cutfile.createNewFile();
            //初始化 uri
            Uri imageUri = uri; //返回来的 uri
            Uri outputUri = null; //真实的 uri
            Log.d(TAG, "CutForPhoto: "+cutfile);
            outputUri = Uri.fromFile(cutfile);
            mCutUri = outputUri;
            Log.d(TAG, "mCameraUri: "+mCutUri);
            // crop为true是设置在开启的intent中设置显示的view可以剪裁
            intent.putExtra("crop",true);
            // aspectX,aspectY 是宽高的比例，这里设置正方形
            intent.putExtra("aspectX",1);
            intent.putExtra("aspectY",1);
            //设置要裁剪的宽高
            intent.putExtra("outputX", 150); //200dp
            intent.putExtra("outputY",150);
            intent.putExtra("scale",true);
            //如果图片过大，会导致oom，这里设置为false
            intent.putExtra("return-data",false);
            if (imageUri != null) {
                intent.setDataAndType(imageUri, "image/*");
            }
            if (outputUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            }
            intent.putExtra("noFaceDetection", true);
            //压缩图片
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            return intent;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void handlerImageOnKitKat(Intent data){
        String imagePath=null;
        Uri uri=data.getData();
        if(DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的Uri,则通过document id处理
            String docId= DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docId.split(":")[1];//解析出数字格式的id
                String selection=MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的URI，则使用普通方式处理
            imagePath=getImagePath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的Uri,直接获取图片路径即可
            imagePath=uri.getPath();
        }
        Log.e(TAG,"---imagePath="+imagePath);

        if(imagePath != null){
            //CutForPhoto(Uri.fromFile(new File(imagePath)));

            startActivityForResult(CutForPhoto(Uri.fromFile(new File(imagePath))),111);
        }

    }


    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

}
