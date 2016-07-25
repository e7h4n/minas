/**
 * @(#)${FILE_NAME}.java, 7/25/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.message.interceptor;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageInterceptor;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpMaterialNews;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author pw
 */
@Service
public class MediaMessageInterceptor implements WxMpMessageInterceptor {

    @Override
    public boolean intercept(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
                             WxSessionManager sessionManager) throws WxErrorException {
        if (wxMessage.getEventKey().indexOf("MEDIA_") != 0) {
            return false;
        }

        String[] split = wxMessage.getEventKey().split("_");
        if (split.length < 2) {
            return false;
        }

        String mediaId = split[1];
        WxMpMaterialNews newsInfo = wxMpService.materialNewsInfo(mediaId);
        if (newsInfo.isEmpty()) {
            return false;
        }

        context.put("materialNews", newsInfo);
        return true;
    }
}
