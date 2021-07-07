package com.miaxis.postal.data.repository;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

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
import com.miaxis.postal.util.StringUtils;
import com.miaxis.postal.util.ValueUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
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

    public void uploadLocalExpress(Express express, TempId tempId, Integer warnLogId, String sendName, int chekStatus) throws IOException, MyException, NetResultFailedException {
        //        List<File> fileList = new ArrayList<>();
        //        for (String path : express.getPhotoPathList()) {
        //            fileList.add(new File(path));
        //        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String path : express.getPhotoPathList()) {
            try {
                Call<ResponseEntity> responseEntityCall = PostalApi.saveOrderPhoto(new File(path));
                Response<ResponseEntity> execute = responseEntityCall.execute();
                ResponseEntity body = execute.body();
                if (body!=null) {
                    if (StringUtils.isEquals(ValueUtil.SUCCESS, body.getCode())) {
                        stringBuilder.append((String) body.getData()).append(",");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                //Log.e("saveOrderPhoto", "Exception:" + e);
                throw new MyException("" + e);
            }
        }
        //Log.e("saveOrderPhoto", "FileList:" + stringBuilder.toString());
        if (stringBuilder.toString().endsWith(",")) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        //Log.e("saveOrderPhoto", "FileList:" + stringBuilder.toString());
        if (TextUtils.isEmpty(express.getBarCode())) {
            throw new MyException("订单号为空");
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
                stringBuilder.toString())
                .execute();
        try {
            ResponseEntity body = execute.body();
            if (body != null) {
                if (!TextUtils.equals(body.getCode(), ValueUtil.SUCCESS)) {
                    throw new NetResultFailedException("服务端返回，" + body.getMessage());
                }
            } else {
                int code = execute.code();
                Log.e("uploadLocalExpress", "code:" + code);
                ResponseBody responseBody = execute.errorBody();
                String string = responseBody.string();
                Log.e("uploadLocalExpress", "errorBody:" + string);
                throw new MyException("服务端返回，空数据，code:" + code + " error:" + string);
            }
        } catch (NetResultFailedException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(e.getMessage());
        }
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
