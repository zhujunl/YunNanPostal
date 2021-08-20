package com.miaxis.postal.view.presenter.download;

import androidx.annotation.NonNull;

/**
 * @author Tank
 * @date 2020/9/10 14:06
 * @des
 * @updateAuthor
 * @updateDes
 */
public class DownloadManager {

    private DownloadThread mDownloadThread;
    private int mBufferSize = 1024 * 5;

    public DownloadManager() {
    }

    protected static class DownloadHelperHolder {
        protected static DownloadManager mDownloadManager = new DownloadManager();
    }

    public static DownloadManager getInstance() {
        return DownloadHelperHolder.mDownloadManager;
    }

    public DownloadManager setBufferSize(int bufferSize) {
        this.mBufferSize = bufferSize;
        return this;
    }

    public void downloadFile(@NonNull String urlPath, @NonNull String savePath, DownloadCallback<?> downloadCallback) {
        close();
        this.mDownloadThread = new DownloadThread()
                .bindDownloadInfo(urlPath, savePath)
                .bindBufferSize(this.mBufferSize)
                .bindDownloadCallback(downloadCallback);
        this.mDownloadThread.start();
    }

    public void close() {
        if (this.mDownloadThread != null) {
            this.mDownloadThread.interrupt();
            this.mDownloadThread = null;
        }
    }

}
