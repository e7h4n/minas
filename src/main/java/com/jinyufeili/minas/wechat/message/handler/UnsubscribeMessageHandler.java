package com.jinyufeili.minas.wechat.message.handler;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.LazyReflectiveObjectGenerator;

import java.util.Map;

/**
 * Created by pw on 6/10/16.
 */
@Service
public class UnsubscribeMessageHandler implements WxMpMessageHandler {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) throws WxErrorException {
        LOG.info("Unsubscribe event, fromUser=" + wxMessage.getFromUserName());

        return null;
    }
}
