package cn.loftown.wechat.app.code.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
    /**
     * 把时间转换成yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String formatToString(Date date){
        String str = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(str);
        return sdf.format(date);
    }
}
