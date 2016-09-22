/**
 * @(#)${FILE_NAME}.java, 22/09/2016.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.message.handler;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.service.UserService;
import com.jinyufeili.minas.crm.data.UserConfigType;
import com.jinyufeili.minas.crm.storage.UserConfigStorage;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author pw
 */
@Service
public class DisableSensorNotificationHandler extends AbstractTextResponseMessageHandler {

    @Autowired
    private UserConfigStorage userConfigStorage;

    @Autowired
    private UserService userService;

    @Override
    protected String generateTextMessage(WxMpXmlMessage message, Map<String, Object> context) {
        User user;
        try {
            user = userService.getByOpenId(message.getFromUserName());
        } catch (EmptyResultDataAccessException e) {
            return "此功能仅对小区内业主开放，请先点击『我的社区』进行身份验证。";
        }

        userConfigStorage.set(user.getId(), UserConfigType.PM25_NOTIFICATION, "-1");

        return "已经关闭空气变化提醒。";
    }
}
