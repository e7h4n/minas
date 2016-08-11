/**
 * @(#)${FILE_NAME}.java, 6/20/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.job;

import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.service.UserService;
import com.jinyufeili.minas.crm.data.Resident;
import com.jinyufeili.minas.crm.service.ResidentService;
import me.chanjar.weixin.common.exception.WxErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

/**
 * @author pw
 */
@Component
public class SyncWechatUserInfoJob {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private ResidentService residentService;

    @Scheduled(cron = "0 0 0 * * *")
    public void sync() throws WxErrorException {
        LOG.info("start job to sync wechat user info");

        int cursor = 0;
        while (true) {
            List<Integer> userIds = userService.getUserIds(cursor, 100);
            if (CollectionUtils.isEmpty(userIds)) {
                break;
            }

            cursor = userIds.get(userIds.size() - 1);

            for (Integer userId : userIds) {
                User user = userService.get(userId);
                Resident resident =
                        residentService.queryByUserIds(Collections.singleton(user.getId())).get(user.getId());

                if (resident != null) {
                    residentService.syncWechatUserInfo(resident.getId());
                }
            }
        }

        LOG.info("job done");
    }
}
