package pers.itxj.pwdmgr.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * @author IT小佳
 * 创建日期： 2025/4/3
 * 描述： SharedPreferences 增强工具类 支持：普通存储 / 加密存储 / 类型安全 / 链式调用
 */
public class PrefsHelper {
    private static final String DEFAULT_NAME = "itxj_app_prefs";
    private final SharedPreferences mPrefs;
    private final SharedPreferences.Editor mEditor;

    // 私有构造方法
    private PrefsHelper(Context context, String name, boolean encrypted) {
        try {
            if (encrypted) {
                MasterKey masterKey = new MasterKey.Builder(context)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();
                mPrefs = EncryptedSharedPreferences.create(
                        context,
                        name,
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );
            } else {
                mPrefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);
            }
            mEditor = mPrefs.edit();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("初始化SharedPreferences失败", e);
        }
    }

    /*---------- 链式操作接口 ----------*/
    public PrefsHelper putString(@NonNull String key, String value) {
        mEditor.putString(key, value);
        return this;
    }

    public PrefsHelper putInt(@NonNull String key, int value) {
        mEditor.putInt(key, value);
        return this;
    }

    public PrefsHelper putBoolean(@NonNull String key, boolean value) {
        mEditor.putBoolean(key, value);
        return this;
    }

    public PrefsHelper putFloat(@NonNull String key, float value) {
        mEditor.putFloat(key, value);
        return this;
    }

    public PrefsHelper putLong(@NonNull String key, long value) {
        mEditor.putLong(key, value);
        return this;
    }

    public PrefsHelper remove(@NonNull String key) {
        mEditor.remove(key);
        return this;
    }

    public PrefsHelper clear() {
        mEditor.clear();
        return this;
    }

    /*---------- 同步/异步提交 ----------*/
    public void apply() {
        mEditor.apply();
    }

    public boolean commit() {
        return mEditor.commit();
    }

    /*---------- 数据读取方法 ----------*/
    public String getString(@NonNull String key, String defValue) {
        return mPrefs.getString(key, defValue);
    }

    public int getInt(@NonNull String key, int defValue) {
        return mPrefs.getInt(key, defValue);
    }

    public boolean getBoolean(@NonNull String key, boolean defValue) {
        return mPrefs.getBoolean(key, defValue);
    }

    public float getFloat(@NonNull String key, float defValue) {
        return mPrefs.getFloat(key, defValue);
    }

    public long getLong(@NonNull String key, long defValue) {
        return mPrefs.getLong(key, defValue);
    }

    /*---------- 构建器模式 ----------*/
    public static class Builder {
        private final Context mContext;
        private String mName = DEFAULT_NAME;
        private boolean mEncrypted = false;

        public Builder(Context context) {
            this.mContext = context.getApplicationContext();
        }

        public Builder setName(String name) {
            this.mName = name;
            return this;
        }

        public Builder setEncrypted(boolean encrypted) {
            this.mEncrypted = encrypted;
            return this;
        }

        public PrefsHelper build() {
            return new PrefsHelper(mContext, mName, mEncrypted);
        }
    }
}
