package com.donew.util;

import com.qualcomm.VuforiaMedia.DebugLog;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UnityService extends Service {
	
	private static final String TAG = "UnityService";
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		DebugLog.LOGI(String.format("%s onCreate", TAG));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		DebugLog.LOGI(String.format("%s onStartCommand", TAG));
		
		Intent startIntent = new Intent(this, PhotoProxyActivity.class);
		startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startIntent.putExtra("type", intent.getStringExtra("type"));
		startActivity(startIntent);
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		DebugLog.LOGI(String.format("%s onDestroy", TAG));
	}
	
}
