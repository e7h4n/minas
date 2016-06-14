/**
 * @(#)${FILE_NAME}.java, 6/12/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.account.web.ctrl;

import com.jinyufeili.minas.account.data.UserGroup;
import com.jinyufeili.minas.account.service.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author pw
 */
@RestController
public class UserGroupController {

    @Autowired
    private UserGroupService userGroupService;

    @RequestMapping("/api/groups")
    public List<UserGroup> getAll() {
        return userGroupService.getAll();
    }

    @RequestMapping("/api/groups/{groupId}")
    public UserGroup get(@PathVariable int groupId) {
        return userGroupService.get(groupId);
    }
}
