package com.miaxis.postal.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * 执行删除通话录音文件任务 30天以外
 */
public class ClearRecordFileWorker extends Worker {

    public ClearRecordFileWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * 耗时的任务，在doWork()方法中执行
     */
    @NonNull
    @Override
    public Result doWork() {
//       Result.success() Result.failure()  Result.retry()
        //判断sd卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Result.failure();
        }
        Log.e("执行任务", "ing");
        //  /sdcard/CallRecord
        String basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CallRecord";
        File file = new File(basePath);
        if (!file.exists()) {
            return Result.failure();
        }
        //如果有文件
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            return Result.failure();
        }
        Date currentDate = new Date();
        for (File f : files) {
            if (f == null) {
                continue;
            }
            if (!file.exists()) {
                continue;
            }
            long time = file.lastModified();
            compareTimeToDeleteFiles(time, file, currentDate);
        }
        //更新扫描指定目录
//        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        scanIntent.setData(Uri.fromFile(file));
//        getApplicationContext().sendBroadcast(scanIntent);
        return Result.success();
    }


    private void compareTimeToDeleteFiles(long lodTime, File file, Date currentDate) {
        try {
            Log.e("执行任务-时间", "当前时间" + currentDate.getTime() + "===创建时间");
            Calendar calc = Calendar.getInstance();
            calc.setTime(new Date(lodTime));
            calc.add(Calendar.DATE, +30);
            Date minDate = calc.getTime();
            //比较时间
            if (currentDate.after(minDate)) {
                boolean delete = file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}