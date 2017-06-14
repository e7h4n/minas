/**
 * @(#)${FILE_NAME}.java, 7/25/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.configuration;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author pw
 */
@Service
public class LuceneConfiguration {

    @Value("${lucene.directory}")
    private String luceneDirectory;

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Bean
    public Directory directory() throws IOException {
        File file = new File(luceneDirectory);
        Path path = file.toPath();

        try {
            return new MMapDirectory(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public Analyzer analyzer() {
        return new SmartChineseAnalyzer();
    }

    @Bean
    public IndexWriter indexWriter(Directory directory, Analyzer analyzer) throws IOException {
        return new IndexWriter(directory, new IndexWriterConfig(analyzer));
    }
}
