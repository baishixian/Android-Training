package com.bsx.jetpacksample.model.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.bsx.jetpacksample.model.database.entity.User;

import java.util.Date;
import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

/**
 * UserDao
 *
 * @author baishixian
 */
@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    List<User> loadAllByIds(String[] userIds);

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    LiveData<User> findByName(String first, String last);

    @Insert(onConflict = REPLACE)
    void save(User user);

    @Insert
    void saveAll(User... users);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM user WHERE uid = :userId")
    LiveData<User> load(String userId);

    @Query("SELECT COUNT(*) FROM user WHERE uid = :userId AND lastRefresh > :lastRefreshMax")
    int hasUser(String userId, Date lastRefreshMax);
}
