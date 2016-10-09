/**
 * @(#)${FILE_NAME}.java, 6/30/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.account.service;

import com.jinyufeili.minas.account.data.User;
import me.chanjar.weixin.common.exception.WxErrorException;

import java.util.List;

/**
 * @author pw
 */
public interface UserService {

    User get(int id);

    int create(User user);

    User getByOpenId(String openId);

    List<User> queryFreeUser();

    boolean update(User user);

    List<User> queryByGroupId(int groupId);

    List<User> queryNonGroupUser();

    List<User> queryUnbinded();

    List<Integer> getUserIds(int cursor, int limit);

    boolean remove(int id);

    List<User> queryByName(String nickName);
}
