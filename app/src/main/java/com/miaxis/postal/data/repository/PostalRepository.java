package com.miaxis.postal.data.repository;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.miaxis.postal.data.dto.TempIdDto;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.TempId;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.model.ExpressModel;
import com.miaxis.postal.data.model.IDCardRecordModel;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.data.net.ResponseEntity;
import com.miaxis.postal.util.FileUtil;
import com.miaxis.postal.util.ValueUtil;
import com.speedata.libid2.IDInfor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

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

    public TempId savePersonFromAppSync(IDCardRecord idCardRecord) throws IOException, MyException {
        String cardFilePath = FileUtil.FACE_IMAGE_PATH + File.separator + "card_" + idCardRecord.getCardNumber() + System.currentTimeMillis() + ".jpg";
        String checkFilePath = FileUtil.FACE_IMAGE_PATH + File.separator + "check" + idCardRecord.getCardNumber() + System.currentTimeMillis() + ".jpg";
        File cardFile = FileUtil.saveBitmap(idCardRecord.getCardBitmap(), cardFilePath);
        File checkFile = FileUtil.saveBitmap(idCardRecord.getFaceBitmap(), checkFilePath);
        Response<ResponseEntity<TempIdDto>> execute = PostalApi.savePersonFromApp(
                idCardRecord.getName(),
                idCardRecord.getNation(),
                idCardRecord.getBirthday(),
                idCardRecord.getCardNumber(),
                idCardRecord.getAddress(),
                idCardRecord.getSex(),
                idCardRecord.getIssuingAuthority(),
                idCardRecord.getValidateStart(),
                checkFile,
                cardFile)
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
        } finally {
            cardFile.delete();
            checkFile.delete();
        }
        throw new MyException("服务端返回，空数据");
    }

    public boolean saveExpressFromAppSync(Express express, TempId tempId, String sendAddress, String sendPhone) throws IOException, MyException {
        List<File> fileList = new ArrayList<>();
        for (int i = 0; i < express.getPhotoList().size(); i++) {
            Bitmap bitmap = express.getPhotoList().get(i);
            String path = FileUtil.ORDER_IMAGE_PATH + File.separator + i + System.currentTimeMillis() + ".jpg";
            File file = FileUtil.saveBitmap(bitmap, path);
            fileList.add(file);
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
        } finally {
            for (File file : fileList) {
                file.delete();
            }
        }
        throw new MyException("服务端返回，空数据");
    }

    public void uploadLocalExpress() {
        List<IDCardRecord> idCardRecordList = IDCardRecordModel.loadAll();
        for (IDCardRecord idCardRecord : idCardRecordList) {
            List<Express> expressList = ExpressModel.loadExpress(idCardRecord.getVerifyId());
            if (expressList != null && !expressList.isEmpty()) {
                TempId tempId;
                List<Express> failedExpressList = new ArrayList<>();
                try {
                    tempId = uploadLocalIDCardRecord(idCardRecord);
                    for (Express express : expressList) {
                        try {
                            uploadLocalExpress(tempId, express);
                            ExpressModel.deleteExpress(express);
                        } catch (IOException | MyException e) {
                            e.printStackTrace();
                            failedExpressList.add(express);
                        }
                    }
                    if (failedExpressList.isEmpty()) {
                        IDCardRecordModel.deleteIDCardRecord(idCardRecord);
                    }
                } catch (MyException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private TempId uploadLocalIDCardRecord(IDCardRecord idCardRecord) throws MyException, IOException {
        File cardFile = new File(idCardRecord.getCardPicture());
        File faceFile = new File(idCardRecord.getFacePicture());
        Response<ResponseEntity<TempIdDto>> execute = PostalApi.savePersonFromApp(
                idCardRecord.getName(),
                idCardRecord.getNation(),
                idCardRecord.getBirthday(),
                idCardRecord.getCardNumber(),
                idCardRecord.getAddress(),
                idCardRecord.getSex(),
                idCardRecord.getIssuingAuthority(),
                idCardRecord.getValidateStart(),
                faceFile,
                cardFile)
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

    private boolean uploadLocalExpress(TempId tempId, Express express) throws IOException, MyException {
        List<File> fileList = new ArrayList<>();
        for (String path : express.getPhotoPathList()) {
            fileList.add(new File(path));
        }
        Response<ResponseEntity> execute = PostalApi.saveOrderFromAppSync(
                tempId.getPersonId(),
                express.getSenderAddress(),
                express.getSenderPhone(),
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

    public void saveLocalExpress(IDCardRecord idCardRecord, List<Express> expressList, String address, String phone) {
        String verifyId = saveLocalIDCardRecord(idCardRecord);
        for (Express express : expressList) {
            saveLocalExpress(verifyId, express, address, phone);
        }
    }

    private String saveLocalIDCardRecord(IDCardRecord idCardRecord) {
        String cardPath = FileUtil.FACE_STOREHOUSE_PATH + File.separator + "card_" +idCardRecord.getCardNumber() + System.currentTimeMillis() + ".jpg";
        String facePath = FileUtil.FACE_STOREHOUSE_PATH + File.separator + "face_" +idCardRecord.getCardNumber() + System.currentTimeMillis() + ".jpg";
        FileUtil.saveBitmap(idCardRecord.getCardBitmap(), cardPath);
        FileUtil.saveBitmap(idCardRecord.getFaceBitmap(), facePath);
        idCardRecord.setCardPicture(cardPath);
        idCardRecord.setFacePicture(facePath);
        String verifyId = idCardRecord.getCardNumber() + "_" + System.currentTimeMillis();
        idCardRecord.setVerifyId(verifyId);
        IDCardRecordModel.saveIDCardRecord(idCardRecord);
        return verifyId;
    }

    private void saveLocalExpress(String verifyId, Express express, String address, String phone) {
        List<Bitmap> photoList = express.getPhotoList();
        List<String> pathList = new ArrayList<>();
        for (int i = 0; i < photoList.size(); i++) {
            Bitmap bitmap = photoList.get(i);
            String path = FileUtil.ORDER_STOREHOUSE_PATH +File.separator + express.getBarCode() + "_" + System.currentTimeMillis() + "_" + i + ".jpg";
            FileUtil.saveBitmap(bitmap, path);
            pathList.add(path);
        }
        express.setPhotoPathList(pathList);
        express.setSenderAddress(address);
        express.setSenderPhone(phone);
        express.setVerifyId(verifyId);
        ExpressModel.saveExpress(express);
    }

}
