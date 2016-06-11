package com.jinyufeili.minas.wechat.web.logic;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.service.UserService;
import com.jinyufeili.minas.web.exception.BadRequestException;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by pw on 6/10/16.
 */
@Service
public class WechatLogic {

    public static final String ALLOWED_HOST = "m.jinyufeili.com";

    public static final String ALLOWED_PROTOCOL = "https";

    public static final String ALLOWED_PATH = "/";

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
        if (!ALLOWED_PROTOCOL.equals(url.getProtocol()) || !ALLOWED_HOST.equals(url.getHost()) || !ALLOWED_PATH.equals(
                url.getPath())) {
            throw new BadRequestException("url 不合法");
        }
    }
}
