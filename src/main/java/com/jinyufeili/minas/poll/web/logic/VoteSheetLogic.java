/**
 * @(#)${FILE_NAME}.java, 6/30/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.poll.web.logic;

import com.jinyufeili.minas.crm.data.Resident;
import com.jinyufeili.minas.crm.service.ResidentService;
import com.jinyufeili.minas.poll.data.VoteSheet;
import com.jinyufeili.minas.poll.service.VoteSheetService;
import com.jinyufeili.minas.poll.web.data.VoteSheetVO;
import com.jinyufeili.minas.poll.web.wrapper.VoteSheetVOWrapper;
import com.jinyufeili.minas.web.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @author pw
 */
@Service
public class VoteSheetLogic {

    @Autowired
    private ResidentService residentService;

    @Autowired
    private VoteSheetService voteSheetService;

    @Autowired
    private VoteSheetVOWrapper voteSheetVOWrapper;

    public VoteSheetVO getByUserId(int pollId, int userId) {
        Resident resident = residentService.queryByUserIds(Collections.singleton(userId)).get(userId);
        if (resident == null) {
            throw new NotFoundException("没有找到当前用户的表决结果");
        }

        VoteSheet voteSheet = voteSheetService.getByResidentId(pollId, resident.getId());
        if (voteSheet == null) {
            throw new NotFoundException("当前用户未参加本次表决");
        }

        return voteSheetVOWrapper.wrap(voteSheet);
    }
}
