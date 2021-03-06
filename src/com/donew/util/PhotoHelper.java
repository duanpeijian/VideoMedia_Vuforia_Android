package com.donew.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

import java.io.File;
import java.io.FileOutputStream;

import com.qualcomm.VuforiaMedia.DebugLog;
import com.unity3d.player.UnityPlayer;

public class PhotoHelper {
	
	private static final int OPEN_CAMERA_CODE = 10;
	private static final int OPEN_GALLERY_CODE = 11;
	private static final int CROP_PHOTO_CODE = 12;
	
	static private PhotoHelper _instance = null;
	
	static public PhotoHelper Get() {
		return _instance;
	}
	
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
	
	public void fetchPhoto(int type, boolean chop){
		Intent startIntent = new Intent(mParentActivity, UnityService.class);
		startIntent.putExtra("type", type);
		startIntent.putExtra("chop", chop);
		mParentActivity.startService(startIntent);
		
		startActivity(mParentActivity, type, chop);
	}
	
	private void startActivity(Activity context, int type, boolean chop){
		Intent myIntent = new Intent(context, PhotoProxyActivity.class);
		myIntent.putExtra("type", type);
		myIntent.putExtra("chop", chop);
		context.startActivity(myIntent);
	}
	
	private void takePhoto(){
		takePhoto(mParentActivity);
	}
	
	public void takePhoto(Activity context){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tmpFile));
		context.startActivityForResult(intent, OPEN_CAMERA_CODE);
	}
	
	public void openGallery(Activity context){
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tmpFile));
		context.startActivityForResult(intent, OPEN_GALLERY_CODE);
	}
	
	private void cropPhoto(Activity context, Uri uri){
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
        intent.putExtra("output", Uri.fromFile(tmpFile));
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 128);
        intent.putExtra("outputY", 128);
        context.startActivityForResult(intent, CROP_PHOTO_CODE);
	}
	
	public void onActivityResult(Activity context, boolean chop, int requestCode, int resultCode, Intent data) {
		DebugLog.LOGI("reach onActivityResult..");
		
		if(resultCode == Activity.RESULT_OK){
			int statusId = resultCode;
			String filePath = null;
			
			try {
				switch(requestCode){
				case OPEN_CAMERA_CODE:
					filePath = tmpFile.getPath();
					if(chop){
						cropPhoto(context, Uri.fromFile(tmpFile));
					}
					
					break;
				case OPEN_GALLERY_CODE:
					filePath = getRealPathFromURI(context, data.getData());
					if(chop){
						cropPhoto(context, data.getData());
					}
					
					break;
				case CROP_PHOTO_CODE:
					filePath = tmpFile.getPath();
					break;
				}
			}
			catch(Exception e){
				e.printStackTrace();
				DebugLog.LOGE("onActivityResult error");
			}
			
			if(chop){
				if(requestCode == CROP_PHOTO_CODE){
					String args = String.format("%s&%s", statusId, filePath);
					DebugLog.LOGI(String.format("GameObject: %s, args: %s", mGoName, args));
					completeCallback(context, args);
				}
			}
			else{
				//filePath = getImageThumbnail(filePath, 512, 512);
				filePath = getImageThumbnail(filePath, 256, 256);
				
				String args = String.format("%s&%s", statusId, filePath);
				DebugLog.LOGI(String.format("GameObject: %s, args: %s", mGoName, args));
				completeCallback(context, args);
			}
		}
		else{
			String args = String.format("%s&%s", "-20", "null");
			
			completeCallback(context, args);
		}
	}
	
	private void completeCallback(Activity context, String args){
		context.finish();
		UnityPlayer.UnitySendMessage(mGoName, "AndroidCallback", args);
		Intent stopIntent = new Intent(context, UnityService.class);
		context.stopService(stopIntent);
	}
	
	public String getRealPathFromURI(Activity context, Uri contentUri) {
		DebugLog.LOGI(String.format("content url: %s", contentUri));
		
	    String res = null;
	    String[] proj = { MediaStore.Images.Media.DATA };
	    Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
	    if(cursor != null){
	    	if(cursor.moveToFirst()){
	 	       int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	 	       res = cursor.getString(column_index);
	 	    }
	    	
	    	cursor.close();
	    }
	    else{
	    	res = contentUri.getPath();
	    }
	    
	    return res;
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
	
	private String getImageThumbnail(String imagePath, int width, int height){
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false;
		int h = options.outHeight;
		int w = options.outWidth;
		int beHeight = h / height;
		int beWidth = w / width;
		int be = 1;
		if(beWidth > beHeight){
			be = beWidth;
		} else {
			be = beHeight;
		}
		
		if(be <= 1){
			be = 1;
		}
		
		options.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		
		//tmpFile.getParentFile();
		
		try{
			FileOutputStream output = new FileOutputStream(tmpFile);
			bitmap.compress(CompressFormat.PNG, 50, output);
		}
		catch(Exception e) {
			DebugLog.LOGI(String.format("exception: %s", e));
		}
		
		return tmpFile.getPath();
	}
}
