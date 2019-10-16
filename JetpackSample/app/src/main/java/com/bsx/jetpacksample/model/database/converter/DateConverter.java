package com.bsx.jetpacksample.model.database.converter;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * DateConverter
 *
 * @author baishixian
 */
public class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
