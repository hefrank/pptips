package com.ppamy.pptips;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ppamy.pptips.service.DeamonService;
import com.ppamy.pptips.ui.AddNewItemActivity;

import java.lang.ref.WeakReference;

/**
 * Created by hefrank on 15-12-19.
 */
public class BgService extends Service{
    public static final String OPERATION = "operation";
    public static final int OPERATION_SHOW = 100;
    public static final int OPERATION_HIDE = 101;

    static boolean isAdded = false; // 是否已增加悬浮窗

    private static WindowManager wm;

    private static WindowManager.LayoutParams params;

    private View floatView;

    private float startX = 0;

    private float startY = 0;

    private float x;

    private float y;

    private String copyValue;
    private ClipboardManager mClipboardManager;

    final static int MSG_WHAT_SHOW_TIPS_WIN = 0;
    final static int MSG_WHAT_CLOSE_TIPS_WIN = 1;
//    private final int MSG_WHAT_SHOW_TIPS_WIN = 2;

    private IBinder mToken,mBinderDeamon;
    private MyConn mMyConn;
    private IBinder mBinder = new IMainApp.Stub(){

        @Override
        public void setMainToken(IBinder token) throws RemoteException {
            mBinderDeamon = token;
//            mBinderDeamon.linkToDeath(mMyDeathReceipent,0);
        }
    };
    private class MyConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("KKK_kkk","bind deamon 服务 成功");
            IDeamon deamon = IDeamon.Stub.asInterface(service);
            try {
                deamon.setDeamonToken(mToken);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("KKK_kkk","demon 服务被杀死");
            Toast.makeText(BgService.this,"demon 服务被杀死，重启之",Toast.LENGTH_SHORT).show();
            startService(new Intent(BgService.this,DeamonService.class));
        }
    }

    private static class UiHandler extends Handler{
        private WeakReference<BgService> bgService;
        UiHandler(BgService in){
            bgService = new WeakReference<BgService>(in);
        }

        @Override
        public void handleMessage(Message msg) {
            BgService bgs = bgService.get();
            if(bgs == null){
                return;
            }
            switch (msg.what){
                case MSG_WHAT_SHOW_TIPS_WIN:
                    bgService.get().createFloatView();
                    bgService.get().setupCellView(bgService.get().floatView);
                    break;
                case MSG_WHAT_CLOSE_TIPS_WIN:
                    if (bgService.get().floatView != null && isAdded){
                        wm.removeView(bgService.get().floatView);
                        isAdded = false;
                    }
                    break;

            }
        }
    }

    private UiHandler mUiHandler = null;
//            new android.os.Handler(){
//
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case MSG_WHAT_SHOW_TIPS_WIN:
//                    createFloatView();
//                    setupCellView(floatView);
//                    break;
//                case MSG_WHAT_CLOSE_TIPS_WIN:
//                    if (floatView != null && isAdded){
//                        wm.removeView(floatView);
//                        isAdded = false;
//                    }
//                    break;
//
//            }
//        }
//    };
    @Override
    public void onCreate() {
        super.onCreate();
        if (mMyConn == null){
            mMyConn = new MyConn();
        }
        mUiHandler = new UiHandler(this);
        wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                String txt = "";
                ClipData clip = mClipboardManager.getPrimaryClip();
                if (clip != null && clip.getItemCount() > 0) {
                    txt = clip.getItemAt(0).coerceToText(BgService.this).toString();
                }
                Toast.makeText(BgService.this, txt, Toast.LENGTH_SHORT).show();
                mUiHandler.sendEmptyMessage(MSG_WHAT_SHOW_TIPS_WIN);
                mUiHandler.sendEmptyMessageDelayed(MSG_WHAT_CLOSE_TIPS_WIN,3000);

            }
        });

        doBind();

    }

    private void doBind(){
        startService(new Intent(BgService.this,DeamonService.class));
        mUiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean bb = bindService(new Intent(BgService.this, DeamonService.class),mMyConn,Context.BIND_AUTO_CREATE);
                Log.e("KKK_kkk","bind deamon 服务 result is:"+bb);
            }
        },500);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 创建悬浮窗
     */

    public void createFloatView() {
        if (floatView != null && isAdded){
            wm.removeView(floatView);
            isAdded = false;
        }

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        floatView = layoutInflater.inflate(R.layout.goto_popup_window, null);

        params = new WindowManager.LayoutParams();

        // 设置window type
//        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;

        /*
         * 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE; 那么优先级会降低一些,
         * 即拉下通知栏不可见
         */
        params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

        // 设置Window flag
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        /*
         * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
         * wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL |
         * LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
         */

        // 设置悬浮窗的长得宽
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;//getResources().getDimensionPixelSize(R.dimen.float_window_width);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 0;

        // 设置悬浮窗的Touch监听
        floatView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                x = event.getRawX();
//                y = event.getRawY();
//
//                switch(event.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        startX = event.getX();
//                        startY = event.getY();
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        params.x = (int)( x - startX);
//                        params.y = (int) (y - startY);
//                        wm.updateViewLayout(floatView, params);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        startX = startY = 0;
//                        break;
//                }

                mUiHandler.sendEmptyMessage(MSG_WHAT_CLOSE_TIPS_WIN);

                String txt = "";
                ClipData clip = mClipboardManager.getPrimaryClip();
                if (clip != null && clip.getItemCount() > 0) {
                    txt = clip.getItemAt(0).coerceToText(BgService.this).toString();
                }
                AddNewItemActivity.startAddEditItemActivityNew(BgService.this, txt);
                return true;
            }
        });

        wm.addView(floatView, params);
        isAdded = true;
    }

    /**
     * 设置浮窗view内部子控件
     * @param rootview
     */
    public void setupCellView(View rootview) {
        ImageView closedImg = (ImageView) rootview.findViewById(R.id.iv_close);
        TextView titleText = (TextView) rootview.findViewById(R.id.tv_save_msg);
//        titleText.setText(copyValue);
        closedImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isAdded) {
                    wm.removeView(floatView);
                    isAdded = false;
                }
            }
        });
//        floatView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                ClipData data = mClipboardManager.getPrimaryClip();
//                ClipData.Item item = data.getItemAt(0);
//                String txt = item.getText().toString();
//                AddNewItemActivity.startAddEditItemActivity(BgService.this,txt);
//
//            }
//        });
    }

}
