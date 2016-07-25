package com.jinyufeili.minas.wechat.service;

import com.jinyufeili.minas.wechat.message.handler.*;
import com.jinyufeili.minas.wechat.message.interceptor.InformationMessageInterceptor;
import com.jinyufeili.minas.wechat.message.interceptor.MediaMessageInterceptor;
import com.jinyufeili.minas.wechat.message.interceptor.ResidentSearchMessageInterceptor;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * Created by pw on 6/10/16.
 */
public class WechatMessageRouter extends WxMpMessageRouter {

    @Autowired
    private SubscribeMessageHandler subscribeMessageHandler;

    @Autowired
    private UnsubscribeMessageHandler unsubscribeMessageHandler;

    @Autowired
    private ResidentSearchMessageInterceptor residentSearchMessageInterceptor;

    @Autowired
    private ResidentSearchMessageHandler residentSearchMessageHandler;

    @Autowired
    private NotFoundMessageHandler notFoundMessageHandler;

    @Autowired
    private InformationMessageInterceptor informationMessageInterceptor;

    @Autowired
    private InformationMessageHandler informationMessageHandler;

    @Autowired
    private WeatherMessageHandler weatherMessageHandler;

    public WechatMessageRouter(WxMpService wxMpService) {
        super(wxMpService);
    }

    @PostConstruct
    private void init() {
        this
                .rule()
                .async(false)
                .msgType(WxConsts.XML_MSG_EVENT)
                .event(WxConsts.EVT_SUBSCRIBE)
                .handler(subscribeMessageHandler)
                .end()

                .rule()
                .async(false)
                .msgType(WxConsts.XML_MSG_EVENT)
                .event(WxConsts.EVT_CLICK)
                .eventKey("WEATHER")
                .handler(weatherMessageHandler)
                .end()

                .rule()
                .msgType(WxConsts.XML_MSG_EVENT)
                .event(WxConsts.EVT_UNSUBSCRIBE)
                .handler(unsubscribeMessageHandler)
                .end()

                .rule()
                .async(false)
                .msgType(WxConsts.XML_MSG_TEXT)
                .interceptor(residentSearchMessageInterceptor)
                .handler(residentSearchMessageHandler)
                .end()

                .rule()
                .async(false)
                .msgType(WxConsts.XML_MSG_EVENT)
                .event(WxConsts.EVT_CLICK)
                .interceptor(informationMessageInterceptor)
                .handler(informationMessageHandler)
                .end()

                .rule()
                .handler(notFoundMessageHandler)
                .end();
    }
}
