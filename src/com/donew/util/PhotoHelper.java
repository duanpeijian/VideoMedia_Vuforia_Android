package com.donew.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Debug;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

import java.io.File;

import com.qualcomm.VuforiaMedia.DebugLog;
import com.unity3d.player.UnityPlayer;

public class PhotoHelper {
	
	private static final int OPEN_CAMERA_CODE = 10;
	private static final int CROP_PHOTO_CODE = 12;
	
	static private PhotoHelper _instance = null;
	
	static public PhotoHelper getInstance() {
		if(_instance == null){
			_instance = new PhotoHelper();
		}
		
		return _instance;
	}
	
	static private int _id = 0;
	
	private PhotoHelper() {
		DebugLog.LOGI(String.format("PhotoHelper: %d", ++_id));
		Thread.dumpStack();
	}
	
	private Activity mParentActivity = null;
	private String mGoName;
	
	public void setActivity(Activity newActivity, String goName){
		mParentActivity = newActivity;
		mGoName = goName;
		
		DebugLog.LOGI(String.format("GameObject: %s", goName));
		
		initFile();
	}
	
	public void addImageToGallery(final String filePath){
		ContentValues values = new ContentValues();
		
		values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
		values.put(Images.Media.MIME_TYPE, "image/jpeg");
		values.put(MediaStore.MediaColumns.DATA, filePath);
		
		Context context = mParentActivity;
		context.getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
	}
	
	public void fetchPhoto(String str){
		Intent intent = new Intent(mParentActivity, PhotoProxyActivity.class);
		intent.putExtra("type", str);
		mParentActivity.startActivity(intent);
	}
	
	public void takePhoto(Activity context){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tmpFile));
		context.startActivityForResult(intent, OPEN_CAMERA_CODE);
	}
	
	private void takePhoto(){
		takePhoto(mParentActivity);
	}
	
	private void cropPhoto(Uri uri){
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
        intent.putExtra("output", Uri.fromFile(tmpFile));
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        mParentActivity.startActivityForResult(intent, CROP_PHOTO_CODE);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		DebugLog.LOGI("reach onActivityResult..");
		
		if(resultCode == Activity.RESULT_OK){
			int statusId = resultCode;
			String filePath = null;
			
			try {
				switch(requestCode){
				case OPEN_CAMERA_CODE:
					filePath = tmpFile.getPath();
					//cropPhoto(data.getData());
					break;
				case CROP_PHOTO_CODE:
					filePath = data.getData().getPath();
					break;
				}
			}
			catch(Exception e){
				e.printStackTrace();
				DebugLog.LOGE("onActivityResult error");
			}
			
			String args = String.format("%s&%s", statusId, filePath);
			DebugLog.LOGI(String.format("GameObject: %s, args: %s", mGoName, args));
			
			//UnityPlayer.UnitySendMessage(mGoName, "AndroidCallback", args);
		}
	}
	
	private File tmpFile;
	
	private void initFile() {
		String path = String.format("%s/%s", GetFilesDir(), "ImageStaging");
		File folder = new File(path);
		if(!folder.exists()){
			folder.mkdir();
		}
		
		tmpFile = new File(String.format("%s/%s", path, "tmp"));
	}
	
	private String GetFilesDir(){
    	File file = mParentActivity.getExternalFilesDir(null);
    	if(file == null){
    		file = mParentActivity.getFilesDir();
    		DebugLog.LOGI(String.format("inner file dir: %s", file.getAbsolutePath()));
    	}
    	else{
    		DebugLog.LOGI(String.format("external file dir: %s", file.getAbsolutePath()));
    	}
    	
    	return file.getAbsolutePath();
    }
}