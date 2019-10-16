package com.bsx.jetpacksample.model.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.bsx.jetpacksample.model.database.converter.DateConverter;
import com.bsx.jetpacksample.model.database.dao.UserDao;
import com.bsx.jetpacksample.model.database.entity.User;

/**
 * AppDatabase
 *
 * @author baishixian
 */
@Database(entities = {User.class}, version = 1)
@TypeConverters(DateConverter.class)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "app-database";
    private static AppDatabase sInstance;

    public abstract UserDao userDao();

    public static AppDatabase getInstance(final Context appContext) {
        if (sInstance == null) {

            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME).build();
                }
            }
        }

        return sInstance;
    }

}