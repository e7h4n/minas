package com.jinyufeili.minas.account.service;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.storage.UserGroupStorage;
import com.jinyufeili.minas.account.storage.UserStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pw on 6/9/16.
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private UserGroupStorage userGroupStorage;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getByOpenId(username);

        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(user.getOpenId(), "",
                        getAuthorities(user.getId()));

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

    public List<User> queryFreeUser() {
        return userStorage.queryFreeUser();
    }

    public boolean update(User user) {
        return userStorage.update(user);
    }

    public List<User> queryByGroupId(int groupId) {
        return userStorage.queryByGroupId(groupId);
    }

    public List<User> queryNonGroupUser() {
        return userStorage.queryNonGroupUser();
    }

    private Collection<GrantedAuthority> getAuthorities(int userId) {
        return userGroupStorage.getGroupsOfUser(userId).stream().map(g -> new SimpleGrantedAuthority("ROLE_" + g.getName()))
                .collect(Collectors.toSet());
    }

    private List<User> queryByName(String name) {
        return userStorage.queryByName(name);
    }
}
