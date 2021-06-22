package com.miaxis.postal.data.repository;

import android.text.TextUtils;

import com.miaxis.postal.data.dto.TempIdDto;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.TempId;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.exception.NetResultFailedException;
import com.miaxis.postal.data.model.IDCardRecordModel;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.data.net.ResponseEntity;
import com.miaxis.postal.manager.DataCacheManager;
import com.miaxis.postal.util.DateUtil;
import com.miaxis.postal.util.FileUtil;
import com.miaxis.postal.util.ValueUtil;

import java.io.File;
import java.io.IOException;
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
        File cardFile = !TextUtils.isEmpty(idCardRecord.getCardPicture()) ? new File(idCardRecord.getCardPicture()) : null;
        File faceFile = !TextUtils.isEmpty(idCardRecord.getFacePicture()) ? new File(idCardRecord.getFacePicture()) : null;
        Courier courier = DataCacheManager.getInstance().getCourier();
        Response<ResponseEntity<TempIdDto>> execute = PostalApi.savePersonFromApp(
                courier.getOrgCode(),
                courier.getOrgNode(),
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
                faceFile,
                cardFile).execute();
        try {
            ResponseEntity<TempIdDto> body = execute.body();
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS) && body.getData() != null) {
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
        if (idCardRecord.getCardBitmap() != null) {
            String cardPath = FileUtil.FACE_STOREHOUSE_PATH + File.separator + "card_" +idCardRecord.getCardNumber() + System.currentTimeMillis() + ".jpg";
            FileUtil.saveBitmap(idCardRecord.getCardBitmap(), cardPath);
            idCardRecord.setCardPicture(cardPath);
        }
        if (idCardRecord.getFaceBitmap() != null) {
            String facePath = FileUtil.FACE_STOREHOUSE_PATH + File.separator + "face_" +idCardRecord.getCardNumber() + System.currentTimeMillis() + ".jpg";
            FileUtil.saveBitmap(idCardRecord.getFaceBitmap(), facePath);
            idCardRecord.setFacePicture(facePath);
        }
        String verifyId = idCardRecord.getCardNumber() + "_" + System.currentTimeMillis();
        idCardRecord.setVerifyId(verifyId);
        IDCardRecordModel.saveIDCardRecord(idCardRecord);
        return verifyId;
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
