package com.jinyufeili.minas.wechat.message.handler;

import com.jinyufeili.minas.info.data.Information;
import com.jinyufeili.minas.wechat.helper.MediaHelper;
import com.jinyufeili.minas.wechat.message.interceptor.InformationMessageInterceptor;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpMaterialNews;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutNewsMessage;
import me.chanjar.weixin.mp.bean.outxmlbuilder.NewsBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by pw on 6/14/16.
 */
@Service
public class InformationMessageHandler extends AbstractTextResponseMessageHandler {

    @Autowired
    private MediaHelper mediaHelper;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) throws WxErrorException {
        if (context.containsKey(InformationMessageInterceptor.INFO_KEY)) {
            return super.handle(wxMessage, context, wxMpService, sessionManager);
        }

        if (context.containsKey(InformationMessageInterceptor.MATERIAL_KEY)) {
            WxMpMaterialNews materialNews = (WxMpMaterialNews) context.get(InformationMessageInterceptor.MATERIAL_KEY);

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

        return null;
    }

    @Override
    protected String generateTextMessage(WxMpXmlMessage message, Map<String, Object> context) {
        if (context.containsKey(InformationMessageInterceptor.INFO_KEY)) {
            return ((Information) context.get(InformationMessageInterceptor.INFO_KEY)).getContent();
        }

        return StringUtils.EMPTY;
    }
}
