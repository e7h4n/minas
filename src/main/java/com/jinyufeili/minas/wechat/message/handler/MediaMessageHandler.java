/**
 * @(#)${FILE_NAME}.java, 7/25/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.message.handler;

import com.jinyufeili.minas.wechat.helper.MediaHelper;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpMaterialNews;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutNewsMessage;
import me.chanjar.weixin.mp.bean.outxmlbuilder.NewsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author pw
 */
@Service
public class MediaMessageHandler implements WxMpMessageHandler {

    @Autowired
    private MediaHelper mediaHelper;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) throws WxErrorException {
        WxMpMaterialNews materialNews = (WxMpMaterialNews) context.get("materialNews");

        NewsBuilder newsBuilder = WxMpXmlOutMessage.NEWS();

        for (WxMpMaterialNews.WxMpMaterialNewsArticle article : materialNews.getArticles()) {
            WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
            item.setTitle(article.getTitle());
            item.setDescription(article.getDigest());
            item.setUrl(article.getUrl());
            item.setPicUrl(mediaHelper.getMediaUrl(article.getThumbMediaId()));

            newsBuilder.addArticle(item);
        }

        newsBuilder.fromUser(wxMessage.getToUserName());
        newsBuilder.toUser(wxMessage.getFromUserName());
        return newsBuilder.build();
    }
}
