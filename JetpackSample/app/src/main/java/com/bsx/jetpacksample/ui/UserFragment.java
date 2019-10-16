package com.bsx.jetpacksample.ui;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsx.jetpacksample.R;
import com.bsx.jetpacksample.model.database.entity.User;
import com.bsx.jetpacksample.viewmodel.UserProfileViewModel;

/**
 * UserFragment
 *
 * @author baishixian
 */
public class UserFragment extends Fragment {
    // FOR DATA
    public static final String UID_KEY = "uid";

    private UserProfileViewModel mViewModel;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String userLogin = getArguments().getString(UID_KEY);
        mViewModel = ViewModelProviders.of(this).get(UserProfileViewModel.class);

        // Use the ViewModel
        mViewModel.getUser(userLogin).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null){
                    String userName = user.firstName;
                    // update ui...
                }
            }
        });
    }

}

