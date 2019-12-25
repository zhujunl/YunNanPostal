package com.miaxis.postal.viewModel;

import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.app.PostalApp;
import com.miaxis.postal.bridge.SingleLiveEvent;
import com.miaxis.postal.bridge.Status;
import com.miaxis.postal.manager.CardManager;
import com.speedata.libid2.IDInfor;

public class CardViewModel extends BaseViewModel {

    public MutableLiveData<Status> initCardResult = new SingleLiveEvent<>();
    public MutableLiveData<IDInfor> idInfoLiveData = new MutableLiveData<>();

    public CardViewModel() {
    }

    public void startReadCard() {
        initCardResult.setValue(Status.LOADING);
        CardManager.getInstance().init(PostalApp.getInstance(), listener);
    }

    public void stopReadCard() {
        CardManager.getInstance().release();
    }

    private CardManager.IDCardListener listener = new CardManager.IDCardListener() {
        @Override
        public void onIDCardInitResult(boolean result) {
            initCardResult.postValue(result ? Status.SUCCESS : Status.FAILED);
        }

        @Override
        public void onIDCardReceive(IDInfor idInfor) {
            idInfoLiveData.setValue(idInfor);
        }
    };

}
