package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.IDCardRecord;
import com.miaxis.postal.data.entity.Photograph;
import com.miaxis.postal.util.BarcodeUtil;
import com.miaxis.postal.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.MutableLiveData;

public class RecordLocalUpdateViewModel extends BaseViewModel {

    public static final int MAX_COUNT = 1;

    public MutableLiveData<IDCardRecord> idCardRecord = new MutableLiveData<>(null);
    public MutableLiveData<Express> express = new MutableLiveData<>(null);
    public MutableLiveData<List<Photograph>> photographList = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<Boolean> orderCodeImageUpdate = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> updateResult = new SingleLiveEvent<>();

    public Bitmap orderCodeBitmapCache = null;

    public RecordLocalUpdateViewModel() {
    }

    public void initData(IDCardRecord mIdCardRecord, Express mExpress) {
        if (idCardRecord.getValue() == null && express.getValue() == null) {
            idCardRecord.setValue(mIdCardRecord);
            express.setValue(mExpress);
            initExpressPhoto(mExpress);
        }
    }

    public void showBarcodeImage(String barcode) {
        App.getInstance().getThreadExecutor().execute(() -> {
            if (!TextUtils.isEmpty(barcode) && !barcode.startsWith(App.getInstance().BarHeader)) {
                orderCodeBitmapCache = BarcodeUtil.createBarcodeBitmap(barcode);
                if (orderCodeBitmapCache != null) {
                    orderCodeImageUpdate.postValue(Boolean.TRUE);
                }
            } else {
                orderCodeImageUpdate.postValue(Boolean.TRUE);
            }
        });
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

    private void initExpressPhoto(Express express) {
        App.getInstance().getThreadExecutor().execute(() -> {
            for (String filePath : express.getPhotoPathList()) {
                Bitmap bitmap = FileUtil.loadBitmap(filePath);
                if (bitmap != null) {
                    addPhotograph(bitmap);
                }
            }
        });
    }

    public void addPhotograph(Bitmap bitmap) {
        List<Photograph> cacheList = new ArrayList<>();
        cacheList.add(new Photograph(bitmap, true, true));
        int selectSize = getSelectSize();
        if (selectSize < MAX_COUNT && cacheList.size() > 0) {
            int surplus = MAX_COUNT - selectSize;
            if (surplus > 0) {
                for (int i = 0; i < surplus; i++) {
                    if (i + 1 > cacheList.size())
                        break;
                    cacheList.get(i).setSelect(true);
                }
            }
        }
        List<Photograph> photoList = getPhotographList();
        photoList.addAll(cacheList);
        this.photographList.postValue(photoList);
    }

    public void addPhotograph(List<Bitmap> bitmapList) {
        List<Photograph> cacheList = new ArrayList<>();
        for (Bitmap bitmap : bitmapList) {
            cacheList.add(new Photograph(bitmap, false, false));
        }
        int selectSize = getSelectSize();
        if (selectSize < InspectViewModel.MAX_COUNT && cacheList.size() > 0) {
            int surplus = InspectViewModel.MAX_COUNT - selectSize;
            if (surplus > 0) {
                for (int i = 0; i < surplus; i++) {
                    if (i + 1 > cacheList.size())
                        break;
                    cacheList.get(i).setSelect(true);
                }
            }
        }
        List<Photograph> photoList = getPhotographList();
        photoList.addAll(cacheList);
        this.photographList.setValue(photoList);
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

}
