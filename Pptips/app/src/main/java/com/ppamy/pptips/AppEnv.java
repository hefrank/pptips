package com.ppamy.pptips;

import android.content.Intent;

import com.ppamy.pptips.util.Utils;

import java.io.File;

public class AppEnv {

	public static final boolean DEBUG = true;

	public static final String ACTION_TIPS_CHANGED = "com.ppamy.datachanged";

	public static String DIR_IN_SD = "pptips";
	public static String FILE_IN_SD = "backup";

	public static String DB_TIPS_NAME = "tips.db";

	static {
		File file = new File(Utils.getSDPathBySDKApi()+File.separator+DIR_IN_SD);
		if (file == null ||(!file.exists())){
			file.mkdir();
		}

	}

	public static String getBackupFilePath(){
		return Utils.getSDPathBySDKApi()+File.separator+DIR_IN_SD+File.separator+FILE_IN_SD;

	}



}
