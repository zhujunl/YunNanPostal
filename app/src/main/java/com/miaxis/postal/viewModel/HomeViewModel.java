package com.miaxis.postal.viewModel;

import com.miaxis.postal.app.App;
import com.miaxis.postal.data.entity.Branch;
import com.miaxis.postal.data.entity.Courier;
import com.miaxis.postal.data.model.BranchModel;
import com.miaxis.postal.manager.DataCacheManager;
import com.miaxis.postal.util.ListUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class HomeViewModel extends BaseViewModel {

    public ObservableField<Courier> courier = new ObservableField<>(DataCacheManager.getInstance().getCourier());
    public MutableLiveData<List<Branch>> branchList = new MutableLiveData<>(new ArrayList<>());

    public HomeViewModel() {
    }

    public void init() {
        Observable.create((ObservableOnSubscribe<List<Branch>>) emitter -> {
            List<Branch> list = BranchModel.find();
            if (!ListUtils.isNullOrEmpty(list)) {
                Branch selected = Branch.findSelected(list);
                if (selected == null) {
                    Branch branch = list.get(0);
                    branch.isSelected = true;
                    BranchModel.save(branch);
                }
            }
            emitter.onNext(list);
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    branchList.postValue(list);
                }, throwable -> {
                    resultMessage.setValue("查询数据失败：" + throwable.getMessage());
                });
    }

    public void flush(List<Branch> list) {
        Observable.create((ObservableOnSubscribe<List<Branch>>) emitter -> {
            if (ListUtils.isNullOrEmpty(list)) {
                BranchModel.deleteAll();
                emitter.onNext(new ArrayList<>());
            } else {
                Branch last = BranchModel.findSelected();
                for (Branch branch : list) {
                    branch.isSelected = last != null && last.id == branch.id;
                    BranchModel.save(branch);
                }
                Branch selected = Branch.findSelected(list);
                if (selected == null) {
                    Branch branch = list.get(0);
                    branch.isSelected = true;
                    BranchModel.save(branch);
                }
                emitter.onNext(list);
            }
        }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    branchList.postValue(result);
                }, throwable -> {
                    resultMessage.setValue("操作数据失败：" + throwable.getMessage());
                });
    }

    public void onItemClick(Branch branch) {
        if (branch != null) {
            Observable.create((ObservableOnSubscribe<List<Branch>>) emitter -> {
                List<Branch> list = BranchModel.find();
                if (ListUtils.isNull(list)) {
                    return;
                }
                for (Branch br : list) {
                    br.isSelected = br.id == branch.id;
                    BranchModel.save(br);
                }
                emitter.onNext(list);
            }).subscribeOn(Schedulers.from(App.getInstance().getThreadExecutor()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        branchList.postValue(result);
                    }, throwable -> {
                        resultMessage.setValue("操作数据失败：" + throwable.getMessage());
                    });
        }
    }

}
