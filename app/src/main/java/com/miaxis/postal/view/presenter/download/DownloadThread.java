package com.miaxis.postal.view.presenter.download;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import androidx.annotation.NonNull;

/**
 * @author Tank
 * @date 2020/9/10 14:15
 * @des
 * @updateAuthor
 * @updateDes
 */
public class DownloadThread extends Thread {

    private int mBufferSize = 1024 * 4;
    private String mUrlPath;
    private String mSavePath;
    private DownloadCallback<?> mDownloadCallback;
    private InputStream mInputStream;
    private FileOutputStream mFileOutputStream;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public DownloadThread() {
    }

    public DownloadThread bindDownloadInfo(@NonNull String urlPath, @NonNull String savePath) {
        this.mUrlPath = urlPath;
        this.mSavePath = savePath;
        return this;
    }

    public DownloadThread bindBufferSize(int bufferSize) {
        this.mBufferSize = bufferSize;
        return this;
    }

    private int mReadTimeOut = 0;
    private int mConnectTimeOut = 5 * 1000;

    public DownloadThread bindDownloadTimeOut(int connectTimeOut, int readTimeOut) {
        if (connectTimeOut < 0 || readTimeOut < 0) {
            return this;
        }
        this.mConnectTimeOut = connectTimeOut;
        this.mReadTimeOut = readTimeOut;
        return this;
    }

    public DownloadThread bindDownloadCallback(DownloadCallback<?> downloadCallback) {
        this.mDownloadCallback = downloadCallback;
        return this;
    }

    private static final HostnameVerifier NOT_VERIYIER = new HostnameVerifier() {
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }
    };

    private static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

        }

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }};

    @Override
    public void run() {
        super.run();
        if (this.mDownloadCallback != null) {
            this.mDownloadCallback.downloadStart(this.mDownloadCallback.getTemp());
        }
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(NOT_VERIYIER);
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            //获取文件名
            URL url = new URL(this.mUrlPath);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(this.mConnectTimeOut);
            urlConnection.setReadTimeout(this.mReadTimeOut);
            urlConnection.connect();
            if (isInterrupted()) {
                return;
            }
            this.mInputStream = urlConnection.getInputStream();
            int contentLength = urlConnection.getContentLength();//根据响应获取文件大小
            if (contentLength <= 0) {
                if (this.mDownloadCallback != null) {
                    this.mDownloadCallback.downloadStop(this.mDownloadCallback.getTemp(), -4, "Content Length Illegal," + contentLength);
                }
                return;
            }
            if (this.mInputStream == null) {
                if (this.mDownloadCallback != null) {
                    this.mDownloadCallback.downloadStop(this.mDownloadCallback.getTemp(), -3, "No InputStream");
                }
                return;
            }
            File file = new File(this.mSavePath);
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (parentFile != null) {
                    boolean mkdirs = parentFile.mkdirs();
                }
            } else {
                boolean delete = file.delete();
            }
            if (isInterrupted()) {
                return;
            }
            String tempPath = this.mSavePath + ".temp";
            //把数据存入路径+文件名
            this.mFileOutputStream = new FileOutputStream(tempPath);
            byte[] buffer = new byte[this.mBufferSize];
            int downLoadFileSize = 0;
            do {
                if (isInterrupted()) {
                    return;
                }
                //循环读取
                int num = this.mInputStream.read(buffer);
                if (num == -1) {
                    break;
                }
                this.mFileOutputStream.write(buffer, 0, num);
                //更新进度条
                downLoadFileSize += num;
                if (this.mDownloadCallback != null) {
                    this.mDownloadCallback.downloadProgress(this.mDownloadCallback.getTemp(), contentLength, downLoadFileSize);
                }
            } while (true);
            if (isInterrupted()) {
                return;
            }
            if (this.mDownloadCallback != null) {
                boolean renameTo = new File(tempPath).renameTo(new File(this.mSavePath));
                this.mDownloadCallback.downloadStop(this.mDownloadCallback.getTemp(), 0, "Success");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (this.mDownloadCallback != null) {
                this.mDownloadCallback.downloadStop(this.mDownloadCallback.getTemp(), -2, String.valueOf(ex));
            }
        } finally {
            close();
        }

    }

    @Override
    public void interrupt() {
        super.interrupt();
        if (this.mDownloadCallback != null) {
            this.mDownloadCallback.downloadStop(this.mDownloadCallback.getTemp(), -1, "取消下载。");
        }
    }

    private void close() {
        try {
            if (this.mInputStream != null) {
                this.mInputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (this.mFileOutputStream != null) {
                this.mFileOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}



