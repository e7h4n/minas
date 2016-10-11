/**
 * @(#)${FILE_NAME}.java, 7/25/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.helper;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMaterialService;
import me.chanjar.weixin.mp.api.WxMpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author pw
 */
@Service
public class MediaHelper {

    public static final String BUCKET = "jyfl";

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WxMpService wxMpService;

    @Autowired
    private Auth qiniuAuth;

    @Autowired
    private BucketManager bucketManager;

    @Autowired
    private WxMpMaterialService materialService;

    @Cacheable("mediaUrls")
    public String getMediaUrl(String mediaId) {
        try {
            bucketManager.stat(BUCKET, convertKey(mediaId));
        } catch (QiniuException e) {
            try {
                uploadMedia(mediaId);
            } catch (WxErrorException e1) {
                return null;
            }
        }

        return qiniuAuth.privateDownloadUrl("https://static.jinyufeili.com/" + convertKey(mediaId), 3600);
    }

    private FileInfo uploadMedia(String mediaId) throws WxErrorException {
        InputStream inputStream = materialService.materialImageOrVoiceDownload(mediaId);

        byte[] bytes;
        try {
            bytes = StreamUtils.copyToByteArray(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UploadManager uploadManager = new UploadManager();
        try {
            uploadManager.put(bytes, convertKey(mediaId), qiniuAuth.uploadToken(BUCKET));
        } catch (QiniuException e) {
            Response r = e.response;
            LOG.error(r.toString());

            try {
                LOG.error(r.bodyString());
            } catch (QiniuException e1) {
            }

            throw new RuntimeException(e);
        }

        try {
            return bucketManager.stat(BUCKET, convertKey(mediaId));
        } catch (QiniuException e) {
            throw new RuntimeException(e);
        }
    }

    private String convertKey(String mediaId) {
        return "wechat_media/" + mediaId;
    }

}
