/**
 * @(#)${FILE_NAME}.java, 6/30/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.poll.web.wrapper;

import com.jinyufeili.minas.poll.data.Answer;
import com.jinyufeili.minas.poll.data.Question;
import com.jinyufeili.minas.poll.web.data.QuestionWithAnswerVO;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author pw
 */
@Service
public class QuestionWithAnswerVOWrapper {

    public List<QuestionWithAnswerVO> wrap(List<Answer> answerList, HashMap<Integer, Question> questionMap) {
        return answerList.stream().map(a -> {
            Question question = questionMap.get(a.getQuestionId());
            QuestionWithAnswerVO vo = new QuestionWithAnswerVO();

            vo.setId(question.getId());
            vo.setResult(a.getResult());
            vo.setVoteSheetId(a.getVoteSheetId());
            vo.setPollId(question.getPollId());
            vo.setContent(question.getContent());

            return vo;
        }).collect(Collectors.toList());
    }
}
