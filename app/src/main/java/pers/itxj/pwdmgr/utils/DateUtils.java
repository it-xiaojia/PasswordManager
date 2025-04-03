package pers.itxj.pwdmgr.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author IT小佳
 * 创建日期： 2025/4/3
 * 描述： 日期工具类
 */
public final class DateUtils {
    private DateUtils() {
    }

    /**
     * 获取当前日期，并格式化输出
     *
     * @param format 格式化输出参数，例如：yyyy-MM-dd HH:mm:ss
     * @return 返回格式化输出后的日期字符串，例如：2025-04-03 10:16:20
     */
    public static String getCurrentDate(String format) {
        // 获取当前日期
        Calendar calendar = Calendar.getInstance();
        // 定义日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        // 格式化输出
        return dateFormat.format(calendar.getTime());
    }
}
