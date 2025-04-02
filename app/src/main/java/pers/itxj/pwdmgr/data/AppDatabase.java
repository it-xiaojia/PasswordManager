package pers.itxj.pwdmgr.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * @author IT小佳
 * 创建日期： 2025/4/1
 * 描述： 数据库
 */
@Database(entities = {PasswordItem.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;

    public abstract PasswordItemDao passwordDao();

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "itxj_password_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
