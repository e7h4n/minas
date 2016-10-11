package com.jinyufeili.minas.wechat.message.interceptor;

import com.jinyufeili.minas.crm.data.Resident;
import com.jinyufeili.minas.crm.data.Room;
import com.jinyufeili.minas.crm.service.ResidentService;
import com.jinyufeili.minas.crm.service.RoomService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by pw on 6/10/16.
 */
@Service
public class ResidentSearchMessageInterceptor extends AbstractRoleInterceptor {

    public static final String ROOM_LIST = "roomList";

    public static final String ROOM_RESIDENT_MAP = "roomResidentMap";

    private static final String PATTERN_HOUSE_ABBR = "\\d{5,6}";

    private static final String PATTERN_MOBILE_PHONE = "\\d{13}";

    private static final String[] USER_GROUPS = {"筹备组", "动员组"};

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RoomService roomService;

    @Autowired
    private ResidentService residentService;

    @Override
    public Set<String> getUserGroups() {
        return Arrays.stream(USER_GROUPS).collect(Collectors.toSet());
    }

    @Override
    protected boolean innerIntercept(WxMpXmlMessage message, Map<String, Object> context, WxMpService wechatService,
                                     WxSessionManager sessionManager) throws WxErrorException {
        String content = message.getContent();
        List<Room> roomList = Collections.emptyList();
        Map<Integer, List<Resident>> roomResidentMap = Collections.emptyMap();
        if (isHouseAbbr(content)) {
            Room room = null;
            try {
                room = roomService.getByAbbr(content);
            } catch (EmptyResultDataAccessException e) {
                LOG.info("miss house abbr, fromUser={}, abbr={}", message.getFromUser(), content);
            }

            if (room != null) {
                roomResidentMap = Collections.singletonMap(room.getId(), residentService.queryByRoomId(room.getId()));
                roomList = Collections.singletonList(room);
            }

            context.put(ROOM_LIST, roomList);
            context.put(ROOM_RESIDENT_MAP, roomResidentMap);
            return true;
        }

        List<Resident> residents = queryResidentByName(content);

        if (!CollectionUtils.isEmpty(residents)) {
            roomResidentMap = getRoomResidentMap(residents);
            Set<Integer> roomIds = roomResidentMap.keySet();
            roomList = roomService.getByIds(roomIds).values().stream().collect(Collectors.toList());
        }

        if (CollectionUtils.isEmpty(roomList)) {
            return false;
        }

        context.put(ROOM_LIST, roomList);
        context.put(ROOM_RESIDENT_MAP, roomResidentMap);

        return true;
    }

    private Map<Integer, List<Resident>> getRoomResidentMap(List<Resident> residents) {
        Map<Integer, List<Resident>> roomResidentMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(residents)) {
            for (Resident resident : residents) {
                if (!roomResidentMap.containsKey(resident.getRoomId())) {
                    roomResidentMap.put(resident.getRoomId(), new ArrayList<>());
                }
                roomResidentMap.get(resident.getRoomId()).add(resident);
            }
        }
        return roomResidentMap;
    }

    private List<Resident> queryResidentByName(String content) {
        List<Resident> residents;

        if (isMobilePhone(content)) {
            residents = residentService.queryByMobilePhone(content);
        } else {
            residents = residentService.queryByName(content);

            if (CollectionUtils.isEmpty(residents)) {
                residents = residentService.queryByUserName(content);
            }
        }
        return residents;
    }

    private boolean isMobilePhone(String content) {
        Pattern pattern = Pattern.compile(PATTERN_MOBILE_PHONE);
        return pattern.matcher(content).find();
    }

    private boolean isHouseAbbr(String content) {
        Pattern pattern = Pattern.compile(PATTERN_HOUSE_ABBR);
        return pattern.matcher(content).find();
    }
}
