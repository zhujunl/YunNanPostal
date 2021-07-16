package com.miaxis.postal.data.repository;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.miaxis.postal.app.App;
import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.data.dto.CourierDto;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.exception.NetResultFailedException;
import com.miaxis.postal.data.model.CourierModel;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.data.net.ResponseEntity;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.DataCacheManager;
import com.miaxis.postal.util.FileUtil;
import com.miaxis.postal.util.ValueUtil;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class LoginRepository extends BaseRepository {

    private LoginRepository() {
    }

    public static LoginRepository getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final LoginRepository instance = new LoginRepository();
    }

    /**
     * ================================ 静态内部类单例写法 ================================
     **/

    public Courier getCourierByPhoneSync(String phone) throws IOException, MyException, NetResultFailedException {
        Response<ResponseEntity<CourierDto>> execute = PostalApi.getExpressmanByPhoneSync(phone).execute();
        try {
            ResponseEntity<CourierDto> body = execute.body();
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
        throw new MyException("服务端返回数据解析失败，或为空");
    }

    public void registerExpressmanSync(String name,
                                       String cardNo,
                                       String phone,
                                       String faceFeature,
                                       String finger1Feature,
                                       String finger2Feature,
                                       Bitmap bitmap) throws IOException, MyException, NetResultFailedException {
        String faceFilePath = FileUtil.FACE_IMAGE_PATH + File.separator + "card_" + cardNo + System.currentTimeMillis() + ".png";
        File file = FileUtil.saveQualityBitmap(bitmap, faceFilePath);
        Response<ResponseEntity> execute = PostalApi.registerExpressmanSync(name,
                cardNo,
                phone,
                faceFeature,
                finger1Feature,
                finger2Feature,
                file).execute();
        try {
            ResponseEntity body = execute.body();
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS)) {
                    return;
                } else {
                    throw new NetResultFailedException("服务端返回，" + body.getMessage());
                }
            }
        } catch (NetResultFailedException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(e.getMessage());
        } finally {
            file.delete();
        }
        throw new MyException("服务端返回数据解析失败，或为空");
    }

    public void editExpressmanSync(String password) throws IOException, NetResultFailedException, MyException {
        long courierId = DataCacheManager.getInstance().getCourier().getCourierId();
        Response<ResponseEntity> execute = PostalApi.editExpressmanSync(courierId, password).execute();
        try {
            ResponseEntity body = execute.body();
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS)) {
                    return;
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
        throw new MyException("服务端返回数据解析失败，或为空");
    }

    public Courier loadCourierSync() {
        return CourierModel.loadCourier();
    }

    public void outLogin() {
        Disposable subscribe = Observable.create((ObservableOnSubscribe<Courier>) emitter -> {
            Courier c = AppDatabase.getInstance().courierDao().loadCourier();
            c.setLogin(false);
            AppDatabase.getInstance().courierDao().updateCourier(c);
            emitter.onNext(c);
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(Schedulers.io())
                .subscribe(courier -> {
                }, throwable -> {

                });
    }


}
