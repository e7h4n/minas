/**
 * @(#)${FILE_NAME}.java, 8/12/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.account.web.ctrl;

import com.jinyufeili.minas.account.data.GeetestConfig;
import com.jinyufeili.minas.account.web.logic.GeetestLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pw
 */
@RestController
public class GeetestController {

    @Autowired
    private GeetestLogic geetestLogic;

    @RequestMapping("/api/geetest/config")
    public GeetestConfig post() {
        return geetestLogic.register();
    }
}
