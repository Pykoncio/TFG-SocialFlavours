package com.iesvegademijas.socialflavours.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    private static final DateFormat DEFAULT_DATE_FORMATTER;

    static {
        DEFAULT_DATE_FORMATTER = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        DEFAULT_DATE_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static String formatDate(Date date) {
        return DEFAULT_DATE_FORMATTER.format(date);
    }

    public static Date parseDate(String dateString) throws ParseException {
        return DEFAULT_DATE_FORMATTER.parse(dateString);
    }

}
