package com.bsx.jetpacksample.model;

import androidx.lifecycle.LiveData;

import com.bsx.jetpacksample.model.database.dao.UserDao;
import com.bsx.jetpacksample.model.database.entity.User;
import com.bsx.jetpacksample.model.remote.Webservice;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executor;

import retrofit2.Response;

/**
 * UserRepository
 *
 * @author baishixian
 */
public class UserRepository {
    private final Webservice webservice;
    private final Executor executor;
    private final UserDao userDao;

    public UserRepository(Webservice webservice, Executor executor, UserDao userDao) {
        this.webservice = webservice;
        this.executor = executor;
        this.userDao = userDao;
    }

    public LiveData<User> getUser(String userId) {
        refreshUser(userId);

        // Returns a LiveData object directly from the database.
        return userDao.load(userId);
    }

    private void refreshUser(final String userId) {
        // Runs in a background thread.
        executor.execute(new Runnable() {

            @Override
            public void run() {
                // Check if user data was fetched recently.
                boolean userExists = userDao.hasUser(userId, new Date()) >= 1;

                if (userExists) {
                    // Refreshes the data.
                    try {
                        Response<User> response = webservice.getUser(userId).execute();

                        // Check for errors here.
                        if (response.isSuccessful()) {

                            // Updates the database. The LiveData object automatically
                            // refreshes, so we don't need to do anything else here.
                            userDao.save(response.body());
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
