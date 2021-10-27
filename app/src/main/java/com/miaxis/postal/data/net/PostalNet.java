package com.miaxis.postal.data.net;

import com.miaxis.postal.data.dto.CourierDto;
import com.miaxis.postal.data.dto.OrderDto;
import com.miaxis.postal.data.dto.SimpleOrderDto;
import com.miaxis.postal.data.dto.TempIdDto;
import com.miaxis.postal.data.dto.UpdateDto;
import com.miaxis.postal.data.entity.AppEntity;
import com.miaxis.postal.data.entity.Branch;
import com.miaxis.postal.data.entity.Customer;
import com.miaxis.postal.data.entity.DevicesStatusEntity;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PostalNet {

    //查询设备状态
    @FormUrlEncoded
    @POST("api/v1/device/getDeviceStatus1")
    Call<ResponseEntity<DevicesStatusEntity.DataDTO>> getDeviceStatus(@Field("macAddress") String macAddress);

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

    //通过手机号获取快递员所绑定的快递公司
    @FormUrlEncoded
    @POST("api/v1/expressman/list")
    Call<ResponseEntity<List<Branch>>> getBranchListSync(
            @Field("phone") String phone
    );

    @GET("api/v1/expressman/brandList")
    Call<ResponseEntity<List<Branch>>> getAllBranchListSync();

    //获取协议客户列表
    @FormUrlEncoded
    @POST("api/v1/person/getContractPersonList")
    Call<ResponseEntity<List<Customer>>> getContractPersonList(
            @Field("expressmanId") String expressmanId,
            @Field("pageNum") String pageNum,
            @Field("pageSize") String pageSize
    );

    //绑定网点
    @FormUrlEncoded
    @POST("api/v1/expressman/bindingNode")
    Call<ResponseEntity> bindingNodeSync(
            @Field("phone") String phone,
            @Field("comcode") String comcode
    );

    //解除绑定网点
    @FormUrlEncoded
    @POST("api/v1/expressman/unboundNode")
    Call<ResponseEntity> unBindingNodeSync(
            @Field("phone") String phone,
            @Field("orgNode") String orgNode
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
//    @Multipart
//    @POST("api/v1/person/savePersonFromApp")
//    Call<ResponseEntity<TempIdDto>> savePersonFromAppSync(
//            @Part("orgCode") String orgCode,
//            @Part("orgNode") String orgNode,
//            @Part("name") String name,
//            @Part("nation") String nation,
//            @Part("birthday") String birthday,
//            @Part("cardNo") String cardNo,
//            @Part("cardAddress") String cardAddress,
//            @Part("sex") String sex,
//            @Part("signOrg") String signOrg,
//            @Part("expireTime") String expireTime,
//            @Part("verifyType") String verifyType,
//            @Part("checkTime") String checkTime,
//            @Part("type") String type,
//            @Part("CardPhoto") String cardPhoto,
//            @Part("CheckPhoto") String checkPhoto
//    );

    /**
     * 上传人证核验记录，并获取核验编号
     * 20210817
     * 在老接口基础上增加expressmanId参数
     */
    @Multipart
    @POST("api/v1/person/savePersonFromApp1")
    Call<ResponseEntity<TempIdDto>> savePersonFromAppSync1(
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
            @Part("checkTime") String checkTime,
            @Part("type") String type,
            @Part("CardPhoto") String cardPhoto,
            @Part("CheckPhoto") String checkPhoto,
            @Part("expressmanId") String expressmanId
    );

    //拿到核验编号后，上传订单信息
    @FormUrlEncoded
    @POST("api/v1/order/saveOrderFromApp1")
    Call<ResponseEntity> saveOrderFromAppSync(
            @Field("orgCode") String orgCode,
            @Field("orgNode") String orgNode,
            @Field("personId") String personId,
            @Field("checkId") String checkId,
            @Field("warnLogId") String warnLogId,
            @Field("expressmanId") String expressmanId,
            @Field("sendAddress") String sendAddress,
            @Field("sendPhone") String sendPhone,
            @Field("sendName") String sendName,
            @Field("orderCode") String orderCode,
            @Field("orderInfo") String orderInfo,
            @Field("weight") String weight,
            @Field("addresseeName") String addresseeName,
            @Field("addresseeAddress") String addresseeAddress,
            @Field("addresseePhone") String addresseePhone,
            @Field("pieceTime") String pieceTime,
            @Field("receipTime") String receipTime,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Field("checkStatus") String checkStatus,
            //@Part List<MultipartBody.Part> file
            //@Field("url") List<String> file
            @Field("urls") String files,
            @Field("customerType") int customerType
    );

    @FormUrlEncoded
    @POST("api/v1/order/saveOrderFromApp1")
    Call<ResponseEntity> saveOrderFromAppSync(
            @Field("orgCode") String orgCode,
            @Field("orgNode") String orgNode,
            @Field("personId") String personId,
            @Field("checkId") String checkId,
            @Field("warnLogId") String warnLogId,
            @Field("expressmanId") String expressmanId,
            @Field("sendAddress") String sendAddress,
            @Field("sendPhone") String sendPhone,
            @Field("sendName") String sendName,
            @Field("orderCode") String orderCode,
            @Field("orderInfo") String orderInfo,
            @Field("weight") String weight,
            @Field("addresseeName") String addresseeName,
            @Field("addresseeAddress") String addresseeAddress,
            @Field("addresseePhone") String addresseePhone,
            @Field("pieceTime") String pieceTime,
            @Field("receipTime") String receipTime,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Field("checkStatus") String checkStatus,
            //@Part List<MultipartBody.Part> file
            //@Field("url") List<String> file
            @Field("urls") String files,
            @Field("customerName") String customerName,
            @Field("goodsName") String goodsName,
            @Field("goodsNumber") int goodsNumber,
            @Field("customerType") int customerType
    );


    /**
     * 上传报警信息
     */
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

    //更新版本
    @FormUrlEncoded
    @POST("api/v1/app/getAppByVersionName")
    Call<ResponseEntity<UpdateDto>> getAppByVersionName(@Field("versionName") String versionName);

    //根据订单单号查询订单详细信息
    @FormUrlEncoded
    @POST("api/v1/order/getOrderByCode")
    Call<ResponseEntity<OrderDto>> getOrderByCode(@Field("orderCode") String orderCode);


    /**
     * 根据订单模糊查询
     *
     * @param startTime 开始时间yyyy-MM-dd HH:mm:ss
     * @param endTime   结束时间yyyy-MM-dd HH:mm:ss
     */
    @FormUrlEncoded
    @POST("api/v1/order/getOrderByCode1")
    Call<ResponseEntity<List<OrderDto>>> getOrderByCode1(
            @Field("expressmanId") String expressmanId,
            @Field("pageNum") String pageNum,
            @Field("pageSize") String pageSize,
            @Field("orderCode") String orderCode,
            @Field("startTime") String startTime,
            @Field("endTime") String endTime
    );

    @Multipart
    @POST("api/v1/order/updateOrderFromApp")
    Call<ResponseEntity> updateOrderFromApp(
            @Part("sendAddress") String sendAddress,
            @Part("sendPhone") String sendPhone,
            @Part("sendName") String sendName,
            @Part("orderCode") String orderCode,
            @Part("goodsName") String goodsName,
            @Part("weight") String weight,
            @Part("addresseeName") String addresseeName,
            @Part("addresseeAddress") String addresseeAddress,
            @Part("addresseePhone") String addresseePhone,
            @Part List<MultipartBody.Part> file
    );

    /**
     * 上传图片
     */
    @Multipart
    @POST("api/v1/order/saveOrderPhoto")
    Call<ResponseEntity> saveOrderPhoto(
            @Part MultipartBody.Part file
    );


    @Multipart
    @POST("api/v1/order/deleteOrderPhoto")
    Call<ResponseEntity> deletePhoto(
            @Part("path") String path
    );


    /**
     * 上传图片
     */
    //    @Multipart
    //    @POST("api/v1/order/saveOrderPhoto")
    //    Call<String> deletePicture(
    //            @Part MultipartBody.Part file
    //    );


    /**
     * 加载App安装页面item
     */
    @POST("api/v1/company/appList")
    Call<ResponseEntity<List<AppEntity.DataBean>>> appInstall();

}
