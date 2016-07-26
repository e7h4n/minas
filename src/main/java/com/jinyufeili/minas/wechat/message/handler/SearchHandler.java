package com.jinyufeili.minas.wechat.message.handler;

import com.jinyufeili.minas.wechat.helper.MediaHelper;
import com.jinyufeili.minas.wechat.message.interceptor.ResidentSearchMessageInterceptor;
import com.jinyufeili.minas.wechat.service.ArticleDirectory;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.*;
import me.chanjar.weixin.mp.bean.outxmlbuilder.NewsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by pw on 6/10/16.
 */
@Service
public class SearchHandler implements WxMpMessageHandler {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MediaHelper mediaHelper;

    @Autowired
    private ResidentSearchMessageInterceptor residentSearchMessageInterceptor;

    @Autowired
    private ResidentSearchMessageHandler residentSearchMessageHandler;

    @Autowired
    private ArticleDirectory articleDirectory;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) throws WxErrorException {


        if (residentSearchMessageInterceptor.intercept(wxMessage, context, wxMpService, sessionManager)) {
            return residentSearchMessageHandler.handle(wxMessage, context, wxMpService, sessionManager);
        }

        String messageContent = wxMessage.getContent();
        List<WxMpMaterialNews.WxMpMaterialNewsArticle> articles = articleDirectory.search(messageContent);

        if (CollectionUtils.isEmpty(articles)) {
            if (wxMessage.getContent().length() < 5) {
                return WxMpXmlOutTextMessage.TEXT().content(String.format("没有找到到关于\"%s\"的内容", wxMessage.getContent()))
                        .fromUser(wxMessage.getToUserName()).toUser(wxMessage.getFromUserName()).build();
            }
        }

        NewsBuilder newsBuilder = WxMpXmlOutMessage.NEWS();
        for (WxMpMaterialNews.WxMpMaterialNewsArticle article : articles) {
            WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
            item.setUrl(article.getUrl());
            item.setPicUrl(mediaHelper.getMediaUrl(article.getThumbMediaId()));
            item.setDescription(article.getDigest());
            item.setTitle(article.getTitle());
            LOG.info("add article {}", item);
            newsBuilder.addArticle(item);
        }
        newsBuilder.toUser(wxMessage.getFromUserName());
        newsBuilder.fromUser(wxMessage.getToUserName());

        return newsBuilder.build();
    }

}
