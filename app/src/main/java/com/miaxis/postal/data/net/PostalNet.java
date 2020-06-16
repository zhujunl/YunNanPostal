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
    Call<ResponseEntity<List<SimpleOrderDto>>> getOrderByCodeAndNameSync(
            @Field("expressmanId") long expressmanId,
            @Field("param") String param,
            @Field("pageNum") int pageNum,
            @Field("pageSize") int pageSize
    );

    //根据订单编号查询订单详细信息
    @FormUrlEncoded
    @POST("api/v1/order/getOrderById")
    Call<ResponseEntity<OrderDto>> getOrderByIdSync(@Field("id") long id);

    //设备心跳
    @FormUrlEncoded
    @POST("api/v1/device/deviceHeartBeat")
    Call<ResponseEntity> deviceHeartBeatSync(
            @Field("macAddress") String macAddress,
            @Field("lat") double lat,
            @Field("lng") double lng
    );

    //联网检查该单号是否重复
    @FormUrlEncoded
    @POST("api/v1/order/checkOrderByCode")
    Call<ResponseEntity> checkOrderByCodeSync(@Field("code") String code);

    //注册快递员
    @Multipart
    @POST("api/v1/expressman/registerExpressman")
    Call<ResponseEntity> registerExpressmanSync(
            @Part("name") String name,
            @Part("cardNo") String cardNo,
            @Part("phone") String phone,
            @Part("faceFeature") String faceFeature,
            @Part("finger1Feature") String finger1Feature,
            @Part("finger2Feature") String finger2Feature,
            @Part MultipartBody.Part file
    );

    //注册快递员
    @FormUrlEncoded
    @POST("api/v1/expressman/editExpressman")
    Call<ResponseEntity> editExpressmanSync(
            @Field("id") long id,
            @Field("password") String password
    );

    //上传人证核验记录，并获取核验编号
    @Multipart
    @POST("api/v1/person/savePersonFromApp")
    Call<ResponseEntity<TempIdDto>> savePersonFromAppSync(
            @Part("orgCode") String orgCode,
            @Part("orgNode") String orgNode,
            @Part("name") String name,
            @Part("nation") String nation,
            @Part("birthday") String birthday,
            @Part("cardNo") String cardNo,
            @Part("cardAddress") String cardAddress,
            @Part("sex") String sex,
            @Part("signOrg") String signOrg,
            @Part("expireTime") String expireTime,
            @Part("verifyType") String verifyType,
            @Part("checkTime") String  checkTime,
            @Part MultipartBody.Part checkFile,
            @Part MultipartBody.Part cardFile
    );

    //拿到核验编号后，上传订单信息
    @Multipart
    @POST("api/v1/order/saveOrderFromApp")
    Call<ResponseEntity> saveOrderFromAppSync(
            @Part("orgCode") String orgCode,
            @Part("orgNode") String orgNode,
            @Part("personId") String personId,
            @Part("checkId") String checkId,
            @Part("warnLogId") String warnLogId,
            @Part("expressmanId") String expressmanId,
            @Part("sendAddress") String sendAddress,
            @Part("sendPhone") String sendPhone,
            @Part("sendName") String sendName,
            @Part("orderCode") String orderCode,
            @Part("orderInfo") String orderInfo,
            @Part("weight") String weight,
            @Part("addresseeName") String addresseeName,
            @Part("addresseeAddress") String addresseeAddress,
            @Part("addresseePhone") String addresseePhone,
            @Part("pieceTime") String pieceTime,
            @Part("receipTime") String receipTime,
            @Part("lat") String lat,
            @Part("lng") String lng,
            @Part List<MultipartBody.Part> file
    );

    @FormUrlEncoded
    @POST("api/v1/warn/saveWarnLog")
    Call<ResponseEntity<Integer>> uploadWarnLog(
            @Field("orgCode") String orgCode,
            @Field("orgNode") String orgNode,
            @Field("personId") String personId,
            @Field("checkId") String checkId,
            @Field("sendAddress") String sendAddress,
            @Field("sendName") String sendName,
            @Field("sendCardNo") String sendCardNo,
            @Field("sendPhone") String sendPhone,
            @Field("expressmanId") long expressmanId,
            @Field("deviceIMEI") String deviceIMEI,
            @Field("expressmanName") String expressmanName,
            @Field("createTime") String createTime
    );

    @FormUrlEncoded
    @POST("api/v1/app/getAppByVersionName")
    Call<ResponseEntity<UpdateDto>> getAppByVersionName(@Field("versionName") String versionName);

    //根据订单单号查询订单详细信息
    @FormUrlEncoded
    @POST("api/v1/order/getOrderByCode")
    Call<ResponseEntity<OrderDto>> getOrderByCode(@Field("orderCode") String orderCode);

    @Multipart
    @POST("api/v1/order/updateOrderFromApp")
    Call<ResponseEntity> updateOrderFromApp(
            @Part("sendAddress") String sendAddress,
            @Part("sendPhone") String sendPhone,
            @Part("sendName") String sendName,
            @Part("orderCode") String orderCode,
            @Part("orderInfo") String orderInfo,
            @Part("weight") String weight,
            @Part("addresseeName") String addresseeName,
            @Part("addresseeAddress") String addresseeAddress,
            @Part("addresseePhone") String addresseePhone,
            @Part List<MultipartBody.Part> file
    );

}
