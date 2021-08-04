package com.miaxis.postal.data.repository;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.miaxis.postal.data.dto.TempIdDto;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.TempId;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.exception.NetResultFailedException;
import com.miaxis.postal.data.model.IDCardRecordModel;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.data.net.ResponseEntity;
import com.miaxis.postal.util.DateUtil;
import com.miaxis.postal.util.FileUtil;
import com.miaxis.postal.util.ValueUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class IDCardRecordRepository {

    private IDCardRecordRepository() {
    }

    public static IDCardRecordRepository getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final IDCardRecordRepository instance = new IDCardRecordRepository();
    }

    /**
     * ================================ 静态内部类单例 ================================
     **/

    public TempId uploadIDCardRecord(IDCardRecord idCardRecord) throws MyException, IOException, NetResultFailedException {
//        File cardFile = !TextUtils.isEmpty(idCardRecord.getCardPicture()) ? new File(idCardRecord.getCardPicture()) : null;
//        File faceFile = !TextUtils.isEmpty(idCardRecord.getFacePicture()) ? new File(idCardRecord.getFacePicture()) : null;
        //拆分成两个接口
        Log.e("uploadIDCardRecord","idCardRecord:"+idCardRecord);
        String webCardPath=null;
        String webFacePath=null;
        if (TextUtils.isEmpty(idCardRecord.getWebCardPath())||TextUtils.isEmpty(idCardRecord.getWebFacePath())){
            if(!TextUtils.isEmpty(idCardRecord.getCardPicture())||!TextUtils.isEmpty(idCardRecord.getFacePicture())){
                List<String> paths=new ArrayList<>();
                paths.add(idCardRecord.getCardPicture());
                paths.add(idCardRecord.getFacePicture());
                Log.e("uploadIDCardRecord","paths:"+paths);
                List<String> stringList = ExpressRepository.getInstance().saveImage(paths);
                Log.e("uploadIDCardRecord","stringList:"+stringList);
                if (stringList!=null&&!stringList.isEmpty()&&stringList.size()>=2){
                    webCardPath=stringList.get(0);
                    webFacePath=stringList.get(1);
                }
            }
        }else{
            webCardPath=idCardRecord.getWebCardPath();
            webFacePath=idCardRecord.getWebFacePath();
        }
        Log.e("uploadIDCardRecord","webCardPath:"+webCardPath);
        Log.e("uploadIDCardRecord","webFacePath:"+webFacePath);
        //Courier courier = DataCacheManager.getInstance().getCourier();
        String orgCode = ValueUtil.readOrgCode();
        String orgNode = ValueUtil.readOrgNode();
        Response<ResponseEntity<TempIdDto>> execute = PostalApi.savePersonFromApp(
                orgCode,
                orgNode,
                idCardRecord.getName(),
                idCardRecord.getNation(),
                idCardRecord.getBirthday(),
                idCardRecord.getCardNumber(),
                idCardRecord.getAddress(),
                idCardRecord.getSex(),
                idCardRecord.getIssuingAuthority(),
                idCardRecord.getValidateEnd(),
                idCardRecord.getVerifyType(),
                DateUtil.DATE_FORMAT.format(idCardRecord.getVerifyTime()),
                idCardRecord.getManualType(),
                webCardPath,
                webFacePath).execute();
        try {
            ResponseEntity<TempIdDto> body = execute.body();
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS) && body.getData() != null) {
                    try {
                        Log.e("uploadIDCardRecord","webCardPath:"+body.getData().transform());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return body.getData().transform();
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

    public String saveIdCardRecord(IDCardRecord idCardRecord) {
        Log.i("TAG===","执行");
        if (idCardRecord.getCardPicture()!=null){
            FileUtil.deleteFolderFile(idCardRecord.getCardPicture(),false);
        }
        if (idCardRecord.getFacePicture()!=null){
            FileUtil.deleteFolderFile(idCardRecord.getFacePicture(),false);
        }
        if (idCardRecord.getCardBitmap() != null) {
            String cardPath = FileUtil.FACE_STOREHOUSE_PATH + File.separator + "card_" + idCardRecord.getCardNumber() + System.currentTimeMillis() + ".jpg";
            FileUtil.saveBitmap(idCardRecord.getCardBitmap(), cardPath);
            idCardRecord.setCardPicture(cardPath);
        }
        if (idCardRecord.getFaceBitmap() != null) {
            String facePath = FileUtil.FACE_STOREHOUSE_PATH + File.separator + "face_" + idCardRecord.getCardNumber() + System.currentTimeMillis() + ".jpg";
            FileUtil.saveBitmap(idCardRecord.getFaceBitmap(), facePath);
            idCardRecord.setFacePicture(facePath);
        }
        String verifyId = idCardRecord.getCardNumber() + "_" + System.currentTimeMillis();
        idCardRecord.setVerifyId(verifyId);
        IDCardRecordModel.saveIDCardRecord(idCardRecord);
        return verifyId;
    }

    //保存身份证头像 人证核验头像
    public List<String> saveFace(String cardNum, Bitmap cardBitmap, Bitmap faceBitmap) {
        Log.i("TAG===","执行2");
        List<String> path = new ArrayList<>();
        String cardPath = FileUtil.FACE_STOREHOUSE_PATH + File.separator + "card_" + cardNum + System.currentTimeMillis() + ".jpg";
        FileUtil.saveBitmap(cardBitmap, cardPath);
        String facePath = FileUtil.FACE_STOREHOUSE_PATH + File.separator + "face_" + cardNum + System.currentTimeMillis() + ".jpg";
        FileUtil.saveBitmap(faceBitmap, facePath);
        path.add(cardPath);
        path.add(facePath);
        return path;
    }


    public void updateIdCardRecord(IDCardRecord idCardRecord) {
        IDCardRecordModel.saveIDCardRecord(idCardRecord);
    }

    public IDCardRecord findOldestIDCardRecord() {
        return IDCardRecordModel.findOldestIDCardRecord();
    }

    public List<IDCardRecord> loadAllNotDraft() {
        return IDCardRecordModel.loadAllNotDraft();
    }

    public IDCardRecord loadIDCardRecordByVerifyId(String verifyId) {
        return IDCardRecordModel.loadIDCardRecordByVerifyId(verifyId);
    }

    public IDCardRecord loadIDCardRecordById(Long id) {
        return IDCardRecordModel.loadIDCardRecordById(id);
    }

    public void deleteIDCardRecord(IDCardRecord idCardRecord) {
        if (!TextUtils.isEmpty(idCardRecord.getCardPicture())) {
            FileUtil.deleteImg(idCardRecord.getCardPicture());
        }
        if (!TextUtils.isEmpty(idCardRecord.getFacePicture())) {
            FileUtil.deleteImg(idCardRecord.getFacePicture());
        }
        IDCardRecordModel.deleteIDCardRecord(idCardRecord);
    }

    public List<IDCardRecord> loadDraftIDCardRecordByPage(int pageNum, int pageSize) {
        return IDCardRecordModel.loadDraftIDCardRecordByPage(pageNum, pageSize);
    }

}
