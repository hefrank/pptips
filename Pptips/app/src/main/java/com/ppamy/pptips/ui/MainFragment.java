package com.ppamy.pptips.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.ChartSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.ppamy.pptips.FragmentBase;
import com.ppamy.pptips.MainApplication;
import com.ppamy.pptips.R;
import com.ppamy.pptips.datamgr.TipsDataManager;
import com.ppamy.pptips.dbs.tips_dao.Tips;
import com.ppamy.pptips.util.DataUtils;
import com.ppamy.pptips.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 常态主页面
 */
public class MainFragment extends FragmentBase {
    private TextView mTvInfo;
    private TipsDataManager mTipsDataManager;
    private String mStrLocAddr;
    private LineChartView mChartOne;
    private long enterTime = 0;
    private static final int MAX_DOT = 8;
    private Handler mUiHandler = new Handler();

    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            enterTime = System.currentTimeMillis();
            drawStaticsView1(Utils.getDateStartTimeBeforeNday(enterTime, MAX_DOT - 1));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//	    mInputMethodManager = (InputMethodManager) Utils.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, null);
        mTipsDataManager = TipsDataManager.getInstance();
        mChartOne = (LineChartView) view.findViewById(R.id.linechart);
        mTvInfo = (TextView) view.findViewById(R.id.tv_info);

        long count = mTipsDataManager.getTipsCount();
        if (count > 0) {
            mTvInfo.setText(getString(R.string.tips_schema, count));
        } else {
            mTvInfo.setText(R.string.tips_0_info);
        }
        enterTime = System.currentTimeMillis();
        mUiHandler.post(new Runnable() {
			@Override
			public void run() {
				drawStaticsView1(Utils.getDateStartTimeBeforeNday(enterTime, MAX_DOT - 1));
			}
		});

        getActivity().registerReceiver(mUpdateReceiver, new IntentFilter(com.ppamy.pptips.AppEnv.ACTION_TIPS_CHANGED));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

//		startTime = Utils.getDateStartTime(enterTime);
//		drawStaticsView1(Utils.getDateStartTimeBeforeNday(enterTime, MAX_DOT - 1));

    }

    private void drawStaticsView1(long startTime) {
//		mChartOne.reset();
        //处理数据
        String[] labelsOne = new String[MAX_DOT];
        float[] value = new float[MAX_DOT];
        prepareData(startTime, labelsOne, value);
        LineSet dataset = new LineSet(labelsOne, value);
        dataset.setColor(Utils.getColor(getActivity(), R.color.main_color_yellow))
//                .setFill(Color.parseColor("#a34545"))
                .setDotsRadius(12)
                .setDotsColor(getResources().getColor(R.color.main_color))
                .setSmooth(true);
        ArrayList<ChartSet> lldata = mChartOne.getData();
        if (DataUtils.isEmpty(lldata)) {
            mChartOne.addData(dataset);
        } else {
            mChartOne.updateValues(0, value);
        }
        mChartOne.setBorderSpacing(Tools.fromDpToPx(5))
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setYLabels(AxisController.LabelPosition.NONE)
                .setLabelsColor(Utils.getColor(getActivity(),R.color.main_color))
                .setXAxis(false)
                .setYAxis(false);
        mChartOne.setAxisLabelsSpacing(Tools.fromDpToPx(10));

        Animation anim = new Animation().setStartPoint(-1, 0).setEndAction(null);

        mChartOne.show(anim);
    }

    private void prepareData(long startTime, String[] labelsOne, float[] value) {
        //获取所有的startTime之后的数据map
        List<Tips> datas = mTipsDataManager.getAllDaysAfterTime(startTime);
        Map<String, Integer> dataMap = new HashMap<String, Integer>(MAX_DOT);
        for (Tips tips : datas) {
            long timeTmp = Utils.getDateStartTime(tips.getDate());
            String sdate = Utils.getFormatedDate(MainApplication.getApp(), timeTmp);
            if (dataMap.containsKey(sdate)) {
                int c = dataMap.get(sdate);
                c++;
                dataMap.put(sdate, c);
            } else {
                dataMap.put(sdate, 1);
            }
        }

        for (int i = 0; i < MAX_DOT; i++) {
            long tt = startTime + i * 86400000;
            labelsOne[i] = Utils.getFormatedDate(MainApplication.getApp(), tt);
            Integer c = dataMap.get(labelsOne[i]);
            if (c == null) {
                c = 0;
            }
            value[i] = c;
        }


        if (labelsOne == null || labelsOne.length <= 0) {
            return;
        }
    }

    /**
     * 绘制统计图表
     */
//	private void drawStaticsView() {
//
//		doDataStatics();
//
//		if (mLabelsOne == null ||mLabelsOne.length <= 0){
//			return;
//		}
//		LineSet dataset = new LineSet(mLabelsOne, mValue);
//		dataset.setColor(Color.parseColor("#a34545"))
////                .setFill(Color.parseColor("#a34545"))
//				.setDotsRadius(12)
//				.setDotsColor(getResources().getColor(R.color.main_color))
//				.setSmooth(true);
//		mChartOne.addData(dataset);
//		mChartOne.setBorderSpacing(Tools.fromDpToPx(0))
//				.setXLabels(AxisController.LabelPosition.INSIDE)
//				.setYLabels(AxisController.LabelPosition.NONE)
//				.setLabelsColor(Color.parseColor("#e08b36"))
//				.setXAxis(false)
//				.setYAxis(false);
//
//		Animation anim = new Animation().setStartPoint(-1, 1).setEndAction(null);
//
//		mChartOne.show(anim);
//	}
//
//	/**
//	 * 计算统计数据
//	 * */
//	public void doDataStatics(){
//		Map<String,Integer> dd = mTipsDataManager.getDateCount();
//        int size = dd.size();
//        if (size > 10){
//            size = 10;
//        }
//
//        ArrayList<String> dateKs = mTipsDataManager.getMarkDate();
//		if (size > 0){
//			mLabelsOne = new String[size];
//			mValue = new float[size];
//			int i = size - 1;
//			Integer ic = null;
//			for(String dk:dateKs){
////				Log.e("kkk", "day is dateKs day is " + dk);
//				if (dk == null){
//					dk = "";
//				}
//				ic = dd.get(dk);
//				if(ic == null){
//					ic = 0;
//				}
////				Log.e("kkk", "day is dateKs day count is " + ic);
//				mLabelsOne[i] = dk;
//				mValue[i] = ic;
//				i--;
//                if ( i < 0){
//                    break;
//                }
//			}
//		}
//
//
//	}
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mUpdateReceiver);
    }

    @Override
    public String getTagText() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
