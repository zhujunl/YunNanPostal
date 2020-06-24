package com.miaxis.postal.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miaxis.postal.BR;
import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Draft;
import com.miaxis.postal.data.entity.DraftMessage;
import com.miaxis.postal.databinding.FragmentDraftBinding;
import com.miaxis.postal.view.adapter.DraftAdapter;
import com.miaxis.postal.view.base.BaseViewModelFragment;
import com.miaxis.postal.viewModel.DraftViewModel;

import java.util.List;

public class DraftFragment extends BaseViewModelFragment<FragmentDraftBinding, DraftViewModel> {

    private DraftAdapter draftAdapter;
    private LinearLayoutManager layoutManager;

    private String filter = "";
    private int page = 1;
    private int localCount = 0;

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
    }

    @Override
    protected void initView() {
        initRecycleView();
        binding.srlDraft.setOnRefreshListener(this::refresh);
        binding.srlDraft.setColorSchemeResources(R.color.main_color,R.color.main_color_dark);
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
        mListener.replaceFragment(ExpressFragment.newInstanceForDraft(draftMessage.getIdCardRecord(), draftMessage.getExpressList()));
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
