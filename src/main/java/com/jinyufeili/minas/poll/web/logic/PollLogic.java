/**
 * @(#)${FILE_NAME}.java, 7/29/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.poll.web.logic;

import com.jinyufeili.minas.poll.service.PollService;
import com.jinyufeili.minas.poll.web.data.PollVO;
import com.jinyufeili.minas.poll.web.wrapper.PollVOWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author pw
 */
@Service
public class PollLogic {

    @Autowired
    private PollVOWrapper wrapper;

    @Autowired
    private PollService pollService;

    public PollVO get(int pollId) {
        return wrapper.wrap(pollService.get(pollId));
    }
}
