/**
 * @(#)${FILE_NAME}.java, 8/11/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.account.service;

import com.jinyufeili.minas.account.helper.SmsHelper;
import com.jinyufeili.minas.web.exception.ConflictException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author pw
 */
@Service
public class VerifyCodeService {

    public static final String SESSION_VERIFY_CODE_CREATED_TIME = "verifyCodeCreatedTime";

    public static final String SESSION_VERIFY_CODE = "verifyCode";

    private static final String SESSION_MOBILE_PHONE = "mobilePhone";

    private static final long VERIFY_CODE_EXPIRE_DURATION = TimeUnit.MINUTES.toMillis(10);

    private static final long MAX_WAIT_TIME = TimeUnit.SECONDS.toMillis(50);

    private Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SmsHelper smsHelper;

    private Map<String, Long> numberLock = new ConcurrentHashMap<>();

    @Autowired
    private HttpSession session;

    @Scheduled(cron = "* * * * * *")
    public void cleanLock() {
        numberLock.entrySet().stream().filter(entry -> entry.getValue() + MAX_WAIT_TIME < System.currentTimeMillis())
                .forEach(entry -> {
                    numberLock.remove(entry.getKey());
                });
    }

    public void send(String mobilePhone) {
        if (numberLock.containsKey(mobilePhone)) {
            long createdTime = numberLock.get(mobilePhone);
            if (createdTime + MAX_WAIT_TIME > System.currentTimeMillis()) {
                throw new ConflictException(String.valueOf(createdTime));
            }
        }

        String verifyCode = null;
        if (checkSessionValue()) {
            if (StringUtils.equals(getSessionMobilePhone(), mobilePhone)) {
                verifyCode = getSessionVerifyCode();
            }
        }

        if (verifyCode == null) {
            verifyCode = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 10000));
            session.setAttribute(SESSION_MOBILE_PHONE, mobilePhone);
            session.setAttribute(SESSION_VERIFY_CODE, verifyCode);
            session.setAttribute(SESSION_VERIFY_CODE_CREATED_TIME, System.currentTimeMillis());
        }

        numberLock.put(mobilePhone, System.currentTimeMillis());
        LOG.info("verify code is {}", verifyCode);
        smsHelper.send(mobilePhone, verifyCode);
    }

    public boolean check(String mobilePhone, String verifyCode) {
        if (!checkSessionValue()) {
            return false;
        }

        if (!StringUtils.equals(getSessionMobilePhone(), mobilePhone)) {
            return false;
        }

        if (!StringUtils.equals(getSessionVerifyCode(), verifyCode)) {
            return false;
        }

        return true;
    }

    public void clear() {
        session.removeAttribute(SESSION_MOBILE_PHONE);
        session.removeAttribute(SESSION_VERIFY_CODE);
        session.removeAttribute(SESSION_VERIFY_CODE_CREATED_TIME);
    }

    private String getSessionMobilePhone() {
        Object mobilePhone = session.getAttribute(SESSION_MOBILE_PHONE);
        return (String) mobilePhone;
    }

    private String getSessionVerifyCode() {
        Object verifyCode = session.getAttribute(SESSION_VERIFY_CODE);
        return (String) verifyCode;
    }

    private boolean checkSessionValue() {
        Object existedMobilePhone = session.getAttribute(SESSION_MOBILE_PHONE);
        Object existedVerifyCode = session.getAttribute(SESSION_VERIFY_CODE);
        Object existedVerifyCodeCreatedTime = session.getAttribute(SESSION_VERIFY_CODE_CREATED_TIME);
        if (existedMobilePhone != null && existedVerifyCode != null && existedVerifyCodeCreatedTime != null &&
                existedMobilePhone instanceof String && existedVerifyCode instanceof String &&
                existedVerifyCodeCreatedTime instanceof Long) {

            long createdTime = (Long) existedVerifyCodeCreatedTime;
            if (createdTime + VERIFY_CODE_EXPIRE_DURATION > System.currentTimeMillis()) {
                return true;
            }
        }

        return false;
    }
}
