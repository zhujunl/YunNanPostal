package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.data.entity.Photograph;

import java.util.ArrayList;
import java.util.List;

public class InspectViewModel extends BaseViewModel {

    public static final int MAX_COUNT = 5;

    public MutableLiveData<List<Photograph>> photographList = new MutableLiveData<>(new ArrayList<>());

    public InspectViewModel() {
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void addPhotograph(List<Bitmap> bitmapList) {
        List<Photograph> photoList = photographList.getValue();
        for (Bitmap bitmap : bitmapList) {
            photoList.add(new Photograph(bitmap, false));
        }
        photographList.setValue(photoList);
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

    public List<Photograph> getSelectList() {
        List<Photograph> selectList = new ArrayList<>();
        for (Photograph photograph : getPhotographList()) {
            if (photograph.isSelect()) {
                selectList.add(photograph);
            }
        }
        return selectList;
    }

}
