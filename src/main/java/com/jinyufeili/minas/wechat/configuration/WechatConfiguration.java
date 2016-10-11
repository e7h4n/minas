package com.jinyufeili.minas.wechat.configuration;

import com.jinyufeili.minas.wechat.service.WechatMessageRouter;
import me.chanjar.weixin.mp.api.*;
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
    public WxMpKefuService wxMpKefuService(WxMpService wxMpService) {
        return wxMpService.getKefuService();
    }

    @Bean
    public WxMpUserService wxMpUserService(WxMpService wxMpService) {
        return wxMpService.getUserService();
    }

    @Bean
    public WxMpUserTagService wxMpUserTagService(WxMpService wxMpService) {
        return wxMpService.getUserTagService();
    }

    @Bean
    public WxMpMaterialService wxMpMaterialService(WxMpService wxMpService) {
        return wxMpService.getMaterialService();
    }

    @Bean
    public WxMpMenuService wxMpMenuService(WxMpService wxMpService) {
        return wxMpService.getMenuService();
    }

    @Bean
    public WechatMessageRouter wechatMessageRouter(WxMpService wechatService) {
        return new WechatMessageRouter(wechatService);
    }
}
