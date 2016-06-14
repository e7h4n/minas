/**
 * @(#)${FILE_NAME}.java, 6/14/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.job;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.service.UserService;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author pw
 */
@Component
@ConditionalOnProperty("jobs.bindNotification")
public class BindNotificationJob implements CommandLineRunner {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Value("jobs.bindNotification")
    private long startTime;

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        List<User> users = userService.queryUnbinded();
        for (User user : users) {
            try {
                String ret = userService.sendBindNotification(user);
                LOG.info("result {}", ret);
            } catch (WxErrorException e) {
                LOG.error("", e);
            }
        }
    }

}
