package com.example.bozhilun.android.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.HomeAdapter;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.util.SharedPreferencesUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferenceUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 欢迎页
 */
public class NewGuidActivity extends WatchBaseActivity {


    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);

        initData();


    }

    private void initData() {
        //是否是第一次进入
        SharedPreferencesUtils.setParam(NewGuidActivity.this,"isGuide",true);
        List<View> pageList = createPageList();
        HomeAdapter adapter = new HomeAdapter();
        adapter.setData(pageList);
        viewPager.setAdapter(adapter);
    }

    private final int[] imageIds = {R.mipmap.image_guide_one, R.mipmap.image_guide_two
            ,R.mipmap.image_guide_three};

    @NonNull
    private List<View> createPageList() {
        List<View> pageList = new ArrayList<>();
        //去掉直接用mipmap.image,用添加ImageView方法，防止图片太大要求的内存太多，超出限制出现OOM
        //获取屏幕的默认分辨率
        Display display = getWindowManager().getDefaultDisplay();
        for (int i = 0; i < imageIds.length; i++) {
            ImageView image = new ImageView(this);
            image.setMinimumHeight(display.getHeight());
            image.setMinimumWidth(display.getWidth());
            image.setMaxHeight(display.getHeight());
            image.setMaxHeight(display.getWidth());
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inPurgeable = true;
            opt.inInputShareable = true;
            InputStream is = getResources().openRawResource(
                    imageIds[i] );
            Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
            BitmapDrawable bd = new BitmapDrawable(getResources(), bm);
            image.setBackgroundDrawable(bd);
            pageList.add(createPageViews(image, i));
        }
        return pageList;
    }


    private View createPageViews(ImageView drawable, final int index) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.banner_guide, null,false);
        RelativeLayout activity_guide = (RelativeLayout) contentView.findViewById(R.id.activity_guide);
        if (index == 2) {
            activity_guide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(NewGuidActivity.this, LoginActivity.class));
                    finish();
                }
            });
        }
        activity_guide.addView(drawable);
        return contentView;
    }

}
