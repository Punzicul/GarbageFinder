package com.example.garbagefinder;

import android.content.Context;
import androidx.room.Room;

import com.example.garbagefinder.UserDatabase;

public class DatabaseClient {

    private static DatabaseClient instance;
    private UserDatabase db;

    private DatabaseClient(Context context) {
        // Build the database here
        db = Room.databaseBuilder(context.getApplicationContext(),
                UserDatabase.class, "users").build();
    }

    public static synchronized DatabaseClient getInstance(Context context) { // checks if a database is created
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public UserDatabase getDb() {
        return db;
    }

}