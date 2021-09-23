package com.miaxis.postal.data.repository;

import android.text.TextUtils;
import android.util.Log;

import com.miaxis.postal.data.bean.Statistical;
import com.miaxis.postal.data.dto.StatisticalDto;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.exception.NetResultFailedException;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.data.net.ResponseEntity;
import com.miaxis.postal.manager.DataCacheManager;
import com.miaxis.postal.util.ValueUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class PostalRepository extends BaseRepository {

    private static final String TAG = "PostalRepository";

    private PostalRepository() {
    }

    public static PostalRepository getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final PostalRepository instance = new PostalRepository();
    }

    /**
     * ================================ 静态内部类单例写法 ================================
     **/

    public boolean checkOrderByCodeSync(String code) throws MyException, IOException {
        Response<ResponseEntity> execute = PostalApi.checkOrderByCodeSync(code).execute();
        try {
            ResponseEntity body = execute.body();
            if (body != null) {
                return TextUtils.equals(body.getCode(), ValueUtil.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(e.getMessage());
        }
        throw new MyException("服务端返回数据解析失败，或为空");
    }

    public List<Statistical> getStatisticsByDate(int pageNum, int pageSize) throws IOException, MyException, NetResultFailedException {
        long courierId = DataCacheManager.getInstance().getCourier().getCourierId();
        Log.e(TAG, "getStatisticsByDate:   courierId：" + courierId+"   pageNum:"+pageNum+"    pageSize："+pageSize);
        Response<ResponseEntity<List<StatisticalDto>>> execute = PostalApi.getStatisticsByDate(courierId, pageNum, pageSize).execute();
        Log.e(TAG, "getStatisticsByDate: " + execute);
        try {
            ResponseEntity<List<StatisticalDto>> body = execute.body();
            Log.e(TAG, "body：" + body);
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS) && body.getData() != null) {
                    return transformDtoList(body.getData());
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

    private List<Statistical> transformDtoList(List<StatisticalDto> dtoList) throws MyException {
        List<Statistical> objects = new ArrayList<>();
        if (dtoList != null && !dtoList.isEmpty()) {
            for (StatisticalDto statisticalDto : dtoList) {
                objects.add(statisticalDto.transform());
            }
        }
        return objects;
        //        HashMap<String, List<Statistical>> map = new HashMap<>();
        //        if (dtoList != null && !dtoList.isEmpty()) {
        //            for (StatisticalDto statisticalDto : dtoList) {
        //                String date = TextUtils.isEmpty(statisticalDto.date) ? "其他" : statisticalDto.date;
        //                if (!map.containsKey(date)) {
        //                    map.put(date, new ArrayList<>());
        //                }
        //                List<Statistical> statisticals = map.get(date);
        //                statisticals.add(statisticalDto.transform());
        //            }
        //        }
        //        return map;
    }

}
