package com.jinyufeili.minas.wechat.service;

import com.jinyufeili.minas.wechat.message.handler.*;
import com.jinyufeili.minas.wechat.message.interceptor.InformationMessageInterceptor;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.api.WxMessageDuplicateChecker;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;

/**
 * Created by pw on 6/10/16.
 */
public class WechatMessageRouter extends WxMpMessageRouter {

    @Autowired
    private SubscribeMessageHandler subscribeMessageHandler;

    @Autowired
    private UnsubscribeMessageHandler unsubscribeMessageHandler;

    @Autowired
    private SearchHandler searchHandler;

    @Autowired
    private InformationMessageInterceptor informationMessageInterceptor;

    @Autowired
    private InformationMessageHandler informationMessageHandler;

    @Autowired
    private WeatherMessageHandler weatherMessageHandler;

    @Autowired
    private NotFoundMessageHandler notFoundMessageHandler;

    @Autowired
    private EnableSensorNotificationHandler enableSensorNotificationHandler;

    @Autowired
    private DisableSensorNotificationHandler disableSensorNotificationHandler;

    @Override
    @Autowired
    public void setExecutorService(ExecutorService executorService) {
        super.setExecutorService(executorService);
    }

    @Override
    @Autowired
    public void setMessageDuplicateChecker(WxMessageDuplicateChecker messageDuplicateChecker) {
        super.setMessageDuplicateChecker(messageDuplicateChecker);
    }

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
                .msgType(WxConsts.XML_MSG_EVENT)
                .event(WxConsts.EVT_CLICK)
                .interceptor(informationMessageInterceptor)
                .handler(informationMessageHandler)
                .end()

                .rule()
                .async(false)
                .msgType(WxConsts.XML_MSG_TEXT)
                .content("打开空气提醒")
                .handler(enableSensorNotificationHandler)
                .end()

                .rule()
                .async(false)
                .msgType(WxConsts.XML_MSG_TEXT)
                .content("开启空气提醒")
                .handler(enableSensorNotificationHandler)
                .end()

                .rule()
                .async(false)
                .msgType(WxConsts.XML_MSG_TEXT)
                .content("关闭空气提醒")
                .handler(disableSensorNotificationHandler)
                .end()

                .rule()
                .async(false)
                .msgType(WxConsts.XML_MSG_TEXT)
                .handler(searchHandler)
                .end()

                .rule()
                .handler(notFoundMessageHandler)
                .end();
    }
}
