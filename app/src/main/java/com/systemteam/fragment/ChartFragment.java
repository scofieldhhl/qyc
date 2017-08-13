package com.systemteam.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.systemteam.R;
import com.systemteam.bean.Car;
import com.systemteam.bean.ChartBean;
import com.systemteam.bean.UseRecord;
import com.systemteam.util.DateUtil;
import com.systemteam.util.LogTool;
import com.systemteam.util.Utils;
import com.systemteam.view.DayAxisValueFormatter;
import com.systemteam.view.MyAxisValueFormatter;
import com.systemteam.view.XYMarkerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

import static com.systemteam.util.Constant.CYCLE_DAY_CHART;
import static com.systemteam.util.Constant.MSG_UPDATE_UI;
import static com.systemteam.util.Constant.REQUEST_KEY_BY_CARNO;

//TODO 解决查询完成后不能及时更新图标 2、部分天数的收益统计查询失败处理
public class ChartFragment extends DemoBase implements OnChartValueSelectedListener {
    public final String DATE_START = "%s 00:00:00";
    public final String DATE_END = "%s 23:59:59";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    protected BarChart mChart;
    private float mMonth = 8f;
    private Car mCar;
    private List<ChartBean> mBeanlist;
    private int mDayOfYear;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_UPDATE_UI:
                    setData(mBeanlist);
                    break;
            }
        }
    };

    public ChartFragment(Car car) {
        this.mCar = car;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initView(View view) {
        mChart = (BarChart) view.findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);

        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(mChart);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(mTfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });

        XYMarkerView mv = new XYMarkerView(mContext, xAxisFormatter);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart

//        setData(12, 50);


        /*// setting data
        mSeekBarY.setProgress(50);
        mSeekBarX.setProgress(12);

        mSeekBarY.setOnSeekBarChangeListener(this);
        mSeekBarX.setOnSeekBarChangeListener(this);*/

        // mChart.setDrawLegend(false);
    }

    @Override
    protected void initData() {
        mBeanlist = new ArrayList<>();
        int dayMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        mDayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        LogTool.d("dayOfYear :" + mDayOfYear + "  DayofMonth : " + dayMonth);
        mProgressHelper.showProgressDialog(getString(R.string.initing));
        for(int i = 0; i < CYCLE_DAY_CHART; i++){
            requestEarnByDay((0 - i - 1));
        }
    }

    @Override
    public void onClick(View v) {

    }



    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    private void setData(List<ChartBean> list) {

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (ChartBean bean : list) {
           yVals1.add(new BarEntry(bean.index, bean.value));
        }

        BarDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, getString(R.string.detail_month_eran, (int)CYCLE_DAY_CHART));

            set1.setDrawIcons(false);

            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(mTfLight);
            data.setBarWidth(0.9f);

            mChart.setData(data);
        }
    }

    private void requestEarnByDay(final int day){
        BmobQuery<UseRecord> query = new BmobQuery<>();
        List<BmobQuery<UseRecord>> and = new ArrayList<>();
        //大于00：00：00
        BmobQuery<UseRecord> q1 = new BmobQuery<>();
//        String start = "2017-08-12 00:00:00";
        String strDate = DateUtil.getSomeDay(new Date(), day);
        LogTool.d("strDate : " + strDate);
        String start = String.format(Locale.US, DATE_START, strDate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date  = null;
        try {
            date = sdf.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        q1.addWhereGreaterThanOrEqualTo("createdAt",new BmobDate(date));
        and.add(q1);
        //小于23：59：59
        BmobQuery<UseRecord> q2 = new BmobQuery<>();
//        String end = "2017-08-12 23:59:59";
        String end = String.format(Locale.US, DATE_END, strDate);
        Date date1  = null;
        try {
            date1 = sdf.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        q2.addWhereEqualTo(REQUEST_KEY_BY_CARNO, mCar.getCarNo());
        q2.addWhereLessThanOrEqualTo("createdAt",new BmobDate(date1));
        and.add(q2);
        //添加复合与查询
        query.and(and);
        query.addWhereEqualTo(REQUEST_KEY_BY_CARNO, mCar.getCarNo());
        /*addSubscription(query.findObjects(new FindListener<UseRecord>() {
            @Override
            public void done(List<UseRecord> object, BmobException e) {
                if(e==null){
                    LogTool.d("object：" + object.size());
                }else{
                    LogTool.e("失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        }));*/
        query.sum(new String[] { "earn" });
        addSubscription(query.findStatistics(UseRecord.class,new QueryListener<JSONArray>() {

            @Override
            public void done(JSONArray ary, BmobException e) {
                float earnDay = 0f;
                ChartBean bean = new ChartBean((mDayOfYear + day + 1), earnDay);
                if(e==null){
                    if(ary!=null){//
                        try {
                            JSONObject obj = ary.getJSONObject(0);
                            LogTool.d(obj.toString());
                            double value = Double.valueOf(obj.getString("_sumEarn"));//_(关键字)+首字母大写的列名
                            LogTool.d("value : " + value);
                            earnDay = Utils.formatDouble2(value);
                            LogTool.d("reuslt : " + earnDay);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }else{
                        earnDay = 0;
                    }
                    LogTool.e("done："+ earnDay);
                }else{
                    LogTool.e("失败："+e.getMessage()+","+e.getErrorCode());
                }
                bean.value = earnDay;
                mBeanlist.add(bean);
                if((day + CYCLE_DAY_CHART) == 0){
                    mProgressHelper.dismissProgressDialog();
                    setData(mBeanlist);
                    mHandler.sendEmptyMessage(MSG_UPDATE_UI);
                }
            }
        }));
    }
}
