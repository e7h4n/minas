package com.jinyufeili.minas.account.service;

import com.jinyufeili.minas.account.data.UserGroup;
import com.jinyufeili.minas.account.storage.UserGroupStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by pw on 6/11/16.
 */
@Service
public class UserGroupService {

    @Autowired
    private UserGroupStorage userGroupStorage;

    public List<UserGroup> getGroupsOfUser(int userId) {
        return userGroupStorage.getGroupsOfUser(userId);
    }
}
