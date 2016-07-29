/**
 * @(#)${FILE_NAME}.java, 6/30/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.poll.web.ctrl;

import com.jinyufeili.minas.poll.data.Poll;
import com.jinyufeili.minas.poll.data.PollStatus;
import com.jinyufeili.minas.poll.service.PollService;
import com.jinyufeili.minas.poll.web.data.PollVO;
import com.jinyufeili.minas.poll.web.logic.PollLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author pw
 */
@RestController
@RequestMapping("/api/polls")
public class PollController {

    @Autowired
    private PollService pollService;

    @Autowired
    private PollLogic pollLogic;

    @RequestMapping
    public List<Poll> query(@RequestParam(value = "status[]") List<String> strStatuses) {
        Set<PollStatus> statuses = strStatuses.stream().map(PollStatus::findByString).collect(Collectors.toSet());

        return pollService.query(statuses);
    }

    @RequestMapping("/{pollId}")
    public PollVO getById(@PathVariable int pollId) {
        return pollLogic.get(pollId);
    }
}
