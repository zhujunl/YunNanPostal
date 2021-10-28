package com.miaxis.postal.view.fragment;

import android.os.Handler;

import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Config;
import com.miaxis.postal.data.entity.DevicesStatusEntity;
import com.miaxis.postal.data.entity.Draft;
import com.miaxis.postal.data.entity.DraftMessage;
import com.miaxis.postal.data.entity.Express;
import com.miaxis.postal.databinding.FragmentDraftBinding;
import com.miaxis.postal.manager.ConfigManager;
import com.miaxis.postal.util.ValueUtil;
import com.miaxis.postal.view.adapter.DraftAdapter;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.DraftViewModel;
import com.miaxis.postal.viewModel.LoginViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

public class DraftFragment extends BaseViewModelFragment<FragmentDraftBinding, DraftViewModel> {

    private DraftAdapter draftAdapter;
    private LinearLayoutManager layoutManager;

    private String filter = "";
    private int page = 1;
    private int localCount = 0;

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
                                .replace(R.id.draft, new LoginFragment(), null)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });

        }
    };

    public static DraftFragment newInstance() {
        return new DraftFragment();
    }

    public DraftFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_draft;
    }

    @Override
    protected DraftViewModel initViewModel() {
        return new ViewModelProvider(this, getViewModelProviderFactory()).get(DraftViewModel.class);
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    protected void initData() {
        viewModel.refreshing.observe(this, refreshingObserver);
        viewModel.draftList.observe(this, draftListObserver);
        viewModel.draftMessageSearch.observe(this, draftMessageSearchObserver);

        //进入延时状态,一小时访问一次接口
        deviceHandler.postDelayed(task,3600000);//延迟调用
    }

    @Override
    protected void initView() {
        initRecycleView();
        binding.srlDraft.setOnRefreshListener(this::refresh);
        binding.srlDraft.setColorSchemeResources(R.color.main_color, R.color.main_color_dark);
        refresh();
    }

    @Override
    public void onBackPressed() {
        mListener.backToStack(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.draftList.removeObserver(draftListObserver);
    }

    private void initRecycleView() {
        draftAdapter = new DraftAdapter(getContext());
        draftAdapter.setListener(adapterListener);
        layoutManager = new LinearLayoutManager(getContext());
        binding.rvDraft.addOnScrollListener(onScrollListener);
        binding.rvDraft.setLayoutManager(layoutManager);
        binding.rvDraft.setAdapter(draftAdapter);
        ((SimpleItemAnimator) binding.rvDraft.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    private DraftAdapter.OnItemClickListener adapterListener = (view, position) -> {
        viewModel.getDraftMessage(draftAdapter.getData(position));
    };

    private Observer<DraftMessage> draftMessageSearchObserver = draftMessage -> {
        if (draftMessage.getIdCardRecord().getType() != 2) {
            mListener.replaceFragment(ExpressFragment.newInstanceForDraft(draftMessage.getIdCardRecord(), draftMessage.getExpressList()));
        } else {
            Express express;
            List<Express> expressList = draftMessage.getExpressList();
            if (expressList.isEmpty()) {
                express = new Express();
            } else {
                express = expressList.get(0);
            }
            mListener.replaceFragment(AgreementCustomersFragment.newInstanceForDraft(draftMessage.getIdCardRecord(), express));
        }
    };

    private Observer<List<Draft>> draftListObserver = draftList -> {
        if (page == 1) {
            draftAdapter.setDataList(draftList);
            draftAdapter.notifyDataSetChanged();
            if (localCount == 0) {
                binding.rvDraft.scrollToPosition(0);
            }
            localCount = draftList.size();
        } else {
            draftAdapter.setDataList(draftList);
            draftAdapter.notifyItemRangeChanged(localCount, draftList.size() - localCount);
            if (localCount != 0) {
                binding.rvDraft.scrollToPosition(localCount);
            }
            localCount = draftList.size();
        }
    };

    private Observer<Boolean> refreshingObserver = flag -> binding.srlDraft.setRefreshing(flag);

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        private boolean loadingMore = true;

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (!loadingMore && layoutManager.findLastVisibleItemPosition() + 1 == draftAdapter.getItemCount()) {
                    loadMore();
                }
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (loadingMore && layoutManager.findLastVisibleItemPosition() + 1 == draftAdapter.getItemCount()) {
                loadMore();
            } else if (loadingMore) {
                loadingMore = false;
            }
        }
    };

    private void refresh() {
        localCount = 0;
        viewModel.loadDraftByPage(page = 1);
    }

    private void loadMore() {
        viewModel.loadDraftByPage(++page);
    }

}
