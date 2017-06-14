/**
 * @(#)${FILE_NAME}.java, 7/25/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.cron;

import com.jinyufeili.minas.wechat.data.ArticleDocument;
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

    private final WxMpMaterialService materialService;

    private final IndexWriter indexWriter;

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ArticleDirectoryUpdateJob(WxMpMaterialService materialService, IndexWriter indexWriter) {
        this.materialService = materialService;
        this.indexWriter = indexWriter;
    }

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

                    String key = String.format("%s_%d", materialNews.getMediaId(), j);
                    Document doc = new Document();
                    doc.add(new StringField(ArticleDocument.FIELD_ID, key, Field.Store.NO));
                    doc.add(new StoredField(ArticleDocument.FIELD_INDEX, j));
                    doc.add(new StringField(ArticleDocument.FIELD_MEDIA_ID, materialNews.getMediaId(),
                            Field.Store.YES));
                    doc.add(new TextField(ArticleDocument.FIELD_CONTENT, article.getContent(), Field.Store.NO));

                    Term keyTerm = new Term(ArticleDocument.FIELD_ID, key);
                    indexWriter.deleteDocuments(keyTerm);
                    indexWriter.addDocument(doc);
                    LOG.info("add doc, mediaId={}, index={}", materialNews.getMediaId(), j);
                }
            }
        }

        indexWriter.commit();
    }
}
