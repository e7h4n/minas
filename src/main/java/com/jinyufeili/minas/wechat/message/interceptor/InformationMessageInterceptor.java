package com.jinyufeili.minas.wechat.message.interceptor;

import com.jinyufeili.minas.info.data.Information;
import com.jinyufeili.minas.info.service.InformationService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageInterceptor;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by pw on 6/14/16.
 */
@Service
public class InformationMessageInterceptor implements WxMpMessageInterceptor {

    public static final String CONTEXT_KEY = "information";

    @Autowired
    private InformationService informationService;

    @Override
    public boolean intercept(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
                             WxSessionManager sessionManager) throws WxErrorException {
        if (wxMessage.getEventKey().indexOf("INFO_") != 0) {
            return false;
        }

        try {
            Information information = informationService.getByKey(wxMessage.getEventKey());
            context.put(CONTEXT_KEY, information);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
}
