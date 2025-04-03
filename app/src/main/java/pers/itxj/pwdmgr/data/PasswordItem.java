package pers.itxj.pwdmgr.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author IT小佳
 * 创建日期： 2025/3/31
 * 描述： 密码条目
 */
@Entity(tableName = "password_item")
public class PasswordItem implements Comparable<PasswordItem> {
    /**
     * 密码主键
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pass_id")
    private Integer passId;

    /**
     * 标题
     */
    @ColumnInfo(name = "title")
    private String title;

    /**
     * 用户名
     */
    @ColumnInfo(name = "username")
    private String username;

    /**
     * 密码
     */
    @ColumnInfo(name = "password")
    private String password;

    /**
     * 创建时间
     */
    @ColumnInfo(name = "created_at")
    private String createdAt;

    /**
     * 更新时间
     */
    @ColumnInfo(name = "updated_at")
    private String updatedAt;

    /**
     * 访问时间
     */
    @ColumnInfo(name = "visited_at")
    private String visitedAt;

    public PasswordItem() {
    }

    public Integer getPassId() {
        return passId;
    }

    public void setPassId(Integer passId) {
        this.passId = passId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getVisitedAt() {
        return visitedAt;
    }

    public void setVisitedAt(String visitedAt) {
        this.visitedAt = visitedAt;
    }

    @NonNull
    @Override
    public String toString() {
        return "PasswordItem{" +
                "passId=" + passId +
                ", title='" + title + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", visitedAt='" + visitedAt + '\'' +
                '}';
    }

    @Override
    public int compareTo(PasswordItem passwordItem) {
        return passId.compareTo(passwordItem.getPassId());
    }

}
