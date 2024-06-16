package com.example.garbagefinder;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "profilepic")
    public String uri;

    @ColumnInfo(name = "locations")
    @TypeConverters(LocationTypeConverter.class)
    public List<String> locations;

    public void setName(String name) {
        this.name = name;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public User(String name, String password, String uri) {
        this.name = name;
        this.password = password;
        this.uri = uri;
        this.locations = new ArrayList<>();
    }

    public void addLocation(String location) {
        if (locations == null) {
            locations = new ArrayList<>();
        }
        locations.add(location);
    }
}
