/**
 * @(#)${FILE_NAME}.java, 8/10/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.web.ctrl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pw
 */
@RestController
public class VerifyCodeController {

    @RequestMapping(value = "/send-verify-code", method = RequestMethod.POST)
    public void sendVerifyCode(@RequestParam String phone) {

    }
}
