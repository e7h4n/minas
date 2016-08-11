/**
 * @(#)${FILE_NAME}.java, 6/12/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.account.web.ctrl;

import com.jinyufeili.minas.account.service.VerifyCodeService;
import com.jinyufeili.minas.account.web.data.UserVO;
import com.jinyufeili.minas.account.web.logic.UserLogic;
import com.jinyufeili.minas.web.exception.BadRequestException;
import com.jinyufeili.minas.web.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author pw
 */
@RestController
public class UserController {

    @Autowired
    private UserLogic userLogic;

    @Autowired
    private VerifyCodeService verifyCodeService;

    @RequestMapping("/api/users/current")
    public UserVO currentUser(Authentication authentication) {
        return userLogic.getByAuthentication(authentication);
    }

    @RequestMapping("/api/users/free")
    @PreAuthorize("hasRole('筹备组')")
    public List<UserVO> queryFreeUser() {
        return userLogic.queryFreeUser();
    }

    @RequestMapping("/api/users/{userId}")
    @PreAuthorize("hasRole('筹备组')")
    public UserVO get(@PathVariable int userId) {
        return userLogic.get(userId);
    }

    @RequestMapping(value = "/api/users/{userId}", method = RequestMethod.POST)
    public UserVO update(@PathVariable int userId, @RequestBody UserVO user,
                         @RequestParam(defaultValue = "") String verifyCode, Authentication authentication,
                         HttpServletRequest request) {
        if (userId != user.getId()) {
            throw new BadRequestException("invalid userId");
        }

        UserVO userVO = userLogic.getByAuthentication(authentication);

        if (userVO.getId() != userId && request.isUserInRole("筹备组")) {
            throw new UnauthorizedException();
        }

        if (request.isUserInRole("筹备组")) {
            return userLogic.update(user);
        } else {
            return userLogic.updateCurrentUser(user, verifyCode);
        }
    }

    @RequestMapping("/api/users")
    @PreAuthorize("hasRole('筹备组') or #groupId == 1")
    public List<UserVO> query(@RequestParam(defaultValue = "-1") int groupId) {
        return userLogic.queryUser(groupId);
    }

    @RequestMapping("/api/users/unbinded")
    @PreAuthorize("hasRole('筹备组')")
    public List<UserVO> queryUnbinded() {
        return userLogic.queryUnbinded();
    }
}
