/**
 * @(#)${FILE_NAME}.java, 7/25/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.service;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.WxMpMaterialNews;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author pw
 */
@Service
public class ArticleDirectory {

    public static final String FIELD_INDEX = "index";

    public static final String FIELD_MEDIA_ID = "mediaId";

    public static final String FIELD_CONTENT = "content";

    @Autowired
    private Directory directory;

    @Autowired
    private WxMpService wechatService;

    public List<WxMpMaterialNews.WxMpMaterialNewsArticle> search(String messageContent) {
        IndexReader indexReader;
        try {
            indexReader = DirectoryReader.open(directory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        IndexSearcher searcher = new IndexSearcher(indexReader);
        TermQuery query = new TermQuery(new Term(FIELD_CONTENT, messageContent));
        TopDocs topDocs;

        try {
            topDocs = searcher.search(query, 5);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        List<WxMpMaterialNews.WxMpMaterialNewsArticle> allArticle = new ArrayList<>();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc;
            try {
                doc = searcher.doc(scoreDoc.doc);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String mediaId = doc.get(FIELD_MEDIA_ID);
            WxMpMaterialNews materialNews;
            try {
                materialNews = wechatService.materialNewsInfo(mediaId);
            } catch (WxErrorException e) {
                throw new RuntimeException(e);
            }
            WxMpMaterialNews.WxMpMaterialNewsArticle article =
                    materialNews.getArticles().get(Integer.valueOf(doc.get(FIELD_INDEX)));
            allArticle.add(article);

        }

        List<WxMpMaterialNews.WxMpMaterialNewsArticle> articleList = new ArrayList<>();
        Set<String> urlSet = new HashSet<>();
        for (WxMpMaterialNews.WxMpMaterialNewsArticle article : allArticle) {
            if (!urlSet.contains(article.getUrl())) {
                urlSet.add(article.getUrl());
                articleList.add(article);
            }
        }

        return articleList;
    }
}
