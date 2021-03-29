package com.kongqw.serialportlibrary.thread;

import android.util.Log;

import com.kongqw.serialportlibrary.utils.ByteUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Kongqw on 2017/11/14.
 * 串口消息读取线程
 */

public abstract class SerialPortReadThread extends Thread {

    public abstract void onDataReceived(byte[] bytes);

    private static final String TAG = SerialPortReadThread.class.getSimpleName();
    private InputStream mInputStream;
    private byte[] mReadBuffer;

    public SerialPortReadThread(InputStream inputStream) {
        mInputStream = inputStream;
        mReadBuffer = new byte[1024];
    }

    @Override
    public void run() {
        super.run();
        Log.i(TAG, "SerialPortReadThread start !");
        while (!isInterrupted()) {
            try {
                if (null == mInputStream) {
                    return;
                }
                int size = mInputStream.read(mReadBuffer);
                if (0 >= size) {
                    break;
                }
                byte[] readBytes = new byte[size];
                System.arraycopy(mReadBuffer, 0, readBytes, 0, size);
                Log.i(TAG, "run: readBytes hex= " + ByteUtils.bytes2hex(readBytes));
                onDataReceived(readBytes);
            } catch (IOException e) {
                break;
            }
        }
        Log.i(TAG, "SerialPortReadThread close !");
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    /**
     * 关闭线程 释放资源
     */
    public void release() {
        interrupt();

        if (null != mInputStream) {
            try {
                mInputStream.close();
                mInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
