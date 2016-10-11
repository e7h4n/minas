/**
 * @(#)${FILE_NAME}.java, 11/10/2016.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.service;

import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMaterialService;
import me.chanjar.weixin.mp.bean.WxMpMaterial;
import me.chanjar.weixin.mp.bean.WxMpMaterialArticleUpdate;
import me.chanjar.weixin.mp.bean.WxMpMaterialNews;
import me.chanjar.weixin.mp.bean.result.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author pw
 */
@Service
@Primary
public class DelegatedWxMpMaterialServiceImpl implements WxMpMaterialService {

    @Autowired
    @Qualifier("wxMpMaterialService")
    private WxMpMaterialService materialService;

    @Override
    public WxMediaUploadResult mediaUpload(String mediaType, File file) throws WxErrorException {
        return materialService.mediaUpload(mediaType, file);
    }

    @Override
    public File mediaDownload(String media_id) throws WxErrorException {
        return materialService.mediaDownload(media_id);
    }

    @Override
    public WxMediaImgUploadResult mediaImgUpload(File file) throws WxErrorException {
        return materialService.mediaImgUpload(file);
    }

    @Override
    public WxMediaUploadResult mediaUpload(String mediaType, String fileType, InputStream inputStream)
            throws WxErrorException, IOException {
        return materialService.mediaUpload(mediaType, fileType, inputStream);
    }

    @Override
    public WxMpMaterialUploadResult materialFileUpload(String mediaType, WxMpMaterial material)
            throws WxErrorException {
        return materialService.materialFileUpload(mediaType, material);
    }

    @Override
    public WxMpMaterialUploadResult materialNewsUpload(WxMpMaterialNews news) throws WxErrorException {
        return materialService.materialNewsUpload(news);
    }

    @Override
    public InputStream materialImageOrVoiceDownload(String media_id) throws WxErrorException {
        return materialService.materialImageOrVoiceDownload(media_id);
    }

    @Override
    public WxMpMaterialVideoInfoResult materialVideoInfo(String media_id) throws WxErrorException {
        return materialService.materialVideoInfo(media_id);
    }

    @Override
    @Cacheable("materialNews")
    public WxMpMaterialNews materialNewsInfo(String media_id) throws WxErrorException {
        return materialService.materialNewsInfo(media_id);
    }

    @Override
    public boolean materialNewsUpdate(WxMpMaterialArticleUpdate wxMpMaterialArticleUpdate) throws WxErrorException {
        return materialService.materialNewsUpdate(wxMpMaterialArticleUpdate);
    }

    @Override
    public boolean materialDelete(String media_id) throws WxErrorException {
        return materialService.materialDelete(media_id);
    }

    @Override
    public WxMpMaterialCountResult materialCount() throws WxErrorException {
        return materialService.materialCount();
    }

    @Override
    public WxMpMaterialNewsBatchGetResult materialNewsBatchGet(int offset, int count) throws WxErrorException {
        return materialService.materialNewsBatchGet(offset, count);
    }

    @Override
    public WxMpMaterialFileBatchGetResult materialFileBatchGet(String type, int offset, int count)
            throws WxErrorException {
        return materialService.materialFileBatchGet(type, offset, count);
    }
}
