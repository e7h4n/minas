/**
 * @(#)${FILE_NAME}.java, 7/25/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.job;

import me.chanjar.weixin.mp.api.WxMpMaterialService;
import me.chanjar.weixin.mp.bean.WxMpMaterialNews;
import me.chanjar.weixin.mp.bean.result.WxMpMaterialNewsBatchGetResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author pw
 */
@Component
@ConditionalOnProperty(name = "jobs.active", havingValue = "article")
public class ArticleJob implements CommandLineRunner {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WxMpMaterialService materialService;

    @Override
    public void run(String... args) throws Exception {
        WxMpMaterialNewsBatchGetResult materialNews = materialService.materialNewsBatchGet(0, 100);
        for (WxMpMaterialNewsBatchGetResult.WxMaterialNewsBatchGetNewsItem item : materialNews.getItems()) {
            LOG.info("mediaId={}", item.getMediaId());
            for (WxMpMaterialNews.WxMpMaterialNewsArticle article : item.getContent().getArticles()) {
                LOG.info("title={}", article.getTitle());
                LOG.info("thumbnailMediaId={}", article.getThumbMediaId());
            }
        }
    }
}
