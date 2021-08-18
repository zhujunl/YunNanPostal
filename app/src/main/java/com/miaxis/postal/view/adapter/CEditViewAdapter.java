package com.miaxis.postal.view.adapter;

import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.Customer;
import com.miaxis.postal.manager.ToastManager;
import com.miaxis.postal.util.EmojiExcludeFilter;
import com.miaxis.postal.util.ListUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Tank
 * @date 2021/8/18 9:44 上午
 * @des
 * @updateAuthor
 * @updateDes
 */
public class CEditViewAdapter implements CustomerAdapter.OnBodyClickListener, PopupWindow.OnDismissListener {

    private LinearLayout parent;
    private OnItemClickListener mOnItemClickListener;
    private PopupWindow popupWindow;
    private List<Customer> customers;
    private CustomerAdapter customerAdapter;

    public CEditViewAdapter(@NonNull LinearLayout layout) {
        this.parent = layout;
        EditText edit_clientSName = this.parent.findViewById(R.id.edit_clientSName);
        edit_clientSName.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        edit_clientSName.addTextChangedListener(new TextWatcher() {
            String last = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //                if (!last.contentEquals(s) || !s.toString().contains(last)) {
                //                    last = s.toString();
                //                    if (s.toString().isEmpty()) {
                //                        return;
                //                    }
                //                    show();
                //                }
                if (last.contentEquals(s)) {
                    return;
                }
                last = s.toString();
                if (!contain(s.toString().trim())) {
                    if (s.toString().trim().isEmpty()) {
                        dismiss();
                    } else {
                        if (System.currentTimeMillis() - lastDismissTime < 500) {
                            return;
                        }
                        show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        View iv_list = this.parent.findViewById(R.id.iv_list);
        iv_list.setOnClickListener(v -> getList());
        new Handler().post(() -> init());
    }

    public void init() {
        initWindow();
    }

    public CEditViewAdapter bind(List<Customer> customers) {
        this.customers = customers;
        return this;
    }

    public CEditViewAdapter bind(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        return this;
    }

    private void getList() {
        if (ListUtils.isNullOrEmpty(this.customers)) {
            ToastManager.toast("暂未查询到协议客户信息", ToastManager.INFO);
            return;
        }
        //        if (System.currentTimeMillis() - this.lastDismissTime < 500) {
        //            return;
        //        }
        show();
    }

    private void initWindow() {
        this.popupWindow = new PopupWindow();
        this.popupWindow.setWidth((int) (this.parent.getWidth() * 0.7F));
        this.popupWindow.setHeight((int) (this.parent.getWidth() * 0.6F));
        View inflate = LayoutInflater.from(this.parent.getContext()).inflate(R.layout.view_customer_list, null);
        RecyclerView rv_list = inflate.findViewById(R.id.rv_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.parent.getContext(), LinearLayoutManager.VERTICAL, false);
        rv_list.setLayoutManager(linearLayoutManager);
        customerAdapter = new CustomerAdapter(this.parent.getContext());
        customerAdapter.setBodyListener(this);
        rv_list.setAdapter(customerAdapter);
        this.popupWindow.setContentView(inflate);
        this.popupWindow.setOutsideTouchable(true);
        this.popupWindow.setBackgroundDrawable(this.parent.getContext().getResources().getDrawable(android.R.color.transparent));
        this.popupWindow.setOnDismissListener(this);
    }

    private boolean contain(String name) {
        if (customerAdapter == null || TextUtils.isEmpty(name)) {
            return false;
        }
        List<Customer> dataList = customerAdapter.getDataList();
        if (ListUtils.isNullOrEmpty(dataList)) {
            return false;
        }
        for (Customer customer : dataList) {
            if (customer.name != null && customer.name.contains(name)) {
                return true;
            }
        }
        return false;
    }

    private void show() {
        if (this.popupWindow == null) {
            return;
        }
        EditText edit_clientSName = this.parent.findViewById(R.id.edit_clientSName);
        String trim = edit_clientSName.getText().toString().trim();
        List<Customer> objects = new ArrayList<>();
        if (trim.isEmpty()) {
            objects.addAll(this.customers);
        } else {
            for (Customer customer : this.customers) {
                if (customer.name != null && customer.name.contains(trim)) {
                    objects.add(customer);
                }
            }
        }
        if (customerAdapter != null) {
            customerAdapter.setDataList(objects);
        }
        if (ListUtils.isNullOrEmpty(objects)) {
            this.popupWindow.dismiss();
        } else {
            this.popupWindow.showAsDropDown(this.parent, this.parent.getWidth() - this.popupWindow.getWidth(), 1);
        }
    }

    private void dismiss() {
        if (this.popupWindow != null) {
            this.popupWindow.dismiss();
        }
    }

    @Override
    public void onBodyClick(View view, Customer customer, int position) {
        if (this.popupWindow != null) {
            this.popupWindow.dismiss();
        }
        if (this.mOnItemClickListener != null) {
            this.mOnItemClickListener.onItemSelect(this.popupWindow, customer);
        }
    }

    private long lastDismissTime;

    @Override
    public void onDismiss() {
        if (customerAdapter != null) {
            customerAdapter.getDataList().clear();
        }
        this.lastDismissTime = System.currentTimeMillis();
    }

    public void clear() {
        if (this.popupWindow != null) {
            this.popupWindow.dismiss();
            this.popupWindow = null;
        }
        if (this.customers != null) {
            this.customers.clear();
            this.customers = null;
        }
        this.parent = null;
        this.mOnItemClickListener = null;
        this.customerAdapter = null;
    }


    public interface OnItemClickListener {

        void onItemSelect(PopupWindow popupWindow, Customer customer);
    }

}
