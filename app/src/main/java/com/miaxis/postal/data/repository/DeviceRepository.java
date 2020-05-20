package com.miaxis.postal.data.repository;

import android.text.TextUtils;

import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.data.net.ResponseEntity;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.util.ValueUtil;

import java.io.IOException;

import retrofit2.Response;

public class DeviceRepository extends BaseRepository {

    private DeviceRepository() {
    }

    public static DeviceRepository getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final DeviceRepository instance = new DeviceRepository();
    }

    /**
     * ================================ 静态内部类单例写法 ================================
     **/

    public Integer registerDevice() throws IOException, MyException {
        String deviceIMEI = ConfigManager.getInstance().getConfig().getDeviceIMEI();
        Response<ResponseEntity<Integer>> execute = PostalApi.registerDeviceSync(deviceIMEI).execute();
        try {
            ResponseEntity<Integer> body = execute.body();
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS) && body.getData() != null) {
                    return body.getData();
                } else {
                    throw new MyException("服务端返回，" + body.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(e.getMessage());
        }
        throw new MyException("服务端返回数据解析失败，或为空");
    }

    public String getDeviceStatus() throws IOException, MyException {
        String deviceIMEI = ConfigManager.getInstance().getConfig().getDeviceIMEI();
        Response<ResponseEntity<String>> execute = PostalApi.getDeviceStatusSync(deviceIMEI).execute();
        try {
            ResponseEntity<String> body = execute.body();
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS) && body.getData() != null) {
                    return body.getData();
                } else {
                    throw new MyException("服务端返回，" + body.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(e.getMessage());
        }
        throw new MyException("服务端返回数据解析失败，或为空");
    }

    public void deviceHeartBeat(String macAddress, double lat, double lng) throws IOException, MyException {
        Response<ResponseEntity> execute = PostalApi.deviceHeartBeatSync(macAddress, lat, lng).execute();
        try {
            ResponseEntity body = execute.body();
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS)) {
                    return;
                } else {
                    throw new MyException("服务端返回，" + body.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(e.getMessage());
        }
        throw new MyException("服务端返回数据解析失败，或为空");
    }

}
