/**
 * @(#)${FILE_NAME}.java, 8/10/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.account.web.ctrl;

import com.jinyufeili.minas.account.service.VerifyCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author pw
 */
@RestController
public class VerifyCodeController {

    @Autowired
    private VerifyCodeService verifyCodeService;

    @RequestMapping(value = "/api/send-verify-code", method = RequestMethod.POST)
    public void sendVerifyCode(@RequestParam String mobilePhone) {
        verifyCodeService.send(mobilePhone);
    }
}