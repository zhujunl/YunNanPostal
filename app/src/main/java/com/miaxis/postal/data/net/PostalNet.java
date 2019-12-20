package com.miaxis.postal.data.net;

import com.miaxis.postal.data.dto.TempIdDto;

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
