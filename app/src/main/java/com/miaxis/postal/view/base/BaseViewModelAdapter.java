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

public abstract class BaseViewModelAdapter<DB extends ViewDataBinding, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {

    protected Context context;

    public BaseViewModelAdapter(@NonNull Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DB binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), setContentView(), parent, false);
        return createViewHolder(binding.getRoot(), binding);
    }

    protected abstract int setContentView();

    protected abstract VH createViewHolder(View view, DB binding);

}
