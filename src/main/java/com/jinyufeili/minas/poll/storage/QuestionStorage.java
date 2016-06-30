/**
 * @(#)${FILE_NAME}.java, 6/30/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.poll.storage;

import com.jinyufeili.minas.poll.data.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author pw
 */
@Repository
public class QuestionStorage {

    private static final RowMapper<Question> ROW_MAPPER = ((rs, rowNum) -> {
        Question q = new Question();

        q.setId(rs.getInt("id"));
        q.setContent(rs.getString("content"));
        q.setPollId(rs.getInt("poll_id"));

        return q;
    });

    @Autowired
    private NamedParameterJdbcOperations db;

    public Map<Integer, List<Question>> queryByPoolIds(Set<Integer> pollIds) {
        if (CollectionUtils.isEmpty(pollIds)) {
            return Collections.emptyMap();
        }

        List<Question> questions = db.query("select * from poll_pollquestion where poll_id in (:pollIds)",
                Collections.singletonMap("pollIds", pollIds), ROW_MAPPER);

        HashMap<Integer, List<Question>> questionMap = new HashMap<>();
        for (Question question : questions) {
            if (!questionMap.containsKey(question.getPollId())) {
                questionMap.put(question.getPollId(), new ArrayList<>());
            }

            questionMap.get(question.getPollId()).add(question);
        }
        return questionMap;
    }
}
