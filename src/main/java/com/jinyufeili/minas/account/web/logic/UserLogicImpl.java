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
import com.jinyufeili.minas.account.service.VerifyCodeService;
import com.jinyufeili.minas.account.web.data.UserVO;
import com.jinyufeili.minas.account.web.wrapper.UserVOWrapper;
import com.jinyufeili.minas.crm.data.Resident;
import com.jinyufeili.minas.crm.data.Room;
import com.jinyufeili.minas.crm.service.ResidentService;
import com.jinyufeili.minas.crm.service.RoomService;
import com.jinyufeili.minas.web.exception.BadRequestException;
import com.jinyufeili.minas.web.exception.ConflictException;
import com.jinyufeili.minas.web.exception.UnauthorizedException;
import com.jinyufeili.minas.wechat.web.logic.WechatLogic;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.WxMpTemplateMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author pw
 */
@Service
public class UserLogicImpl implements UserLogic {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private UserVOWrapper userVOWrapper;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private WxMpService wechatService;

    @Autowired
    private VerifyCodeService verifyCodeService;

    @Autowired
    private WechatLogic wechatLogic;

    @Override
    public UserVO getByAuthentication(Authentication authentication) {
        User user;
        try {
            user = userService.getByOpenId(authentication.getName());
        } catch (EmptyResultDataAccessException e) {
            throw new UnauthorizedException();
        }
        return userVOWrapper.wrap(user);
    }

    @Override
    public List<UserVO> queryFreeUser() {
        return userVOWrapper.wrap(userService.queryFreeUser());
    }

    @Override
    public UserVO get(int userId) {
        return userVOWrapper.wrap(userService.get(userId));
    }

    @Override
    public UserVO update(UserVO vo) {
        int userId = vo.getId();

        User user = userService.get(userId);
        user.setId(userId);
        user.setName(vo.getName());
        userService.update(user);

        boolean residentUserIdChanged = false;
        if (vo.getResident() != null && vo.getResident().getId() == 0 && vo.getRoom() != null &&
                vo.getRoom().getId() > 0) {
            residentUserIdChanged = true;
            Resident resident = new Resident();
            resident.setName(vo.getResident().getName());
            resident.setMobilePhone(vo.getResident().getMobilePhone());
            resident.setRoomId(vo.getRoom().getId());
            resident.setUserId(userId);
            resident.setVerified(true);
            residentService.create(resident);
        } else if (vo.getResident() != null && vo.getResident().getId() > 0) {
            Resident resident = residentService.get(vo.getResident().getId());
            residentUserIdChanged = resident.getUserId() == 0;
            resident.setName(vo.getResident().getName());
            resident.setMobilePhone(vo.getResident().getMobilePhone());
            resident.setUserId(userId);
            residentService.update(resident);
        }

        userGroupService
                .updateUserGroup(userId, vo.getGroups().stream().map(UserGroup::getId).collect(Collectors.toSet()));

        if (residentUserIdChanged) {
            try {
                sendBindResidentNotification(userId);
            } catch (WxErrorException e) {
                LOG.error("", e);
            }
        }

        return get(userId);
    }

    @Override
    public List<UserVO> queryUnbinded() {
        return fillRoom(userVOWrapper.wrap(userService.queryUnbinded()));
    }

    @Override
    public List<UserVO> queryUser(int groupId) {
        if (groupId == 0) {
            return userVOWrapper.wrap(userService.queryNonGroupUser());
        } else {
            return userVOWrapper.wrap(userService.queryByGroupId(groupId));
        }
    }

    @Override
    public UserVO updateCurrentUser(UserVO vo, String verifyCode) {
        if (vo.getRoom() == null || vo.getResident() == null) {
            throw new BadRequestException("找不到 room 或 resident 信息");
        }

        int userId = vo.getId();
        boolean phoneValid = verifyCodeService.check(vo.getResident().getMobilePhone(), verifyCode);

        int roomId = 0;
        if (vo.getRoom().getId() != 0) {
            Room room = roomService.getByIds(Collections.singleton(vo.getRoom().getId())).get(vo.getRoom().getId());
            if (room.getRegion() == vo.getRoom().getRegion() && room.getBuilding() == vo.getRoom().getBuilding() &&
                    room.getUnit() == vo.getRoom().getUnit() && room.getHouseNumber() == vo.getRoom().getHouseNumber()) {
                roomId = room.getId();
            }
        }

        if (roomId == 0) {
            Room room = roomService
                    .getByLocation(vo.getRoom().getRegion(), vo.getRoom().getBuilding(), vo.getRoom().getUnit(),
                            vo.getRoom().getHouseNumber());
            roomId = room.getId();
        }

        if (roomId == 0) {
            throw new BadRequestException("room 信息不正确");
        }

        int residentId = vo.getResident().getId();

        if (residentId == 0) {
            // 尝试从现有的房间中找到住户

            List<Resident> residents = residentService.queryByRoomId(roomId);
            String name = vo.getResident().getName();
            Optional<Resident> residentOptional =
                    residents.stream().filter(r -> StringUtils.equals(r.getName(), name)).findFirst();

            // 如果找到住户
            if (residentOptional.isPresent()) {
                // 如果住户已经绑定并且不是当前用户在编辑, 就拒绝绑定
                if (residentOptional.get().getUserId() != 0 && residentOptional.get().getUserId() != userId) {
                    throw new ConflictException("该用户已被绑定");
                }

                residentId = residentOptional.get().getId();
            }
        }

        if (residentId != 0) {
            Resident resident = residentService.get(residentId);
            boolean updateResident = false;

            // 如果住户已经绑定并且不是当前用户在编辑, 就拒绝绑定
            if (resident.getUserId() != 0 && resident.getUserId() != userId) {
                throw new ConflictException("该用户已被绑定");
            }

            // 绑定到已有住户
            if (resident.getUserId() == 0) {
                if (!phoneValid) {
                    throw new BadRequestException("验证码输入错误");
                }

                resident.setUserId(userId);
                resident.setVerified(false);
                updateResident = true;
            }

            // 改手机号的话不会影响 verified 状态, 但是需要验证码
            if (!StringUtils.equals(resident.getMobilePhone(), vo.getResident().getMobilePhone())) {
                if (!phoneValid) {
                    throw new BadRequestException("验证码输入错误");
                }

                resident.setMobilePhone(vo.getResident().getMobilePhone());
                updateResident = true;
            }

            // 修改房间或者姓名, 需要重新 verify
            if (resident.getRoomId() != roomId ||
                    !StringUtils.equals(resident.getName(), vo.getResident().getName())) {
                LOG.info("update resident, resident = {}");

                resident.setVerified(false);
                resident.setName(vo.getResident().getName());
                resident.setMobilePhone(vo.getResident().getMobilePhone());
                resident.setRoomId(roomId);
                updateResident = true;
            }

            if (updateResident) {
                residentService.update(resident);
            }
        } else if (!phoneValid) { // 新用户第一次绑定必须验证码
            throw new BadRequestException("验证码输入错误");
        } else {
            Resident resident = new Resident();
            resident.setMobilePhone(vo.getResident().getMobilePhone());
            resident.setUserId(userId);
            resident.setVerified(false);
            resident.setRoomId(roomId);
            resident.setName(vo.getResident().getName());
            residentService.create(resident);
        }

        verifyCodeService.clear();
        try {
            wechatLogic.sendNotifyToAdmin(userId);
        } catch (WxErrorException e) {
            LOG.error("", e);
        }

        return get(userId);
    }

    private List<UserVO> fillRoom(List<UserVO> userVOs) {
        return userVOs;
    }

    private void sendBindResidentNotification(int userId) throws WxErrorException {
        UserVO vo = get(userId);
        User user = userService.get(userId);
        WxMpTemplateMessage message = new WxMpTemplateMessage();
        message.setTemplateId("xs9cvsdgVcBCN3fDsDet2X9bJnmwOGn62dvbVqhFP-Y");
        message.setToUser(user.getOpenId());
        message.setUrl("https://m.jinyufeili.com");
        message.getDatas().add(new WxMpTemplateData("first", "社区实名认证"));
        message.getDatas().add(new WxMpTemplateData("keyword1", vo.getResident().getName()));
        message.getDatas().add(new WxMpTemplateData("keyword2", "认证完成"));
        message.getDatas().add(new WxMpTemplateData("remark", "您现在可以点击 \"我的社区\" 查看通过认证的个人信息"));
        wechatService.templateSend(message);
    }
}
