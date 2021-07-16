package com.miaxis.postal.data.repository;

import android.text.TextUtils;

import com.google.android.material.internal.ViewUtils;
import com.miaxis.postal.data.dto.UpdateDto;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.Update;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.exception.NetResultFailedException;
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
    private static   boolean isSetHost=true;

    public String getDeviceStatus() throws IOException, MyException, NetResultFailedException {
        //每次进入显示默认地址
        if (isSetHost) {
            Config config = ConfigManager.getInstance().getConfig();
            config.setHost(ValueUtil.DEFAULT_BASE_HOST);
            ConfigManager.getInstance().saveConfigSync(config);
            isSetHost=false;
        }
        String deviceIMEI = ConfigManager.getInstance().getConfig().getDeviceIMEI();
        Response<ResponseEntity<String>> execute = PostalApi.getDeviceStatusSync(deviceIMEI).execute();
        try {
            ResponseEntity<String> body = execute.body();
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS) && body.getData() != null) {
                    return body.getData();
                } else {
                    throw new NetResultFailedException("服务端返回，" + body.getMessage());
                }
            }
        }  catch (NetResultFailedException e) {
            throw e;
        }  catch (Exception e) {
            e.printStackTrace();
            throw new MyException(e.getMessage());
        }
        throw new MyException("服务端返回数据解析失败，或为空");
    }

    public void deviceHeartBeat(String macAddress, double lat, double lng) throws IOException, MyException, NetResultFailedException {
        Response<ResponseEntity> execute = PostalApi.deviceHeartBeatSync(macAddress, lat, lng).execute();
        try {
            ResponseEntity body = execute.body();
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS)) {
                    return;
                } else {
                    throw new NetResultFailedException("服务端返回，" + body.getMessage());
                }
            }
        }  catch (NetResultFailedException e) {
            throw e;
        }  catch (Exception e) {
            e.printStackTrace();
            throw new MyException(e.getMessage());
        }
        throw new MyException("服务端返回数据解析失败，或为空");
    }

    public Update updateApp(String versionName) throws IOException, MyException, NetResultFailedException {
        Response<ResponseEntity<UpdateDto>> execute = PostalApi.updateApp(versionName).execute();
        try {
            ResponseEntity<UpdateDto> body = execute.body();
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS) && body.getData() != null) {
                    return body.getData().transform();
                } else {
                    throw new NetResultFailedException("服务端返回，" + body.getMessage());
                }
            }
        }  catch (NetResultFailedException e) {
            throw e;
        }  catch (Exception e) {
            e.printStackTrace();
            throw new MyException(e.getMessage());
        }
        throw new MyException("服务端返回数据解析失败，或为空");
    }

}
