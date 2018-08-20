package com.example.bozhilun.android.b30.b30view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.load.engine.Initializable;
import com.example.bozhilun.android.R;
import com.veepoo.protocol.model.datas.SleepData;

import java.util.List;

/**
 * Created by Administrator on 2018/8/16.
 */

public class B30CusSleepView extends View {

    private static final String TAG = "B30CusSleepView";
    //深睡颜色
    private int hightSleepColor;
    //浅睡颜色
    private int deepSleepColor;
    //清醒状态颜色
    private int awakeSleepColor;

    private float sleepHeight;

    private Paint hightPaint;
    private Paint deepPaint;
    private Paint awakePaint;
    private Paint emptyPaint;

    private float width;

    private List<Integer> sleepList;

    //#fcd647 清醒  潜水 #a6a8ff 深睡 #b592d6
    public B30CusSleepView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context,attrs);
    }

    public B30CusSleepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context,attrs);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray =context.obtainStyledAttributes(attrs,R.styleable.B30CusSleepView);
        if(typedArray != null){
            hightSleepColor = typedArray.getColor(R.styleable.B30CusSleepView_lightSleepColor,0);
            deepSleepColor = typedArray.getColor(R.styleable.B30CusSleepView_deepSleepColor,0);
            awakeSleepColor = typedArray.getColor(R.styleable.B30CusSleepView_awakeSleepColor,0);
            sleepHeight = typedArray.getDimension(R.styleable.B30CusSleepView_sleepViewHeight,DimenUtil.dp2px(context,180));
            typedArray.recycle();
        }
        initPath();

    }

    private void initPath() {
        hightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        hightPaint.setColor(hightSleepColor);
        hightPaint.setAntiAlias(true);
        hightPaint.setStrokeWidth(5f);


        deepPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        deepPaint.setColor(deepSleepColor);
        deepPaint.setAntiAlias(true);
        deepPaint.setStrokeWidth(5f);


        awakePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        awakePaint.setColor(awakeSleepColor);
        awakePaint.setAntiAlias(true);
        awakePaint.setStrokeWidth(5f);

        emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        emptyPaint.setColor(Color.parseColor("#88d785"));
        emptyPaint.setStrokeWidth(5);
        emptyPaint.setTextSize(20f);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
         width = getMeasuredWidth();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getWidth();
        Log.e(TAG,"---width-="+width);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawEmptyTxt(canvas);
        //坐标点平移
        canvas.translate(0,getHeight());
       // canvas.rotate(270);
        canvas.save();
        if(sleepList != null && sleepList.size()>0){
           float mCurrentWidth = getWidth()/sleepList.size();
            Log.e(TAG,"---size=-"+sleepList.size()+"-mCurrentWidth="+mCurrentWidth);
            for(int i = 0;i<sleepList.size();i++){
                if(sleepList.get(i) == 0){  //浅睡
                    RectF rectF = new RectF(i*mCurrentWidth,-180,i*mCurrentWidth+mCurrentWidth,0);
                    canvas.drawRect(rectF,hightPaint);
                }else if(sleepList.get(i) == 1){    //深睡
                    RectF rectF = new RectF(i*mCurrentWidth,-100,i*mCurrentWidth+mCurrentWidth,0);
                    canvas.drawRect(rectF,deepPaint);
                }else if(sleepList.get(i) == 2){    //清醒
                    RectF rectF = new RectF(i*mCurrentWidth,-300,i*mCurrentWidth+mCurrentWidth,0);
                    canvas.drawRect(rectF,awakePaint);
                }

            }
        }

    }

    public void drawEmptyTxt(Canvas canvas) {
        if(sleepList == null || sleepList.size()<=0){
            canvas.drawText("No Data",getWidth()/2-40,getHeight()/2,emptyPaint);
        }

    }

    public List<Integer> getSleepList() {
        return sleepList;
    }

    public void setSleepList(List<Integer> sleepList) {
        this.sleepList = sleepList;
        invalidate();
    }
}
