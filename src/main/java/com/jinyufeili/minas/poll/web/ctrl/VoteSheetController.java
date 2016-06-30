/**
 * @(#)${FILE_NAME}.java, 6/30/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.poll.web.ctrl;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.service.UserService;
import com.jinyufeili.minas.poll.web.data.VoteSheetVO;
import com.jinyufeili.minas.poll.web.logic.VoteSheetLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pw
 */
@RestController
@RequestMapping
public class VoteSheetController {

    @Autowired
    private VoteSheetLogic voteSheetLogic;

    @Autowired
    private UserService userService;

    @RequestMapping("/api/polls/{pollId}/vote-sheets/current")
    public VoteSheetVO getById(@PathVariable int pollId, Authentication authentication) {
        User user = userService.getByOpenId(authentication.getName());
        return voteSheetLogic.getByUserId(pollId, user.getId());
    }
}
