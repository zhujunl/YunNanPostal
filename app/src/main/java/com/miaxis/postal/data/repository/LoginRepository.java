package com.miaxis.postal.data.repository;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.model.CourierModel;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.data.net.ResponseEntity;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.util.FileUtil;
import com.miaxis.postal.util.ValueUtil;

import java.io.File;
import java.io.IOException;

import retrofit2.Response;

public class LoginRepository extends BaseRepository {

    private LoginRepository() {
    }

    public static LoginRepository getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final LoginRepository instance = new LoginRepository();
    }

    /**
     * ================================ 静态内部类单例写法 ================================
     **/

    public Courier getCourierByPhoneSync(String phone) throws IOException, MyException {
        Response<ResponseEntity<Courier>> execute = PostalApi.getExpressmanByPhoneSync(phone).execute();
        try {
            ResponseEntity<Courier> body = execute.body();
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

    public void registerExpressmanSync(String name,
                                       String cardNo,
                                       String phone,
                                       String faceFeature,
                                       String finger1Feature,
                                       String finger2Feature,
                                       Bitmap bitmap) throws IOException, MyException {
        String path = FileUtil.FACE_IMAGE_PATH + File.separator;
        String cardFileName = "card_" + cardNo + System.currentTimeMillis() + ".jpg";
        Response<ResponseEntity> execute = PostalApi.registerExpressmanSync(name,
                cardNo,
                phone,
                faceFeature,
                finger1Feature,
                finger2Feature,
                FileUtil.saveBitmap(bitmap, path, cardFileName)).execute();
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

    public Courier loadCourierSync() {
        return CourierModel.loadCourier();
    }

}
