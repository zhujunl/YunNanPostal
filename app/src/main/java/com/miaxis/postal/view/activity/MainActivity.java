package com.miaxis.postal.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.R;
import com.miaxis.postal.databinding.ActivityMainBinding;
import com.miaxis.postal.manager.CameraManager;
import com.miaxis.postal.view.base.BaseActivity;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.view.base.OnFragmentInteractionListener;
import com.miaxis.postal.view.fragment.IdentityFragment;
import com.miaxis.postal.view.fragment.OrderFragment;
import com.miaxis.postal.view.fragment.PreludeFragment;

import java.io.IOException;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements OnFragmentInteractionListener {

    private MaterialDialog waitDialog;
    private MaterialDialog quitDialog;

    private String root;

    @Override
    protected int setContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        initDialog();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.cl_container, PreludeFragment.newInstance())
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount > 1) {
            backToStack(null);
        } else {
            exitApp();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("asd", "onActivityResult");
        List<Fragment> visibleFragment = getVisibleFragment(getSupportFragmentManager());
        for (Fragment fragment : visibleFragment) {
            if (requestCode == OrderFragment.REQUEST_CODE && fragment instanceof OrderFragment) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /** OnFragmentInteractionListener方法区 **/

    @Override
    public void setRoot(Fragment fragment) {
        root = fragment.getClass().getName();
        replaceFragment(fragment);
    }

    @Override
    public void backToRoot() {
        getSupportFragmentManager().popBackStack(root, 0);
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.cl_container, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    @Override
    public void backToStack(Class<? extends Fragment> fragment) {
        if (fragment != null) {
            getSupportFragmentManager().popBackStack(fragment.getName(), 0);
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void addFragment(Fragment lastFragment, Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .hide(lastFragment)
                .add(R.id.cl_container, fragment)
                .show(fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
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
    public void exitApp() {
        quitDialog.show();
    }

    /** OnFragmentInteractionListener方法区 **/

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
    }

}