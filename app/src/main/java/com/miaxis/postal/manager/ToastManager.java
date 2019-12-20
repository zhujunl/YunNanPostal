package com.miaxis.postal.manager;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.miaxis.postal.app.PostalApp;

import es.dmoral.toasty.Toasty;

public class ToastManager {

    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";
    public static final String INFO = "INFO";

    private static Toast toast;

    private static Handler handler = new Handler(Looper.getMainLooper());

    public static void toast(String message, String toastMode) {
        handler.post(() -> {
            if (toast != null) {
                toast.cancel();
            }
            switch (toastMode) {
                case SUCCESS:
                    toast = Toasty.success(PostalApp.getInstance().getApplicationContext(), message, Toast.LENGTH_SHORT, true);
                    break;
                case ERROR:
                    toast = Toasty.error(PostalApp.getInstance().getApplicationContext(), message, Toast.LENGTH_SHORT, true);
                    break;
                case INFO:
                    toast = Toasty.info(PostalApp.getInstance().getApplicationContext(), message, Toast.LENGTH_SHORT, true);
                    break;
            }
            toast.show();
        });
    }

}
