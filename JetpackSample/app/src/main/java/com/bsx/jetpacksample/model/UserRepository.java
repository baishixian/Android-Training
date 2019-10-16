package com.bsx.jetpacksample.model;

import com.bsx.jetpacksample.model.database.dao.UserDao;
import com.bsx.jetpacksample.model.remote.Webservice;

import java.util.concurrent.Executor;


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
}
