/**
 * @(#)${FILE_NAME}.java, 6/30/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.crm.service;

import com.jinyufeili.minas.crm.data.Resident;
import me.chanjar.weixin.common.exception.WxErrorException;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pw
 */
public interface ResidentService {

    long GROUP_ID = 100l;

    List<Resident> queryByRoomId(int roomId);

    List<Resident> queryByMobilePhone(String mobilePhone);

    List<Resident> queryByName(String name);

    List<Resident> queryByUserName(String userName);

    Map<Integer, Resident> queryByUserIds(Set<Integer> userIds);

    List<Resident> queryByRoom(int region, int building, int unit, int houseNumber);

    boolean update(Resident resident);

    void syncWechatUserInfo(int residentId) throws WxErrorException;

    Resident get(int residentId);

    int create(Resident resident);

    Map<Integer, Resident> getByIds(Set<Integer> residentIds);
}
