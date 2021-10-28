package com.miaxis.postal.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.DevicesStatusEntity;
import com.miaxis.postal.data.entity.Local;
import com.miaxis.postal.databinding.FragmentLocalBinding;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.manager.PostalManager;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.adapter.LocalAdapter;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.LocalViewModel;
import com.miaxis.postal.viewModel.LoginViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

public class LocalFragment extends BaseViewModelFragment<FragmentLocalBinding, LocalViewModel> {

    private LocalAdapter localAdapter;
    private LinearLayoutManager layoutManager;
    private String filter = "";
    private int page = 1;
    private int localCount = 0;

    public static LocalFragment newInstance() {
        return new LocalFragment();
    }

    public LocalFragment() {
        // Required empty public constructor
    }

    private Handler deviceHandler = new Handler();
    private Runnable task =new Runnable() {
        public void run() {
            // TODOAuto-generated method stub
            deviceHandler.postDelayed(this,10*1000);//设置延迟时间
            //需要执行的代码
            //获取设备状态,判断设备状态是启用还是禁用
            LoginViewModel loginViewModel = new LoginViewModel();
            Config config = ConfigManager.getInstance().getConfig();
            loginViewModel.getDevices(config.getDeviceIMEI());
            loginViewModel.deviceslist.observe(getActivity(), new Observer<DevicesStatusEntity.DataDTO>() {
                @Override
                public void onChanged(DevicesStatusEntity.DataDTO dataDTO) {
                    //如果是启用状态不做任何操作
                    if (dataDTO.getStatus().equals(ValueUtil.DEVICE_ENABLE)){

                    }else {
                        //如果从启用状态切换到了禁用状态强制退出登录跳到登录页面
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.local_device, new LoginFragment(), null)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });

        }
    };

    @Override
    protected int setContentView() {
        return R.layout.fragment_local;
    }

    @Override
    protected LocalViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(LocalViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.refreshing.observe(this, refreshingObserver);
        viewModel.localList.observe(this, localListObserver);
        viewModel.loadCardResult.observe(this, loadCardResultObserver);
        //进入延时状态,一小时访问一次接口
        deviceHandler.postDelayed(task,3600000);//延迟调用
    }

    @Override
    protected void initView() {
        initRecycleView();
//        binding.ivBack.setOnClickListener(v -> onBackPressed());
        binding.srlLocal.setOnRefreshListener(this::refresh);
        binding.srlLocal.setColorSchemeResources(R.color.main_color, R.color.main_color_dark);
//        viewModel.loadIdCardRecord();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadIdCardRecord();
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.localList.removeObserver(localListObserver);

    }

    private void initRecycleView() {
        localAdapter = new LocalAdapter(getContext());
        localAdapter.setListener(adapterListener);
        layoutManager = new LinearLayoutManager(getContext());
        binding.rvLocal.addOnScrollListener(onScrollListener);
        binding.rvLocal.setLayoutManager(layoutManager);
        binding.rvLocal.setAdapter(localAdapter);
        ((SimpleItemAnimator) binding.rvLocal.getItemAnimator()).setSupportsChangeAnimations(false);
    }

//    private void initSearchView() {
//        binding.svSearch.setQueryHint("请输入寄件人姓名或快递单号...");
//        binding.svSearch.setSubmitButtonEnabled(true);
//        binding.svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                filter = query;
//                refresh();
//                binding.svSearch.clearFocus();
//                return true;
//            }
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return true;
//            }
//        });
//        binding.svSearch.setOnCloseListener(() -> {
//            filter = "";
//            refresh();
//            return false;
//        });
//    }

    private LocalAdapter.OnItemClickListener adapterListener = new LocalAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Local local = viewModel.getLocalList().get(position);
            if (local == null || local.getExpress() == null) {
                return;
            }
            new MaterialDialog.Builder(getContext())
                    .title("提示！")
                    .content("是否删除此订单？")
                    .positiveText("确认")
                    .onPositive((dialog, which) -> {
                        viewModel.deleteSelf(local.getIdCardRecord(), local.getExpress());
                    })
                    .negativeText("取消")
                    .show();
//            viewModel.getOrderById(orderAdapter.getData(position));
        }

        @Override
        public void onThumbnail(String url) {
//            mListener.replaceFragment(PhotoFragment.newInstance(url));
        }
    };

    private Observer<List<Local>> localListObserver = localList -> {
        if (page == 1) {
            localAdapter.setDataList(localList);
            if (localCount == 0) {
                binding.rvLocal.scrollToPosition(0);
            }
            localCount = localList.size();
        } else {
            localAdapter.setDataList(localList);
            localAdapter.notifyItemRangeChanged(localCount, localList.size() - localCount);
            if (localCount != 0) {
                binding.rvLocal.scrollToPosition(localCount);
            }
            localCount = localList.size();
        }
    };

    private Observer<Boolean> refreshingObserver = flag -> binding.srlLocal.setRefreshing(flag);

    private Observer<Boolean> loadCardResultObserver = result -> refresh();

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        private boolean loadingMore = true;

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (!loadingMore && layoutManager.findLastVisibleItemPosition() + 1 == localAdapter.getItemCount()) {
                    loadMore();
                }
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (loadingMore && layoutManager.findLastVisibleItemPosition() + 1 == localAdapter.getItemCount()) {
                loadMore();
            } else if (loadingMore) {
                loadingMore = false;
            }
        }
    };

    private void refresh() {
        PostalManager.getInstance().startPostal();
        localCount = 0;
        viewModel.loadExpressByPage(page = 1);
    }

    private void loadMore() {
        viewModel.loadExpressByPage(++page);
    }




}
