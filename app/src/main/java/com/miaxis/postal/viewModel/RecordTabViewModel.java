package com.miaxis.postal.viewModel;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.repository.LoginRepository;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.ValueUtil;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RecordTabViewModel extends BaseViewModel {


    public MutableLiveData<Boolean> isExist=new MutableLiveData<>();
    public RecordTabViewModel() {
    }

}
