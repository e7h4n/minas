package com.jinyufeili.minas.wechat.web.logic;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.service.UserService;
import com.jinyufeili.minas.web.exception.BadRequestException;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.WxMpTemplateMessage;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * Created by pw on 6/10/16.
 */
@Service
public class WechatLogic {

    public static final String ALLOWED_HOST = "m.jinyufeili.com";

    public static final String ALLOWED_PROTOCOL = "https";

    public static final String ALLOWED_PATH = "/";

    public static final int ADMIN_ID = 1;

    private static final String LANG = "zh-cn";

    @Autowired
    private WxMpService wechatService;

    @Autowired
    private UserService userService;

    public User initUserByCode(String code) throws WxErrorException {
        WxMpOAuth2AccessToken accessToken = wechatService.oauth2getAccessToken(code);
        WxMpUser wechatUser = wechatService.oauth2getUserInfo(accessToken, LANG);

        User user;
        try {
            user = userService.getByOpenId(wechatUser.getOpenId());
        } catch (EmptyResultDataAccessException e) {
            user = new User();
            user.setAccessToken(accessToken.getAccessToken());
            user.setRefreshToken(accessToken.getRefreshToken());
            user.setExpiredTime(System.currentTimeMillis() + accessToken.getExpiresIn() * 1000);
            user.setName(wechatUser.getNickname());
            user.setOpenId(wechatUser.getOpenId());
            int userId = userService.create(user);
            user = userService.get(userId);
        }

        return user;
    }

    public void checkJsUrl(@RequestParam("url") String urlString) throws MalformedURLException {
        URL url = new URL(urlString);
        if (!ALLOWED_PROTOCOL.equals(url.getProtocol()) || !ALLOWED_HOST.equals(url.getHost()) ||
                !ALLOWED_PATH.equals(url.getPath())) {
            throw new BadRequestException("url 不合法");
        }
    }

    public void sendNotifyToAdmin(User user) throws WxErrorException {
        User admin = userService.get(ADMIN_ID);
        WxMpTemplateMessage message = new WxMpTemplateMessage();
        message.setToUser(admin.getOpenId());
        message.setTemplateId("0NTYqPHErV5rlcsBZ7xDOqxmkv6EnMOo4HylFkRAVlg");
        message.getData().add(new WxMpTemplateData("first", "有新用户注册，请联系用户核实身份"));
        message.getData().add(new WxMpTemplateData("keyword1", user.getName()));
        message.getData().add(new WxMpTemplateData("keyword2", new Date().toString()));
        message.getData().add(new WxMpTemplateData("remark", "点击进入审批页面"));
        message.setUrl(String.format("https://m.jinyufeili.com/#/users/%d", user.getId()));

        wechatService.templateSend(message);
    }

    public void sendNotifyToAdmin(int userId) throws WxErrorException {
        sendNotifyToAdmin(userService.get(userId));
    }
}
