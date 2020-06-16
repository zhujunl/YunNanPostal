package com.miaxis.postal.data.net;

import android.graphics.Bitmap;

import com.miaxis.postal.data.dto.CourierDto;
import com.miaxis.postal.data.dto.OrderDto;
import com.miaxis.postal.data.dto.SimpleOrderDto;
import com.miaxis.postal.data.dto.TempIdDto;
import com.miaxis.postal.data.dto.UpdateDto;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.util.EncryptUtil;
import com.miaxis.postal.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class PostalApi extends BaseAPI {

    public static Call<ResponseEntity<String>> getDeviceStatusSync(String macAddress) {
        return getPostalNetSync().getDeviceStatusSync(macAddress);
    }

    public static Call<ResponseEntity<CourierDto>> getExpressmanByPhoneSync(String macAddress) {
        return getPostalNetSync().getExpressmanByPhoneSync(macAddress);
    }

    public static Call<ResponseEntity<List<SimpleOrderDto>>> getOrderByCodeAndNameSync(
            long expressmanId,
            String param,
            int pageNum,
            int pageSize) {
        return getPostalNetSync().getOrderByCodeAndNameSync(
                expressmanId,
                param,
                pageNum,
                pageSize);
    }

    public static Call<ResponseEntity<OrderDto>> getOrderByIdSync(long id) {
        return getPostalNetSync().getOrderByIdSync(id);
    }

    public static Call<ResponseEntity> deviceHeartBeatSync(String macAddress, double lat, double lng) {
        return getPostalNetSync().deviceHeartBeatSync(macAddress, lat, lng);
    }

    public static Call<ResponseEntity> registerExpressmanSync(
            String name,
            String cardNo,
            String phone,
            String faceFeature,
            String finger1Feature,
            String finger2Feature,
            File file) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part fileBody = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        return getPostalNetSync().registerExpressmanSync(
                name,
                cardNo,
                phone,
                faceFeature,
                finger1Feature,
                finger2Feature,
                fileBody);
    }

    public static Call<ResponseEntity> editExpressmanSync(long id, String password) {
        return getPostalNetSync().editExpressmanSync(id, password);
    }

    public static Call<ResponseEntity> checkOrderByCodeSync(String code) {
        return getPostalNetSync().checkOrderByCodeSync(code);
    }

    public static Call<ResponseEntity<TempIdDto>> savePersonFromApp(
            String orgCode,
            String orgNode,
            String name,
            String nation,
            String birthday,
            String cardNo,
            String cardAddress,
            String sex,
            String signOrg,
            String expireTime,
            String verifyType,
            String  checkTime,
            File checkFile,
            File cardFile) {
        MultipartBody.Part checkFileBody = null;
        if (checkFile != null) {
            RequestBody checkRequestFile = RequestBody.create(MediaType.parse("multipart/form-data"), checkFile);
            checkFileBody = MultipartBody.Part.createFormData("checkFile", checkFile.getName(), checkRequestFile);
        }
        MultipartBody.Part cardFileBody = null;
        if (cardFile != null) {
            RequestBody cardRequestFile = RequestBody.create(MediaType.parse("multipart/form-data"), cardFile);
            cardFileBody = MultipartBody.Part.createFormData("cardFile", cardFile.getName(), cardRequestFile);
        }
        return getPostalNetSync().savePersonFromAppSync(
                orgCode,
                orgNode,
                name,
                nation,
                birthday,
                cardNo,
                cardAddress,
                sex,
                signOrg,
                expireTime,
                verifyType,
                checkTime,
                checkFileBody,
                cardFileBody);
    }

    public static Call<ResponseEntity> saveOrderFromAppSync(
            String orgCode,
            String orgNode,
            String personId,
            String checkId,
            String warnLogId,
            String expressmanId,
            String sendAddress,
            String sendPhone,
            String sendName,
            String orderCode,
            String orderInfo,
            String weight,
            String addresseeName,
            String addresseeAddress,
            String addresseePhone,
            String pieceTime,
            String receipTime,
            String lat,
            String lng,
            List<File> fileList) {
        List<MultipartBody.Part> parts = new ArrayList<>(fileList.size());
        for (File file : fileList) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
            parts.add(part);
        }
        return getPostalNetSync().saveOrderFromAppSync(
                orgCode,
                orgNode,
                personId,
                checkId,
                warnLogId,
                expressmanId,
                sendAddress,
                sendPhone,
                sendName,
                orderCode,
                orderInfo,
                weight,
                addresseeName,
                addresseeAddress,
                addresseePhone,
                pieceTime,
                receipTime,
                lat,
                lng,
                parts);
    }

    public static Call<ResponseEntity<Integer>> uploadWarnLog(
            String orgCode,
            String orgNode,
            String personId,
            String checkId,
            String sendAddress,
            String sendName,
            String sendCardNo,
            String sendPhone,
            long expressmanId,
            String deviceIMEI,
            String expressmanName,
            String createTime) {
        return getPostalNetSync().uploadWarnLog(
                orgCode,
                orgNode,
                personId,
                checkId,
                sendAddress,
                sendName,
                sendCardNo,
                sendPhone,
                expressmanId,
                deviceIMEI,
                expressmanName,
                createTime);
    }

    public static Call<ResponseEntity<UpdateDto>> updateApp(String versionName) {
        return getPostalNetSync().getAppByVersionName(versionName);
    }

    public static Call<ResponseEntity<OrderDto>> getOrderByCode(String orderCode) {
        return getPostalNetSync().getOrderByCode(orderCode);
    }

    public static Call<ResponseEntity> updateOrderFromApp(
            String sendAddress,
            String sendPhone,
            String sendName,
            String orderCode,
            String orderInfo,
            String weight,
            String addresseeName,
            String addresseeAddress,
            String addresseePhone,
            List<Bitmap> bitmapList) {
        List<MultipartBody.Part> parts = new ArrayList<>(bitmapList.size());
        for (Bitmap bitmap : bitmapList) {
            byte[] data = FileUtil.bitmapToByteArray(bitmap);
            if (data != null) {
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), data, 0, data.length);
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", EncryptUtil.getRandomString(10) + ".jpg", requestBody);
                parts.add(part);
            }
        }
        return getPostalNetSync().updateOrderFromApp(
                sendAddress,
                sendPhone,
                sendName,
                orderCode,
                orderInfo,
                weight,
                addresseeName,
                addresseeAddress,
                addresseePhone,
                parts);
    }

}
