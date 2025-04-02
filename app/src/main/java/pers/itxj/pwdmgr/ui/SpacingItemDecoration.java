package pers.itxj.pwdmgr.ui;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

/**
 * @author IT小佳
 * 创建日期： 2025/4/1
 * 描述： 间距装饰
 */
public class SpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final int spacing;
    private final boolean includeEdge;

    public SpacingItemDecoration(int spacing, boolean includeEdge) {
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int itemCount = Objects.requireNonNull(parent.getAdapter()).getItemCount();
        // 垂直列表的间距设置
        if (includeEdge) {
            outRect.top = (position == 0) ? spacing : 0; // 第一个项顶部间距
            outRect.bottom = spacing; // 所有项底部间距
        } else {
            outRect.bottom = (position == itemCount - 1) ? 0 : spacing; // 最后一项无底部间距
        }
    }
}
