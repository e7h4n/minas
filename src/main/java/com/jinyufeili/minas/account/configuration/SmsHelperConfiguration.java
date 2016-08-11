/**
 * @(#)${FILE_NAME}.java, 8/10/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.account.configuration;

import com.jinyufeili.minas.account.helper.SmsHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author pw
 */
@Configuration
public class SmsHelperConfiguration {

    @Value("${luosimao.key}")
    public String key;

    @Bean
    public SmsHelper smsHelper() {
        return new SmsHelper(key);
    }
}
