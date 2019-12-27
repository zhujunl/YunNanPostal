package com.miaxis.postal.data.repository;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.miaxis.postal.data.dto.TempIdDto;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.TempId;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.data.net.ResponseEntity;
import com.miaxis.postal.util.FileUtil;
import com.miaxis.postal.util.ValueUtil;
import com.speedata.libid2.IDInfor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public TempId savePersonFromAppSync(IDInfor idInfor, Bitmap bitmap) throws IOException, MyException {
        String path = FileUtil.FACE_IMAGE_PATH + File.separator;
        String cardFileName = "card_" + idInfor.getNum() + System.currentTimeMillis() + ".jpg";
        String checkFileName = "check" + idInfor.getNum() + System.currentTimeMillis() + ".jpg";
        Response<ResponseEntity<TempIdDto>> execute = PostalApi.savePersonFromApp(idInfor.getName(),
                idInfor.getNation(),
                idInfor.getYear() + "-" + idInfor.getMonth() + "-" + idInfor.getDay(),
                idInfor.getNum(),
                idInfor.getAddress().trim(),
                idInfor.getSex(),
                idInfor.getQianFa().trim(),
                idInfor.getEndYear() + "-" + idInfor.getEndMonth() + "-" + idInfor.getEndDay(),
                FileUtil.saveBitmap(bitmap, path, checkFileName),
                FileUtil.saveBitmap(idInfor.getBmps(), path, cardFileName))
                .execute();
        try {
            ResponseEntity<TempIdDto> body = execute.body();
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS) && body.getData() != null) {
                    return body.getData().transform();
                } else {
                    throw new MyException("服务端返回，" + body.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(e.getMessage());
        }
        throw new MyException("服务端返回，空数据");
    }

    public void saveExpressFromAppSync(Express express, TempId tempId, String sendAddress, String sendPhone) throws IOException, MyException {
        List<File> fileList = new ArrayList<>();
        for (String path : express.getPhotoList()) {
            fileList.add(new File(path));
        }
        Response<ResponseEntity> execute = PostalApi.saveOrderFromAppSync(
                tempId.getPersonId(),
                sendAddress,
                sendPhone,
                express.getBarCode(),
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                tempId.getCheckId(),
                fileList).execute();
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
        throw new MyException("服务端返回，空数据");
    }

}
