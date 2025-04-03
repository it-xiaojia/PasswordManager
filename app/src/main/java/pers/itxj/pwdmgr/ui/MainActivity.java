package pers.itxj.pwdmgr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import pers.itxj.pwdmgr.adapter.PasswordItemAdapter;
import pers.itxj.pwdmgr.constant.CommonConstant;
import pers.itxj.pwdmgr.constant.SortType;
import pers.itxj.pwdmgr.constant.keys.CommonIntentExtraKey;
import pers.itxj.pwdmgr.constant.keys.CommonSPKey;
import pers.itxj.pwdmgr.constant.keys.PasswordItemIntentExtraKey;
import pers.itxj.pwdmgr.data.PasswordItem;
import pers.itxj.pwdmgr.databinding.ActivityMainBinding;
import pers.itxj.pwdmgr.utils.DateUtils;
import pers.itxj.pwdmgr.utils.PrefsHelper;
import pers.itxj.pwdmgr.viewmodel.PasswordViewModel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private ActivityResultLauncher<Intent> register;
    private PasswordItemAdapter passwordItemAdapter;
    private PasswordViewModel passwordViewModel;
    /**
     * 密码列表项的位置
     */
    private int itemPosition;
    /**
     * 当前选中的密码条目对象
     */
    private PasswordItem currentSelectPasswordItem;
    private SortDialog sortDialog;
    private int selectedSortId, selectedSortMethodId;
    private PrefsHelper prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        prefs = new PrefsHelper.Builder(this).build();

        binding.rvPassword.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPassword.setHasFixedSize(true);
        binding.rvPassword.addItemDecoration(new SpacingItemDecoration(20, true));

        passwordItemAdapter = new PasswordItemAdapter(new ArrayList<>());
        binding.rvPassword.setAdapter(passwordItemAdapter);

        passwordViewModel = new ViewModelProvider(this).get(PasswordViewModel.class);
        passwordViewModel.getAllPasswords().observe(this, passwordItemList -> {
            passwordItemAdapter.setPasswordItemList(passwordItemList);
            Log.d(TAG, "密码列表改变，重新执行数据库查询获取数据");
            this.selectedSortId = prefs.getInt(CommonSPKey.SELECTED_SORT_ID.name(), 0);
            this.selectedSortMethodId = prefs.getInt(CommonSPKey.SELECTED_SORT_METHOD_ID.name(), 0);
            Log.d(TAG, "this.selectedSortId=" + this.selectedSortId);
            Log.d(TAG, "this.selectedSortMethodId=" + this.selectedSortMethodId);
            if (this.selectedSortId != 0 && this.selectedSortMethodId != 0) {
                // 新增或者更新成功后，应用过滤
                sortPasswordItemList();
            }
        });

        // 列表项单击
        passwordItemAdapter.setOnItemClickListener(passwordItem -> {
            // 点击查看
            this.currentSelectPasswordItem = passwordItem;
            toPasswordOperationActivity(true);
            // 更新访问时间
            passwordItem.setVisitedAt(DateUtils.getCurrentDate(CommonConstant.DATE_FORMAT));
            passwordViewModel.update(passwordItem);
        });

        // 列表项长按
        passwordItemAdapter.setOnItemLongClickListener((position, passwordItem) -> {
            this.itemPosition = position;
            this.currentSelectPasswordItem = passwordItem;
            passwordItemAdapter.setItemCheckStatus(position, true);
            // 隐藏添加按钮
            binding.fabAddPassword.setVisibility(View.GONE);
            // 显示操作按钮
            binding.opLayout.setVisibility(View.VISIBLE);
        });

        // 设置界面按钮的点击监听
        binding.fabAddPassword.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
        binding.btnUpdate.setOnClickListener(this);
        binding.btnDelete.setOnClickListener(this);
        binding.btnSort.setOnClickListener(this);

        // 接收从另一个Activity返回的结果
        register = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResult -> {
            if (activityResult != null) {
                Intent intent = activityResult.getData();
                if (intent != null && activityResult.getResultCode() == RESULT_OK) {
                    int passId = intent.getIntExtra(PasswordItemIntentExtraKey.PASS_ID.name(), 0);
                    PasswordItem passwordItem = new PasswordItem();
                    passwordItem.setTitle(intent.getStringExtra(PasswordItemIntentExtraKey.TITLE.name()));
                    passwordItem.setUsername(intent.getStringExtra(PasswordItemIntentExtraKey.USERNAME.name()));
                    passwordItem.setPassword(intent.getStringExtra(PasswordItemIntentExtraKey.PASSWORD.name()));
                    passwordItem.setCreatedAt(intent.getStringExtra(PasswordItemIntentExtraKey.CREATED_AT.name()));
                    passwordItem.setUpdatedAt(intent.getStringExtra(PasswordItemIntentExtraKey.UPDATED_AT.name()));
                    passwordItem.setVisitedAt(intent.getStringExtra(PasswordItemIntentExtraKey.VISITED_AT.name()));
                    // 新增
                    if (passId == 0) {
                        passwordViewModel.insert(passwordItem);
                        Log.d(TAG, "成功给数据库新增一条数据");
                    } else {
                        // 更新
                        passwordItem.setPassId(passId);
                        passwordViewModel.update(passwordItem);
                        Log.d(TAG, "成功将passId=" + passId + "的数据进行更新");
                    }
                }
            }
        });

        // 搜索监听
        binding.svPassword.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                passwordItemAdapter.getFilter().filter(newText);
                return true;
            }
        });

        // 创建搜索对话框实例
        sortDialog = new SortDialog(MainActivity.this, (selectedSortId, selectedSortMethodId) -> {
            this.selectedSortId = selectedSortId;
            this.selectedSortMethodId = selectedSortMethodId;
            Log.d(TAG, "selectedSortId=" + selectedSortId);
            Log.d(TAG, "selectedSortMethodId=" + selectedSortMethodId);
            sortPasswordItemList();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出应用的时候，将存储的值从SP中移除
        prefs.remove(CommonSPKey.SELECTED_SORT_ID.name());
        prefs.remove(CommonSPKey.SELECTED_SORT_METHOD_ID.name());
        prefs.commit();
        Log.d(TAG, "程序退出，清理SP中存储的selectedSortId, selectedSortMethodId");
    }

    private void sortPasswordItemList() {
        Log.d(TAG, "排序ID：selectedSortId=" + selectedSortId + ",selectedSortMethodId=" + selectedSortMethodId);
        // 源列表
        List<PasswordItem> passwordItemList = passwordItemAdapter.getPasswordItemList();
        // 排序后的列表
        List<PasswordItem> sortedPasswordItemList = new ArrayList<>();
        if (selectedSortMethodId == SortType.ASC.getRadioButtonId()) {
            Log.d(TAG, "升序排序");
            if (selectedSortId == SortType.CREATED_AT.getRadioButtonId()) {
                sortedPasswordItemList = passwordItemList.stream()
                        .sorted(Comparator.comparing(PasswordItem::getCreatedAt))
                        .collect(Collectors.toList());
                Log.d(TAG, "升序排序：CREATED_AT");
            }
            if (selectedSortId == SortType.UPDATED_AT.getRadioButtonId()) {
                sortedPasswordItemList = passwordItemList.stream()
                        .sorted(Comparator.comparing(PasswordItem::getUpdatedAt))
                        .collect(Collectors.toList());
                Log.d(TAG, "升序排序：UPDATED_AT");
            }
            if (selectedSortId == SortType.VISITED_AT.getRadioButtonId()) {
                sortedPasswordItemList = passwordItemList.stream()
                        .sorted(Comparator.comparing(PasswordItem::getVisitedAt))
                        .collect(Collectors.toList());
                Log.d(TAG, "升序排序：VISITED_AT");
            }
            if (selectedSortId == SortType.TITLE.getRadioButtonId()) {
                sortedPasswordItemList = passwordItemList.stream()
                        .sorted(Comparator.comparing(PasswordItem::getTitle))
                        .collect(Collectors.toList());
                Log.d(TAG, "升序排序：TITLE");
            }
            if (selectedSortId == SortType.USERNAME.getRadioButtonId()) {
                sortedPasswordItemList = passwordItemList.stream()
                        .sorted(Comparator.comparing(PasswordItem::getUsername))
                        .collect(Collectors.toList());
                Log.d(TAG, "升序排序：USERNAME");
            }
        }
        if (selectedSortMethodId == SortType.DESC.getRadioButtonId()) {
            Log.d(TAG, "降序排序");
            if (selectedSortId == SortType.CREATED_AT.getRadioButtonId()) {
                sortedPasswordItemList = passwordItemList.stream()
                        .sorted(Comparator.comparing(PasswordItem::getCreatedAt).reversed())
                        .collect(Collectors.toList());
                Log.d(TAG, "降序排序：CREATED_AT");
            }
            if (selectedSortId == SortType.UPDATED_AT.getRadioButtonId()) {
                sortedPasswordItemList = passwordItemList.stream()
                        .sorted(Comparator.comparing(PasswordItem::getUpdatedAt).reversed())
                        .collect(Collectors.toList());
                Log.d(TAG, "降序排序：UPDATED_AT");
            }
            if (selectedSortId == SortType.VISITED_AT.getRadioButtonId()) {
                sortedPasswordItemList = passwordItemList.stream()
                        .sorted(Comparator.comparing(PasswordItem::getVisitedAt).reversed())
                        .collect(Collectors.toList());
                Log.d(TAG, "降序排序：VISITED_AT");
            }
            if (selectedSortId == SortType.TITLE.getRadioButtonId()) {
                sortedPasswordItemList = passwordItemList.stream()
                        .sorted(Comparator.comparing(PasswordItem::getTitle).reversed())
                        .collect(Collectors.toList());
                Log.d(TAG, "降序排序：TITLE");
            }
            if (selectedSortId == SortType.USERNAME.getRadioButtonId()) {
                sortedPasswordItemList = passwordItemList.stream()
                        .sorted(Comparator.comparing(PasswordItem::getUsername).reversed())
                        .collect(Collectors.toList());
                Log.d(TAG, "降序排序：USERNAME");
            }
        }
        passwordItemAdapter.setPasswordItemList(sortedPasswordItemList);
    }

    private void restoreDefault() {
        // 隐藏操作区域
        binding.opLayout.setVisibility(View.GONE);
        // 显示新增按钮
        binding.fabAddPassword.setVisibility(View.VISIBLE);
        // 将密码条目恢复至取消选择状态
        passwordItemAdapter.setItemCheckStatus(this.itemPosition, false);
    }

    private void toPasswordOperationActivity(boolean isView) {
        Intent intent = new Intent(this, PasswordOperationActivity.class);
        intent.putExtra(CommonIntentExtraKey.IS_VIEW.name(), isView);
        intent.putExtra(PasswordItemIntentExtraKey.PASS_ID.name(), currentSelectPasswordItem.getPassId());
        intent.putExtra(PasswordItemIntentExtraKey.TITLE.name(), currentSelectPasswordItem.getTitle());
        intent.putExtra(PasswordItemIntentExtraKey.USERNAME.name(), currentSelectPasswordItem.getUsername());
        intent.putExtra(PasswordItemIntentExtraKey.PASSWORD.name(), currentSelectPasswordItem.getPassword());
        intent.putExtra(PasswordItemIntentExtraKey.CREATED_AT.name(), currentSelectPasswordItem.getCreatedAt());
        intent.putExtra(PasswordItemIntentExtraKey.UPDATED_AT.name(), currentSelectPasswordItem.getUpdatedAt());
        intent.putExtra(PasswordItemIntentExtraKey.VISITED_AT.name(), currentSelectPasswordItem.getVisitedAt());
        register.launch(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.fabAddPassword.getId()) {
            Intent intent = new Intent(this, PasswordOperationActivity.class);
            register.launch(intent);
        }
        if (id == binding.btnCancel.getId()) {
            restoreDefault();
        }
        if (id == binding.btnUpdate.getId()) {
            toPasswordOperationActivity(false);
            restoreDefault();
        }
        if (id == binding.btnDelete.getId()) {
            passwordViewModel.delete(currentSelectPasswordItem);
            restoreDefault();
        }
        if (id == binding.btnSort.getId()) {
            sortDialog.show();
        }
    }

}