package com.miaxis.postal.data.repository;

import android.text.TextUtils;

import com.miaxis.postal.data.dto.OrderDto;
import com.miaxis.postal.data.dto.SimpleOrderDto;
import com.miaxis.postal.data.entity.Order;
import com.miaxis.postal.data.entity.SimpleOrder;
import com.miaxis.postal.data.exception.MyException;
import com.miaxis.postal.data.net.PostalApi;
import com.miaxis.postal.data.net.ResponseEntity;
import com.miaxis.postal.util.ValueUtil;

import java.io.IOException;
import java.util.ArrayList;
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

    public List<SimpleOrder> getOrderByCodeAndNameSync(String param, int pageNum, int pageSize) throws IOException, MyException {
        Response<ResponseEntity<List<SimpleOrderDto>>> execute = PostalApi.getOrderByCodeAndNameSync(param, pageNum, pageSize).execute();
        try {
            ResponseEntity<List<SimpleOrderDto>> body = execute.body();
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS) && body.getData() != null) {
                    return transformSimpleOrderDtoList(body.getData());
                } else {
                    throw new MyException("服务端返回，" + body.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(e.getMessage());
        }
        throw new MyException("服务端返回数据解析失败，或为空");
    }

    public Order getOrderByIdSync(long id) throws IOException, MyException {
        Response<ResponseEntity<OrderDto>> execute = PostalApi.getOrderByIdSync(id).execute();
        try {
            ResponseEntity<OrderDto> body = execute.body();
            if (body != null) {
                if (TextUtils.equals(body.getCode(), ValueUtil.SUCCESS) && body.getData() != null) {
                    return body.getData().transform();
                } else {
                    throw new MyException("服务端返回，" + body.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MyException(e.getMessage());
        }
        throw new MyException("服务端返回数据解析失败，或为空");
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
