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
import com.jinyufeili.minas.web.exception.UnauthorizedException;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.WxMpTemplateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author pw
 */
@Service
public class UserLogic {

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
    private WxMpService wechatService;

    public UserVO getByAuthentication(Authentication authentication) {
        User user;
        try {
            user = userService.getByOpenId(authentication.getName());
        } catch (EmptyResultDataAccessException e) {
            throw new UnauthorizedException();
        }
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

        boolean residentUserIdChanged = false;
        if (vo.getResident() != null && vo.getResident().getId() == 0 && vo.getRoom() != null && vo.getRoom().getId() > 0) {
            residentUserIdChanged = true;
            Resident resident = new Resident();
            resident.setName(vo.getResident().getName());
            resident.setMobilePhone(vo.getResident().getMobilePhone());
            resident.setRoomId(vo.getRoom().getId());
            resident.setUserId(userId);
            residentService.create(resident);
        } else if (vo.getResident() != null && vo.getResident().getId() > 0){
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

    public List<UserVO> queryUser(int groupId) {
        if (groupId == 0) {
            return userVOWrapper.wrap(userService.queryNonGroupUser());
        } else {
            return userVOWrapper.wrap(userService.queryByGroupId(groupId));
        }
    }
}
