package pers.itxj.pwdmgr.ui;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import pers.itxj.pwdmgr.R;
import pers.itxj.pwdmgr.constant.SortType;
import pers.itxj.pwdmgr.constant.keys.CommonSPKey;
import pers.itxj.pwdmgr.utils.PrefsHelper;

/**
 * @author IT小佳
 * 创建日期： 2025/4/2
 * 描述： 自定义排序弹框
 */
public class SortDialog extends Dialog {
    private static final String TAG = "SortDialog";
    private final RadioGroup rgSort;
    private final RadioGroup rgSortMethod;
    private RadioButton rbCreatedAt, rbUpdatedAt, rbVisitedAt, rbTitle, rbUsername, rbDesc, rbAsc;

    public interface OnSortChangedListener {
        void onSortChanged(int selectedSortId, int selectedSortMethodId);
    }

    public SortDialog(@NonNull Context context, OnSortChangedListener sortChangedListener) {
        super(context);
        setContentView(R.layout.diaglog_sort);

        rgSort = findViewById(R.id.rg_sort);
        rgSortMethod = findViewById(R.id.rg_sort_method);
        rbCreatedAt = findViewById(R.id.rb_created_at);
        rbUpdatedAt = findViewById(R.id.rb_updated_at);
        rbVisitedAt = findViewById(R.id.rb_visited_at);
        rbTitle = findViewById(R.id.rb_title);
        rbUsername = findViewById(R.id.rb_username);
        rbDesc = findViewById(R.id.rb_desc);
        rbAsc = findViewById(R.id.rb_asc);

        // 默认选中
        rbDesc.setChecked(true);
        rbCreatedAt.setChecked(true);

        // 按钮事件
        findViewById(R.id.btn_sort_cancel).setOnClickListener(v -> dismiss());
        findViewById(R.id.btn_sort_confirm).setOnClickListener(v -> {
            int selectedSortId = rgSort.getCheckedRadioButtonId();
            int selectedSortMethodId = rgSortMethod.getCheckedRadioButtonId();
            // 存储选中的id到SP中
            Log.d(TAG, "存储选中的id到SP中");
            PrefsHelper prefs = new PrefsHelper.Builder(context).build();
            prefs.putInt(CommonSPKey.SELECTED_SORT_ID.name(), selectedSortId)
                    .putInt(CommonSPKey.SELECTED_SORT_METHOD_ID.name(), selectedSortMethodId)
                    .apply();

            // 设置选中的ID
            rbCreatedAt.setChecked(selectedSortId == SortType.CREATED_AT.getRadioButtonId());
            rbUpdatedAt.setChecked(selectedSortId == SortType.UPDATED_AT.getRadioButtonId());
            rbVisitedAt.setChecked(selectedSortId == SortType.VISITED_AT.getRadioButtonId());
            rbTitle.setChecked(selectedSortId == SortType.TITLE.getRadioButtonId());
            rbUsername.setChecked(selectedSortId == SortType.USERNAME.getRadioButtonId());
            rbDesc.setChecked(selectedSortMethodId == SortType.DESC.getRadioButtonId());
            rbAsc.setChecked(selectedSortMethodId == SortType.ASC.getRadioButtonId());
            sortChangedListener.onSortChanged(selectedSortId, selectedSortMethodId);
            dismiss();
        });
    }
}
