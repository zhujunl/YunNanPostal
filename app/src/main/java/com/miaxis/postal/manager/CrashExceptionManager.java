package com.miaxis.postal.manager;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.miaxis.postal.app.PostalApp;
import com.miaxis.postal.view.activity.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class CrashExceptionManager implements Thread.UncaughtExceptionHandler {

    private CrashExceptionManager() {}

    public static CrashExceptionManager getInstance () {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final CrashExceptionManager instance = new CrashExceptionManager();
    }

    /** ================================ 静态内部类单例写法 ================================ **/

    public static String TAG = "MyCrash";
    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Context mContext;
    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<>();
    // 用于格式化日期,作为日志文件名的一部分

    /**
     * 初始化
     */
    public void init(Application application) {
        mContext = application;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        ex.printStackTrace();
        Log.e("asd", "" + ex.getMessage());
        new Thread(() -> {
            Looper.prepare();
            Toast.makeText(mContext, "很抱歉，程序出现异常，即将重新启动", Toast.LENGTH_LONG).show();
            Looper.loop();
        }).start();
        try {
            thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        restartApp();
    }

    private void restartApp(){
//        Intent intent = new Intent(mContext, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        mContext.startActivity(intent);
//        android.os.Process.killProcess(android.os.Process.myPid());//再此之前可以做些退出等操作
        Context context = PostalApp.getInstance().getApplicationContext();
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
