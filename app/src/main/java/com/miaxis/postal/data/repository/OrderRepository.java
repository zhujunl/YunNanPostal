package com.miaxis.postal.data.repository;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.miaxis.postal.data.dto.OrderDto;
import com.miaxis.postal.data.dto.SimpleOrderDto;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.data.entity.SimpleOrder;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.exception.NetResultFailedException;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.data.net.ResponseEntity;
import com.miaxis.postal.manager.DataCacheManager;
import com.miaxis.postal.util.DateUtil;
import com.miaxis.postal.util.ListUtils;
import com.miaxis.postal.util.ValueUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Response;

public class OrderRepository extends BaseRepository {

    private OrderRepository() {
    }

    public static OrderRepository getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final OrderRepository instance = new OrderRepository();
    }

    /**
     * ================================ 静态内部类单例写法 ================================
     **/

    public List<SimpleOrder> getOrderByCodeAndNameSync(String param, int pageNum, int pageSize) throws IOException, MyException, NetResultFailedException {
        long courierId = DataCacheManager.getInstance().getCourier().getCourierId();
        Response<ResponseEntity<List<SimpleOrderDto>>> execute = PostalApi.getOrderByCodeAndNameSync(courierId, param, pageNum, pageSize).execute();
        try {
            ResponseEntity<List<SimpleOrderDto>> body = execute.body();
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS) && body.getData() != null) {
                    return transformSimpleOrderDtoList(body.getData());
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

    public Order getOrderByIdSync(long id) throws IOException, MyException, NetResultFailedException {
        Response<ResponseEntity<OrderDto>> execute = PostalApi.getOrderByIdSync(id).execute();
        try {
            ResponseEntity<OrderDto> body = execute.body();
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

    public Order getOrderByCode(String orderCode) throws IOException, MyException, NetResultFailedException {
        Response<ResponseEntity<OrderDto>> execute = PostalApi.getOrderByCode(orderCode).execute();
        try {
            ResponseEntity<OrderDto> body = execute.body();
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


    public List<Order> getOrderByCode(String orderCode, int page) throws IOException, MyException, NetResultFailedException {
        Courier courier = DataCacheManager.getInstance().getCourier();
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                23, 59, 59);
        Date endOfDate = calendar.getTime();

        Date beginOfDate = new Date(endOfDate.getTime() - 604799 * 1000);
        String startTime = DateUtil.DATE_FORMAT.format(beginOfDate);


        String endTime = DateUtil.DATE_FORMAT.format(endOfDate);
        Log.e("getOrderByCode", "pageNum:" + page);
        Response<ResponseEntity<List<OrderDto>>> execute =
                PostalApi.getOrderByCode1(
                        "" + courier.getCourierId(), "" + page, "" + 5, orderCode, startTime, endTime).execute();
        try {
            ResponseEntity<List<OrderDto>> body = execute.body();
            Log.e("getOrderByCode", "" + body);
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS) && body.getData() != null) {
                    List<OrderDto> data = body.getData();
                    List<Order> objects = new ArrayList<>();
                    if (!ListUtils.isNullOrEmpty(data)) {
                        for (OrderDto orderDto : data) {
                            Order transform = orderDto.transform();
                            objects.add(transform);
                        }
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
            throw new MyException(e.getMessage());
        }
        throw new MyException("服务端返回数据解析失败，或为空");
    }

    public void updateOrderFromApp(Order order, List<Bitmap> photoList) throws IOException, MyException, NetResultFailedException {
        Response<ResponseEntity> execute = PostalApi.updateOrderFromApp(
                order.getSenderAddress(),
                order.getSenderPhone(),
                order.getSenderName(),
                order.getOrderCode(),
                order.getGoodsName(),
                order.getWeight(),
                order.getAddresseeName(),
                order.getAddresseeAddress(),
                order.getAddresseePhone(),
                photoList)
                .execute();
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
        throw new MyException("服务端返回，空数据");
    }

    private List<SimpleOrder> transformSimpleOrderDtoList(List<SimpleOrderDto> simpleOrderDtoList) throws MyException {
        List<SimpleOrder> simpleOrderList = new ArrayList<>();
        try {
            for (SimpleOrderDto simpleOrderDto : simpleOrderDtoList) {
                simpleOrderList.add(simpleOrderDto.transform());
            }
        } catch (MyException e) {
            e.printStackTrace();
            throw new MyException("解析上行数据出错");
        }
        return simpleOrderList;
    }

}
