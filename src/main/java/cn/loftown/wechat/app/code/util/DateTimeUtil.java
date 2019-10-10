package cn.loftown.wechat.app.code.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {
    static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
    static SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 把时间转换成yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String formatToString(Date date){
        return sdfDateTime.format(date);
    }

    /**
     * 把时间转换成yyyy-MM-dd
     * @param date
     * @return
     */
    public static String formatToDateString(Date date){
        return sdfDate.format(date);
    }

    /**
     * yyyy-MM-dd HH:mm:ss转日期
     * @param date
     * @return
     * @throws Exception
     */
    public static Date getDateToString(String date) throws Exception{
        Date strDate =sdfDateTime.parse(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(strDate);
        return calendar.getTime();
    }
}
