package com.example.garbagefinder;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {User.class}, version = 2)
@TypeConverters({LocationTypeConverter.class})
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
