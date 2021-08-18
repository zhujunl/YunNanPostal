package com.miaxis.postal.data.repository;

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
import com.miaxis.postal.util.ListUtils;
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
        //上传订单图片 判断是否一致
        List<String> stringList = new ArrayList<>();
        if (!ListUtils.isNullOrEmpty(express.getPhotoPathList())) {
            for (String path : express.getPhotoPathList()) {
                try {
                    Call<ResponseEntity> responseEntityCall = PostalApi.saveOrderPhoto(new File(path));
                    Response<ResponseEntity> execute = responseEntityCall.execute();
                    ResponseEntity body = execute.body();
                    if (body != null) {
                        if (StringUtils.isEquals(ValueUtil.SUCCESS, body.getCode())) {
                            try {
                                String data = (String) body.getData();
                                stringList.add(data);
                                Log.e("ExpressRepository", "" + data);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ExpressRepository", "Exception:" + e);
                }
            }
        }
        //        if (express.getPhotoPathList().size() != 0) {
        //            //如果长度不相等证明订单图片丢失 则不上传
        //            if (stringList.size() != express.getPhotoPathList().size()) {
        //                //删除图片操作 如果不通过图片记录需要删除
        //                for (String path : stringList) {
        //                    if (!TextUtils.isEmpty(path)) {
        //                        deleteWebPicture(path);
        //                    }
        //                }
        //                throw new MyException("订单异常，请删除后重试！");
        //            }
        //        }
        //else {
        //   throw new MyException("该订单未拍照，无法上传。");
        //}
        StringBuilder stringBuilder = new StringBuilder();
        //添加图片
        for (String s : stringList) {
            stringBuilder.append(s).append(",");
        }
        if (stringBuilder.toString().endsWith(",")) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        if (TextUtils.isEmpty(express.getBarCode())) {
            throw new MyException("订单号为空");
        }
        Courier courier = DataCacheManager.getInstance().getCourier();
        String type = express.getCustomerType();
        Response<ResponseEntity> execute = sendOrder(("1".equals(type)), courier, tempId, warnLogId, sendName, chekStatus, express, stringBuilder.toString());
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

    //提交订单筛选
    private Response<ResponseEntity> sendOrder(boolean type, Courier courier, TempId tempId, Integer warnLogId, String sendName, int chekStatus, Express express, String photos) throws IOException, MyException, NetResultFailedException {
        Response<ResponseEntity> execute;
        String warnLog = warnLogId != null ? String.valueOf(warnLogId) : "";
        Log.e("Express", "sendOrder:" + express);
        if (type) {
            execute = PostalApi.saveOrderFromAppSync(
                    express.getOrgCode(),
                    express.getOrgNode(),
                    tempId.getPersonId(),
                    tempId.getCheckId(),
                    warnLog,
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
                    photos).execute();
        } else {
            execute = PostalApi.saveOrderFromAppSync(
                    express.getOrgCode(),
                    express.getOrgNode(),
                    tempId.getPersonId(),
                    tempId.getCheckId(),
                    warnLog,
                    String.valueOf(courier.getCourierId()),
                    express.getSenderAddress(),
                    express.getCustomerPhone(),
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
                    photos,
                    express.getCustomerName(), express.getGoodsName(), Integer.parseInt(express.getGoodsNumber())).execute();
        }
        return execute;
    }

    //删除网络图片
    public void deleteWebPicture(String path) {
        try {
            Call<ResponseEntity> responseEntityCall = PostalApi.deleteWebPicture(path);
            Response<ResponseEntity> execute = responseEntityCall.execute();
            //            ResponseEntity body = execute.body();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //保存图片
    public List<String> saveImage(List<String> path) {
        List<String> webPicturePath = new ArrayList<>();
        if (path == null || path.isEmpty()) {
            return webPicturePath;
        }
        for (String s : path) {
            try {
                Call<ResponseEntity> responseEntityCall = PostalApi.saveOrderPhoto(new File(s));
                Response<ResponseEntity> execute = responseEntityCall.execute();
                ResponseEntity body = execute.body();
                if (body != null) {
                    if (StringUtils.isEquals(ValueUtil.SUCCESS, body.getCode())) {
                        webPicturePath.add((String) body.getData());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                webPicturePath.add("");
            }
        }
        return webPicturePath;
    }

    public void saveExpress(Express express) throws MyException {
        //        List<Bitmap> photoList = express.getPhotoList();
        //        List<String> pathList = new ArrayList<>();
        //        for (int i = 0; i < photoList.size(); i++) {
        //            Bitmap bitmap = photoList.get(i);
        //            String path = FileUtil.ORDER_STOREHOUSE_PATH + File.separator + express.getBarCode() + "_" + System.currentTimeMillis() + "_" + i + ".jpg";
        //            FileUtil.saveBitmap(bitmap, path);
        //            pathList.add(path);
        //        }
        //        express.setPhotoPathList(pathList);
        ExpressModel.saveExpress(express);
    }

    public void updateExpress(Express express) throws MyException {
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

    public int loadExpressAllCount() {
        return ExpressModel.loadExpressAllCount();
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
