package com.karatek.tgbot.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }
}
