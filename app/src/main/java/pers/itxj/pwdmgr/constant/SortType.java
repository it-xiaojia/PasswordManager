package pers.itxj.pwdmgr.constant;

import pers.itxj.pwdmgr.R;

/**
 * @author IT小佳
 * 创建日期： 2025/4/2
 * 描述： 排序类型
 */
public enum SortType {
    ASC(R.id.rb_asc),
    DESC(R.id.rb_desc),
    CREATED_AT(R.id.rb_created_at),
    UPDATED_AT(R.id.rb_updated_at),
    VISITED_AT(R.id.rb_visited_at),
    TITLE(R.id.rb_title),
    USERNAME(R.id.rb_username);

    private final int radioButtonId;

    SortType(int radioButtonId) {
        this.radioButtonId = radioButtonId;
    }

    public int getRadioButtonId() {
        return radioButtonId;
    }
}
