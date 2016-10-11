package com.jinyufeili.minas.wechat.message.interceptor;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.data.UserGroup;
import com.jinyufeili.minas.account.service.UserGroupService;
import com.jinyufeili.minas.account.service.UserService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageInterceptor;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by pw on 6/11/16.
 */
public abstract class AbstractRoleInterceptor implements WxMpMessageInterceptor {

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private UserService userService;

    public abstract Set<String> getUserGroups();

    private boolean checkGroup(WxMpXmlMessage wxMessage) throws WxErrorException {

        User user;
        try {
            user = userService.getByOpenId(wxMessage.getFromUser());
        } catch (EmptyResultDataAccessException e) {
            return false;
        }

        List<UserGroup> userGroupList = userGroupService.getGroupsOfUser(user.getId());
        Set<String> userGroupNameSet = userGroupList.stream().map(UserGroup::getName).collect(Collectors.toSet());

        userGroupNameSet.retainAll(getUserGroups());
        return !CollectionUtils.isEmpty(userGroupNameSet);
    }

    @Override
    public final boolean intercept(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
                             WxSessionManager sessionManager) throws WxErrorException {
        if (!checkGroup(wxMessage)) {
            return false;
        }

        return innerIntercept(wxMessage, context, wxMpService, sessionManager);
    }

    protected abstract boolean innerIntercept(WxMpXmlMessage message, Map<String, Object> context,
                                              WxMpService wechatService,
                                              WxSessionManager sessionManager) throws WxErrorException;
}
