package com.example.bozhilun.android.b30.b30minefragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.MyPersonalActivity;
import com.example.bozhilun.android.b30.B30DeviceActivity;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.LazyFragment;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;

import org.json.JSONException;
import org.json.JSONObject;

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

    private RequestPressent requestPressent;

    private AlertDialog.Builder builder;

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
                b30MineDeviceTv.setText("B30");
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
            R.id.b30MineSleepRel, R.id.b30MineUnitRel, R.id.b30MineAboutRel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b30userImageHead: //头像
                startActivity(new Intent(getActivity(), MyPersonalActivity.class));
                break;
            case R.id.b30MineDeviceRel: //设备
                startActivity(new Intent(getActivity(), B30DeviceActivity.class));
                break;
            case R.id.b30MineSportRel:  //运动目标

                break;
            case R.id.b30MineSleepRel:  //睡眠目标

                break;
            case R.id.b30MineUnitRel:   //单位设置
               showUnitDialog();
                break;
            case R.id.b30MineAboutRel:  //关于

                break;
        }
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
