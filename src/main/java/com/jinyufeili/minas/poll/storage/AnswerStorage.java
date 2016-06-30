/**
 * @(#)${FILE_NAME}.java, 6/30/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.poll.storage;

import com.jinyufeili.minas.poll.data.Answer;
import com.jinyufeili.minas.poll.data.AnswerResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @author pw
 */
@Repository
public class AnswerStorage {

    private static final RowMapper<Answer> ROW_MAPPER = ((rs, rowNum) -> {
        Answer a = new Answer();

        a.setId(rs.getInt("id"));
        a.setQuestionId(rs.getInt("poll_question_id"));
        a.setResult(AnswerResult.findByInt(rs.getInt("answer")));
        a.setVoteSheetId(rs.getInt("vote_sheet_id"));
        return a;
    });

    @Autowired
    private NamedParameterJdbcOperations db;

    public Map<Integer, List<Answer>> queryByVoteSheetIds(Collection<Integer> voteSheetIds) {
        List<Answer> answerList =
                db.query("select * from poll_pollquestionanswer where vote_sheet_id in (:voteSheetIds)",
                        Collections.singletonMap("voteSheetIds", voteSheetIds), ROW_MAPPER);

        HashMap<Integer, List<Answer>> answerMap = new HashMap<>();
        for (Answer answer : answerList) {
            if (!answerMap.containsKey(answer.getVoteSheetId())) {
                answerMap.put(answer.getVoteSheetId(), new ArrayList<>());
            }
            answerMap.get(answer.getVoteSheetId()).add(answer);
        }
        return answerMap;
    }
}
