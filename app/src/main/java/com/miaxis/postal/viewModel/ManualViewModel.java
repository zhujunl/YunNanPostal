package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.Photograph;
import com.miaxis.postal.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ManualViewModel extends BaseViewModel {

    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> identityNumber = new ObservableField<>();

    public MutableLiveData<List<Photograph>> photographList = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<Boolean> confirm = new SingleLiveEvent<>();

    public IDCardRecord idCardRecord;

    public ManualViewModel() {
    }

    public void addPhotograph(List<Bitmap> bitmapList) {
        List<Photograph> cacheList = new ArrayList<>();
        for (Bitmap bitmap : bitmapList) {
            cacheList.add(new Photograph(bitmap, false));
        }
        int selectSize = getSelectSize();
        if (selectSize < InspectViewModel.MAX_COUNT && cacheList.size() > 0) {
            int surplus = InspectViewModel.MAX_COUNT - selectSize;
            if (surplus > 0) {
                for (int i = 0; i < surplus; i++) {
                    if (i + 1 > cacheList.size()) break;
                    cacheList.get(i).setSelect(true);
                }
            }
        }
        List<Photograph> photoList = getPhotographList();
        photoList.addAll(cacheList);
        this.photographList.setValue(photoList);
    }

    public List<Photograph> getPhotographList() {
        List<Photograph> value = photographList.getValue();
        if (value == null) {
            List<Photograph> newArrayList = new ArrayList<>();
            photographList.setValue(newArrayList);
            return newArrayList;
        } else {
            return value;
        }
    }

    public int getSelectSize() {
        int count = 0;
        for (Photograph photograph : getPhotographList()) {
            if (photograph.isSelect()) {
                count++;
            }
        }
        return count;
    }

    public List<Bitmap> getSelectList() {
        List<Bitmap> selectList = new ArrayList<>();
        for (Photograph photograph : getPhotographList()) {
            if (photograph.isSelect()) {
                selectList.add(photograph.getBitmap());
            }
        }
        return selectList;
    }

    public void confirm() {
        waitMessage.setValue("确认中，请稍后...");
        App.getInstance().getThreadExecutor().execute(() -> {
            try {
                if (idCardRecord == null) {
                    idCardRecord = makeIDCardRecord();
                } else {
                    idCardRecord.setManualType("1");
                }
                if (idCardRecord.getCardBitmap() != null) {
                    String cardPath = FileUtil.FACE_STOREHOUSE_PATH + File.separator + "card_" +idCardRecord.getCardNumber() + System.currentTimeMillis() + ".jpg";
                    FileUtil.saveBitmap(idCardRecord.getCardBitmap(), cardPath);
                    idCardRecord.setCardPicture(cardPath);
                }
                List<Bitmap> selectList = getSelectList();
                Bitmap bitmap = selectList.get(0);
                idCardRecord.setFaceBitmap(bitmap);
                String facePath = FileUtil.FACE_STOREHOUSE_PATH + File.separator + "face_" +idCardRecord.getCardNumber() + System.currentTimeMillis() + ".jpg";
                FileUtil.saveBitmap(idCardRecord.getFaceBitmap(), facePath);
                idCardRecord.setFacePicture(facePath);
                waitMessage.postValue("");
                confirm.postValue(Boolean.TRUE);
            } catch (Exception e) {
                e.printStackTrace();
                waitMessage.postValue("");
                confirm.postValue(Boolean.FALSE);
            }
        });
    }

    private IDCardRecord makeIDCardRecord() {
        String nameStr = name.get();
        String identityNumberStr = identityNumber.get();
        return new IDCardRecord.Builder()
                .name(nameStr)
                .cardNumber(identityNumberStr)
                .manualType("2")
                .build();
    }

}
