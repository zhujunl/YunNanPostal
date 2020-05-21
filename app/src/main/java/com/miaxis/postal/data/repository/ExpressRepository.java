package com.miaxis.postal.data.repository;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.TempId;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.model.ExpressModel;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.data.net.ResponseEntity;
import com.miaxis.postal.manager.AmapManager;
import com.miaxis.postal.util.FileUtil;
import com.miaxis.postal.util.ValueUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class ExpressRepository {

    private ExpressRepository() {
    }

    public static ExpressRepository getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final ExpressRepository instance = new ExpressRepository();
    }

    /**
     * ================================ 静态内部类单例 ================================
     **/

    public boolean uploadLocalExpress(Express express, TempId tempId) throws IOException, MyException {
        List<File> fileList = new ArrayList<>();
        for (String path : express.getPhotoPathList()) {
            fileList.add(new File(path));
        }
        Response<ResponseEntity> execute = PostalApi.saveOrderFromAppSync(
                tempId.getPersonId(),
                express.getSenderAddress(),
                express.getSenderPhone(),
                express.getBarCode(),
                express.getInfo(),
                "",
                "",
                "",
                "",
                "",
                express.getLatitude(),
                express.getLongitude(),
                tempId.getCheckId(),
                fileList)
                .execute();
        try {
            ResponseEntity body = execute.body();
            if (body != null) {
                return TextUtils.equals(body.getCode(), ValueUtil.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(e.getMessage());
        }
        throw new MyException("服务端返回，空数据");
    }

    public void saveExpress(Express express) {
        List<Bitmap> photoList = express.getPhotoList();
        List<String> pathList = new ArrayList<>();
        for (int i = 0; i < photoList.size(); i++) {
            Bitmap bitmap = photoList.get(i);
            String path = FileUtil.ORDER_STOREHOUSE_PATH + File.separator + express.getBarCode() + "_" + System.currentTimeMillis() + "_" + i + ".jpg";
            FileUtil.saveBitmap(bitmap, path);
            pathList.add(path);
        }
        express.setPhotoPathList(pathList);
        ExpressModel.saveExpress(express);
    }

    public void updateExpress(Express express) {
        ExpressModel.saveExpress(express);
    }

    public List<Express> loadExpressByVerifyId(String verifyId) {
        return ExpressModel.loadExpress(verifyId);
    }

    public List<Express> loadRecordByPage(int pageNum, int pageSize) {
        return ExpressModel.loadExpressByPage(pageNum, pageSize);
    }

    public int loadExpressCount() {
        return ExpressModel.loadExpressCount();
    }

}
