package com.miaxis.postal.data.repository;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.miaxis.postal.app.App;
import com.miaxis.postal.data.dao.AppDatabase;
import com.miaxis.postal.data.dto.CourierDto;
import com.miaxis.postal.data.entity.Branch;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.exception.NetResultFailedException;
import com.miaxis.postal.data.model.CourierModel;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.data.net.ResponseEntity;
import com.miaxis.postal.manager.DataCacheManager;
import com.miaxis.postal.util.FileUtil;
import com.miaxis.postal.util.ValueUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public List<Branch> getBranchListSync(String phone) throws IOException, MyException, NetResultFailedException {
        Response<ResponseEntity<List<Branch>>> execute = PostalApi.getBranchListSync(phone).execute();
        try {
            ResponseEntity<List<Branch>> body = execute.body();
            Log.e("Repository", "getBranchListSync:" + body);
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS) && body.getData() != null) {
                    List<Branch> data = body.getData();
                    List<Branch> objects = new ArrayList<>();
                    for (Branch branch : data) {
                        if (branch != null && !branch.isEmpty()) {
                            objects.add(branch);
                        }
                    }
                    if (objects.isEmpty()) {
                        throw new MyException("没有可用的网点信息。");
                    }
                    return objects;
                } else {
                    throw new NetResultFailedException("服务端返回，" + body.getMessage());
                }
            }
        } catch (NetResultFailedException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Repository", "getBranchListSync   Exception:" + e);
            throw new MyException(e.getMessage());
        }
        throw new MyException("服务端返回数据解析失败，或为空");
    }

    public List<Branch> getAllBranchListSync() throws IOException, MyException, NetResultFailedException {
        Response<ResponseEntity<List<Branch>>> execute = PostalApi.getAllBranchListSync().execute();
        try {
            ResponseEntity<List<Branch>> body = execute.body();
            Log.e("Repository", "getAllBranchListSync:" + body);
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS) && body.getData() != null) {
                    List<Branch> data = body.getData();
                    List<Branch> objects = new ArrayList<>();
                    for (Branch branch : data) {
                        if (branch != null && !branch.isEmpty2()) {
                            objects.add(branch);
                        }
                    }
                    if (objects.isEmpty()) {
                        throw new MyException("没有可用的网点信息。");
                    }
                    return objects;
                } else {
                    throw new NetResultFailedException("服务端返回，" + body.getMessage());
                }
            }
        } catch (NetResultFailedException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Repository", "getAllBranchListSync   Exception:" + e);
            throw new MyException(e.getMessage());
        }
        throw new MyException("服务端返回数据解析失败，或为空");
    }

    public String bindingNodeSync(String phone, String comcode) throws IOException, MyException {
        Response<ResponseEntity> execute = PostalApi.bindingNodeSync(phone, comcode).execute();
        try {
            ResponseEntity body = execute.body();
            Log.e("Repository", "bindingNodeSync:" + body);
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS)) {
                    return null;
                } else {
                    return body.getMessage() + "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Repository", "bindingNodeSync   Exception:" + e);
            throw new MyException(e.getMessage());
        }
        throw new MyException("服务端返回数据解析失败，或为空");
    }

    public String unBindingNodeSync(String phone, String orgNode) throws IOException, MyException {
        try {
            Response<ResponseEntity> execute = PostalApi.unBindingNodeSync(phone, orgNode).execute();
            ResponseEntity body = execute.body();
            Log.e("Repository", "unBindingNodeSync:" + body);
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS)) {
                    return null;
                } else {
                    return body.getMessage() + "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Repository", "unBindingNodeSync   Exception:" + e);
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
