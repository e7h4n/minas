/**
 * @(#)${FILE_NAME}.java, 7/25/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.configuration;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.lionsoul.jcseg.analyzer.v5x.JcsegAnalyzer5X;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.lionsoul.jcseg.tokenizer.core.DictionaryFactory;
import org.lionsoul.jcseg.tokenizer.core.JcsegTaskConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author pw
 */
@Service
public class LuceneConfiguration {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Bean
    public Directory directory() {
        return new RAMDirectory();
    }

    @Bean
    public JcsegTaskConfig taskConfig() {
        return new JcsegTaskConfig();
    }

    @Bean
    public ADictionary aDictionary(JcsegTaskConfig taskConfig) {
        return DictionaryFactory.createDefaultDictionary(taskConfig, false);
    }

    @Bean
    public Analyzer analyzer(JcsegTaskConfig config, ADictionary dictionary) throws IOException {
        return new JcsegAnalyzer5X(JcsegTaskConfig.COMPLEX_MODE, config, dictionary);
    }
}
