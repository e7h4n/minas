/**
 * @(#)${FILE_NAME}.java, 8/12/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.account.configuration;

import com.jinyufeili.minas.account.helper.GeetestHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author pw
 */
@Configuration
public class GeetestHelperConfiguration {

    @Value("${geetest.captchaId}")
    private String captchaId;

    @Value("${geetest.privateKey}")
    private String privateKey;

    @Bean
    public GeetestHelper geetestHelper() {
        return new GeetestHelper(captchaId, privateKey);
    }
}
