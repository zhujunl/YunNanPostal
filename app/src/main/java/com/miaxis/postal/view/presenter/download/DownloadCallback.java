package com.miaxis.postal.view.presenter.download;

/**
 * @author Tank
 * @date 2020/9/10 14:13
 * @des
 * @updateAuthor
 * @updateDes
 */
public abstract class DownloadCallback<T> {

    private T temp;

    public DownloadCallback(T temp) {
        this.temp = temp;
    }

    public void downloadStart(Object data) {
        try {
            if (data != null) {
                this.onDownloadStart((T) data);
            } else {
                this.onDownloadStart(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void onDownloadStart(T data);

    public void downloadProgress(Object data, int total, int progress) {
        try {
            if (data != null) {
                this.onDownloadProgress((T) data, total, progress);
            } else {
                this.onDownloadProgress(null, total, progress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract void onDownloadProgress(T data, int total, int progress);


    public void downloadStop(Object data, int error, String msg) {
        try {
            if (data != null) {
                this.onDownloadStop((T) data, error, msg);
            } else {
                this.onDownloadStop(null, error, msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        clearTemp();
    }

    public abstract void onDownloadStop(T data, int error, String msg);

    public T getTemp() {
        return temp;
    }

    public void clearTemp() {
        this.temp = null;
    }
}
