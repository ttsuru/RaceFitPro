package com.example.bozhilun.android.b30;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.b30.adapter.B30BloadDetailAdapter;
import com.example.bozhilun.android.b30.b30view.B30CusBloadView;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchConstants;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.veepoo.protocol.model.datas.HalfHourBpData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/8/6.
 */

/**
 * B30血压详情界面
 */
public class B30BloadDetailActivity extends WatchBaseActivity {

    @BindView(R.id.commentB30BackImg)
    ImageView commentB30BackImg;
    @BindView(R.id.commentB30TitleTv)
    TextView commentB30TitleTv;
    @BindView(R.id.commentB30ShareImg)
    ImageView commentB30ShareImg;
    @BindView(R.id.b30DetailBloadView)
    B30CusBloadView b30DetailBloadView;
    @BindView(R.id.b30DetailLowestBloadTv)
    TextView b30DetailLowestBloadTv;
    @BindView(R.id.b30DetailHeightBloadTv)
    TextView b30DetailHeightBloadTv;
    @BindView(R.id.b30DetailLowestBloadDateTv)
    TextView b30DetailLowestBloadDateTv;
    @BindView(R.id.b30DetailHeightBloadDateTv)
    TextView b30DetailHeightBloadDateTv;
    @BindView(R.id.b30DetailBloadRecyclerView)
    RecyclerView b30DetailBloadRecyclerView;
    @BindView(R.id.bloadCurrDateTv)
    TextView bloadCurrDateTv;

    private B30BloadDetailAdapter b30BloadDetailAdapter;
    private List<HalfHourBpData> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b30_bload_detail);
        ButterKnife.bind(this);

        initViews();
        initData();
    }

    private void initData() {
        List<Map<Integer, Integer>> listMap = WatchConstants.tmpListMap;
        if (listMap != null) {
            b30DetailBloadView.setMapList(listMap);

        }
        //展示数据
        List<HalfHourBpData> tmpList = WatchConstants.tmpBloadList;
        if (tmpList != null) {
            Collections.sort(tmpList, new Comparator<HalfHourBpData>() {
                @Override
                public int compare(HalfHourBpData o1, HalfHourBpData o2) {
                    return o2.getTime().getColck().compareTo(o1.getTime().getColck());
                }
            });
            list.addAll(tmpList);
            b30BloadDetailAdapter.notifyDataSetChanged();

        }
    }

    private void initViews() {
        commentB30BackImg.setVisibility(View.VISIBLE);
        commentB30TitleTv.setText(getResources().getString(R.string.blood));
        commentB30ShareImg.setVisibility(View.VISIBLE);
        bloadCurrDateTv.setText(WatchUtils.getCurrentDate());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        b30DetailBloadRecyclerView.setLayoutManager(layoutManager);
        b30DetailBloadRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        list = new ArrayList<>();
        b30BloadDetailAdapter = new B30BloadDetailAdapter(B30BloadDetailActivity.this, list);
        b30DetailBloadRecyclerView.setAdapter(b30BloadDetailAdapter);

    }

    @OnClick({R.id.commentB30BackImg, R.id.commentB30ShareImg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.commentB30BackImg:

                break;
            case R.id.commentB30ShareImg:

                break;
        }
    }
}
