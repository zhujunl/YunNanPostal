package com.miaxis.postal.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.miaxis.postal.R;
import com.miaxis.postal.data.entity.IDCard;
import com.miaxis.postal.util.ValueUtil;

import java.util.ArrayList;
import java.util.List;

public class IDCardFilterAdapter extends BaseAdapter implements Filterable {

    private ArrayFilter mFilter;
    private List<IDCard> dataList;
    private Context context;
    private ArrayList<IDCard> unfilteredData;
    private OnItemClickListener listener;

    public IDCardFilterAdapter(Context context) {
        this.dataList = new ArrayList<>();
        this.unfilteredData = new ArrayList<>();
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public IDCard getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = View.inflate(context, R.layout.item_id_card, null);
            holder = new ViewHolder();
            holder.llItem = view.findViewById(R.id.ll_item);
            holder.tvName = view.findViewById(R.id.tv_name);
            holder.tvCardNumber = view.findViewById(R.id.tv_card_number);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        IDCard idCard = dataList.get(position);
        holder.tvName.setText(ValueUtil.nameDesensitization(idCard.getName()));
        holder.tvCardNumber.setText(ValueUtil.cardNumberDesensitization(idCard.getCardNumber()));
        holder.llItem.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(holder.llItem, position);
            }
        });
        return view;
    }

    static class ViewHolder {
        public TextView tvName;
        public TextView tvCardNumber;
        public LinearLayout llItem;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    public List<IDCard> getDataList() {
        return dataList;
    }

    public void setDataList(List<IDCard> dataList) {
        this.dataList = dataList;
    }

    public void setUnfilteredData(ArrayList<IDCard> unfilteredData) {
        this.unfilteredData = unfilteredData;
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

    private class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (prefix == null || prefix.length() == 0) {
                ArrayList<IDCard> list = unfilteredData;
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();
                ArrayList<IDCard> unfilteredValues = unfilteredData;
                int count = unfilteredValues.size();
                ArrayList<IDCard> newValues = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    IDCard idCard = unfilteredValues.get(i);
                    if (idCard != null) {
                        if (idCard.getCardNumber().startsWith(prefixString)) {
                            newValues.add(idCard);
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            dataList = (List<IDCard>) results.values;
//            if (results.count > 0) {
                notifyDataSetChanged();
//            } else {
//                notifyDataSetInvalidated();
//            }
        }

    }
}