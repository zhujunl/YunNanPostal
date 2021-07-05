package com.miaxis.postal.data.converter;

import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.miaxis.postal.data.bean.OrderImage;
import com.miaxis.postal.util.ValueUtil;

import java.util.List;

import androidx.room.TypeConverter;

public class OrderImageListConverter {

    @TypeConverter
    public static List<OrderImage> revertOrderImage(String s) {
        try {
            return ValueUtil.GSON.fromJson(s, new TypeToken<List<OrderImage>>() {}.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @TypeConverter
    public static String converterOrderImage(List<OrderImage> list) {
        Log.e("converterOrderImage","list:"+ValueUtil.GSON.toJson(list));
        return ValueUtil.GSON.toJson(list);
    }

}
