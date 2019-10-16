package com.bsx.jetpacksample.model;

import android.app.Application;

import com.bsx.jetpacksample.model.database.AppDatabase;
import com.bsx.jetpacksample.model.remote.Webservice;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Retrofit;

/**
 * RepositoryFactory
 *
 * @author baishixian
 */
public class RepositoryFactory {
    private static final String BASE_URL = "https://xxx.com/user";

    private UserRepository userRepository;
    private AppDatabase appDatabase;
    private Webservice webservice;
    private Executor executor;

    public void init(Application application) {
        appDatabase = AppDatabase.getInstance(application);
        executor = Executors.newSingleThreadExecutor();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();
        webservice = retrofit.create(Webservice.class);
    }

    private static class Holder {
        private static final RepositoryFactory INSTANCE = new RepositoryFactory();
    }

    private RepositoryFactory() {
    }

    public static RepositoryFactory getInstance() {
        return Holder.INSTANCE;
    }

    public synchronized UserRepository getUserRepository() {
        if (userRepository == null) {
            userRepository = new UserRepository(webservice, executor, appDatabase.userDao());
        }

        return userRepository;
    }
}
