/**
 * @(#)${FILE_NAME}.java, 7/26/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.cron;

import me.chanjar.weixin.common.exception.WxErrorException;
import org.lionsoul.jcseg.tokenizer.core.ADictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author pw
 */
@Service
public class JcsegDictionaryUpdateJob {

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Value("${jcseg.lexiconPath}")
    private String lexiconPath;

    @Autowired
    private ADictionary dictionary;

    @Autowired
    private ArticleDirectoryUpdateJob articleDirectoryUpdateJob;

    private boolean loaded = false;

    @Scheduled(cron = "*/10 * * * * *")
    @Async
    public void updateDictionary() throws IOException, WxErrorException {
        LOG.info("reload dictionary {}", lexiconPath);
        if (lexiconPath.startsWith("classpath:")) {
            String resourcePath = lexiconPath.replace("classpath:", "");
            LOG.info("load resource {}", resourcePath);
            dictionary.load(getResourceAsStream(resourcePath));
        } else if (lexiconPath.startsWith("file:")) {
            String pathname = lexiconPath.replace("file:", "");
            LOG.info("load file {}", pathname);
            dictionary.load(new File(pathname));
        }

        if (!loaded) {
            loaded = true;
            articleDirectoryUpdateJob.updateDirectory();
        }
    }

    private InputStream getResourceAsStream(String lexicon) {
        return this.getClass().getClassLoader().getResourceAsStream(lexicon);
    }
}
