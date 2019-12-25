package com.miaxis.postal.view.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.dto.TempIdDto;
import com.miaxis.postal.databinding.FragmentExpressBinding;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.ExpressViewModel;
import com.speedata.libid2.IDInfor;

public class ExpressFragment extends BaseViewModelFragment<FragmentExpressBinding, ExpressViewModel> {

    private IDInfor idInfor;
    private Bitmap header;
    private TempIdDto tempIdDto;

    public static ExpressFragment newInstance(IDInfor idInfor, Bitmap header, TempIdDto tempIdDto) {
        ExpressFragment fragment = new ExpressFragment();
        fragment.setIdInfor(idInfor);
        fragment.setHeader(header);
        fragment.setTempIdDto(tempIdDto);
        return fragment;
    }

    public ExpressFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_express;
    }

    @Override
    protected ExpressViewModel initViewModel() {
        return ViewModelProviders.of(this).get(ExpressViewModel.class);
    }

    @Override
    public int initVariableId() {
        return com.miaxis.postal.BR.viewModel;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }

    public void setIdInfor(IDInfor idInfor) {
        this.idInfor = idInfor;
    }

    public void setHeader(Bitmap header) {
        this.header = header;
    }

    public void setTempIdDto(TempIdDto tempIdDto) {
        this.tempIdDto = tempIdDto;
    }
}
