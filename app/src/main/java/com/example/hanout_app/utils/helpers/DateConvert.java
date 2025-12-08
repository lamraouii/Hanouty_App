package com.example.hanout_app.utils.helpers;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConvert{

    @TypeConverter
    public static Long tolong(Date date) {
        if (date != null) {
        return date.getTime();
        }else {
            return null;
        }
    }

    @TypeConverter
    public static Date todate(Long date) {
        if (date != null) {
        return new Date(date);
        }else {
            return null;
        }
    }
}
