/**
 * @(#)${FILE_NAME}.java, 7/29/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.poll.web.wrapper;

import com.jinyufeili.minas.poll.data.Poll;
import com.jinyufeili.minas.poll.data.Question;
import com.jinyufeili.minas.poll.service.QuestionService;
import com.jinyufeili.minas.poll.web.data.PollVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author pw
 */
@Service
public class PollVOWrapper {

    @Autowired
    private QuestionService questionService;

    public PollVO wrap(Poll poll) {
        return wrap(Collections.singletonList(poll)).get(0);
    }

    public List<PollVO> wrap(List<Poll> pollList) {
        Set<Integer> pollIds = pollList.stream().map(Poll::getId).collect(Collectors.toSet());
        Map<Integer, List<Question>> pollQuestionMap = questionService.queryByPollIds(pollIds);
        return pollList.stream().map(p -> {
            List<Question> questions = pollQuestionMap.get(p.getId());

            return wrap(p, questions);
        }).collect(Collectors.toList());
    }

    private PollVO wrap(Poll poll, List<Question> questions) {
        PollVO vo = new PollVO();
        vo.setId(poll.getId());
        vo.setName(poll.getName());
        vo.setDesc(poll.getDesc());
        vo.setStatus(poll.getStatus());
        vo.setQuestions(questions);
        return vo;
    }
}
