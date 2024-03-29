package com.example.bozhilun.android.fragment;

import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.TextView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.HeathtestAdapter;
import com.example.bozhilun.android.base.BaseFragment;
import com.example.bozhilun.android.bean.HeartRateList;
import com.example.bozhilun.android.bean.HeartRateone;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.coverflow.ListViewForScrollView;
import com.example.bozhilun.android.net.OkHttpObservable;
import com.example.bozhilun.android.rxandroid.CommonSubscriber;
import com.example.bozhilun.android.rxandroid.SubscriberOnNextListener;
import com.example.bozhilun.android.util.Common;
import com.example.bozhilun.android.util.URLs;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by thinkpad on 2017/3/24.
 * xinlv日
 */

public class HataFragment extends BaseFragment {
    @BindView(R.id.chart_step_shuimia_hata) ColumnChartView shuimianjilu;
    @BindView(R.id.tabs_dtarepot_shuimianone_hata) TabLayout tabs;

    @BindView(R.id.stepval_tv_shuimianone_hata) TextView quantian;
    @BindView(R.id.activityval_tv_shuimianone_hata) TextView Shengshuimian;
    @BindView(R.id.lichengval_tv_shuimianone_hata) TextView Qianshuimian;

    @BindView(R.id.xinlvtest_ListView_report)ListViewForScrollView listview;

    private boolean hasAxes = true;
    private CommonSubscriber commonSubscriber;
    private SubscriberOnNextListener subscriberOnNextListener;
    private HeartRateone dataActivityReport;
    ColumnChartData  stepColumdata;
    SimpleDateFormat sdf;
    Calendar calendar = Calendar.getInstance();
  HeathtestAdapter oxyygenAdapter;
    Handler handler;
    private List<Map<String, Object>> mList;
    private List  mysleep;
    private int shengshui,QIANSHUI,zongshichang;
    private String[] weekDay = new String[] {  "01", "02", "03", "04", "05", "06","07","08","09","10","11","12","13","14","15","16","17","18", "19","20","21","22","23","24"};
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    @Override
    protected void initViews() {

        /**
         * X 轴的显示
         */
        for (int i = 0; i < weekDay.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(weekDay[i]));
        }
        if(null==handler){handler = new Handler() {@Override
        public void handleMessage(android.os.Message msg)  {
            switch (msg.what){
                case  1:
                    listview.invalidate();
                    if(oxyygenAdapter!=null){
                        oxyygenAdapter.notifyDataSetChanged();
                    }break;}}};}

        mysleep=new ArrayList();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onNext(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String loginResult = jsonObject.getString("resultCode");
                    Gson gson = new Gson();
                    mList=new ArrayList<Map<String, Object>>();
                    if ("001".equals(loginResult)) {
                        dataActivityReport = gson.fromJson(result, HeartRateone.class);
                        try{
                            ArrayList<HeartRateList> sportHours = dataActivityReport.getHeartRate();
                            if(null!=dataActivityReport.getHeartRate()){
                                shuimianjilu.setVisibility(View.VISIBLE);
                                List<Column> columns = new ArrayList<>();
                                List<SubcolumnValue> values;
                                String dateNum;
                                int stepNum;
                                Integer timeNum;
                                for (int i = 0; i < 24; ++i) {
                                    values = new ArrayList<>();
                                    for (int j = 0; j < sportHours.size(); ++j) {
                                        dateNum = sportHours.get(j).getRtc();
                                        stepNum = sportHours.get(j).getHeartRate();
                                        timeNum = Integer.valueOf(dateNum.substring(11, 13));

                                        if (i == timeNum) {
                                            values.add(new SubcolumnValue((float) stepNum, getResources().getColor(R.color.chang_white)));
                                    //设置X轴的柱子所对应的属性名称

                                        }
                                    }



                                    //将每个属性的拥有的柱子，添加到Column中
                                    Column column = new Column(values);
                                    //是否显示每个柱子的Lable
                                    column.setHasLabels(true);
                                    //设置每个柱子的Lable是否选中，为false，表示不用选中，一直显示在柱子上
                                    column.setHasLabelsOnlyForSelected(true);
                                    //将每个属性得列全部添加到List中
                                    columns.add(column);
                                }



                                  stepColumdata = new ColumnChartData(columns);
                                if (hasAxes) {
                                    Axis axisX = new Axis();
                                    axisX.setTextSize(6);
                                    axisX.setValues(mAxisXValues);
                                    Axis axisY = new Axis().setHasLines(true);
                                    stepColumdata.setAxisXBottom(axisX);
                                } else {
                                    stepColumdata.setAxisXBottom(null);
                                    stepColumdata.setAxisYLeft(null);
                                }
                                shuimianjilu.setZoomEnabled(false);//设置是否支持缩放
                                stepColumdata .setValueLabelTypeface(Typeface.SANS_SERIF);// 设置数据文字样式
                                stepColumdata.setValueLabelBackgroundAuto(false);// 设置数据背景是否跟随节点颜色
                                stepColumdata.setValueLabelBackgroundColor(R.color.tweet_list_divider);// 设置数据背景颜色
                                stepColumdata.setValueLabelBackgroundEnabled(false);// 设置是否有数据背景
                                stepColumdata.setValueLabelsTextColor(R.color.mpc_end_color);// 设置数据文字颜色
                                stepColumdata.setValueLabelTextSize(12);// 设置数据文字大小
                                shuimianjilu.setColumnChartData(stepColumdata);
                                prepareDataAnimation();
                                shuimianjilu.startDataAnimation();

                                shuimianjilu.setVisibility(View.VISIBLE);
                                String avgHeartRate = jsonObject.getString("avgHeartRate");
                                if(!"{}".equals(avgHeartRate)){
                                    JSONObject AAA=new JSONObject(avgHeartRate);
                                    quantian.setText(AAA.getString("maxHeartRate").toString());
                                    Shengshuimian.setText(AAA.getString("avgHeartRate").toString());
                                   Qianshuimian.setText(AAA.getString("minHeartRate").toString());
                                }else{
                                    quantian.setText("- -");
                                    Shengshuimian.setText("- -");
                                    Qianshuimian.setText("- -");
                                }

                            }

                        String manual=jsonObject.getString("manual");
                            if(!manual.equals("[]")){
                                JSONArray oArrb=new JSONArray(manual);
                                for (int i = 0; i < oArrb.length(); i++) {
                                    JSONObject jo = (JSONObject) oArrb.get(i);
                                    String   date=jo.getString("rtc");	//星期几
                                    String   stepNumbera=jo.getString("heartRate");//心率时间段

                                    Map<String, Object> map2 = new HashMap<String, Object>();
                                    map2.put("title", date);
                                    map2.put("info", stepNumbera+getResources().getString(R.string.BPM));
                                    mList.add(map2);
                                }
                                // 把添加了Map的List和Context传进适配器mListViewAdapter
                                listview.setAdapter(oxyygenAdapter=new HeathtestAdapter(getActivity(), mList));
                                Message message = new Message();message.what = 1;handler.sendMessage(message);
                            }
                        }catch (Exception E){E.printStackTrace();}

                    } else {

                        mList.clear();
                        listview.setAdapter(oxyygenAdapter=new HeathtestAdapter(getActivity(), mList));
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                        shuimianjilu.setVisibility(View.INVISIBLE);
                         quantian.setText("- -");
                        Shengshuimian.setText("- -");
                        Qianshuimian.setText("- -");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };





        List<String> mDataList = Common.getDayListOfMonth(getActivity());
        for (int i = 0; i < mDataList.size(); i++) {
            tabs.addTab(tabs.newTab().setText(mDataList.get(i)));
        }
        tabs.getTabAt(mDataList.size() - 1).select();
        tabs.setScrollPosition(mDataList.size()-1, 1F, true);
        ///移动到最后去
       new Handler().postDelayed((new Runnable() { @Override public void run() {
             tabs.scrollTo(10000,0);} }),5);
        int tabCount = tabs.getTabCount();
        for(int i = 0 ; i < tabCount; i++){
            TabLayout.Tab tab = tabs.getTabAt(i);
            if(tab == null){continue;}
            Class c = tab.getClass(); // 这里使用到反射，拿到Tab对象后获取Class
            try{//    Filed “字段、属性”的意思，c.getDeclaredField 获取私有属性。“mView”是Tab的私有属性名称，类型是 TabView ，TabLayout私有内部类。
                Field field = c.getDeclaredField("mView");
                if(field ==null) {continue;}
                field.setAccessible(true);
                final View view = (View) field.get(tab);
                if(view ==null) {continue;}
                view.setTag(i);
                view.setOnClickListener(new View.OnClickListener() {@Override
                    public void onClick(View v) {
                        int position = (int)view.getTag();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                    sdf.format(new Date());
                    String datas;
                    if(String.valueOf(position+1).length()==1){
                        datas="0"+String.valueOf(position+1);
                    }else{
                        datas=String.valueOf(position+1);
                    }

                    getData(sdf.format(new Date()).toString()+"-"+datas);

                    }});}catch(NoSuchFieldException e) {e.printStackTrace();
            }catch(IllegalAccessException e) {e.printStackTrace();}}


        getData(sdf.format(new Date()).toString());
    }





    private void prepareDataAnimation() {
        for (Column column : stepColumdata.getColumns()) {
            for (SubcolumnValue value : column.getValues()) {
                value.setTarget(Float.valueOf(value.getValue()) );
            }
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_hata;
    }

    private void getData(String data) {
        Gson gson = new Gson();
        HashMap<String, String> map = new HashMap<>();

        map.put("date", data);
        map.put("deviceCode", MyCommandManager.ADDRESS);
        map.put("userId", Common.customer_id);
        String mapjson = gson.toJson(map);
        commonSubscriber = new CommonSubscriber(subscriberOnNextListener, getActivity());
        OkHttpObservable.getInstance().getData(commonSubscriber, URLs.HTTPs+URLs.getHeartD, mapjson);
    }




}
