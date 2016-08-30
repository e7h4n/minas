/**
 * @(#)${FILE_NAME}.java, 8/11/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

import java.util.concurrent.Executors;

/**
 * @author pw
 */
@Configuration
public class SpringConfiguration {

    @Bean
    public TaskExecutor taskExecutor() {
        return new ConcurrentTaskExecutor(Executors.newFixedThreadPool(3));
    }
}
