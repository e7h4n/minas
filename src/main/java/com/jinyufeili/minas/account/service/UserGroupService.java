/**
 * @(#)${FILE_NAME}.java, 6/30/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.account.service;

import com.jinyufeili.minas.account.data.UserGroup;

import java.util.List;
import java.util.Set;

/**
 * @author pw
 */
public interface UserGroupService {

    List<UserGroup> getGroupsOfUser(int userId);

    List<UserGroup> getAll();

    boolean updateUserGroup(int userId, Set<Integer> groupIds);

    UserGroup get(int groupId);
}
