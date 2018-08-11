package com.example.bozhilun.android.b30.b30view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.example.bozhilun.android.B18I.b18isupport.Chart;
import com.example.bozhilun.android.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018/8/1.
 */

public class B30CusHeartView extends View {

    //连线的paint
    private Paint linPain;
    //绘制日期的画笔
    private Paint txtPaint;
    //矩形
    private RectF rectF;
    private Paint recfPaint;
    private int recfColor;
    //画笔
    private Paint paint;
    //宽度
    private int width;
    //父控件的高度
    private float height;
    //点的颜色
    private int pointColor;
    //当前点的宽度
    private float mCurrentWidth;
    //点的半径
    private float pointRadio;
     //心率数据集合
    private List<Integer> rateDataList;

    private String[] timeStr = new String[]{"00:00","·","06:00","·","12:00","·","18:00","·","23:59"};
    private float txtCurrentWidth;

    public B30CusHeartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
    }

    public B30CusHeartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray  = context.obtainStyledAttributes(attrs, R.styleable.B30CusHeartView);
        if(typedArray != null){
            height = typedArray.getDimension(R.styleable.B30CusHeartView_parentHeight,dp2px(200));
            pointColor = typedArray.getColor(R.styleable.B30CusHeartView_pointColor,0);
            recfColor = typedArray.getColor(R.styleable.B30CusHeartView_recfColor,0);
            typedArray.recycle();
        }
        initPaint();
    }

    private void initPaint() {
        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(pointColor);
        txtPaint.setTextSize(16f);
        txtPaint.setStrokeWidth(3);
        txtPaint.setTextAlign(Paint.Align.CENTER);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(pointColor);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        linPain = new Paint(Paint.ANTI_ALIAS_FLAG);
        linPain.setStyle(Paint.Style.FILL_AND_STROKE);
        linPain.setStrokeWidth(2f);

        recfPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        recfPaint.setStrokeWidth(height-1);
        recfPaint.setColor(recfColor);
        recfPaint.setAntiAlias(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getWidth();
        Log.e("HEART","--getMeasuredWidth--="+getMeasuredWidth());
        mCurrentWidth = (float) (width /48);
        txtCurrentWidth = width/timeStr.length;
        Log.e("HEART","-----width="+width+"--mCurrentWidth="+mCurrentWidth+"--txtCurrentWidth="+txtCurrentWidth);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画字
        if(rateDataList!= null && rateDataList.size()>0){
            float mWidth = (float) (width * 0.4 /2 + 5);
            int maxValue = Collections.max(rateDataList);
            float tmpMid = maxValue/height;
            Log.e("HEART","-----mWidth="+mWidth);
            for(int i = 0;i<48;i++){
                if(rateDataList.size()-1>=i){
                    Log.e("HEART","-----value--="+rateDataList.get(i)+"--height="+i*mCurrentWidth+5);
                    if(rateDataList.get(i) != 0){
                        canvas.drawCircle(i==0?5:i*mCurrentWidth+5,rateDataList.get(i)+200,pointRadio,paint);
                    }else{
                        canvas.drawCircle(i==0?5:i*mCurrentWidth+5,rateDataList.get(i)+200,-3,paint);
                    }
//                    Path path = new Path();
//                    float x = (i==0?5:i*mCurrentWidth+5);
//                    float y = rateDataList.get(i)+200;
//                    float x2 = (i<47?i+1*mCurrentWidth+5:i*mCurrentWidth+5);
//                    float y2 = (i<47?rateDataList.get(i+1)+200:rateDataList.get(i)+200);
//
//                    Log.e("HEART","-----xy="+x+"y="+y+"-x2="+x2+"-y2="+y2);
//                    path.moveTo(x,y);
//                    path.lineTo(x2,y2);
//                    path.close();
//                    canvas.drawPath(path,linPain);

                }

            }

            drawTimeText(canvas);
        }


    }

    //画字
    private void drawTimeText(Canvas canvas) {
        //
//        rectF = new RectF(0,10,getWidth()/3,height-10);
//        canvas.drawRect(rectF,txtPaint);

        for(int i = 0;i<timeStr.length;i++){

            canvas.drawText(timeStr[i],txtCurrentWidth*i+40,height-10,txtPaint);
        }
    }

    public List<Integer> getRateDataList() {
        return rateDataList;
    }

    public void setRateDataList(List<Integer> rateDataList) {
        this.rateDataList = rateDataList;
        invalidate();
    }

    public float getPointRadio() {
        return pointRadio;
    }

    public void setPointRadio(float pointRadio) {
        this.pointRadio = pointRadio;
    }

    /**
     * dp 2 px
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }
}
