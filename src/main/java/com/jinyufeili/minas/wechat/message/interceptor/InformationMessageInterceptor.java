package com.jinyufeili.minas.wechat.message.interceptor;

import com.jinyufeili.minas.info.data.Information;
import com.jinyufeili.minas.info.service.InformationService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageInterceptor;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpMaterialNews;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by pw on 6/14/16.
 */
@Service
public class InformationMessageInterceptor implements WxMpMessageInterceptor {

    public static final String INFO_KEY = "information";

    public static final String MATERIAL_KEY = "materialNews";

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InformationService informationService;

    @Override
    public boolean intercept(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
                             WxSessionManager sessionManager) throws WxErrorException {
        if (wxMessage.getEventKey().indexOf("INFO_") == 0) {
            return infoContent(wxMessage, context, wxMpService);
        }

        if (wxMessage.getEventKey().indexOf("MEDIA_") == 0) {
            return materialContent(wxMessage, context, wxMpService);
        }

        return false;
    }

    private boolean materialContent(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService)
            throws WxErrorException {
        String[] split = wxMessage.getEventKey().split("_");
        if (split.length < 2) {
            return false;
        }

        String mediaId = split[1];
        WxMpMaterialNews newsInfo = wxMpService.materialNewsInfo(mediaId);
        if (newsInfo.isEmpty()) {
            return false;
        }

        context.put(MATERIAL_KEY, newsInfo);
        return true;
    }

    private boolean infoContent(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService) {
        try {
            Information information = informationService.getByKey(wxMessage.getEventKey());
            context.put(INFO_KEY, information);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
}
