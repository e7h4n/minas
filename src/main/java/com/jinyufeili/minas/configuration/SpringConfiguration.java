/**
 * @(#)${FILE_NAME}.java, 8/11/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.support.ExecutorServiceAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ExecutorService;

/**
 * @author pw
 */
@Configuration
public class SpringConfiguration {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadGroupName("minas-executor-pool");
        taskExecutor.setThreadNamePrefix("executor-task");
        taskExecutor.setQueueCapacity(25);
        taskExecutor.setMaxPoolSize(4);
        return taskExecutor;
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler poolTaskScheduler = new ThreadPoolTaskScheduler();
        poolTaskScheduler.setPoolSize(3);
        poolTaskScheduler.setThreadNamePrefix("scheduled-task");
        poolTaskScheduler.setThreadGroupName("minas-scheduler-pool");
        return poolTaskScheduler;
    }

    @Bean
    @Autowired
    public ExecutorService executorService(TaskExecutor taskExecutor) {
        return new ExecutorServiceAdapter(taskExecutor);
    }
}
