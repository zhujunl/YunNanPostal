package com.miaxis.postal.view.activity;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.R;
import com.miaxis.postal.databinding.ActivityMainBinding;
import com.miaxis.postal.view.base.BaseActivity;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.view.base.OnFragmentInteractionListener;
import com.miaxis.postal.view.fragment.CameraFragment;
import com.miaxis.postal.view.fragment.ExpressFragment;
import com.miaxis.postal.view.fragment.HomeFragment;
import com.miaxis.postal.view.fragment.InspectFragment;
import com.miaxis.postal.view.fragment.PreludeFragment;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements OnFragmentInteractionListener {

    private MaterialDialog waitDialog;
    private MaterialDialog resultDialog;
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
        replaceFragment(PreludeFragment.newInstance());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Fragment visibleFragment = getVisibleFragment();
        if (visibleFragment != null) {
            BaseViewModelFragment fragment = (BaseViewModelFragment) visibleFragment;
            fragment.onBackPressed();
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
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount > 1) {
            if (fragment != null) {
                getSupportFragmentManager().popBackStack(fragment.getName(), 0);
            } else {
                getSupportFragmentManager().popBackStack();
            }
        } else {
            exitApp();
        }
    }

//    @Override
//    public void addFragment(Fragment lastFragment, Fragment fragment) {
//        getSupportFragmentManager().beginTransaction()
//                .hide(lastFragment)
//                .add(R.id.cl_container, fragment)
//                .show(fragment)
//                .addToBackStack(fragment.getClass().getName())
//                .commit();
//    }

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
        resultDialog = new MaterialDialog.Builder(this)
                .content("")
                .positiveText("确认")
                .build();
    }

}