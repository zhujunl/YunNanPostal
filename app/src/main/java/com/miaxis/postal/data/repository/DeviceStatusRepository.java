package com.miaxis.postal.data.repository;

import android.text.TextUtils;
import android.util.Log;

import com.miaxis.postal.data.entity.AppEntity;
import com.miaxis.postal.data.entity.DevicesStatusEntity;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.exception.NetResultFailedException;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.data.net.ResponseEntity;
import com.miaxis.postal.util.ValueUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class DeviceStatusRepository extends BaseRepository {

    public DeviceStatusRepository() {

    }

    public static DeviceStatusRepository getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final DeviceStatusRepository instance = new DeviceStatusRepository();
    }

    public DevicesStatusEntity.DataDTO getStatus(String macAddress) throws IOException, MyException {

        Response<ResponseEntity<DevicesStatusEntity.DataDTO>> execute = PostalApi.deviceStatus(macAddress).execute();
        try {
            ResponseEntity<DevicesStatusEntity.DataDTO> body = execute.body();
            Log.e("Repository", "getContractPersonList:" + body);
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS) && body.getData() != null) {
                    return body.getData();
                } else {
                    throw new NetResultFailedException("服务端返回，" + body.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Repository", "getContractPersonList   Exception:" + e);
            throw new MyException(e.getMessage());
        }
        throw new MyException("服务端返回数据解析失败，或为空");

    }
}
