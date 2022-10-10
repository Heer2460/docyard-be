package com.infotech.docyard.dochandling.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static LocalDateTime addMinutesToLocalDateTime(LocalDateTime dateTime, long mins) {
        return dateTime.plusMinutes(mins);
    }

    public static String convertDateToUFDateFormat(ZonedDateTime d) {
        String str = "";
        if (d != null) {
            ZonedDateTime start = d;
            ZonedDateTime end = ZonedDateTime.now();
            DateTimeFormatter sameYrDateFormat = DateTimeFormatter.ofPattern(AppConstants.DateTimeFormat.DATE_PATTERN_SAME_YEAR),
                    preYrDateFormat = DateTimeFormatter.ofPattern(AppConstants.DateTimeFormat.DATE_PATTERN_PREVIOUS_YEAR),
                    stf = DateTimeFormatter.ofPattern(AppConstants.DateTimeFormat.TIME_PATTERN);
            long diff = end.toInstant().toEpochMilli() -
                    start.toInstant().toEpochMilli();

            if ((diff / 1000) <= 60) {
                str = "few seconds ago";
            } else if ((diff / 60000) <= 60) {
                str = (diff / 60000) + " minutes ago";
            } else if ((diff / 3600000) <= 24) {
                str = (diff / 3600000) + " hours ago";
            } else if ((diff / 3600000) > 24 && (diff / 3600000) < 48) {
                str = "Yesterday at " + stf.format(d).toLowerCase();
            } else if (start.getYear() != end.getYear()) {
                str = preYrDateFormat.format(d) + " at " + stf.format(d).toLowerCase();
            } else if ((diff / 31536000000L) <= 12) {
                str = sameYrDateFormat.format(d) + " at " + stf.format(d).toLowerCase();
            }
        }
        return str;
    }

    public static String convertDateTimeLocalFormat(LocalDateTime d) {
        String str = "";
        if (d != null) {
            LocalDateTime end = LocalDateTime.now();
            DateTimeFormatter sameYrDateFormat = DateTimeFormatter.ofPattern(AppConstants.DateTimeFormat.DATE_PATTERN_SAME_YEAR),
                    stf = DateTimeFormatter.ofPattern(AppConstants.DateTimeFormat.TIME_PATTERN);
            str = sameYrDateFormat.format(d) + " at " + stf.format(d).toLowerCase();
        }
        return str;
    }

    public static String getFormattedDateTimeFromGivenFormat(LocalDateTime d, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        return formatter.format(d);
    }

    public static String getFormattedDateFromGivenFormat(LocalDate d, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        return formatter.format(d);
    }

    public static LocalDate getLocalDateFromString(String formattedDate, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        return LocalDate.parse(formattedDate, formatter);
    }

}
