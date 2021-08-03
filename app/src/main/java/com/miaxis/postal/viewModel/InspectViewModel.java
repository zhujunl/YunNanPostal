package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.Photograph;
import com.miaxis.postal.data.event.ExpressEditEvent;
import com.miaxis.postal.util.BarcodeUtil;
import com.miaxis.postal.util.FileUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

public class InspectViewModel extends BaseViewModel {
    private final String TAG = "InspectViewModel";

    public static final int MAX_COUNT = 1;

    public ObservableField<Express> express = new ObservableField<>();
    public MutableLiveData<List<Photograph>> photographList = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<Boolean> barcodeImageUpdate = new SingleLiveEvent<>();

    private boolean modified = false;

    public Bitmap barcodeBitmapCache = null;

    public InspectViewModel() {
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void initExpress(Express express) {
        Log.e(TAG, "express:" + express);
        //        if (express.getPhotoList() != null) {
        //            List<Photograph> expressPhotoList = getPhotographList();
        //            for (Bitmap bitmap : express.getPhotoList()) {
        //                expressPhotoList.add(new Photograph(bitmap, true));
        //            }
        //            photographList.setValue(expressPhotoList);
        //        }
        List<String> photoPathList = express.getPhotoPathList();
        if (photoPathList != null && !photoPathList.isEmpty()) {
            List<Photograph> expressPhotoList = getPhotographList();
            for (String path : photoPathList) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                expressPhotoList.add(new Photograph(bitmap, true));
            }
            photographList.setValue(expressPhotoList);
        }
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
                    if (i + 1 > cacheList.size())
                        break;
                    cacheList.get(i).setSelect(true);
                }
            }
        }
        List<Photograph> photoList = getPhotographList();
        photoList.addAll(cacheList);
        if (!cacheList.isEmpty()) {
            modified = true;
        }
        this.photographList.setValue(photoList);
    }

    public void makeModifyResult() {
        Express local = express.get();
        if (local != null) {
            local.setComplete(true);
            List<Bitmap> selectList = getSelectList();
            local.setPhotoList(selectList);
            local.setPhotoPathList(bitmapToPaths(selectList));
            EventBus.getDefault().postSticky(new ExpressEditEvent(ExpressEditEvent.MODE_MODIFY, local));
        }
    }

    private List<String> bitmapToPaths(List<Bitmap> paths) {
        List<String> objects = new ArrayList<>();
        if (paths != null && !paths.isEmpty()) {
            for (Bitmap bitmap : paths) {
                String path = FileUtil.IMAGE_PATH + File.separator + System.currentTimeMillis() + ".jpeg";
                boolean saveBitmapToJPEG = FileUtil.saveBitmapToJPEG(bitmap, path);
                if (saveBitmapToJPEG) {
                    objects.add(path);
                }
            }
        }
        return objects;
    }

    public void makeDraftResult() {
        Express local = express.get();
        if (local != null) {
            local.setComplete(false);
            List<Bitmap> selectList = getSelectList();
            local.setPhotoList(selectList);
            local.setPhotoPathList(bitmapToPaths(selectList));
            EventBus.getDefault().postSticky(new ExpressEditEvent(ExpressEditEvent.MODE_MODIFY, local));
        }
    }

    public void makeDeleteResult() {
        Express local = express.get();
        if (local != null) {
            List<Bitmap> selectList = getSelectList();
            local.setPhotoList(selectList);
            local.setPhotoPathList(bitmapToPaths(selectList));
            EventBus.getDefault().postSticky(new ExpressEditEvent(ExpressEditEvent.MODE_DELETE, local));
        }
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

    public boolean checkEmptyExpress() {
        Express value = this.express.get();
        if (value == null)
            return true;
        return value.getPhotoList() == null || value.getPhotoList().isEmpty();
    }

    public boolean needBackCheck() {
        return modified;
    }

    public void showBarcodeImage(String barcode) {
        App.getInstance().getThreadExecutor().execute(() -> {
            if (!TextUtils.isEmpty(barcode) && !barcode.startsWith(App.getInstance().BarHeader)) {
                barcodeBitmapCache = BarcodeUtil.createBarcodeBitmap(barcode);
                if (barcodeBitmapCache != null) {
                    barcodeImageUpdate.postValue(Boolean.TRUE);
                }
            } else {
                barcodeImageUpdate.postValue(Boolean.TRUE);
            }
        });
    }

    public void alarm() {
        Express local = express.get();
        if (local != null) {
            List<Bitmap> selectList = getSelectList();
            local.setPhotoList(selectList);
            local.setPhotoPathList(bitmapToPaths(selectList));
            EventBus.getDefault().postSticky(new ExpressEditEvent(ExpressEditEvent.MODE_ALARM, local));
        }
    }

}
