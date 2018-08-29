package com.example.bozhilun.android.b30.b30view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import com.example.bozhilun.android.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/4.
 */

public class B30CusBloadView extends View {

    private static final String TAG = "B30CusBloadView";

    //低压的画笔
    private Paint lowPaint;
    //高压的画笔
    private Paint highPaint;
    //连线的画笔
    private Paint linPaint;
    //绘制日期的画笔
    private Paint timePaint;
    //绘制无数据时显示的txt
    private Paint emptyPaint;


    //低压画笔的颜色
    private int lowColor;
    //高压画笔的颜色
    private int hightColor;
    //连线的画笔颜色
    private int linColor;
    //日期的画笔颜色
    private int timeColor;

    //画横线的画笔
    private Paint horiPaint;

    //画刻度尺的画笔
    private Paint scalePaint;

    private int horiColor;
    //高度
    private int height;
    //宽度
    private int width;

    //当前点的宽度
    private float mCurrentWidth;
    //当前时间的宽度
    private float mTimeCurrentWidth;

    private float mTxtCurrentWidth;

    //是否绘制刻度和横线
    private boolean isScal = false;

    //时间点
    private ArrayList<String> timeList = new ArrayList<>();
    //点的集合
    private List<Map<Integer,Integer>> mapList = new ArrayList<>();

    private String[] timeStr = new String[]{"00:00","03:00","06:00","09:00","12:00","15:00","18:00","21:00","23:59"};

    public B30CusBloadView(Context context) {
        super(context);
    }

    public B30CusBloadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
    }

    public B30CusBloadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.B30CusBloadView);
        if(typedArray != null){
            lowColor = typedArray.getColor(R.styleable.B30CusBloadView_lowPointColor,0);
            hightColor = typedArray.getColor(R.styleable.B30CusBloadView_highPointColor,0);
            linColor = typedArray.getColor(R.styleable.B30CusBloadView_linPaintColor,0);
            timeColor = typedArray.getColor(R.styleable.B30CusBloadView_timeColor,0);
            typedArray.recycle();
        }
        initPaint();
    }

    private void initPaint() {
        lowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        lowPaint.setColor(lowColor);
        lowPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        //linPaint.setTextAlign(Paint.Align.CENTER);

        highPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highPaint.setColor(hightColor);
        highPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        highPaint.setTextAlign(Paint.Align.LEFT);

        linPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linPaint.setColor(linColor);
        linPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        linPaint.setStrokeWidth(3f);


        timePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        timePaint.setColor(timeColor);
        timePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        timePaint.setTextSize(16f);
        timePaint.setTextAlign(Paint.Align.LEFT);

        emptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        emptyPaint.setTextAlign(Paint.Align.CENTER);
        emptyPaint.setTextSize(18f);

        horiPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        horiPaint.setStrokeWidth(1f);
        horiPaint.setColor(Color.WHITE);
        horiPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        scalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scalePaint.setStrokeWidth(1f);
        scalePaint.setColor(Color.WHITE);
        scalePaint.setTextSize(20f);
        scalePaint.setStyle(Paint.Style.FILL_AND_STROKE);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        height = getHeight();
        width = getWidth();
        Log.e(TAG,"----onMeasure---="+height+"--wi="+width);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Log.e(TAG,"----onSizeChanged="+w+"--h="+h+"--oldw="+oldh+"-oldh="+oldh);
        height = getHeight();
        width = getWidth();
        Log.e(TAG,"-----onSize="+height+"--wi="+width);
        if(mapList != null && mapList.size()>0){
            mCurrentWidth = width/mapList.size();
            Log.e(TAG,"----mCurrentWidth="+mCurrentWidth);
        }
        if(timeList != null && timeList.size()>0){
            mTimeCurrentWidth = width/timeList.size();
            Log.e(TAG,"---mTimeCurrentWidth-"+mTimeCurrentWidth);
        }
        mTxtCurrentWidth = width/timeStr.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0,getHeight());
        canvas.save();
        Log.e(TAG,"-----onDraw----");
        //绘制横线
        drawHorizonLin(canvas);
        //绘制坐标
       // drawScale(canvas);
        //绘制日期
        drawTimeLin(canvas);
        //绘制点
        drawListPoints(canvas);
        //绘制无数据
        drawEmptyTxt(canvas);
    }

    private void drawScale(Canvas canvas) {
        for(int i = 1;i<=5;i++){
            canvas.drawText(30 * i+"",10,-i*getHeight()/5,scalePaint);
        }
    }

    //绘制横线
    private void drawHorizonLin(Canvas canvas) {
        if(isScal){
            for(int j = 0;j<timeStr.length;j++){
                canvas.drawText(timeStr[j],mTxtCurrentWidth*j+40,-10,timePaint);
            }
            for(int i = 1;i<=5;i++){

                //绘制4条横线
                canvas.drawLine(40,-i*getHeight()/5,getWidth(),-i*getHeight()/5,horiPaint);

                switch (i){
                    case 1:
                        canvas.drawText(30 +"",10,-i*getHeight()/5+5,scalePaint);
                        break;
                    case 2:
                        canvas.drawText(80 +"",10,-i*getHeight()/5+5,scalePaint);
                        break;
                    case 3:
                        canvas.drawText(130 +"",10,-i*getHeight()/5+5,scalePaint);
                        break;
                    case 4:
                        canvas.drawText(180 +"",10,-i*getHeight()/5+5,scalePaint);
                        break;
                    case 5:
                        canvas.drawText(230+"",10,-i*getHeight()/5+5,scalePaint);
                        break;
                }

            }
        }

    }

    private void drawEmptyTxt(Canvas canvas) {
        if(mapList == null || mapList.size()==0){
            canvas.translate(getWidth()/2,-getHeight()/2);
            canvas.drawText("No Data",0,0,emptyPaint);
        }
    }

    //绘制点
    private void drawListPoints(Canvas canvas) {
        //圆的半径
        float mCirRadio = 5;
        if(mapList != null && mapList.size()>0){
            for(int i = 0;i<mapList.size();i++){
                Map<Integer,Integer> mp = mapList.get(i);
                for(Map.Entry<Integer,Integer> mps : mp.entrySet()){
                    Log.e(TAG,"-----高低-="+mps.toString());
                    Log.e(TAG,"-----宽度222="+getWidth()/mapList.size());
                    //绘制低压的点
                    canvas.drawCircle(i==0?40:i * getWidth()/mapList.size() + 10,-mps.getKey(),mCirRadio,lowPaint);
                    //绘制高压的点
                    canvas.drawCircle( i==0?40:i* getWidth()/mapList.size() + 10,-mps.getValue() ,mCirRadio,highPaint);
                    //绘制连线
                    Path path = new Path();
                    path.moveTo(i==0?40:i * getWidth()/mapList.size() + 10,-mps.getKey());
                    path.lineTo(i==0?40:i * getWidth()/mapList.size() + 10,-mps.getValue());
                    path.close();
                    canvas.drawPath(path,linPaint);
                }

            }
        }

    }

    //绘制日期
    private void drawTimeLin(Canvas canvas) {
        if(timeList != null && timeList.size()>0){
            if(timeList.size()<=8){
                for(int i = 0;i<timeList.size();i++){
                  canvas.drawText(timeList.get(i),i*getWidth()/timeList.size()+5,-20,timePaint);
                }
            }else{
                for(int j = 0;j<timeStr.length;j++){
                    canvas.drawText(timeStr[j],mTxtCurrentWidth*j+40,-10,timePaint);
                }
            }

        }
    }

    public ArrayList<String> getTimeList() {
        return timeList;
    }

    public void setTimeList(ArrayList<String> timeList) {
        this.timeList = timeList;
        invalidate();
    }

    public List<Map<Integer, Integer>> getMapList() {
        return mapList;
    }

    public void setMapList(List<Map<Integer, Integer>> mapList) {
        this.mapList = mapList;
        invalidate();
    }

    public boolean isScal() {
        return isScal;
    }

    public void setScal(boolean scal) {
        isScal = scal;
        invalidate();
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
