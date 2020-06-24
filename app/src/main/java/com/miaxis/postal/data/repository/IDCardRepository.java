package com.miaxis.postal.data.repository;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;

import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.IDCard;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.model.IDCardModel;
import com.miaxis.postal.util.EncryptUtil;
import com.miaxis.postal.util.FileUtil;

import java.io.File;
import java.util.Date;
import java.util.List;

public class IDCardRepository {

    private IDCardRepository() {
    }

    public static IDCardRepository getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final IDCardRepository instance = new IDCardRepository();
    }

    /**
     * ================================ 静态内部类单例 ================================
     **/

    public void addNewIDCard(IDCardRecord idCardRecord) {
        App.getInstance().getThreadExecutor().execute(() -> {
            IDCard search = IDCardRepository.getInstance().findIDCardByCardNumber(idCardRecord.getCardNumber());
            if (search == null) {
                IDCard idCard = makeIDCard(idCardRecord);
                IDCardRepository.getInstance().saveIDCard(idCard);
                clearSurplus();
            } else {
                search.setVerifyTime(new Date());
                IDCardRepository.getInstance().updateIdCard(search);
            }
        });
    }

    private IDCard makeIDCard(IDCardRecord idCardRecord) {
        IDCard idCard = transIDCard(idCardRecord);
        String path = FileUtil.LOCAL_STOREHOUSE_PATH + File.separator + idCard.getCardNumber() + System.currentTimeMillis() + EncryptUtil.getRandomString(10) + ".png";
        File file = FileUtil.saveQualityBitmap(idCardRecord.getCardBitmap(), path);
        if (file != null) {
            idCard.setCardPicture(path);
        }
        idCard.setVerifyTime(new Date());
        return idCard;
    }

    private IDCard transIDCard(IDCardRecord idCardRecord) {
        return new IDCard.Builder()
                .cardType("")
                .name(idCardRecord.getName())
                .birthday(idCardRecord.getBirthday())
                .address(idCardRecord.getAddress())
                .cardNumber(idCardRecord.getCardNumber())
                .issuingAuthority(idCardRecord.getIssuingAuthority())
                .validateStart(idCardRecord.getValidateStart())
                .validateEnd(idCardRecord.getValidateEnd())
                .sex(idCardRecord.getSex())
                .nation(idCardRecord.getNation())
                .passNumber(idCardRecord.getPassNumber())
                .issueCount(idCardRecord.getIssueCount())
                .chineseName(idCardRecord.getChineseName())
                .version(idCardRecord.getVersion())
                .build();
    }

    private void clearSurplus() {
        IDCardRepository idCardRepository = IDCardRepository.getInstance();
        if (idCardRepository.loadIDCardCount() > 200) {
            IDCard oldestIDCard = idCardRepository.findOldestIDCard();
            if (oldestIDCard != null) {
                idCardRepository.deleteIDCard(oldestIDCard);
            }
        }
    }

    public void saveIDCard(IDCard idCard) {
        IDCardModel.save(idCard);
    }

    public void updateIdCard(IDCard idCard) {
        IDCardModel.update(idCard);
    }

    public IDCard findOldestIDCard() {
        return IDCardModel.findOldestIDCard();
    }

    public IDCard findIDCardByCardNumber(String cardNumber) {
        return IDCardModel.findIDCardByCardNumber(cardNumber);
    }

    public int loadIDCardCount() {
        return IDCardModel.loadIDCardCount();
    }

    public void deleteIDCard(IDCard idCard) {
        if (!TextUtils.isEmpty(idCard.getCardPicture())) {
            FileUtil.deleteImg(idCard.getCardPicture());
        }
        IDCardModel.delete(idCard);
    }

    public LiveData<List<IDCard>> loadAllWithLiveData() {
        return IDCardModel.loadAllWithLiveData();
    }

}
