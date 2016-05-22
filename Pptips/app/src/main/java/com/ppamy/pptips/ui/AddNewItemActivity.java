package com.ppamy.pptips.ui;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.ppamy.pptips.MainApplication;
import com.ppamy.pptips.R;
import com.ppamy.pptips.datamgr.TipsDataManager;
import com.ppamy.pptips.dbs.tips_dao.Tips;
import com.ppamy.pptips.util.Utils;

/**
 * 新建/修改
 * */
public class AddNewItemActivity extends AppCompatActivity {
	private TextView mTvDate;
	private TipsDataManager mTipsDataManager;
	private EditText mEtSubject;
	private EditText mEtInput;
	private ActionBar mActionBar;
	private Toolbar mToolbar;
//	private View mViewBack;
	private String mStrLocAddr;

	public InputMethodManager mInputMethodManager = null;
	private final static String INTENT_EXTRA_ID = "id";
	private final static String INTENT_EXTRA_TXT = "txt";
	private long mId = 0;
	private String mTxtIn;
	private Tips mTips = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    mInputMethodManager = (InputMethodManager) Utils.getSystemService(Context.INPUT_METHOD_SERVICE);
		mTipsDataManager = TipsDataManager.getInstance();
		setContentView(R.layout.add_new_item);

		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setHomeButtonEnabled(true);

        mEtInput = (EditText) findViewById(R.id.et_txt);
        mEtSubject = (EditText) findViewById(R.id.et_subject);
//        mTvLoc = (TextView) view.findViewById(R.id.tv_loc);
        mTvDate = (TextView) findViewById(R.id.tv_date);

        long dateNow = System.currentTimeMillis();
        mTvDate.setText(DateFormat.format("yyyy-MM-dd hh:mm ", dateNow));


        mLocationClient = new LocationClient(MainApplication.getApp());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        mGeofenceClient = new GeofenceClient(MainApplication.getApp());
        mVibrator =(Vibrator)MainApplication.getApp().getSystemService(Service.VIBRATOR_SERVICE);

        InitLocation();
		mId = getIntent().getLongExtra(INTENT_EXTRA_ID, 0L);
		mTxtIn = getIntent().getStringExtra(INTENT_EXTRA_TXT);
		if (mId > 0){
			mTips =mTipsDataManager.getTipsItemById(mId);
		}else if (!Utils.isEmpty(mTxtIn)){
			mEtInput.setText(mTxtIn);
		}
		if (mTips != null){
			mEtSubject.setText(mTips.getSubject());
			String body = mTips.getBody();
			mEtInput.setText(body);
			mEtInput.setSelection(body.length());
			mToolbar.setTitle(mTips.getLoc_name());
		}
        mLocationClient.start();
	}

	@Override
	public void onResume() {
	    mEtInput.requestFocus();
//	    mInputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//        mInputMethodManager.showSoftInputFromInputMethod(mEtInput.getWindowToken(), 0);
	    super.onResume();
	}
	
	@Override
	public void onPause() {
	    mInputMethodManager.hideSoftInputFromWindow(mEtInput.getWindowToken(), 0);
	    super.onPause();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case android.R.id.home:
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
        saveTips();
		mLocationClient.stop();
	}

    /**保存tips*/
	private void saveTips(){
		String txt = mEtInput.getEditableText().toString();
		String tSubject = mEtSubject.getEditableText().toString();
		boolean hasNew = false;
		if (!Utils.isEmpty(txt)){
            long t = System.currentTimeMillis();
            if (mTips == null ){
				Tips tip = new Tips();
				if (Utils.isEmpty(tSubject)){
					tip.setSubject("无题");
				}else{
					tip.setSubject(tSubject);
				}
				tip.setBody(txt);
				tip.setDate(t);
				tip.setAction_date(t);
				tip.setLoc_name(mStrLocAddr);
				mTipsDataManager.addOrUpdateItem(tip);
				hasNew  = true;

			} else if (mTips != null
							   && (!(txt.equals(mTips.getBody()))
										   || !(tSubject.equals(mTips.getSubject())))) {
				mTips.setBody(txt);
                mTips.setDate(t);
                if (Utils.isEmpty(tSubject)){
                    mTips.setSubject("无题");
                }else{
                    mTips.setSubject(tSubject);
                }
				hasNew  = true;
				mTipsDataManager.addOrUpdateItem(mTips);
			}
		}
		if (hasNew){
			sendBroadcast(new Intent(com.ppamy.pptips.AppEnv.ACTION_TIPS_CHANGED));
		}
	}

	public static void startAddEditItemActivity(Context c,long id){
		Intent intent = new Intent(c,AddNewItemActivity.class);
		if (id > 0){
			intent.putExtra(INTENT_EXTRA_ID, id);
		}
		c.startActivity(intent);
	}

	public static void startAddEditItemActivity(Context c,String txt){
		Intent intent = new Intent(c,AddNewItemActivity.class);
		if (!Utils.isEmpty(txt)){
			intent.putExtra(INTENT_EXTRA_TXT,txt);
		}
		c.startActivity(intent);
	}
	public static void startAddEditItemActivityNew(Context c,String txt){
		Intent intent = new Intent(c,AddNewItemActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (!Utils.isEmpty(txt)){
			intent.putExtra(INTENT_EXTRA_TXT,txt);
		}
		c.startActivity(intent);
	}

	public LocationClient mLocationClient;
	public GeofenceClient mGeofenceClient;
	public MyLocationListener mMyLocationListener;
	public Vibrator mVibrator;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor="gcj02";
	private void InitLocation(){
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);//���ö�λģʽ
		option.setCoorType(tempcoor);//���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
		int span=1000;
		option.setScanSpan(span);//���÷���λ����ļ��ʱ��Ϊ5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}
	
    /**
	 * ʵ��ʵλ�ص�����
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			//Receive Location
			String province = location.getProvince();
			String city = location.getCity();
			String district =location.getDistrict();
			String street = location.getStreet();
			StringBuffer sb = new StringBuffer(256);
			if(!province.equals(city)){
				sb.append(province);
				sb.append(city);
			}else{
				sb.append(city);
			}
			sb.append(district);
			sb.append(street);
			if (!Utils.isEmpty(sb)){
				mStrLocAddr = sb.toString();
				mToolbar.setTitle(mStrLocAddr);
			}
		}
	}
}
