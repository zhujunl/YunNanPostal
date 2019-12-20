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

public abstract class BaseAdapter<T, DB extends ViewDataBinding, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {

    protected Context context;
    protected List<T> dataList = new ArrayList<>();

    public BaseAdapter(@NonNull Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DB binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), setContentView(), parent, false);
        return createViewHolder(binding.getRoot(), binding);
    }

    public T getData(int position) {
        return dataList.get(position);
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void appendDataList(List<T> appendDataList) {
        int positionStart = dataList.size();
        this.dataList.addAll(appendDataList);
        notifyItemRangeInserted(positionStart, appendDataList.size());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    protected abstract int setContentView();

    protected abstract VH createViewHolder(View view, DB binding);

}
