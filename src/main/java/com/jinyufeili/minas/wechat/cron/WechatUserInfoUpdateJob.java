/**
 * @(#)${FILE_NAME}.java, 6/20/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.wechat.cron;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinyufeili.minas.account.data.User;
import com.jinyufeili.minas.account.service.UserService;
import com.jinyufeili.minas.crm.data.Resident;
import com.jinyufeili.minas.crm.service.ResidentService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.common.util.StringUtils;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author pw
 */
@Component
public class WechatUserInfoUpdateJob {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private WxMpService wechatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ResidentService residentService;

    @Scheduled(cron = "0 */10 * * * *")
    @Async
    public void update() throws WxErrorException, IOException {
        LOG.info("start job to update wechat user info");

        int cursor = 0;
        while (true) {
            List<Integer> userIds = userService.getUserIds(cursor, 100);
            if (CollectionUtils.isEmpty(userIds)) {
                break;
            }

            cursor = userIds.get(userIds.size() - 1);

            List<Map<String, Object>> userIdMap = userIds.stream().map(id -> {
                User user = userService.get(id);
                Map<String, Object> entry = new HashMap<>();
                entry.put("openid", user.getOpenId());
                entry.put("lang", "zh_CN");
                return entry;
            }).collect(Collectors.toList());

            Map<String, List<Map<String, Object>>> userMap = Collections.singletonMap("user_list", userIdMap);
            String requestBody = objectMapper.writeValueAsString(userMap);

            String result = wechatService.post("https://api.weixin.qq.com/cgi-bin/user/info/batchget", requestBody);
            Map<String, List<Map<String, Object>>> retMap = objectMapper.readValue(result, Map.class);

            for (Map<String, Object> userInfoMap : retMap.get("user_info_list")) {
                WxMpUser wxMpUser = WxMpUser.fromJson(objectMapper.writeValueAsString(userInfoMap));
                User user = userService.getByOpenId(wxMpUser.getOpenId());
                Resident resident =
                        residentService.queryByUserIds(Collections.singleton(user.getId())).get(user.getId());

                if (!wxMpUser.isSubscribe() && resident == null) {
                    LOG.info("unbinded user unsubscribed, try to remove this user, userId={}, name={}", user.getId(),
                            user.getName());
                    userService.remove(user.getId());
                    return;
                }

                if (StringUtils.isNotBlank(wxMpUser.getNickname())) {
                    if (!user.getName().equals(wxMpUser.getNickname())) {
                        LOG.info("update user, openId={}, oldName={}, newName={}", user.getOpenId(), user.getName(),
                                wxMpUser.getNickname());
                        user.setName(wxMpUser.getNickname());
                        try {
                            userService.update(user);
                        } catch (RuntimeException e) {
                            LOG.error("", e);
                        }
                    }
                }
            }
        }

        LOG.info("job done");
    }
}
