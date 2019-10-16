package com.bsx.jetpacksample.model.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

/**
 * User
 *
 * @author baishixian
 */
@Entity
public class User {
    @PrimaryKey
    public String uid;

    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String lastName;

    public Date lastRefresh;
}
