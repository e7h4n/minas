package com.jinyufeili.minas.wechat.web.ctrl;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.service.UserService;
import com.jinyufeili.minas.crm.service.ResidentService;
import com.jinyufeili.minas.web.exception.BadRequestException;
import com.jinyufeili.minas.wechat.web.logic.WechatLogic;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.util.StringUtils;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.WxMpXmlOutMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by pw on 6/10/16.
 */
@RestController
public class WechatController {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private TokenBasedRememberMeServices rememberMeServices;

    @Autowired
    private WxMpService wechatService;

    @Autowired
    private WxMpConfigStorage wechatConfig;

    @Autowired
    private WxMpMessageRouter wechatMessageRouter;

    @Autowired
    private WechatLogic wechatLogic;

    @Autowired
    private ResidentService residentService;

    @RequestMapping("/api/wechat/test")
    public String test() {
        return "test";
    }

    @RequestMapping("/api/wechat/handler")
    public String wechatVerifyHandler(@RequestParam String timestamp, @RequestParam String nonce,
                                      @RequestParam String signature,
                                      @RequestParam(name = "echostr") String echoString) {
        if (!wechatService.checkSignature(timestamp, nonce, signature)) {
            throw new BadRequestException("微信签名验证错误");
        }

        return echoString;
    }

    @RequestMapping(value = "/api/wechat/handler", method = RequestMethod.POST)
    public String wechatHandler(@RequestParam String timestamp, @RequestParam String nonce,
                                @RequestParam String signature,
                                @RequestParam(value = "encrypt_type", defaultValue = "raw") String encryptType,
                                @RequestParam(value = "msg_signature") String msgSignature,
                                @RequestBody String message) {
        if (!wechatService.checkSignature(timestamp, nonce, signature)) {
            throw new BadRequestException("微信签名验证错误");
        }

        if (!"aes".equals(encryptType)) {
            throw new BadRequestException("不可识别的加密类型");
        }

        if (StringUtils.isBlank(msgSignature)) {
            throw new BadRequestException("msg_signature 不能为空");
        }

        WxMpXmlMessage inMessage =
                WxMpXmlMessage.fromEncryptedXml(message, wechatConfig, timestamp, nonce, msgSignature);
        WxMpXmlOutMessage outMessage = wechatMessageRouter.route(inMessage);
        if (outMessage == null) {
            LOG.warn("empty out message");
            outMessage = WxMpXmlOutMessage.TEXT().content("").fromUser(inMessage.getToUserName())
                    .toUser(inMessage.getFromUserName()).build();
        }
        return outMessage.toEncryptedXml(wechatConfig);
    }

    @RequestMapping("/api/wechat/oauth2-callback")
    public void wechatLoginCallback(HttpServletRequest request, HttpServletResponse response,
                                    @RequestParam String callback, @RequestParam String code)
            throws IOException, WxErrorException {
        User user = wechatLogic.initUserByCode(code);
        UserDetails userDetails = userService.loadUserByUsername(user.getOpenId());
        Authentication authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        rememberMeServices.loginSuccess(request, response, authenticationToken);
        response.sendRedirect(callback);

        Set<Integer> userIds = new HashSet<>();
        userIds.add(user.getId());

        if (residentService.queryByUserIds(userIds).size() == 0) {
            wechatLogic.sendNotifyToAdmin(user);
            userService.sendBindNotification(user);
        }
    }

    @RequestMapping("/api/wechat/js_signature")
    public WxJsapiSignature createJsSignature(@RequestParam("url") String urlString)
            throws MalformedURLException, WxErrorException {
        wechatLogic.checkJsUrl(urlString);
        return wechatService.createJsapiSignature(urlString);
    }
}
