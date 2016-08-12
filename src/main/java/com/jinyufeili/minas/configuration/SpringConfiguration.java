/**
 * @(#)${FILE_NAME}.java, 8/11/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.configuration;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author pw
 */
@Configuration
public class SpringConfiguration {

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(2, 10, TimeUnit.SECONDS.toMillis(10), TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue());
        BasicThreadFactory factory = new BasicThreadFactory.Builder().namingPattern("task-pool-thread-%d").build();
        threadPoolExecutor.setThreadFactory(factory);
        ConcurrentTaskExecutor taskExecutor = new ConcurrentTaskExecutor(threadPoolExecutor);
        return taskExecutor;
    }
}
