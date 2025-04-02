package pers.itxj.pwdmgr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import pers.itxj.pwdmgr.databinding.ActivityPasswordOperationBinding;

public class PasswordOperationActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityPasswordOperationBinding binding;
    private int passId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordOperationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.btnCancel.setOnClickListener(this);
        binding.btnSave.setOnClickListener(this);
        Intent intent = getIntent();
        this.passId = intent.getIntExtra("passId", 0);
        boolean isView = intent.getBooleanExtra("isView", false);
        binding.tvAddTitle.setEnabled(!isView);
        binding.tvAddUsername.setEnabled(!isView);
        binding.tvAddPassword.setEnabled(!isView);
        binding.btnSave.setVisibility(isView ? View.GONE : View.VISIBLE);
        if (this.passId > 0) {
            binding.tvAddTitle.setText(intent.getStringExtra("title"));
            binding.tvAddUsername.setText(intent.getStringExtra("username"));
            binding.tvAddPassword.setText(intent.getStringExtra("password"));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == binding.btnCancel.getId()) {
            finish();
        }
        if (id == binding.btnSave.getId()) {
            Intent intent = new Intent();
            String title = binding.tvAddTitle.getText().toString();
            String username = binding.tvAddUsername.getText().toString();
            String password = binding.tvAddPassword.getText().toString();
            intent.putExtra("passId", this.passId);
            intent.putExtra("title", title);
            intent.putExtra("username", username);
            intent.putExtra("password", password);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}