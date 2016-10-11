package com.jinyufeili.minas.wechat.message.handler;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.common.util.StringUtils;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by pw on 6/11/16.
 */
public abstract class AbstractTextResponseMessageHandler implements WxMpMessageHandler {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) throws WxErrorException {
        String content = this.generateTextMessage(wxMessage, context);

        return WxMpXmlOutMessage.TEXT().content(content).fromUser(wxMessage.getToUser())
                .toUser(wxMessage.getFromUser()).build();
    }

    protected abstract String generateTextMessage(WxMpXmlMessage message, Map<String, Object> context);
}
