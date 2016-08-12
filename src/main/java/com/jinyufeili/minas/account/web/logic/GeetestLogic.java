/**
 * @(#)${FILE_NAME}.java, 8/12/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.account.web.logic;

import com.jinyufeili.minas.account.data.GeetestConfig;
import com.jinyufeili.minas.account.helper.GeetestHelper;
import me.chanjar.weixin.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

/**
 * @author pw
 */
@Service
public class GeetestLogic {

    private static final String FALLBACK_TIME = "geetestFallbackTime";

    private static final long FALLBACK_TIME_MAX_WAIT = TimeUnit.HOURS.toMillis(1);

    @Autowired
    private GeetestHelper geetestHelper;

    @Autowired
    private HttpSession httpSession;

    public GeetestConfig register() {
        String challenge = geetestHelper.registerChallenge();
        GeetestConfig config = new GeetestConfig();

        if (StringUtils.isNotBlank(challenge)) {
            config.setCaptchaId(geetestHelper.getCaptchaId());
            config.setChallengeId(challenge);
            config.setType(GeetestConfig.Type.NORMAL);
            httpSession.removeAttribute(FALLBACK_TIME);
        } else {
            config.setType(GeetestConfig.Type.FALLBACK);
            httpSession.setAttribute(FALLBACK_TIME, System.currentTimeMillis());
        }

        return config;
    }

    public boolean verify(String challenge, String validate, String securityCode) {
        Object fallbackTime = httpSession.getAttribute(FALLBACK_TIME);
        if (fallbackTime != null && fallbackTime instanceof Long &&
                (Long) fallbackTime + FALLBACK_TIME_MAX_WAIT > System.currentTimeMillis()) {
            return true;
        }

        return geetestHelper.validateRequest(challenge, validate, securityCode);
    }
}
