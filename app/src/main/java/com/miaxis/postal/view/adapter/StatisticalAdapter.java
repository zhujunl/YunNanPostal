package com.miaxis.postal.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miaxis.postal.R;
import com.miaxis.postal.data.bean.Statistical;
import com.miaxis.postal.util.DateUtil;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StatisticalAdapter extends RecyclerView.Adapter<StatisticalAdapter.BodyViewHolder> {

    //    private TreeMap<String, TreeMap<String, Statistical>> itemList;
    //    private List<Map.Entry<String, TreeMap<String, Statistical>>> itemList;

    private List<StatisticalItem> itemList;

    public StatisticalAdapter() {
    }

    public void setItemList(List<StatisticalItem> list) {
        this.itemList = list;
        notifyDataSetChanged();
    }

    @Override
    public StatisticalAdapter.BodyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistical, parent, false);
        return new BodyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticalAdapter.BodyViewHolder holder, int position) {
        if (this.itemList != null) {
            StatisticalItem statistical = itemList.get(position);
            holder.bind(statistical);
        }
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    public static class BodyViewHolder extends RecyclerView.ViewHolder {

        BodyViewHolder(View itemView) {
            super(itemView);
        }

        public void bind(StatisticalItem item) {
            //this.itemView.setBackgroundResource(branch.isSelected ? R.drawable.bg_branch : R.drawable.bg_white_shape);
            Date date = null;
            try {
                date = DateUtil.DATE_FORMAT_TIME.parse(item.date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (date==null){
                return;
            }
            TextView tv_date_year = (TextView) itemView.findViewById(R.id.tv_date_year);
            tv_date_year.setText(DateUtil.DATE_FORMAT_YEAR.format(date));
            TextView tv_date_date = (TextView) itemView.findViewById(R.id.tv_date_date);
            tv_date_date.setText(DateUtil.DATE_FORMAT_DATE.format(date));

            TextView tv_all_num = (TextView) itemView.findViewById(R.id.tv_all_num);
            tv_all_num.setText("" + item.allCounts);

            TextView tv_check_num = (TextView) itemView.findViewById(R.id.tv_check_num);
            tv_check_num.setText("" + item.checkCounts);

            TextView tv_no_check_num = (TextView) itemView.findViewById(R.id.tv_no_check_num);
            tv_no_check_num.setText("" + item.noCheckCounts);

            List<Statistical> list = item.list;
            LinearLayout ll_branch = itemView.findViewById(R.id.ll_branch);
            ll_branch.removeAllViews();
            if (list != null && !list.isEmpty()) {
                View title = LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_item_title_statistical, (ViewGroup) itemView, false);
                ll_branch.addView(title);
                for (Statistical statistical : list) {
                    View view = LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_item_statistical, (ViewGroup) itemView, false);

                    TextView tv_branch_name = (TextView) view.findViewById(R.id.tv_branch_name);
                    tv_branch_name.setText(statistical.NAME);
                    TextView item_all_num = (TextView) view.findViewById(R.id.tv_all_num);
                    item_all_num.setText("" + statistical.sendNumber);

                    TextView item_check_num = (TextView) view.findViewById(R.id.tv_check_num);
                    item_check_num.setText("" + statistical.checkNumber);

                    TextView item_no_check_num = (TextView) view.findViewById(R.id.tv_no_check_num);
                    item_no_check_num.setText("" + statistical.noCheckNumber);
                    ll_branch.addView(view);
                }
            }
        }
    }

    public static class StatisticalItem {
        public String date;
        public long allCounts;
        public long checkCounts;
        public long noCheckCounts;
        public List<Statistical> list;

        public StatisticalItem(String date, long allCounts, long checkCounts, long noCheckCounts, List<Statistical> list) {
            this.date = date;
            this.allCounts = allCounts;
            this.checkCounts = checkCounts;
            this.noCheckCounts = noCheckCounts;
            this.list = list;
        }

        @Override
        public String toString() {
            return "StatisticalItem{" +
                    "date='" + date + '\'' +
                    ", allCounts=" + allCounts +
                    ", checkCounts=" + checkCounts +
                    ", noCheckCounts=" + noCheckCounts +
                    ", list=" + list +
                    '}';
        }
    }
}
