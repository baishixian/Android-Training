package com.bsx.jetpacksample.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.bsx.jetpacksample.model.RepositoryFactory;
import com.bsx.jetpacksample.model.UserRepository;
import com.bsx.jetpacksample.model.database.entity.User;

/**
 * UserProfileViewModel
 *
 * @author baishixian
 */
public class UserProfileViewModel extends ViewModel {
    private final UserRepository userRepository;
    private LiveData<User> user;

    public UserProfileViewModel() {
        this.userRepository = RepositoryFactory.getInstance().getUserRepository();
    }

    public LiveData<User> getUser(String userId) {
        if (this.user == null) {
            this.user = userRepository.getUser(userId);
        }

        return user;
    }
}
