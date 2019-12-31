package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.data.entity.Photograph;
import com.miaxis.postal.data.event.ExpressEditEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class InspectViewModel extends BaseViewModel {

    public static final int MAX_COUNT = 5;

    public ObservableField<Express> express = new ObservableField<>();
    public MutableLiveData<List<Photograph>> photographList = new MutableLiveData<>(new ArrayList<>());

    private boolean modified = false;

    public InspectViewModel() {
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void initExpress(Express express) {
        if (express.getPhotoList() != null) {
            List<Photograph> expressPhotoList = getPhotographList();
            for (Bitmap bitmap : express.getPhotoList()) {
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
                    if (i + 1 > cacheList.size()) break;
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
            local.setPhotoList(getSelectList());
            EventBus.getDefault().postSticky(new ExpressEditEvent(ExpressEditEvent.MODE_MODIFY, local));
        }
    }

    public void makeDeleteResult() {
        Express local = express.get();
        if (local != null) {
            local.setPhotoList(getSelectList());
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
        if (value == null) return true;
        return value.getPhotoList() == null || value.getPhotoList().isEmpty();
    }

    public boolean needBackCheck() {
        return modified;
    }

}
