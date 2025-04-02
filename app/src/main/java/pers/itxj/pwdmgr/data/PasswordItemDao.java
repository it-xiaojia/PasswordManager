package pers.itxj.pwdmgr.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * @author IT小佳
 * 创建日期： 2025/4/1
 * 描述： 密码数据访问
 */
@Dao
public interface PasswordItemDao {
    @Insert
    void insert(PasswordItem passwordItem);

    @Update
    void update(PasswordItem passwordItem);

    @Delete
    void delete(PasswordItem passwordItem);

    @Query("select * from password_item order by created_at desc")
    LiveData<List<PasswordItem>> loadAllPasswords();

    @Query("select * from password_item where pass_id = :passId")
    LiveData<PasswordItem> loadById(String passId);
}
