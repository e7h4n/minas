/**
 * @(#)${FILE_NAME}.java, 6/30/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.account.web.logic;

import com.jinyufeili.minas.account.web.data.UserVO;
import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * @author pw
 */
public interface UserLogic {

    UserVO getByAuthentication(Authentication authentication);

    List<UserVO> queryFreeUser();

    UserVO get(int userId);

    UserVO update(UserVO vo);

    List<UserVO> queryUnbinded();

    List<UserVO> queryUser(int groupId);

    UserVO updateCurrentUser(UserVO user, String verifyCode);

    void remove(int userId);
}
