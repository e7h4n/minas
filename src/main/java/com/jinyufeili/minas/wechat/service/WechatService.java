/**
 * @(#)${FILE_NAME}.java, 7/25/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.service;

import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.WxMenu;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.util.http.RequestExecutor;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.*;
import me.chanjar.weixin.mp.bean.result.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author pw
 */
@Component
@Primary
public class WechatService implements WxMpService {

    @Autowired
    @Qualifier("wxMpService")
    private WxMpService wxMpService;

    @Override
    public boolean checkSignature(String timestamp, String nonce, String signature) {
        return wxMpService.checkSignature(timestamp, nonce, signature);
    }

    @Override
    public String getAccessToken() throws WxErrorException {
        return wxMpService.getAccessToken();
    }

    @Override
    public String getAccessToken(boolean forceRefresh) throws WxErrorException {
        return wxMpService.getAccessToken(forceRefresh);
    }

    @Override
    public String getJsapiTicket() throws WxErrorException {
        return wxMpService.getJsapiTicket();
    }

    @Override
    public String getJsapiTicket(boolean forceRefresh) throws WxErrorException {
        return wxMpService.getJsapiTicket(forceRefresh);
    }

    @Override
    public WxJsapiSignature createJsapiSignature(String url) throws WxErrorException {
        return wxMpService.createJsapiSignature(url);
    }

    @Override
    public WxMediaUploadResult mediaUpload(String mediaType, String fileType, InputStream inputStream)
            throws WxErrorException, IOException {
        return wxMpService.mediaUpload(mediaType, fileType, inputStream);
    }

    @Override
    public WxMpMaterialUploadResult materialFileUpload(String mediaType, WxMpMaterial material)
            throws WxErrorException {
        return wxMpService.materialFileUpload(mediaType, material);
    }

    @Override
    public WxMpMaterialUploadResult materialNewsUpload(WxMpMaterialNews news) throws WxErrorException {
        return wxMpService.materialNewsUpload(news);
    }

    @Override
    public InputStream materialImageOrVoiceDownload(String media_id) throws WxErrorException {
        return wxMpService.materialImageOrVoiceDownload(media_id);
    }

    @Override
    public WxMpMaterialVideoInfoResult materialVideoInfo(String media_id) throws WxErrorException {
        return wxMpService.materialVideoInfo(media_id);
    }

    @Override
    @Cacheable("materialNews")
    public WxMpMaterialNews materialNewsInfo(String media_id) throws WxErrorException {
        return wxMpService.materialNewsInfo(media_id);
    }

    @Override
    public boolean materialNewsUpdate(WxMpMaterialArticleUpdate wxMpMaterialArticleUpdate) throws WxErrorException {
        return wxMpService.materialNewsUpdate(wxMpMaterialArticleUpdate);
    }

    @Override
    public boolean materialDelete(String media_id) throws WxErrorException {
        return wxMpService.materialDelete(media_id);
    }

    @Override
    public WxMpMaterialCountResult materialCount() throws WxErrorException {
        return wxMpService.materialCount();
    }

    @Override
    public WxMpMaterialNewsBatchGetResult materialNewsBatchGet(int offset, int count) throws WxErrorException {
        return wxMpService.materialNewsBatchGet(offset, count);
    }

    @Override
    public WxMpMaterialFileBatchGetResult materialFileBatchGet(String type, int offset, int count)
            throws WxErrorException {
        return wxMpService.materialFileBatchGet(type, offset, count);
    }

    @Override
    public WxMediaUploadResult mediaUpload(String mediaType, File file) throws WxErrorException {
        return wxMpService.mediaUpload(mediaType, file);
    }

    @Override
    public File mediaDownload(String media_id) throws WxErrorException {
        return wxMpService.mediaDownload(media_id);
    }

    @Override
    public void customMessageSend(WxMpCustomMessage message) throws WxErrorException {
        wxMpService.customMessageSend(message);
    }

    @Override
    public WxMpMassUploadResult massNewsUpload(WxMpMassNews news) throws WxErrorException {
        return wxMpService.massNewsUpload(news);
    }

    @Override
    public WxMpMassUploadResult massVideoUpload(WxMpMassVideo video) throws WxErrorException {
        return wxMpService.massVideoUpload(video);
    }

    @Override
    public WxMpMassSendResult massGroupMessageSend(WxMpMassGroupMessage message) throws WxErrorException {
        return wxMpService.massGroupMessageSend(message);
    }

    @Override
    public WxMpMassSendResult massOpenIdsMessageSend(WxMpMassOpenIdsMessage message) throws WxErrorException {
        return wxMpService.massOpenIdsMessageSend(message);
    }

    @Override
    public void menuCreate(WxMenu menu) throws WxErrorException {
        wxMpService.menuCreate(menu);
    }

    @Override
    public void menuDelete() throws WxErrorException {
        wxMpService.menuDelete();
    }

    @Override
    public void menuDelete(String menuid) throws WxErrorException {
        wxMpService.menuDelete(menuid);
    }

    @Override
    public WxMenu menuGet() throws WxErrorException {
        return wxMpService.menuGet();
    }

    @Override
    public WxMenu menuTryMatch(String userid) throws WxErrorException {
        return wxMpService.menuTryMatch(userid);
    }

    @Override
    public WxMpGroup groupCreate(String name) throws WxErrorException {
        return wxMpService.groupCreate(name);
    }

    @Override
    public List<WxMpGroup> groupGet() throws WxErrorException {
        return wxMpService.groupGet();
    }

    @Override
    public long userGetGroup(String openid) throws WxErrorException {
        return wxMpService.userGetGroup(openid);
    }

    @Override
    public void groupUpdate(WxMpGroup group) throws WxErrorException {
        wxMpService.groupUpdate(group);
    }

    @Override
    public void userUpdateGroup(String openid, long to_groupid) throws WxErrorException {
        wxMpService.userUpdateGroup(openid, to_groupid);
    }

    @Override
    public void userUpdateRemark(String openid, String remark) throws WxErrorException {
        wxMpService.userUpdateRemark(openid, remark);
    }

    @Override
    public WxMpUser userInfo(String openid, String lang) throws WxErrorException {
        return wxMpService.userInfo(openid, lang);
    }

    @Override
    public WxMpUserList userList(String next_openid) throws WxErrorException {
        return wxMpService.userList(next_openid);
    }

    @Override
    public WxMpQrCodeTicket qrCodeCreateTmpTicket(int scene_id, Integer expire_seconds) throws WxErrorException {
        return wxMpService.qrCodeCreateTmpTicket(scene_id, expire_seconds);
    }

    @Override
    public WxMpQrCodeTicket qrCodeCreateLastTicket(int scene_id) throws WxErrorException {
        return wxMpService.qrCodeCreateLastTicket(scene_id);
    }

    @Override
    public WxMpQrCodeTicket qrCodeCreateLastTicket(String scene_str) throws WxErrorException {
        return wxMpService.qrCodeCreateLastTicket(scene_str);
    }

    @Override
    public File qrCodePicture(WxMpQrCodeTicket ticket) throws WxErrorException {
        return wxMpService.qrCodePicture(ticket);
    }

    @Override
    public String shortUrl(String long_url) throws WxErrorException {
        return wxMpService.shortUrl(long_url);
    }

    @Override
    public String templateSend(WxMpTemplateMessage templateMessage) throws WxErrorException {
        return wxMpService.templateSend(templateMessage);
    }

    @Override
    public WxMpSemanticQueryResult semanticQuery(WxMpSemanticQuery semanticQuery) throws WxErrorException {
        return wxMpService.semanticQuery(semanticQuery);
    }

    @Override
    public String oauth2buildAuthorizationUrl(String scope, String state) {
        return wxMpService.oauth2buildAuthorizationUrl(scope, state);
    }

    @Override
    public String oauth2buildAuthorizationUrl(String redirectURI, String scope, String state) {
        return wxMpService.oauth2buildAuthorizationUrl(redirectURI, scope, state);
    }

    @Override
    public WxMpOAuth2AccessToken oauth2getAccessToken(String code) throws WxErrorException {
        return wxMpService.oauth2getAccessToken(code);
    }

    @Override
    public WxMpOAuth2AccessToken oauth2refreshAccessToken(String refreshToken) throws WxErrorException {
        return wxMpService.oauth2refreshAccessToken(refreshToken);
    }

    @Override
    public WxMpUser oauth2getUserInfo(WxMpOAuth2AccessToken oAuth2AccessToken, String lang) throws WxErrorException {
        return wxMpService.oauth2getUserInfo(oAuth2AccessToken, lang);
    }

    @Override
    public boolean oauth2validateAccessToken(WxMpOAuth2AccessToken oAuth2AccessToken) {
        return wxMpService.oauth2validateAccessToken(oAuth2AccessToken);
    }

    @Override
    public String[] getCallbackIP() throws WxErrorException {
        return wxMpService.getCallbackIP();
    }

    @Override
    public List<WxMpUserSummary> getUserSummary(Date beginDate, Date endDate) throws WxErrorException {
        return wxMpService.getUserSummary(beginDate, endDate);
    }

    @Override
    public List<WxMpUserCumulate> getUserCumulate(Date beginDate, Date endDate) throws WxErrorException {
        return wxMpService.getUserCumulate(beginDate, endDate);
    }

    @Override
    public String get(String url, String queryParam) throws WxErrorException {
        return wxMpService.get(url, queryParam);
    }

    @Override
    public String post(String url, String postData) throws WxErrorException {
        return wxMpService.post(url, postData);
    }

    @Override
    public <T, E> T execute(RequestExecutor<T, E> executor, String uri, E data) throws WxErrorException {
        return wxMpService.execute(executor, uri, data);
    }

    @Override
    public void setWxMpConfigStorage(WxMpConfigStorage wxConfigProvider) {
        wxMpService.setWxMpConfigStorage(wxConfigProvider);
    }

    @Override
    public void setRetrySleepMillis(int retrySleepMillis) {
        wxMpService.setRetrySleepMillis(retrySleepMillis);
    }

    @Override
    public void setMaxRetryTimes(int maxRetryTimes) {
        wxMpService.setMaxRetryTimes(maxRetryTimes);
    }

    @Override
    @Deprecated
    public WxMpPrepayIdResult getPrepayId(String openId, String outTradeNo, double amt, String body, String tradeType,
                                          String ip, String notifyUrl) {
        return wxMpService.getPrepayId(openId, outTradeNo, amt, body, tradeType, ip, notifyUrl);
    }

    @Override
    public WxMpPrepayIdResult getPrepayId(Map<String, String> parameters) {
        return wxMpService.getPrepayId(parameters);
    }

    @Override
    public Map<String, String> getJSSDKPayInfo(Map<String, String> parameters) {
        return wxMpService.getJSSDKPayInfo(parameters);
    }

    @Override
    @Deprecated
    public Map<String, String> getJSSDKPayInfo(String openId, String outTradeNo, double amt, String body,
                                               String tradeType, String ip, String notifyUrl) {
        return wxMpService.getJSSDKPayInfo(openId, outTradeNo, amt, body, tradeType, ip, notifyUrl);
    }

    @Override
    public WxMpPayResult getJSSDKPayResult(String transactionId, String outTradeNo) {
        return wxMpService.getJSSDKPayResult(transactionId, outTradeNo);
    }

    @Override
    public WxMpPayCallback getJSSDKCallbackData(String xmlData) {
        return wxMpService.getJSSDKCallbackData(xmlData);
    }

    @Override
    public boolean checkJSSDKCallbackDataSignature(Map<String, String> kvm, String signature) {
        return wxMpService.checkJSSDKCallbackDataSignature(kvm, signature);
    }

    @Override
    public WxRedpackResult sendRedpack(Map<String, String> parameters) throws WxErrorException {
        return wxMpService.sendRedpack(parameters);
    }
}
