package com.donew.util;

import com.qualcomm.VuforiaMedia.DebugLog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PhotoProxyActivity extends Activity {
	
	private final String TAG = "PhotoProxyActivity";
	
	private PhotoHelper m_photoHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		m_photoHelper = PhotoHelper.getInstance();
		
		String type = this.getIntent().getStringExtra("type");
		
		DebugLog.LOGI(String.format("%s: type: %s", TAG, type));
		
		if(type.equals("1")){
			m_photoHelper.takePhoto(this);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		DebugLog.LOGI("onActivityResult called..");
		
		m_photoHelper.onActivityResult(requestCode, resultCode, data);
		
		finish();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
		DebugLog.LOGW(String.format("%s stopped", TAG));
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		DebugLog.LOGW(String.format("%s destroyed", TAG));
	}
}
