/**
 * @(#)${FILE_NAME}.java, 6/12/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.account.web.logic;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.data.UserGroup;
import com.jinyufeili.minas.account.service.UserGroupService;
import com.jinyufeili.minas.account.service.UserService;
import com.jinyufeili.minas.account.web.data.UserVO;
import com.jinyufeili.minas.account.web.wrapper.UserVOWrapper;
import com.jinyufeili.minas.crm.data.Resident;
import com.jinyufeili.minas.crm.service.ResidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author pw
 */
@Service
public class UserLogic {

    @Autowired
    private UserService userService;

    @Autowired
    private UserVOWrapper userVOWrapper;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private UserGroupService userGroupService;

    public UserVO getByAuthentication(Authentication authentication) {
        User user = userService.getByOpenId(authentication.getName());
        return userVOWrapper.wrap(user);
    }

    public List<UserVO> queryFreeUser() {
        return userVOWrapper.wrap(userService.queryFreeUser());
    }

    public UserVO get(int userId) {
        return userVOWrapper.wrap(userService.get(userId));
    }

    public UserVO update(UserVO vo) {
        int userId = vo.getId();

        User user = userService.get(userId);
        user.setId(userId);
        user.setName(vo.getName());
        userService.update(user);

        if (vo.getResident().getId() == 0) {
            Resident resident = new Resident();
            resident.setName(vo.getResident().getName());
            resident.setMobilePhone(vo.getResident().getMobilePhone());
            resident.setRoomId(vo.getRoom().getId());
            resident.setUserId(userId);
            residentService.create(resident);
        } else {
            Resident resident = residentService.get(vo.getResident().getId());
            resident.setName(vo.getResident().getName());
            resident.setMobilePhone(vo.getResident().getMobilePhone());
            residentService.update(resident);
        }

        userGroupService
                .updateUserGroup(userId, vo.getGroups().stream().map(UserGroup::getId).collect(Collectors.toSet()));
        return get(userId);
    }

    public List<UserVO> queryUser(int groupId) {
        if (groupId == 0) {
            return userVOWrapper.wrap(userService.queryNonGroupUser());
        } else {
            return userVOWrapper.wrap(userService.queryByGroupId(groupId));
        }
    }
}
