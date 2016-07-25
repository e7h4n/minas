/**
 * @(#)${FILE_NAME}.java, 7/25/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.configuration;

import com.qiniu.storage.BucketManager;
import com.qiniu.util.Auth;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author pw
 */
@Configuration
public class QiniuConfiguration {

    @Bean
    public Auth auth(QiniuConfig qiniuConfig) {
        Auth auth = Auth.create(qiniuConfig.getAccess(), qiniuConfig.getSecret());
        auth.uploadToken(qiniuConfig.getBucket());
        return auth;
    }

    @Bean
    public BucketManager bucketManager(Auth auth) {
        return new BucketManager(auth);
    }

    @Bean
    @ConfigurationProperties(prefix = "qiniu")
    public QiniuConfig qiniuConfig() {
        return new QiniuConfig();
    }

    private class QiniuConfig {

        private String access;

        private String secret;

        private String bucket;

        public String getAccess() {
            return access;
        }

        public void setAccess(String access) {
            this.access = access;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getBucket() {
            return bucket;
        }

        public void setBucket(String bucket) {
            this.bucket = bucket;
        }
    }
}
