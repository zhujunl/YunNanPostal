package com.miaxis.postal.viewModel;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.miaxis.postal.app.App;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.data.entity.Photograph;
import com.miaxis.postal.data.repository.OrderRepository;
import com.miaxis.postal.util.BarcodeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RecordUpdateViewModel extends BaseViewModel {

    public static final int MAX_COUNT = 1;

    public MutableLiveData<Order> orderData = new MutableLiveData<>(null);
    public MutableLiveData<List<Photograph>> photographList = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<Boolean> orderCodeImageUpdate = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> updateResult = new SingleLiveEvent<>();

    public Bitmap orderCodeBitmapCache = null;

    public RecordUpdateViewModel() {
    }

    public void initOrder(Order order) {
        if (orderData.getValue() == null) {
            orderData.setValue(order);
            initOrderPhoto(order);
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

    private void initOrderPhoto(Order order) {
        App.getInstance().getThreadExecutor().execute(() -> {
            for (String url : order.getImageList()) {
                try {
                    Bitmap bitmap = downloadPicture(url);
                    addPhotograph(bitmap);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Bitmap downloadPicture(String url) throws ExecutionException, InterruptedException {
        if (TextUtils.isEmpty(url))
            return null;
        FutureTarget<Bitmap> futureTarget = Glide.with(App.getInstance().getApplicationContext())
                .asBitmap()
                .load(url + "?" + System.currentTimeMillis())
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .submit();
        return futureTarget.get();
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

    public boolean isPhotoUpdate() {
        for (Photograph photograph : getPhotographList()) {
            if (!photograph.isLocal()) {
                return true;
            }
        }
        return false;
    }

    public void updateOrder() {
        Order value = orderData.getValue();
        if (value == null)
            return;
        waitMessage.setValue("上传中，请稍后...");
        Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            List<Bitmap> bitmapList;
            if (isPhotoUpdate()) {
                bitmapList = getSelectList();
            } else {
                bitmapList = new ArrayList<>();
            }
            OrderRepository.getInstance().updateOrderFromApp(value, bitmapList);
            emitter.onNext(Boolean.TRUE);
        })
                .subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    waitMessage.setValue("");
                    resultMessage.setValue("修改成功");
                    updateResult.setValue(Boolean.TRUE);
                }, throwable -> {
                    waitMessage.setValue("");
                    resultMessage.setValue(handleError(throwable));
                });
    }

}
