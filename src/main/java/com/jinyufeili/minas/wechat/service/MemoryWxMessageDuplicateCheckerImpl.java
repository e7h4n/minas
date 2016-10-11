/**
 * @(#)${FILE_NAME}.java, 11/10/2016.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.service;

import me.chanjar.weixin.common.api.WxMessageDuplicateChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * 默认消息重复检查器
 * 将每个消息id保存在内存里，每隔5秒清理已经过期的消息id，每个消息id的过期时间是15秒
 * </pre>
 *
 * @author pw
 */
@Service
public class MemoryWxMessageDuplicateCheckerImpl implements WxMessageDuplicateChecker {

    /**
     * 一个消息ID在内存的过期时间：15秒
     */
    private final Long timeToLive = TimeUnit.SECONDS.toMillis(15);

    /**
     * 消息id->消息时间戳的map
     */
    private final ConcurrentHashMap<String, Long> msgId2Timestamp = new ConcurrentHashMap<>();

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean isDuplicate(String messageId) {
        if (messageId == null) {
            return false;
        }
        Long timestamp = this.msgId2Timestamp.putIfAbsent(messageId, System.currentTimeMillis());
        return timestamp != null;
    }

    @Scheduled(cron = "*/5 * * * * *")
    public void startCheck() {
        Long now = System.currentTimeMillis();
        for (Map.Entry<String, Long> entry : msgId2Timestamp.entrySet()) {
            if (now - entry.getValue() > timeToLive) {
                msgId2Timestamp.entrySet().remove(entry);
            }
        }
    }
}

