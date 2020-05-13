package com.miaxis.postal.util;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import com.miaxis.postal.app.PostalApp;

import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class DeviceUtil {

    public static String getDeviceId(Context context) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get =c.getMethod("get", String.class);
            String serial = (String)get.invoke(c, "ro.serialno");
            return serial;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    public static String getIMEI() {
//        try {
//            TelephonyManager manager = (TelephonyManager) PostalApp.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
//            Method method = manager.getClass().getMethod("getImei", int.class);
//            String imei = (String) method.invoke(manager, 0);
//            return imei;
//        } catch (Exception e) {
//            return "";
//        }
        String imei0 = getIMEI(PostalApp.getInstance(), 0);
        String imei1 = getIMEI(PostalApp.getInstance(), 1);
        if (!TextUtils.isEmpty(imei1) && !imei1.contains("000")) {
            return imei1;
        }
        if (!TextUtils.isEmpty(imei0) && !imei0.contains("000")) {
            return imei0;
        }
        return "";
    }

    public static String getIMEI(Context context, int slotId) {
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Method method = manager.getClass().getMethod("getImei", int.class);
            String imei = (String) method.invoke(manager, slotId);
            return imei;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getCurVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    public static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
