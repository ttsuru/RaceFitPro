package com.example.bozhilun.android.b30.b30view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/1.
 */

public class B30CusHeartView extends View {

    private static final String TAG = "B30CusHeartView";

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

    //无数据时显示No data 的画笔
    private Paint emptyPaint;

    private String[] timeStr = new String[]{"00:00","03:00","06:00","09:00","12:00","15:00","18:00","21:00","23:59"};
    private float txtCurrentWidth;
    private List<Map<Float,Float>> listMap = new ArrayList<>();
    private Path path;


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
            height = typedArray.getDimension(R.styleable.B30CusHeartView_parentHeight,dp2px(120));
            pointColor = typedArray.getColor(R.styleable.B30CusHeartView_pointColor,0);
            recfColor = typedArray.getColor(R.styleable.B30CusHeartView_recfColor,0);
            typedArray.recycle();
        }
        initPaint();
    }

    private void initPaint() {
        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(pointColor);
        txtPaint.setTextSize(20f);
        txtPaint.setStrokeWidth(8f);
        txtPaint.setTextAlign(Paint.Align.CENTER);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(pointColor);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        linPain = new Paint(Paint.ANTI_ALIAS_FLAG);
        linPain.setStyle(Paint.Style.STROKE);
        linPain.setColor(pointColor);
        linPain.setStrokeWidth(3f);

        recfPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        recfPaint.setStrokeWidth(height-1);
        recfPaint.setColor(recfColor);
        recfPaint.setAntiAlias(true);

        path = new Path();

        emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        emptyPaint.setStrokeWidth(5f);
        emptyPaint.setColor(Color.parseColor("#88d785"));
        emptyPaint.setAntiAlias(true);
        emptyPaint.setTextSize(20f);

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
        canvas.translate(0,getHeight());
        canvas.save();
        Log.e("HEART","---改变="+getHeight());
        drawEmptyTxt(canvas);
        //画字
        if(rateDataList!= null && rateDataList.size()>0){
            float mWidth = (float) (width * 0.4 /2 + 5);
            int maxValue = Collections.max(rateDataList);
            float tmpMid = maxValue/height;
            Log.e("HEART","-----mWidth="+mWidth);
            for(int i = 0;i<48;i++){
                if(rateDataList.size()-1>=i){
                    Log.e("HEART","-----value--="+rateDataList.get(i)+"--width="+i*mCurrentWidth+5);
                    if(rateDataList.get(i) != 0){
                        canvas.drawCircle(i==0?5:i*mCurrentWidth+5,-rateDataList.get(i)-150,pointRadio,paint);
                        float pointX = (i==0?5:i*mCurrentWidth+5);
                        float porintY = (-rateDataList.get(i)-150);
                        Map<Float,Float> tmpMap = new HashMap<>();
                        tmpMap.put(pointX,porintY);
                        listMap.add(tmpMap);
                    }

                }

            }


//            for(int j = 0;j<listMap.size();j++){
//                Map<Float,Float> map = listMap.get(j);
//                for(Map.Entry<Float,Float> valueMap : map.entrySet()){
//                    float xValue = valueMap.getKey();
//                    float yValue = valueMap.getValue();
//                    Log.e(TAG,"----xValue="+xValue+"--yValue="+yValue);
//                    if(j == 0){
//                        path.moveTo(xValue,yValue);
//                        path.close();
//                    }else{
//                       // path.lineTo(xValue,yValue);
//                        path.quadTo(xValue,yValue,xValue,yValue);
//                    }
//
//                }
//            }
//
//
//
//            path.close();
//            canvas.drawPath(path,linPain);
            drawTimeText(canvas);
        }


    }
    //绘制空数据时显示的文字
    private void drawEmptyTxt(Canvas canvas){
        if(rateDataList== null || rateDataList.size()<=0){
            canvas.drawText("No Data",getWidth()/2-40,-getHeight()/2,emptyPaint);
        }
    }

    //画字
    private void drawTimeText(Canvas canvas) {
        for(int i = 0;i<timeStr.length;i++){

            canvas.drawText(timeStr[i],txtCurrentWidth*i+40,-10,txtPaint);
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
