package pers.itxj.pwdmgr.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executors;

import pers.itxj.pwdmgr.data.AppDatabase;
import pers.itxj.pwdmgr.data.PasswordItem;
import pers.itxj.pwdmgr.data.PasswordItemDao;

/**
 * @author IT小佳
 * 创建日期： 2025/4/1
 * 描述： 密码数据视图模型
 */
public class PasswordViewModel extends AndroidViewModel {
    private final PasswordItemDao passwordItemDao;
    private final LiveData<List<PasswordItem>> passwordItemLiveData;

    public PasswordViewModel(@NonNull Application application) {
        super(application);
        AppDatabase instance = AppDatabase.getInstance(application);
        passwordItemDao = instance.passwordDao();
        passwordItemLiveData = passwordItemDao.loadAllPasswords();
    }

    public LiveData<List<PasswordItem>> getAllPasswords() {
        return passwordItemLiveData;
    }

    public void insert(PasswordItem passwordItem) {
        Executors.newSingleThreadExecutor().execute(() -> {
            passwordItemDao.insert(passwordItem);
        });
    }

    public void update(PasswordItem passwordItem) {
        Executors.newSingleThreadExecutor().execute(() -> {
            passwordItemDao.update(passwordItem);
        });
    }

    public void delete(PasswordItem passwordItem) {
        Executors.newSingleThreadExecutor().execute(() -> {
            passwordItemDao.delete(passwordItem);
        });
    }
}
