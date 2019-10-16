package com.bsx.jetpacksample.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.bsx.jetpacksample.model.database.entity.User;

/**
 * UserProfileViewModel
 *
 * @author baishixian
 */
public class UserProfileViewModel extends ViewModel {
    private LiveData<User> user;

    public UserProfileViewModel() {
    }
}
