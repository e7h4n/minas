package com.jinyufeili.minas.wechat.configuration;

import com.jinyufeili.minas.wechat.service.WechatMessageRouter;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by pw on 6/10/16.
 */
@Configuration
public class WechatConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "wechat")
    public WxMpConfigStorage wxMpConfigStorage() {
        return new WxMpInMemoryConfigStorage();
    }

    @Bean
    public WxMpService wxMpService(WxMpConfigStorage configStorage) {
        WxMpServiceImpl service = new WxMpServiceImpl();
        service.setWxMpConfigStorage(configStorage);
        return service;
    }

    @Bean
    public WechatMessageRouter wechatMessageRouter(WxMpService wechatService) {
        return new WechatMessageRouter(wechatService);
    }
}
