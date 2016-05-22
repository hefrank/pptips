package com.ppamy.pptips;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.ppamy.pptips.util.Utils;

public class MainApplication extends Application {
    private static MainApplication sApp = null;

    public static MainApplication getApp(){
        return sApp;
    }
    @Override
    protected void attachBaseContext(Context base) {
    	super.attachBaseContext(base);
    	sApp = this;
    }
    
    @Override
    public void onCreate() {
    	super.onCreate();
        Intent itt = new Intent(this,BgService.class);
        startService(itt);
//    	Utils.copyFile("/data/data/com.ppamy.pptips/databases/tips.db", "/sdcard/tips.db");
//    	Utils.copyFile("/sdcard/tips.db","/data/data/com.ppamy.pptips/databases/tips.db");
    }
    
    @Override
    public void onTerminate() {
    	// TODO Auto-generated method stub
    	super.onTerminate();
    }

}
