package com.donew.util;

import com.qualcomm.VuforiaMedia.DebugLog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class PhotoProxyActivity extends Activity {
	
	private final String TAG = "PhotoProxyActivity";
	
	private boolean need_chop = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		PhotoHelper m_photoHelper = PhotoHelper.Get();
		
		if(m_photoHelper != null){
			int type = this.getIntent().getIntExtra("type", 0);
			need_chop = this.getIntent().getBooleanExtra("chop", false);
			
			DebugLog.LOGI(String.format("%s: type: %d", TAG, type));
			
			if(type == 1){
				m_photoHelper.takePhoto(this);
			}
			else if(type == 2){
				m_photoHelper.openGallery(this);
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		DebugLog.LOGI("onActivityResult called..");
		
		PhotoHelper m_photoHelper = PhotoHelper.Get();
		if(m_photoHelper != null){
			m_photoHelper.onActivityResult(this, need_chop, requestCode, resultCode, data);
		}
		else{
			finish();
		}
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
