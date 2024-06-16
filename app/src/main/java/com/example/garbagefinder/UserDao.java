    package com.example.garbagefinder;

    import androidx.lifecycle.LiveData;
    import androidx.room.Dao;
    import androidx.room.Delete;
    import androidx.room.Insert;
    import androidx.room.Query;
    import androidx.room.Update;

    import java.util.List;

    @Dao
    public interface
    UserDao {

        @Insert
        void insertUser(User user);

        @Update
        void update(User user);

        @Delete
        void delete(User user);

        @Query("DELETE FROM User")
        void deleteAllUsers();

        @Query("SELECT * FROM User")
        LiveData<List<User>> getAllUsers();

        @Query("SELECT * FROM User WHERE name = :username")
        User findUserByUsername(String username);
    }
