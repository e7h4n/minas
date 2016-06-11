package com.jinyufeili.minas.account.service;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.storage.UserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Created by pw on 6/9/16.
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserStorage userStorage;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getByOpenId(username);

        org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(
                user.getOpenId(), "", Collections.emptyList());

        return userDetails;
    }

    public User get(int id) {
        return userStorage.get(id);
    }

    public int create(User user) {
        return userStorage.create(user);
    }

    public User getByOpenId(String openId) {
        return userStorage.getByOpenId(openId);
    }

    private List<User> queryByName(String name) {
        return userStorage.queryByName(name);
    }
}
