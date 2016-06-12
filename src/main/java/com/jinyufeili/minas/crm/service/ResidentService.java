package com.jinyufeili.minas.crm.service;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.storage.UserStorage;
import com.jinyufeili.minas.crm.data.Resident;
import com.jinyufeili.minas.crm.storage.ResidentStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
        return residentStorage.queryByUserIds(userIds).values().stream().collect(Collectors.toList());
    }

    public Map<Integer, Resident> queryByUserIds(Set<Integer> userIds) {
        return residentStorage.queryByUserIds(userIds);
    }

    public List<Resident> queryByRoom(int region, int building, int unit, int houseNumber) {
        return residentStorage.queryByRoom(region, building, unit, houseNumber);
    }

    public boolean update(Resident resident) {
        return residentStorage.update(resident);
    }

    public Resident get(int residentId) {
        return residentStorage.get(residentId);
    }

    public boolean create(Resident resident) {
        return residentStorage.create(resident);
    }
}
