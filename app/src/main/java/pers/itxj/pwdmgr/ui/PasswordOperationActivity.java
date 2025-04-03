package pers.itxj.pwdmgr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import pers.itxj.pwdmgr.constant.CommonConstant;
import pers.itxj.pwdmgr.constant.keys.CommonIntentExtraKey;
import pers.itxj.pwdmgr.constant.keys.PasswordItemIntentExtraKey;
import pers.itxj.pwdmgr.databinding.ActivityPasswordOperationBinding;
import pers.itxj.pwdmgr.utils.DateUtils;

public class PasswordOperationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PasswordOperationActivity";
    private ActivityPasswordOperationBinding binding;
    /**
     * 密码ID
     */
    private int passId = 0;
    /**
     * 访问时间
     */
    private String visitedAt;
    private boolean isView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordOperationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // 绑定按钮监听
        binding.btnCancel.setOnClickListener(this);
        binding.btnSave.setOnClickListener(this);

        // 获取从上一个activity中传递的的数据
        Intent intent = getIntent();
        // 从intent中读取并存储全局密码ID
        passId = intent.getIntExtra(PasswordItemIntentExtraKey.PASS_ID.name(), 0);
        Log.d(TAG, "全局密码ID：" + passId);
        // 是否查看模式
        isView = intent.getBooleanExtra(CommonIntentExtraKey.IS_VIEW.name(), false);
        Log.d(TAG, "当前模式：" + (isView ? "查看模式" : "新增/更新模式"));
        // 如果是查看模式，则设置不可编辑且隐藏掉保存按钮
        binding.etTitle.setEnabled(!isView);
        binding.etUsername.setEnabled(!isView);
        binding.etPassword.setEnabled(!isView);
        binding.btnSave.setVisibility(isView ? View.GONE : View.VISIBLE);
        // 点进来的时候，设置访问时间为当前时间
        this.visitedAt = DateUtils.getCurrentDate(CommonConstant.DATE_FORMAT);
        // 更新
        if (passId > 0) {
            Log.d(TAG, "更新/查看数据");
            binding.etTitle.setText(intent.getStringExtra(PasswordItemIntentExtraKey.TITLE.name()));
            binding.etUsername.setText(intent.getStringExtra(PasswordItemIntentExtraKey.USERNAME.name()));
            binding.etPassword.setText(intent.getStringExtra(PasswordItemIntentExtraKey.PASSWORD.name()));
            binding.etCreatedAt.setText(intent.getStringExtra(PasswordItemIntentExtraKey.CREATED_AT.name()));
            binding.etUpdatedAt.setText(intent.getStringExtra(PasswordItemIntentExtraKey.UPDATED_AT.name()));
            binding.etVisitedAt.setText(intent.getStringExtra(PasswordItemIntentExtraKey.VISITED_AT.name()));
            // 查看
            if (isView) {
                Log.d(TAG, "查看数据");
                // 显示创建时间、更新时间、最后一次访问时间
                binding.etCreatedAt.setVisibility(View.VISIBLE);
                binding.etUpdatedAt.setVisibility(View.VISIBLE);
                binding.etVisitedAt.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.btnCancel.getId()) {
            finish();
        }

        if (id == binding.btnSave.getId()) {
            String title = binding.etTitle.getText().toString();
            String username = binding.etUsername.getText().toString();
            String password = binding.etPassword.getText().toString();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(this, "名称不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(username)) {
                Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            String currentDate = DateUtils.getCurrentDate(CommonConstant.DATE_FORMAT);
            Intent intent = new Intent();
            // 新增时创建时间传当前时间，更新或查看传第一次创建时写入的时间
            String createdAt = passId == 0 ? currentDate : binding.etCreatedAt.getText().toString();
            // 新增和更新时更新时间传当前时间，查看时传第一次更新写入的时间
            String updatedAt = isView ? binding.etUpdatedAt.getText().toString() : currentDate;
            intent.putExtra(PasswordItemIntentExtraKey.PASS_ID.name(), passId);
            intent.putExtra(PasswordItemIntentExtraKey.TITLE.name(), title);
            intent.putExtra(PasswordItemIntentExtraKey.USERNAME.name(), username);
            intent.putExtra(PasswordItemIntentExtraKey.PASSWORD.name(), password);
            intent.putExtra(PasswordItemIntentExtraKey.CREATED_AT.name(), createdAt);
            intent.putExtra(PasswordItemIntentExtraKey.UPDATED_AT.name(), updatedAt);
            // 新增、更新和查看传点进去的时间
            intent.putExtra(PasswordItemIntentExtraKey.VISITED_AT.name(), visitedAt);
            Log.d(TAG, "操作参数：passId=" + passId + ",title=" + title + ",username=" + username + "," +
                    "password=" + password + ",createdAt=" + createdAt + ",updatedAt=" + updatedAt + ",visitedAt=" + visitedAt);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}