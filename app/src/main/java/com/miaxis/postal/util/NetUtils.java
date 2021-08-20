package com.miaxis.postal.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author Tank
 * @date 2021/8/16 6:17 下午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class NetUtils {

    public static int getNetStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable()) {
            int type2 = networkInfo.getType();
            switch (type2) {
                case 0://移动 网络    2G 3G 4G 都是一样的 实测 mix2s 联通卡
                    break;
                case 1: //wifi网络
                    break;
                case 9:  //网线连接
                    break;
            }
            return type2;
        } else {// 无网络
            return -1;
        }
    }
}
