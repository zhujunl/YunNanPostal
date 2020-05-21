package com.miaxis.postal.data.net;

import com.miaxis.postal.data.dto.CourierDto;
import com.miaxis.postal.data.dto.OrderDto;
import com.miaxis.postal.data.dto.SimpleOrderDto;
import com.miaxis.postal.data.dto.TempIdDto;
import com.miaxis.postal.data.dto.UpdateDto;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PostalNet {

    //获取设备状态
    @FormUrlEncoded
    @POST("api/v1/device/getDeviceStatus")
    Call<ResponseEntity<String>> getDeviceStatusSync(@Field("macAddress") String macAddress);

    //通过手机号查询快递员，用于登录
    @FormUrlEncoded
    @POST("api/v1/expressman/getExpressmanByPhone")
    Call<ResponseEntity<CourierDto>> getExpressmanByPhoneSync(@Field("phone") String phone);

    //获取订单信息列表，省略信息
    @FormUrlEncoded
    @POST("api/v1/order/getOrderByCodeAndName")
    Call<ResponseEntity<List<SimpleOrderDto>>> getOrderByCodeAndNameSync(@Field("param") String param,
                                                                         @Field("pageNum") int pageNum,
                                                                         @Field("pageSize") int pageSize);

    //根据订单编号查询订单详细信息
    @FormUrlEncoded
    @POST("api/v1/order/getOrderById")
    Call<ResponseEntity<OrderDto>> getOrderByIdSync(@Field("id") long id);

    //设备心跳
    @FormUrlEncoded
    @POST("api/v1/device/deviceHeartBeat")
    Call<ResponseEntity> deviceHeartBeatSync(@Field("macAddress") String macAddress,
                                             @Field("lat") double lat,
                                             @Field("lng") double lng);

    //联网检查该单号是否重复
    @FormUrlEncoded
    @POST("api/v1/order/checkOrderByCode")
    Call<ResponseEntity> checkOrderByCodeSync(@Field("code") String code);

    //注册快递员
    @Multipart
    @POST("api/v1/expressman/registerExpressman")
    Call<ResponseEntity> registerExpressmanSync(@Part("name") String name,
                                                @Part("cardNo") String cardNo,
                                                @Part("phone") String phone,
                                                @Part("faceFeature") String faceFeature,
                                                @Part("finger1Feature") String finger1Feature,
                                                @Part("finger2Feature") String finger2Feature,
                                                @Part MultipartBody.Part file);

    //上传人证核验记录，并获取核验编号
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

    //拿到核验编号后，上传订单信息
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

    @FormUrlEncoded
    @POST("api/v1/update/updateApp")
    Call<ResponseEntity<UpdateDto>> updateApp(@Field("version") String version);

}
