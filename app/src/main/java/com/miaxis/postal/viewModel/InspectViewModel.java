package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.data.entity.Photograph;

import java.util.ArrayList;
import java.util.List;

public class InspectViewModel extends BaseViewModel {

    public MutableLiveData<List<Photograph>> photographList = new MutableLiveData<>(new ArrayList<>());

    public InspectViewModel() {
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public void addPhotograph(List<Bitmap> bitmapList) {
        List<Photograph> photoList = getPhotoList();
        for (Bitmap bitmap : bitmapList) {
            photoList.add(new Photograph(bitmap, false));
        }
        photographList.setValue(photoList);
    }

    public List<Photograph> getPhotoList() {
        List<Photograph> value = photographList.getValue();
        if (value == null) {
            List<Photograph> newArrayList = new ArrayList<>();
            photographList.setValue(newArrayList);
            return newArrayList;
        } else {
            return value;
        }
    }

}
