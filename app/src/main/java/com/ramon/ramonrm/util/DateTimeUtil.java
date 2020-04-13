package com.ramon.ramonrm.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

    public static Date Now() {
        return new Date(System.currentTimeMillis());
    }

    public static String DateTimeToString(long time, String format) {
        Date date = new Date(time);
        String strs = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            //进行格式化
            strs = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strs;
    }

    public static long StringToDateTime(String strTime,String formate){
        SimpleDateFormat sdf = new SimpleDateFormat(formate);
        try{
            return sdf.parse(strTime).getTime();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}
