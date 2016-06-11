package com.jinyufeili.minas.crm.service;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.storage.UserStorage;
import com.jinyufeili.minas.crm.data.Resident;
import com.jinyufeili.minas.crm.storage.ResidentStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by pw on 6/11/16.
 */
@Service
public class ResidentService {

    @Autowired
    private ResidentStorage residentStorage;

    @Autowired
    private UserStorage userStorage;

    public List<Resident> queryByRoomId(int roomId) {
        return residentStorage.queryByRoomId(roomId);
    }

    public List<Resident> queryByMobilePhone(String mobilePhone) {
        return residentStorage.queryByMobilePhone(mobilePhone);
    }

    public List<Resident> queryByName(String name) {
        return residentStorage.queryByName(name);
    }

    public List<Resident> queryByUserName(String userName) {
        List<User> users = userStorage.queryByName(userName);
        Set<Integer> userIds = users.stream().map(User::getId).collect(Collectors.toSet());
        return residentStorage.queryByUserIds(userIds);
    }
}
