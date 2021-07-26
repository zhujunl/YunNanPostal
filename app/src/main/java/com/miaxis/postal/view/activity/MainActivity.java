package com.miaxis.postal.view.activity;

import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.R;
import com.miaxis.postal.databinding.ActivityMainBinding;
import com.miaxis.postal.manager.CardManager;
import com.miaxis.postal.manager.PostalManager;
import com.miaxis.postal.util.ClearRecordFileWorker;
import com.miaxis.postal.view.base.BaseActivity;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.view.base.OnFragmentInteractionListener;
import com.miaxis.postal.view.fragment.PreludeFragment;
import com.miaxis.postal.view.presenter.UpdatePresenter;

import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements OnFragmentInteractionListener {

    private MaterialDialog waitDialog;
    private MaterialDialog resultDialog;
    private MaterialDialog quitDialog;

    private String root;
    private UpdatePresenter updatePresenter;

    @Override
    protected int setContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        // ScanManager.getInstance().powerOff();
    }

    @Override
    protected void initView() {
        initDialog();
        updatePresenter = new UpdatePresenter(this);
        replaceFragment(PreludeFragment.newInstance());
        performTask();
    }
    //开始任务 重复执行
    private  void performTask(){
        //重复性任务 12小时执行一次
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(ClearRecordFileWorker.class,
                24, TimeUnit.DAYS).build();
        //一次性任务
//        WorkRequest request = new OneTimeWorkRequest.Builder(ClearRecordFileWorker.class).build();
        WorkManager.getInstance(this).enqueue(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (!TextUtils.isEmpty(root)) {
//            PostalManager.getInstance().startPostal();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CardManager.getInstance().stopReadCard();
    }

    @Override
    public void onBackPressed() {
        Fragment visibleFragment = getVisibleFragment();
        if (visibleFragment != null) {
            BaseViewModelFragment fragment = (BaseViewModelFragment) visibleFragment;
            fragment.onBackPressed();
        }
    }

    /**
     * OnFragmentInteractionListener方法区
     **/

    @Override
    public void setRoot(Fragment fragment) {
        root = fragment.getClass().getName();
        replaceFragment(fragment);
        PostalManager.getInstance().init();
        updatePresenter.checkUpdate();
    }

    @Override
    public void backToRoot() {
        getSupportFragmentManager().popBackStack(root, 1);
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        hideInputMethod();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.cl_container, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    @Override
    public void backToStack(Class<? extends Fragment> fragment) {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount > 1) {
            if (fragment != null) {
                getSupportFragmentManager()
                        .popBackStackImmediate(fragment.getName(), 0);
            } else {
                getSupportFragmentManager().popBackStackImmediate(null, 0);
            }
        } else {
            exitApp();
        }
    }

    @Override
    public void showWaitDialog(String message) {
        waitDialog.getContentView().setText(message);
        waitDialog.show();
    }

    @Override
    public void dismissWaitDialog() {
        if (waitDialog.isShowing()) {
            waitDialog.dismiss();
        }
    }

    @Override
    public void showResultDialog(String message) {
        resultDialog.getContentView().setText(message);
        resultDialog.show();
    }

    @Override
    public void dismissResultDialog() {
        if (resultDialog.isShowing()) {
            resultDialog.dismiss();
        }
    }

    @Override
    public void updateApp(UpdatePresenter.OnCheckUpdateResultListener listener) {
        if (updatePresenter != null) {
            updatePresenter.checkUpdate(listener);
        }
    }

    @Override
    public void exitApp() {
        quitDialog.show();
    }

    /**
     * OnFragmentInteractionListener方法区
     **/

    private void initDialog() {
        waitDialog = new MaterialDialog.Builder(this)
                .progress(true, 100)
                .content("请稍后")
                .cancelable(false)
                .autoDismiss(false)
                .build();
        quitDialog = new MaterialDialog.Builder(this)
                .title("确认退出?")
                .positiveText("确认")
                .onPositive((dialog, which) -> {
                    finish();
                    System.exit(0);
                })
                .negativeText("取消")
                .build();
        resultDialog = new MaterialDialog.Builder(this)
                .content("")
                .positiveText("确认")
                .build();
    }

}