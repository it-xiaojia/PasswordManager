package pers.itxj.pwdmgr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import pers.itxj.pwdmgr.adapter.PasswordItemAdapter;
import pers.itxj.pwdmgr.data.PasswordItem;
import pers.itxj.pwdmgr.databinding.ActivityMainBinding;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.rvPassword.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPassword.setHasFixedSize(true);
        binding.rvPassword.addItemDecoration(new SpacingItemDecoration(20, true));

        passwordItemAdapter = new PasswordItemAdapter();
        binding.rvPassword.setAdapter(passwordItemAdapter);

        passwordViewModel = new ViewModelProvider(this).get(PasswordViewModel.class);
        passwordViewModel.getAllPasswords().observe(this, passwordItemList ->
                passwordItemAdapter.setPasswordItemList(passwordItemList));

        // 列表项单击
        passwordItemAdapter.setOnItemClickListener(passwordItem -> {
            // 点击查看
            this.currentSelectPasswordItem = passwordItem;
            toPasswordOperationActivity(true);
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

        // 接收从另一个Activity返回的结果
        register = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResult -> {
            if (activityResult != null) {
                Intent intent = activityResult.getData();
                if (intent != null && activityResult.getResultCode() == RESULT_OK) {
                    int passId = intent.getIntExtra("passId", 0);
                    PasswordItem passwordItem = new PasswordItem();
                    passwordItem.setTitle(intent.getStringExtra("title"));
                    passwordItem.setUsername(intent.getStringExtra("username"));
                    passwordItem.setPassword(intent.getStringExtra("password"));
                    // 获取当前日期
                    Calendar calendar = Calendar.getInstance();
                    // 定义日期格式
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    // 格式化输出
                    String currentDate = dateFormat.format(calendar.getTime());
                    // 新增
                    if (passId == 0) {
                        passwordItem.setCreatedAt(currentDate);
                        passwordItem.setUpdatedAt(currentDate);
                        passwordItem.setVisitedAt(currentDate);
                        passwordViewModel.insert(passwordItem);
                    } else {
                        // 更新
                        passwordItem.setPassId(passId);
                        passwordItem.setUpdatedAt(currentDate);
                        passwordViewModel.update(passwordItem);
                    }
                }
            }
        });
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
        intent.putExtra("isView", isView);
        intent.putExtra("passId", currentSelectPasswordItem.getPassId());
        intent.putExtra("title", currentSelectPasswordItem.getTitle());
        intent.putExtra("username", currentSelectPasswordItem.getUsername());
        intent.putExtra("password", currentSelectPasswordItem.getPassword());
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
    }

}