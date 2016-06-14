package com.jinyufeili.minas.wechat.message.handler;

import com.jinyufeili.minas.info.data.Information;
import com.jinyufeili.minas.wechat.message.interceptor.InformationMessageInterceptor;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by pw on 6/14/16.
 */
@Service
public class InformationMessageHandler extends AbstractTextResponseMessageHandler {

    @Override
    protected String generateTextMessage(WxMpXmlMessage message, Map<String, Object> context) {
        return ((Information) context.get(InformationMessageInterceptor.CONTEXT_KEY)).getContent();
    }
}
