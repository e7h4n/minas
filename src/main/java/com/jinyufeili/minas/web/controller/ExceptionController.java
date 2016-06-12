/**
 * @(#)${FILE_NAME}.java, 6/12/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author pw
 */
@Controller
public class ExceptionController {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(CookieTheftException.class)
    public void cookieTheft() {
        LOG.warn("cookie theft");
    }
}
