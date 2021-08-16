package com.miaxis.postal.view.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.R;
import com.miaxis.postal.databinding.ActivityMainBinding;
import com.miaxis.postal.manager.CardManager;
import com.miaxis.postal.manager.PostalManager;
import com.miaxis.postal.util.ClearRecordFileWorker;
import com.miaxis.postal.util.NetUtils;
import com.miaxis.postal.view.base.BaseActivity;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.view.base.OnFragmentInteractionListener;
import com.miaxis.postal.view.fragment.PreludeFragment;
import com.miaxis.postal.view.presenter.UpdatePresenter;

import java.util.concurrent.TimeUnit;

import androidx.fragment.app.Fragment;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements OnFragmentInteractionListener {

    private MaterialDialog waitDialog;
    private MaterialDialog resultDialog;
    private MaterialDialog quitDialog;
    private BroadcastReceiver netReceiver;

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
    private void performTask() {
        //重复性任务 24小时执行一次
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(ClearRecordFileWorker.class,
                24, TimeUnit.HOURS).build();
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
        if (netReceiver != null) {
            unregisterReceiver(netReceiver);
        }
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
        netReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    int netStatus = NetUtils.getNetStatus(context);
                    if (netStatus >= 0) {
                        PostalManager.getInstance().startPostal();
                    }
                }
            }
        };
        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction("android.net.ethernet.ETHERNET_STATE_CHANGED");
        timeFilter.addAction("android.net.ethernet.STATE_CHANGE");
        timeFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        timeFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        timeFilter.addAction("android.net.wifi.STATE_CHANGE");
        timeFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(netReceiver, timeFilter);
    }

    @Override
    public void backToRoot() {
        getSupportFragmentManager().popBackStack(root, 1);
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideInputMethod();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.cl_container, fragment)
                        .addToBackStack(fragment.getClass().getName())
                        .commit();
            }
        });
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                waitDialog.getContentView().setText(message);
                waitDialog.show();
            }
        });
    }

    @Override
    public void dismissWaitDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (waitDialog.isShowing()) {
                    waitDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void showResultDialog(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultDialog.getContentView().setText(message);
                resultDialog.show();
            }
        });
    }

    @Override
    public void dismissResultDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (resultDialog.isShowing()) {
                    resultDialog.dismiss();
                }
            }
        });
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