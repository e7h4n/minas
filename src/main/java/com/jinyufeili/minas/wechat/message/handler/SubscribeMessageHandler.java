package com.jinyufeili.minas.wechat.message.handler;

import com.jinyufeili.minas.account.service.UserService;
import com.jinyufeili.minas.info.service.InformationService;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by pw on 6/10/16.
 */
@Service
public class SubscribeMessageHandler extends AbstractTextResponseMessageHandler {

    @Autowired
    private WxMpService wechatService;

    @Autowired
    private InformationService informationService;

    @Autowired
    private UserService userService;

    @Override
    protected String generateTextMessage(WxMpXmlMessage message, Map<String, Object> context) {
        StringBuilder sb;
        try {
            sb = new StringBuilder(informationService.getByKey("INFO_SUBSCRIBE").getContent());
        } catch (EmptyResultDataAccessException e) {
            sb = new StringBuilder("感谢关注翡丽社区");
        }

        String fromUserName = message.getFromUser();
        try {
            userService.getByOpenId(fromUserName);
        } catch (EmptyResultDataAccessException e) {
            String oauthUrl = wechatService
                    .oauth2buildAuthorizationUrl("https://m.jinyufeili.com/", WxConsts.OAUTH2_SCOPE_USER_INFO,
                            WxConsts.EVT_SUBSCRIBE);

            sb.append(String.format("\n\n-------\n本微信号仅对翡丽铂庭小区业主服务，在使用前，请您先<a href=\"%s\">点击此处进行账号绑定</a>。", oauthUrl));
        }

        return sb.toString();
    }
}
