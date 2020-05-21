package com.miaxis.postal.data.repository;

import android.text.TextUtils;

import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.data.net.ResponseEntity;
import com.miaxis.postal.util.ValueUtil;

import java.io.IOException;

import retrofit2.Response;

public class PostalRepository extends BaseRepository {

    private PostalRepository() {
    }

    public static PostalRepository getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final PostalRepository instance = new PostalRepository();
    }

    /**
     * ================================ 静态内部类单例写法 ================================
     **/

    public boolean checkOrderByCodeSync(String code) throws MyException, IOException {
        Response<ResponseEntity> execute = PostalApi.checkOrderByCodeSync(code).execute();
        try {
            ResponseEntity body = execute.body();
            if (body != null) {
                return TextUtils.equals(body.getCode(), ValueUtil.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(e.getMessage());
        }
        throw new MyException("服务端返回数据解析失败，或为空");
    }

}
