package com.donew.util;

import com.unity3d.player.UnityPlayerNativeActivity;

import android.content.Intent;
import android.os.Bundle;

import java.lang.Thread.UncaughtExceptionHandler;

import com.qualcomm.VuforiaMedia.DebugLog;;

public class UnityPlayerActivity extends UnityPlayerNativeActivity implements UncaughtExceptionHandler {
	
	private final String TAG = "UnityPlayerActivity";
	
	private PhotoHelper m_photoHelper;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		Thread.setDefaultUncaughtExceptionHandler(this);
		
		m_photoHelper = PhotoHelper.getInstance();
		DebugLog.LOGI("onCreate: get photo instance..");
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		DebugLog.LOGE(String.format("uncaughtException: %s", ex.getMessage()));
		ex.printStackTrace();
	}
	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
//		
//		DebugLog.LOGI("onActivityResult called..");
//		
//		m_photoHelper.onActivityResult(requestCode, resultCode, data);
//	}
	
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
