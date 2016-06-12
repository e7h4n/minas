package com.jinyufeili.minas.wechat.message.handler;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by pw on 6/10/16.
 */
@Service
public class SubscribeMessageHandler extends AbstractTextResponseMessageHandler {

    @Autowired
    private WxMpService wechatService;

    @Override
    protected String generateTextMessage(WxMpXmlMessage message, Map<String, Object> context) {
        String oauthUrl = wechatService.oauth2buildAuthorizationUrl("https://m.jinyufeili.com/wx#/close",
                WxConsts.OAUTH2_SCOPE_USER_INFO,
                WxConsts.EVT_SUBSCRIBE);

        return String.format("感谢关注翡丽社区，请先进行账号<a href=\"%s\">授权</a>。", oauthUrl);
    }
}
