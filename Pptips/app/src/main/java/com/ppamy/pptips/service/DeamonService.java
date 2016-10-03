package com.ppamy.pptips.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.ppamy.pptips.BgService;
import com.ppamy.pptips.IDeamon;
import com.ppamy.pptips.IMainApp;

/**
 * Created by hefrank on 16-9-28.
 */
public class DeamonService extends Service {
    private IBinder mBinder,mToken;
    private IBinder iMainApp;
    private MyConn mMyConn;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mToken = new Binder();
        mBinder = new IDeamon.Stub(){

            @Override
            public void setDeamonToken(IBinder token) throws RemoteException {
                iMainApp = token;
            }

        };
        if (mMyConn == null){
            mMyConn = new MyConn();
        }
        doBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
//        return null;
    }

    private void doBinder(){
        startService(new Intent(DeamonService.this,BgService.class));
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean bb = bindService(new Intent(DeamonService.this, BgService.class),mMyConn, Context.BIND_AUTO_CREATE);
                Log.e("KKK_kkk","bind 主服务 result is:"+bb);
            }
        }, 500);


    }


    private class MyConn implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("KKK_kkk","bind 主服务成功");
            IMainApp mainapp = IMainApp.Stub.asInterface(service);
            try {
                mainapp.setMainToken(mToken);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("KKK_kkk"," 主服务 被杀死 ------");
            Toast.makeText(DeamonService.this,"main app 主程序服务被杀死，重启之",Toast.LENGTH_SHORT).show();
            startService(new Intent(DeamonService.this,BgService.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(mMyConn);
    }
}
