package com.miaxis.postal.data.repository;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.TempId;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.exception.NetResultFailedException;
import com.miaxis.postal.data.model.ExpressModel;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.data.net.ResponseEntity;
import com.miaxis.postal.manager.DataCacheManager;
import com.miaxis.postal.util.DateUtil;
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

    public void uploadLocalExpress(Express express, TempId tempId, Integer warnLogId, String sendName,int chekStatus) throws IOException, MyException, NetResultFailedException {
        List<File> fileList = new ArrayList<>();
        for (String path : express.getPhotoPathList()) {
            fileList.add(new File(path));
        }
        Courier courier = DataCacheManager.getInstance().getCourier();
        Response<ResponseEntity> execute = PostalApi.saveOrderFromAppSync(
                courier.getOrgCode(),
                courier.getOrgNode(),
                tempId.getPersonId(),
                tempId.getCheckId(),
                warnLogId != null ? String.valueOf(warnLogId) : "",
                String.valueOf(courier.getCourierId()),
                express.getSenderAddress(),
                express.getSenderPhone(),
                sendName,
                express.getBarCode(),
                express.getInfo(),
                express.getWeight(),
                express.getAddresseeName(),
                express.getAddresseeAddress(),
                express.getAddresseePhone(),
                DateUtil.DATE_FORMAT.format(express.getPieceTime()),
                "",
                express.getLatitude(),
                express.getLongitude(),
                chekStatus,
                fileList)
                .execute();
        try {
            ResponseEntity body = execute.body();
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS)) {
                    return;
                } else {
                    throw new NetResultFailedException("服务端返回，" + body.getMessage());
                }
            }
        } catch (NetResultFailedException e) {
            throw e;
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

    public void deleteExpress(Express express) {
        if (express.getPhotoPathList() != null) {
            for (String path : express.getPhotoPathList()) {
                FileUtil.deleteImg(path);
            }
        }
        ExpressModel.deleteExpress(express);
    }

    public Express loadExpressWithCode(String code) {
        return ExpressModel.loadExpressWithCode(code);
    }

}
