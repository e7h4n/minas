/**
 * @(#)${FILE_NAME}.java, 7/25/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.cron;

import com.jinyufeili.minas.wechat.service.ArticleDirectory;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpMaterialService;
import me.chanjar.weixin.mp.bean.WxMpMaterialNews;
import me.chanjar.weixin.mp.bean.result.WxMpMaterialCountResult;
import me.chanjar.weixin.mp.bean.result.WxMpMaterialNewsBatchGetResult;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author pw
 */
@Service
@ConditionalOnProperty(value = "cron.articleDirectoryUpdate", matchIfMissing = true)
public class ArticleDirectoryUpdateJob {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IndexWriter indexWriter;

    @Autowired
    private WxMpMaterialService materialService;

    @Scheduled(cron = "0 */10 * * * *")
    public void updateDirectory() throws WxErrorException, IOException {
        WxMpMaterialCountResult materialCount = materialService.materialCount();
        int newsCount = materialCount.getNewsCount();

        for (int i = 0; i < newsCount; i += 100) {
            WxMpMaterialNewsBatchGetResult newsList = materialService.materialNewsBatchGet(i, 100);
            for (WxMpMaterialNewsBatchGetResult.WxMaterialNewsBatchGetNewsItem materialNews : newsList.getItems()) {
                List<WxMpMaterialNews.WxMpMaterialNewsArticle> newsArticles = materialNews.getContent().getArticles();
                for (int j = 0; j < newsArticles.size(); j++) {
                    WxMpMaterialNews.WxMpMaterialNewsArticle article = newsArticles.get(j);
                    if (article.getTitle().startsWith("*")) {
                        continue;
                    }

                    Document doc = new Document();
                    doc.add(new IntField(ArticleDirectory.FIELD_INDEX, j, Field.Store.YES));
                    doc.add(new StringField(ArticleDirectory.FIELD_MEDIA_ID, materialNews.getMediaId(),
                            Field.Store.YES));
                    doc.add(new TextField(ArticleDirectory.FIELD_CONTENT, article.getContent(), Field.Store.YES));
                    indexWriter
                            .updateDocument(new Term("id", String.format("%s_%d", materialNews.getMediaId(), j)), doc);
                    LOG.info("add doc, mediaId={}, index={}", materialNews.getMediaId(), j);
                }
            }
        }

        indexWriter.commit();
    }
}
