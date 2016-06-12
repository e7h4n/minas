/**
 * @(#)${FILE_NAME}.java, 6/12/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.account.web.wrapper;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.data.UserGroup;
import com.jinyufeili.minas.account.service.UserGroupService;
import com.jinyufeili.minas.account.web.data.UserVO;
import com.jinyufeili.minas.crm.data.Resident;
import com.jinyufeili.minas.crm.data.Room;
import com.jinyufeili.minas.crm.service.ResidentService;
import com.jinyufeili.minas.crm.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author pw
 */
@Service
public class UserVOWrapper {

    @Autowired
    private ResidentService residentService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserGroupService userGroupService;

    public UserVO wrap(User user) {
        return wrap(Collections.singletonList(user)).get(0);
    }

    public List<UserVO> wrap(List<User> users) {
        Set<Integer> userIds = users.stream().map(User::getId).collect(Collectors.toSet());
        Map<Integer, Resident> userResidentMap = residentService.queryByUserIds(userIds);
        Set<Integer> roomIds = userResidentMap.values().stream().filter(r -> r != null).map(Resident::getRoomId)
                .collect(Collectors.toSet());
        Map<Integer, Room> roomMap = roomService.getByIds(roomIds);
        return users.stream().map(u -> {
            Resident resident = userResidentMap.get(u.getId());
            Room room = resident != null ? roomMap.get(resident.getRoomId()) : null;
            List<UserGroup> userGroups = userGroupService.getGroupsOfUser(u.getId());

            return wrap(u, resident, room, userGroups);
        }).collect(Collectors.toList());
    }

    private UserVO wrap(User u, Resident resident, Room room, List<UserGroup> userGroups) {
        UserVO vo = new UserVO();

        vo.setId(u.getId());
        vo.setName(u.getName());
        vo.setResident(resident);
        vo.setRoom(room);
        vo.setGroups(userGroups);

        return vo;
    }
}
