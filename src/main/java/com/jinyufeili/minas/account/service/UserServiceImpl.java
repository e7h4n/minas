package com.jinyufeili.minas.account.service;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.storage.UserGroupStorage;
import com.jinyufeili.minas.account.storage.UserStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
public class UserServiceImpl implements UserDetailsService, UserService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private UserGroupStorage userGroupStorage;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        try {
            user = getByOpenId(username);
        } catch (EmptyResultDataAccessException e) {
            throw new UsernameNotFoundException("user " + username + " not found");
        }

        org.springframework.security.core.userdetails.User userDetails =
                new org.springframework.security.core.userdetails.User(user.getOpenId(), user.getOpenId(),
                        getAuthorities(user.getId()));

        return userDetails;
    }

    @Override
    public User get(int id) {
        return userStorage.get(id);
    }

    @Override
    public int create(User user) {
        return userStorage.create(user);
    }

    @Override
    public User getByOpenId(String openId) {
        return userStorage.getByOpenId(openId);
    }

    @Override
    public List<User> queryFreeUser() {
        return userStorage.queryFreeUser();
    }

    @Override
    public boolean update(User user) {
        return userStorage.update(user);
    }

    @Override
    public List<User> queryByGroupId(int groupId) {
        return userStorage.queryByGroupId(groupId);
    }

    @Override
    public List<User> queryNonGroupUser() {
        return userStorage.queryNonGroupUser();
    }

    @Override
    public List<User> queryUnbinded() {
        return userStorage.queryUnbinded();
    }

    @Override
    public List<Integer> getUserIds(int cursor, int limit) {
        return userStorage.getUserIds(cursor, limit);
    }

    @Override
    public boolean remove(int id) {
        return userStorage.remove(id);
    }

    private Collection<GrantedAuthority> getAuthorities(int userId) {
        return userGroupStorage.getGroupsOfUser(userId).stream()
                .map(g -> new SimpleGrantedAuthority("ROLE_" + g.getName())).collect(Collectors.toSet());
    }

    @Override
    public List<User> queryByName(String name) {
        return userStorage.queryByName(name);
    }
}
