package com.jinyufeili.minas.account.service;

import com.jinyufeili.minas.account.data.UserGroup;
import com.jinyufeili.minas.account.storage.UserGroupStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public List<UserGroup> getAll() {
        return userGroupStorage.getAllGroups();
    }

    public boolean updateUserGroup(int userId, Set<Integer> groupIds) {
        Set<Integer> oldGroupIds = getGroupsOfUser(userId).stream().map(UserGroup::getId).collect(Collectors.toSet());

        Set<Integer> removeGroupIds = new HashSet<>(oldGroupIds);
        removeGroupIds.removeAll(groupIds);

        Set<Integer> newGroupIds = new HashSet<>(groupIds);
        newGroupIds.removeAll(oldGroupIds);

        userGroupStorage.unlinkGroupByUserId(userId, removeGroupIds);
        userGroupStorage.linkGroupByUserId(userId, newGroupIds);
        return true;
    }

    public UserGroup get(int groupId) {
        return userGroupStorage.get(groupId);
    }
}
