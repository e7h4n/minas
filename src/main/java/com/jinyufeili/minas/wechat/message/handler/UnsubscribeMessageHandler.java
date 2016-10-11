package com.jinyufeili.minas.wechat.message.handler;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.service.UserService;
import com.jinyufeili.minas.crm.data.Resident;
import com.jinyufeili.minas.crm.service.ResidentService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

/**
 * Created by pw on 6/10/16.
 */
@Service
public class UnsubscribeMessageHandler implements WxMpMessageHandler {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private ResidentService residentService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) throws WxErrorException {
        LOG.info("Unsubscribe event, fromUser=" + wxMessage.getFromUser());

        User user;
        try {
            user = userService.getByOpenId(wxMessage.getFromUser());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        Resident resident = residentService.queryByUserIds(Collections.singleton(user.getId())).get(user.getId());
        if (resident != null) {
            return null;
        }

        LOG.info("remove user by unsubscribe event, userId={}, name={}", user.getId(), user.getName());
        userService.remove(user.getId());
        return null;
    }
}
