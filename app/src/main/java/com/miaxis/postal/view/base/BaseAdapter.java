package com.miaxis.postal.view.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected Context context;
    protected List<T> dataList = new ArrayList<>();

    public BaseAdapter(@NonNull Context context) {
        this.context = context;
    }

    public T getData(int position) {
        return dataList.get(position);
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void appendDataList(List<T> appendDataList) {
        this.dataList.addAll(appendDataList);
    }

}
