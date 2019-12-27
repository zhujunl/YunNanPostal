package com.miaxis.postal.data.net;

import com.miaxis.postal.data.dto.CourierDto;
import com.miaxis.postal.data.dto.OrderDto;
import com.miaxis.postal.data.dto.SimpleOrderDto;
import com.miaxis.postal.data.dto.TempIdDto;
import com.miaxis.postal.data.entity.Courier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class PostalApi extends BaseAPI {

    public static Call<ResponseEntity<Integer>> registerDeviceSync(String macAddress) {
        return getPostalNetSync().registerDeviceSync(macAddress);
    }

    public static Call<ResponseEntity<String>> getDeviceStatusSync(String macAddress) {
        return getPostalNetSync().getDeviceStatusSync(macAddress);
    }

    public static Call<ResponseEntity<CourierDto>> getExpressmanByPhoneSync(String macAddress) {
        return getPostalNetSync().getExpressmanByPhoneSync(macAddress);
    }

    public static Call<ResponseEntity<List<SimpleOrderDto>>> getOrderByCodeAndNameSync(String param,
                                                                                       int pageNum,
                                                                                       int pageSize) {
        return getPostalNetSync().getOrderByCodeAndNameSync(param,
                pageNum,
                pageSize);
    }

    public static Call<ResponseEntity<OrderDto>> getOrderByIdSync(long id) {
        return getPostalNetSync().getOrderByIdSync(id);
    }

    public static Call<ResponseEntity> deviceHeartBeatSync(String macAddress, double lat, double lng) {
        return getPostalNetSync().deviceHeartBeatSync(macAddress, lat, lng);
    }

    public static Call<ResponseEntity> registerExpressmanSync(String name,
                                                              String cardNo,
                                                              String phone,
                                                              String faceFeature,
                                                              String finger1Feature,
                                                              String finger2Feature,
                                                              File file) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part fileBody = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        return getPostalNetSync().registerExpressmanSync(name,
                cardNo,
                phone,
                faceFeature,
                finger1Feature,
                finger2Feature,
                fileBody);
    }

    public static Call<ResponseEntity<TempIdDto>> savePersonFromApp(String name,
                                                                    String nation,
                                                                    String birthday,
                                                                    String cardNo,
                                                                    String cardAddress,
                                                                    String sex,
                                                                    String signOrg,
                                                                    String expireTime,
                                                                    File checkFile,
                                                                    File cardFile) {
        RequestBody checkRequestFile = RequestBody.create(MediaType.parse("multipart/form-data"), checkFile);
        MultipartBody.Part checkFileBody = MultipartBody.Part.createFormData("checkFile", checkFile.getName(), checkRequestFile);
        RequestBody cardRequestFile = RequestBody.create(MediaType.parse("multipart/form-data"), cardFile);
        MultipartBody.Part cardFileBody = MultipartBody.Part.createFormData("cardFile", cardFile.getName(), cardRequestFile);
        return getPostalNetSync().savePersonFromAppSync(name,
                nation,
                birthday,
                cardNo,
                cardAddress,
                sex,
                signOrg,
                expireTime,
                checkFileBody,
                cardFileBody);
    }

    public static Call<ResponseEntity> saveOrderFromAppSync(String personId,
                                                            String sendAddress,
                                                            String sendPhone,
                                                            String orderCode,
                                                            String orderInfo,
                                                            String addresseeName,
                                                            String addresseeAddress,
                                                            String addresseePhone,
                                                            String pieceTime,
                                                            String receipTime,
                                                            String lat,
                                                            String lng,
                                                            String checkId,
                                                            List<File> fileList) {
        List<MultipartBody.Part> parts = new ArrayList<>(fileList.size());
        for (File file : fileList) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            parts.add(part);
        }
        return getPostalNetSync().saveOrderFromAppSync(personId,
                sendAddress,
                sendPhone,
                orderCode,
                orderInfo,
                addresseeName,
                addresseeAddress,
                addresseePhone,
                pieceTime,
                receipTime,
                lat,
                lng,
                checkId,
                parts);
    }

}
