package com.example.bozhilun.android.activity.wylactivity;

import android.content.Intent;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.base.BaseActivity;
import com.linj.FileOperateUtil;
import com.linj.album.view.AlbumViewPager;
import com.linj.album.view.AlbumViewPager.OnPlayVideoListener;
import com.linj.album.view.MatrixImageView.OnSingleTapListener;
import com.linj.video.view.VideoPlayerContainer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @ClassName: W30SAlbumItemAty
 * @Description:相册图片大图Activity 包含图片编辑功能
 */
public class AlbumItemActivity extends BaseActivity implements OnSingleTapListener, OnPlayVideoListener {
    public final static String TAG = "AlbumDetailAty";
    private String mSaveRoot;
    AlbumViewPager mViewPager;//显示大图
    @BindView(R.id.albumviewpager)
    AlbumViewPager mAlbumView;

    @BindView(R.id.videoview)
    VideoPlayerContainer mContainer;
    @BindView(R.id.header_bar_photo_back)
    ImageView mBackView;
    @BindView(R.id.header_bar_photo_to_camera)
    ImageView mCameraView;
    @BindView(R.id.header_bar_photo_count)
    TextView mCountView;
    @BindView(R.id.album_item_header_bar)
    View mHeaderBar;
    @BindView(R.id.album_item_bottom_bar)
    View mBottomBar;
    @BindView(R.id.delete)
    Button mDeleteButton;
    @BindView(R.id.edit)
    Button mEditButton;


    @Override
    protected int getStatusBarColor() {
        return -1;
    }

    protected void initViews() {
        try {
            mViewPager = (AlbumViewPager) findViewById(R.id.albumviewpager);
            mSaveRoot = "image/*";
            mViewPager.setOnPageChangeListener(pageChangeListener);
            mViewPager.setOnSingleTapListener(this);
            mViewPager.setOnPlayVideoListener(this);
            String currentFileName = null;
            if (getIntent().getExtras() != null)
                currentFileName = getIntent().getExtras().getString("path");
            if (!WatchUtils.isEmpty(currentFileName)) {
                File file = new File(currentFileName);
                currentFileName = file.getName();
                if (currentFileName.indexOf(".") > 0)
                    currentFileName = currentFileName.substring(0, currentFileName.lastIndexOf("."));
            }
            loadAlbum(mSaveRoot, currentFileName);
        }catch (Exception e){
            e.getMessage();
        }
    }

    protected int getContentViewId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.activity_albumitem;
    }


    /**
     * 加载图片
     *
     * @param rootPath 图片根路径
     */
    public void loadAlbum(String rootPath, String fileName) {
        //获取根目录下缩略图文件夹
        String folder = FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_IMAGE, rootPath);
        String thumbnailFolder = FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_THUMBNAIL, rootPath);
        //获取图片文件大图
        List<File> imageList = FileOperateUtil.listFiles(folder, ".jpg");
        //获取视频文件缩略图
        List<File> videoList = FileOperateUtil.listFiles(thumbnailFolder, ".jpg", "video");
        List<File> files = new ArrayList<File>();
        //将视频文件缩略图加入图片大图列表中
        if (videoList != null && videoList.size() > 0) {
            files.addAll(videoList);
        }
        if (imageList != null && imageList.size() > 0) {
            files.addAll(imageList);
        }
        FileOperateUtil.sortList(files, false);
        if (files.size() > 0) {
            List<String> paths = new ArrayList<String>();
            int currentItem = 0;
            for (File file : files) {
                if (fileName != null && file.getName().contains(fileName))
                    currentItem = files.indexOf(file);
                paths.add(file.getAbsolutePath());
            }
            mViewPager.setAdapter(mViewPager.new ViewPagerAdapter(paths, AlbumItemActivity.this));
            mViewPager.setCurrentItem(currentItem);
            mCountView.setText((currentItem + 1) + "/" + paths.size());
        } else {
            mCountView.setText("0/0");
        }
    }


    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            if (mViewPager.getAdapter() != null) {
                String text = (position + 1) + "/" + mViewPager.getAdapter().getCount();
                mCountView.setText(text);
            } else {
                mCountView.setText("0/0");
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }
    };

    @Override
    public void onSingleTap() {
        if (mHeaderBar.getVisibility() == View.VISIBLE) {
            AlphaAnimation animation = new AlphaAnimation(1, 0);
            animation.setDuration(300);
            mHeaderBar.startAnimation(
                    animation);
            mBottomBar.startAnimation(animation);
            mHeaderBar.setVisibility(View.GONE);
            mBottomBar.setVisibility(View.GONE);
        } else {
            AlphaAnimation animation = new AlphaAnimation(0, 1);
            animation.setDuration(300);
            mHeaderBar.startAnimation(animation);
            mBottomBar.startAnimation(animation);
            mHeaderBar.setVisibility(View.VISIBLE);
            mBottomBar.setVisibility(View.VISIBLE);
        }
    }


    @OnClick({R.id.header_bar_photo_back, R.id.header_bar_photo_to_camera, R.id.delete, R.id.edit})
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.header_bar_photo_back:
                finish();
                break;
            case R.id.header_bar_photo_to_camera:
                startActivity(new Intent(this, CameraActivity.class));
                break;
            case R.id.delete:
                String result = mViewPager.deleteCurrentPath();
                if (result != null)
                    mCountView.setText(result);
                mViewPager.postInvalidate();//刷新
                break;
            case R.id.edit:
                mContainer.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mContainer.getVisibility() == View.VISIBLE)
            mContainer.stopPlay();
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        if (mContainer.getVisibility() == View.VISIBLE)
            mContainer.stopPlay();
        super.onStop();
    }

    @Override
    public void onPlay(String path) {
        // TODO Auto-generated method stub
        try {
            mContainer.playVideo(path);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
