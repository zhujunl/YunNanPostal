package com.miaxis.postal.data.net;

import com.miaxis.postal.data.dto.TempIdDto;
import com.miaxis.postal.data.entity.Courier;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PostalNet {

    @FormUrlEncoded
    @POST("api/v1/device/registerDevice")
    Call<ResponseEntity<Integer>> registerDeviceSync(@Field("macAddress") String macAddress);

    @FormUrlEncoded
    @POST("api/v1/device/getDeviceStatus")
    Call<ResponseEntity<String>> getDeviceStatusSync(@Field("macAddress") String macAddress);

    @FormUrlEncoded
    @POST("api/v1/expressman/getExpressmanByPhone")
    Call<ResponseEntity<Courier>> getExpressmanByPhoneSync(@Field("phone") String phone);

    @Multipart
    @POST("api/v1/expressman/registerExpressman")
    Call<ResponseEntity> registerExpressmanSync(@Part("name") String name,
                                                @Part("cardNo") String cardNo,
                                                @Part("phone") String phone,
                                                @Part("faceFeature") String faceFeature,
                                                @Part("finger1Feature") String finger1Feature,
                                                @Part("finger2Feature") String finger2Feature,
                                                @Part MultipartBody.Part file);

    @Multipart
    @POST("api/v1/person/savePersonFromApp")
    Call<ResponseEntity<TempIdDto>> savePersonFromAppSync(@Part("name") String name,
                                                          @Part("nation") String nation,
                                                          @Part("birthday") String birthday,
                                                          @Part("cardNo") String cardNo,
                                                          @Part("cardAddress") String cardAddress,
                                                          @Part("sex") String sex,
                                                          @Part("signOrg") String signOrg,
                                                          @Part("expireTime") String expireTime,
                                                          @Part MultipartBody.Part checkFile,
                                                          @Part MultipartBody.Part cardFile);

    @Multipart
    @POST("api/v1/order/saveOrderFromApp")
    Call<ResponseEntity> saveOrderFromAppSync(@Part("personId") String personId,
                                              @Part("sendAddress") String sendAddress,
                                              @Part("sendPhone") String sendPhone,
                                              @Part("orderCode") String orderCode,
                                              @Part("orderInfo") String orderInfo,
                                              @Part("addresseeName") String addresseeName,
                                              @Part("addresseeAddress") String addresseeAddress,
                                              @Part("addresseePhone") String addresseePhone,
                                              @Part("pieceTime") String pieceTime,
                                              @Part("receipTime") String receipTime,
                                              @Part("lat") String lat,
                                              @Part("lng") String lng,
                                              @Part("checkId") String checkId,
                                              @Part List<MultipartBody.Part> file);

}
