package pers.itxj.pwdmgr.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pers.itxj.pwdmgr.R;
import pers.itxj.pwdmgr.data.PasswordItem;

/**
 * @author IT小佳
 * 创建日期： 2025/3/31
 * 描述： 密码条目适配器
 */
public class PasswordItemAdapter extends RecyclerView.Adapter<PasswordItemAdapter.ViewHolder> implements Filterable {
    /**
     * 密码条目列表（原始数据）
     */
    private final List<PasswordItem> passwordItemList;
    /**
     * 过滤后的密码条目列表
     */
    private final List<PasswordItem> filteredPasswordItemList;
    /**
     * 密码条目单击事件监听
     */
    private OnItemClickListener itemClickListener;
    /**
     * 密码条目单击事件监听
     */
    private OnItemLongClickListener itemLongClickListener;
    /**
     * 是否长按选中密码条目
     */
    private boolean isCheck = false;

    public PasswordItemAdapter(List<PasswordItem> passwordItemList) {
        this.passwordItemList = new ArrayList<>(passwordItemList);
        this.filteredPasswordItemList = new ArrayList<>(passwordItemList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.password_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        PasswordItem passwordItem = filteredPasswordItemList.get(position);
        if (passwordItem != null) {
            viewHolder.getTvTitle().setText(passwordItem.getTitle());
            viewHolder.getTvUsername().setText(passwordItem.getUsername());
            viewHolder.getTvCreatedAt().setText("创建时间：" + passwordItem.getCreatedAt());
            viewHolder.getTvUpdatedAt().setText("修改时间：" + passwordItem.getUpdatedAt());
            viewHolder.getTvVisitedAt().setText("访问时间：" + passwordItem.getVisitedAt());
        }
        // 设置长按选中与取消选中时密码条目的样式
        Drawable drawable;
        int colorRes;
        Context context = viewHolder.itemView.getContext();
        if (this.isCheck) {
            drawable = ContextCompat.getDrawable(context, R.drawable.password_item_rounded_corner_checked);
            colorRes = ContextCompat.getColor(context, R.color.fontWhite);
        } else {
            drawable = ContextCompat.getDrawable(context, R.drawable.password_item_rounded_corner);
            colorRes = ContextCompat.getColor(context, R.color.fontBlack);
        }
        viewHolder.getPasswordItemLayout().setBackground(drawable);
        viewHolder.getTvTitle().setTextColor(colorRes);
        viewHolder.getTvUsername().setTextColor(colorRes);

        // 点击事件处理
        viewHolder.itemView.setOnClickListener(v -> {
            if (itemClickListener != null && position != RecyclerView.NO_POSITION) {
                itemClickListener.onItemClick(passwordItem);
            }
        });

        // 长按事件处理
        viewHolder.itemView.setOnLongClickListener(v -> {
            if (itemLongClickListener != null && position != RecyclerView.NO_POSITION) {
                itemLongClickListener.onItemLongClick(position, passwordItem);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return filteredPasswordItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<PasswordItem> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(passwordItemList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (PasswordItem item : passwordItemList) {
                        if (item.getTitle().toLowerCase().contains(filterPattern) || item.getUsername().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredPasswordItemList.clear();
                filteredPasswordItemList.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }

    /**
     * 设置密码条目列表数据，并通知UI线程以更新界面
     *
     * @param passwordItemList 密码条目列表
     */
    public void setPasswordItemList(List<PasswordItem> passwordItemList) {
        this.passwordItemList.clear();
        this.passwordItemList.addAll(passwordItemList);
        filteredPasswordItemList.clear();
        filteredPasswordItemList.addAll(passwordItemList);
        notifyDataSetChanged();
    }

    /**
     * 根据密码条目索引获取密码项
     *
     * @param position 密码索引
     * @return 返回密码条目对象
     */
    public PasswordItem getPasswordItemAt(int position) {
        return filteredPasswordItemList.get(position);
    }

    public List<PasswordItem> getPasswordItemList() {
        return passwordItemList;
    }

    /**
     * 设置条目的选中状态，并通知UI线程以更新界面
     *
     * @param position 条目对应的索引
     * @param isCheck  是否选中
     */
    public void setItemCheckStatus(int position, boolean isCheck) {
        this.isCheck = isCheck;
        notifyItemChanged(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout passwordItemLayout;
        private final TextView tvTitle;
        private final TextView tvUsername;
        private final TextView tvCreatedAt;
        private final TextView tvUpdatedAt;
        private final TextView tvVisitedAt;

        public ViewHolder(View view) {
            super(view);
            passwordItemLayout = view.findViewById(R.id.password_item_layout);
            tvTitle = view.findViewById(R.id.tv_title);
            tvUsername = view.findViewById(R.id.tv_username);
            tvCreatedAt = view.findViewById(R.id.tv_created_at);
            tvUpdatedAt = view.findViewById(R.id.tv_updated_at);
            tvVisitedAt = view.findViewById(R.id.tv_visited_at);
        }

        public LinearLayout getPasswordItemLayout() {
            return passwordItemLayout;
        }

        public TextView getTvTitle() {
            return tvTitle;
        }

        public TextView getTvUsername() {
            return tvUsername;
        }

        public TextView getTvCreatedAt() {
            return tvCreatedAt;
        }

        public TextView getTvUpdatedAt() {
            return tvUpdatedAt;
        }

        public TextView getTvVisitedAt() {
            return tvVisitedAt;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(PasswordItem passwordItem);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position, PasswordItem passwordItem);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }
}
