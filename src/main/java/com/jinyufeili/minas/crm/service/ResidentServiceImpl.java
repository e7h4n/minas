package com.jinyufeili.minas.crm.service;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.storage.UserStorage;
import com.jinyufeili.minas.crm.data.Resident;
import com.jinyufeili.minas.crm.data.Room;
import com.jinyufeili.minas.crm.storage.ResidentStorage;
import com.jinyufeili.minas.crm.storage.RoomStorage;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by pw on 6/11/16.
 */
@Service
public class ResidentServiceImpl implements ResidentService {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ResidentStorage residentStorage;

    @Autowired
    private UserStorage userStorage;

    @Autowired
    private RoomStorage roomStorage;

    @Autowired
    private WxMpService wechatService;

    @Override
    public List<Resident> queryByRoomId(int roomId) {
        return residentStorage.queryByRoomId(roomId);
    }

    @Override
    public List<Resident> queryByMobilePhone(String mobilePhone) {
        return residentStorage.queryByMobilePhone(mobilePhone);
    }

    @Override
    public List<Resident> queryByName(String name) {
        return residentStorage.queryByName(name);
    }

    @Override
    public List<Resident> queryByUserName(String userName) {
        List<User> users = userStorage.queryByName(userName);
        Set<Integer> userIds = users.stream().map(User::getId).collect(Collectors.toSet());
        return residentStorage.queryByUserIds(userIds).values().stream().collect(Collectors.toList());
    }

    @Override
    public Map<Integer, Resident> queryByUserIds(Set<Integer> userIds) {
        return residentStorage.queryByUserIds(userIds);
    }

    @Override
    public List<Resident> queryByRoom(int region, int building, int unit, int houseNumber) {
        return residentStorage.queryByRoom(region, building, unit, houseNumber);
    }

    @Override
    public boolean update(Resident resident) {
        boolean result = residentStorage.update(resident);
        if (result && resident.getUserId() > 0 && resident.getRoomId() > 0) {
            syncWechat(resident);
        }
        return result;
    }

    @Override
    public void syncWechatUserInfo(int residentId) throws WxErrorException {
        Resident resident;
        resident = get(residentId);
        Room room = roomStorage.getByIds(Collections.singleton(resident.getRoomId())).get(resident.getRoomId());
        User user = userStorage.get(resident.getUserId());

        wechatService.userUpdateGroup(user.getOpenId(), GROUP_ID);
        if (room.getRegion() == 1) {
            wechatService.userUpdateRemark(user.getOpenId(),
                    String.format("%d区 %d-%d %s", room.getRegion(), room.getBuilding(), room.getHouseNumber(),
                            resident.getName()));
        } else {
            wechatService.userUpdateRemark(user.getOpenId(),
                    String.format("%d区 %d-%d-%d %s", room.getRegion(), room.getBuilding(), room.getUnit(),
                            room.getHouseNumber(), resident.getName()));
        }
    }

    @Override
    public Resident get(int residentId) {
        return residentStorage.get(residentId);
    }

    @Override
    public int create(Resident resident) {
        int residentId = residentStorage.create(resident);
        syncWechat(residentId);
        return residentId;
    }

    @Override
    public Map<Integer, Resident> getByIds(Set<Integer> residentIds) {
        return residentStorage.getByIds(residentIds);
    }

    private void syncWechat(Resident resident) {
        syncWechat(resident.getId());
    }

    private void syncWechat(int residentId) {
        try {
            syncWechatUserInfo(residentId);
        } catch (WxErrorException e) {
            LOG.error("", e);
        }
    }
}
