package com.jinyufeili.minas.account.web.logic;

import com.jinyufeili.minas.account.service.UserService;
import com.jinyufeili.minas.account.service.VerifyCodeService;
import com.jinyufeili.minas.account.web.data.UserVO;
import com.jinyufeili.minas.account.web.wrapper.UserVOWrapper;
import com.jinyufeili.minas.crm.data.Resident;
import com.jinyufeili.minas.crm.data.Room;
import com.jinyufeili.minas.crm.service.ResidentService;
import com.jinyufeili.minas.crm.service.RoomService;
import com.jinyufeili.minas.web.exception.BadRequestException;
import com.jinyufeili.minas.web.exception.ConflictException;
import com.jinyufeili.minas.wechat.web.logic.WechatLogic;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.exceptions.ExceptionIncludingMockitoWarnings;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author pw
 */
@RunWith(MockitoJUnitRunner.class)
public class UserLogicImplTest {

    public static final String NEW_RESIDENT_NAME = "resident";

    public static final String NEW_RESIDENT_MOBILE_PHONE = "1231";

    private static final int NEW_RESIDENT_USER_ID = 4;

    public static final int UNBINDED_USER_ID = 999;

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Mock
    private RoomService roomService;

    @Mock
    private ResidentService residentService;

    @Mock
    private VerifyCodeService verifyCodeService;

    @Mock
    private UserVOWrapper userVOWrapper;

    @Mock
    private UserService userService;

    @Mock
    private WechatLogic wechatLogic;

    @InjectMocks
    private UserLogic userLogic = new UserLogicImpl();

    @Before
    public void setUp() throws Exception {
        Room room601 = new Room();
        room601.setId(1);
        room601.setRegion(2);
        room601.setBuilding(1);
        room601.setUnit(2);
        room601.setHouseNumber(601);
        Room room602 = new Room();
        room602.setId(2);
        room602.setRegion(2);
        room602.setBuilding(1);
        room602.setUnit(2);
        room602.setHouseNumber(602);

        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(room601);
        rooms.add(room602);
        Mockito.when(roomService.getByIds(Mockito.anySetOf(Integer.class))).thenAnswer(invocation -> {
            Set<Integer> ids = invocation.getArgumentAt(0, Set.class);
            return rooms.stream().filter(r -> ids.contains(r.getId()))
                    .collect(Collectors.toMap(Room::getId, this::clone));
        });

        Mockito.when(roomService.getByLocation(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenAnswer(invocation -> {
                    int region = invocation.getArgumentAt(0, Integer.class);
                    int building = invocation.getArgumentAt(1, Integer.class);
                    int unit = invocation.getArgumentAt(2, Integer.class);
                    int houseNumber = invocation.getArgumentAt(3, Integer.class);
                    Optional<Room> roomOptional = rooms.stream()
                            .filter(r -> r.getRegion() == region && r.getBuilding() == building &&
                                    r.getUnit() == unit && r.getHouseNumber() == houseNumber).findFirst();

                    if (!roomOptional.isPresent()) {
                        throw new EmptyResultDataAccessException(1);
                    }

                    return clone(roomOptional.get());
                });

        Resident papa = new Resident();
        papa.setId(1);
        papa.setName("papa");
        papa.setMobilePhone("1234");
        papa.setVerified(true);
        papa.setUserId(1);
        papa.setRoomId(1);

        Resident mama = new Resident();
        mama.setId(2);
        mama.setName("mama");
        mama.setMobilePhone("1233");
        mama.setVerified(false);
        mama.setUserId(2);
        mama.setRoomId(1);

        Resident yeye = new Resident();
        yeye.setId(3);
        yeye.setName("yeye");
        yeye.setMobilePhone("1232");
        yeye.setVerified(false);
        yeye.setUserId(0);
        yeye.setRoomId(1);

        ArrayList<Resident> residents = new ArrayList<>();
        residents.add(papa);
        residents.add(mama);
        residents.add(yeye);

        Mockito.when(residentService.queryByRoomId(Mockito.anyInt())).thenAnswer(invocation -> {
            int roomId = invocation.getArgumentAt(0, Integer.class);
            return residents.stream().filter(r -> r.getRoomId() == roomId).map(this::clone)
                    .collect(Collectors.toList());
        });
        Mockito.when(residentService.get(Mockito.anyInt())).thenAnswer(invocation -> {
            LOG.info("residents list size: {}", residents.size());
            int residentId = invocation.getArgumentAt(0, Integer.class);
            Optional<Resident> residentOptional = residents.stream().filter(r -> r.getId() == residentId).findFirst();
            if (!residentOptional.isPresent()) {
                throw new EmptyResultDataAccessException(1);
            }

            return clone(residentOptional.get());
        });

        Mockito.when(residentService.update(Mockito.any(Resident.class))).thenAnswer(invocation -> {
            Resident resident = invocation.getArgumentAt(0, Resident.class);
            Resident oldResident = residents.get(resident.getId() - 1);
            oldResident.setUserId(resident.getUserId());
            oldResident.setRoomId(resident.getRoomId());
            oldResident.setVerified(resident.isVerified());
            oldResident.setMobilePhone(resident.getMobilePhone());
            oldResident.setName(resident.getName());
            return true;
        });

        Mockito.when(residentService.create(Mockito.any(Resident.class))).thenAnswer(invocation -> {
            Resident resident = invocation.getArgumentAt(0, Resident.class);
            resident.setId(residents.get(residents.size() - 1).getId() + 1);
            residents.add(resident);
            return resident.getId();
        });

        Mockito.when(verifyCodeService.check(Mockito.anyString(), Mockito.eq("true"))).thenReturn(true);
        Mockito.when(verifyCodeService.check(Mockito.anyString(), Mockito.eq("false"))).thenReturn(false);
    }

    @Test(expected = BadRequestException.class)
    public void updateCurrentUser_InvalidVerifyCode() throws Exception {
        UserVO userVO = createNewResident();

        userLogic.updateCurrentUser(userVO, "false");

        Assert.fail("should not be here");
    }

    // 新建一个用户
    @Test
    public void updateCurrentUser() throws Exception {
        UserVO userVO = createNewResident();

        userLogic.updateCurrentUser(userVO, "true");

        Resident resident = residentService.get(4);
        Assert.assertEquals(resident.getName(), NEW_RESIDENT_NAME);
        Assert.assertEquals(resident.getMobilePhone(), NEW_RESIDENT_MOBILE_PHONE);
        Assert.assertEquals(resident.getUserId(), NEW_RESIDENT_USER_ID);
        Assert.assertEquals(resident.getRoomId(), 1);
    }

    // 绑定已有住户会失败
    @Test(expected = ConflictException.class)
    public void updateCurrentUser_bindExisted1() throws Exception {
        UserVO userVO = createConflictUser();

        userLogic.updateCurrentUser(userVO, "true");
    }

    // 同样的名字和内容, 换个房间就没问题
    @Test
    public void updateCurrentUser_bindExisted2() throws Exception {
        UserVO userVO = createConflictUser();
        userVO.getRoom().setHouseNumber(602);

        userLogic.updateCurrentUser(userVO, "true");
    }

    // 房间不存在应该报错
    @Test(expected = EmptyResultDataAccessException.class)
    public void updateCurrentUser_invalidRoom() throws Exception {
        UserVO userVO = createConflictUser();
        userVO.getRoom().setHouseNumber(604);

        userLogic.updateCurrentUser(userVO, "true");
    }

    // 自己保存自己应该没事, 也不会去校验验证码
    @Test
    public void updateCurrentUser_saveSelf() throws Exception {
        UserVO userVO = getExistedUserVO();
        userLogic.updateCurrentUser(userVO, "false");
        Assert.assertEquals(residentService.get(1).isVerified(), true);
    }

    @Test
    public void updateCurrentUser_updatePhone() throws Exception {
        UserVO userVO = getExistedUserVO();

        userVO.getResident().setMobilePhone("1235");

        try {
            userLogic.updateCurrentUser(userVO, "false");
            Assert.fail("should not be here");
        } catch (BadRequestException e) {
        }

        userLogic.updateCurrentUser(userVO, "true");
        Assert.assertEquals(residentService.get(1).getMobilePhone(), "1235");
        Assert.assertEquals(residentService.get(1).isVerified(), true);
    }

    @Test
    public void updateCurrentUser_updateName() throws Exception {
        UserVO userVO = getExistedUserVO();

        // 改名字会影响 verified 状态
        userVO.getResident().setName("papa2");
        userLogic.updateCurrentUser(userVO, "false");
        Assert.assertEquals(residentService.get(1).getName(), "papa2");
        Assert.assertEquals(residentService.get(1).isVerified(), false);
    }

    @Test
    public void updateCurrentUser_updateRoom() throws Exception {
        UserVO userVO = getExistedUserVO();
        userVO.getRoom().setHouseNumber(602);

        // 改房间会影响 verified 状态
        userLogic.updateCurrentUser(userVO, "false");
        Assert.assertEquals(residentService.get(1).getRoomId(), 2);
        Assert.assertEquals(residentService.queryByRoomId(1).size(), 2);
        Assert.assertEquals(residentService.get(1).isVerified(), false);
    }

    @Test
    public void updateCurrentUser_updateRoomAndName() throws Exception {
        UserVO userVO = getExistedUserVO();
        userVO.getRoom().setHouseNumber(602);
        userVO.getResident().setName("papa2");

        // 改房间会影响 verified 状态
        userLogic.updateCurrentUser(userVO, "false");
        Assert.assertEquals(residentService.get(1).getRoomId(), 2);
        Assert.assertEquals(residentService.get(1).getName(), "papa2");
        Assert.assertEquals(residentService.queryByRoomId(1).size(), 2);
        Assert.assertEquals(residentService.get(1).isVerified(), false);
    }

    @Test(expected = BadRequestException.class)
    public void updateCurrentUser_updateRoomAndNameAndPhone() throws Exception {
        UserVO userVO = getExistedUserVO();
        userVO.getRoom().setHouseNumber(602);
        userVO.getResident().setName("papa2");
        userVO.getResident().setMobilePhone("1111");

        // 改房间会影响 verified 状态
        userLogic.updateCurrentUser(userVO, "false");
        Assert.assertEquals(residentService.get(1).getRoomId(), 2);
        Assert.assertEquals(residentService.get(1).getName(), "papa2");
        Assert.assertEquals(residentService.queryByRoomId(1).size(), 2);
        Assert.assertEquals(residentService.get(1).isVerified(), false);
    }

    @Test(expected = BadRequestException.class)
    public void updateCurrentUser_bindEmptyWithInvalidSms() throws Exception {
        UserVO userVO = createExistedUnbindedUserVO();
        userLogic.updateCurrentUser(userVO, "false");
        Assert.fail("should not be here");
    }

    @Test
    public void updateCurrentUser_bindEmpty() throws Exception {
        UserVO userVO = createExistedUnbindedUserVO();
        userLogic.updateCurrentUser(userVO, "true");

        Assert.assertEquals(residentService.get(3).getUserId(), UNBINDED_USER_ID);
        Assert.assertEquals(residentService.get(3).isVerified(), false);
    }

    @Test
    public void updateCurrentUser_bindEmptyAndUpdate() throws Exception {
        UserVO userVO = createExistedUnbindedUserVO();

        userVO.getResident().setMobilePhone("7777");
        userLogic.updateCurrentUser(userVO, "true");
        Assert.assertEquals(residentService.get(3).getUserId(), UNBINDED_USER_ID);
        Assert.assertEquals(residentService.get(3).getMobilePhone(), "7777");
        Assert.assertEquals(residentService.get(3).isVerified(), false);
    }

    private UserVO createExistedUnbindedUserVO() {
        UserVO userVO = new UserVO();
        userVO.setId(UNBINDED_USER_ID);
        userVO.setResident(residentService.get(3));
        userVO.setRoom(roomService.getByLocation(2, 1, 2, 601));
        userVO.getRoom().setId(0);
        userVO.getResident().setId(0);
        return userVO;
    }

    private UserVO getExistedUserVO() {
        Resident resident = residentService.get(1);

        Room room = roomService.getByIds(Collections.singleton(1)).get(1);
        UserVO userVO = new UserVO();
        userVO.setId(resident.getId());
        userVO.setName("pw");
        userVO.setRoom(room);
        userVO.setResident(resident);
        return userVO;
    }

    private Resident clone(Resident oldResident) {
        Resident newResident = new Resident();
        newResident.setId(oldResident.getId());
        newResident.setName(oldResident.getName());
        newResident.setUserId(oldResident.getUserId());
        newResident.setRoomId(oldResident.getRoomId());
        newResident.setMobilePhone(oldResident.getMobilePhone());
        newResident.setVerified(oldResident.isVerified());
        return newResident;
    }

    private Room clone(Room oldRoom) {
        Room newRoom = new Room();
        newRoom.setId(oldRoom.getId());
        newRoom.setRegion(oldRoom.getRegion());
        newRoom.setBuilding(oldRoom.getBuilding());
        newRoom.setUnit(oldRoom.getUnit());
        newRoom.setHouseNumber(oldRoom.getHouseNumber());
        return newRoom;
    }

    private UserVO createConflictUser() {
        UserVO userVO = createNewResident();
        userVO.getResident().setName("papa");
        userVO.getResident().setMobilePhone("1122");
        return userVO;
    }

    private UserVO createNewResident() {
        UserVO userVO = new UserVO();
        userVO.setId(NEW_RESIDENT_USER_ID);
        userVO.setName("pw");
        Resident resident = new Resident();
        resident.setName(NEW_RESIDENT_NAME);
        resident.setMobilePhone(NEW_RESIDENT_MOBILE_PHONE);
        userVO.setResident(resident);
        Room room = new Room();
        room.setRegion(2);
        room.setBuilding(1);
        room.setUnit(2);
        room.setHouseNumber(601);
        userVO.setRoom(room);
        return userVO;
    }
}