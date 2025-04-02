package pers.itxj.pwdmgr.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class PasswordItemAdapter extends RecyclerView.Adapter<PasswordItemAdapter.ViewHolder> {
    /**
     * 密码条目列表
     */
    private List<PasswordItem> passwordItemList = new ArrayList<>();
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.password_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        PasswordItem passwordItem = passwordItemList.get(position);
        if (passwordItem != null) {
            viewHolder.getTvTitle().setText(passwordItem.getTitle());
            viewHolder.getTvUsername().setText(passwordItem.getUsername());
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
    }

    @Override
    public int getItemCount() {
        return passwordItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 设置密码条目列表数据，并通知UI线程以更新界面
     *
     * @param passwordItemList 密码条目列表
     */
    public void setPasswordItemList(List<PasswordItem> passwordItemList) {
        this.passwordItemList = passwordItemList;
        notifyDataSetChanged();
    }

    /**
     * 根据密码条目索引获取密码项
     *
     * @param position 密码索引
     * @return 返回密码条目对象
     */
    public PasswordItem getPasswordItemAt(int position) {
        return passwordItemList.get(position);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout passwordItemLayout;
        private final TextView tvTitle;
        private final TextView tvUsername;

        public ViewHolder(View view) {
            super(view);
            passwordItemLayout = view.findViewById(R.id.password_item_layout);
            tvTitle = view.findViewById(R.id.tv_title);
            tvUsername = view.findViewById(R.id.tv_username);

            view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (itemClickListener != null && position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(passwordItemList.get(position));
                }
            });

            view.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (itemLongClickListener != null && position != RecyclerView.NO_POSITION) {
                    itemLongClickListener.onItemLongClick(position, passwordItemList.get(position));
                    return true;
                }
                return false;
            });
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
