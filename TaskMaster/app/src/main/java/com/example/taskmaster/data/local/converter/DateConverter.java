package com.example.taskmaster.data.local.converter;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Type converter for Room database to handle Date objects.
 * Converts between Date objects and Long timestamps.
 */
public class DateConverter {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}