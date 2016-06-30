/**
 * @(#)${FILE_NAME}.java, 6/30/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.poll.service;

import com.jinyufeili.minas.poll.data.Question;
import com.jinyufeili.minas.poll.storage.QuestionStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pw
 */
@Service
public class QuestionService {

    @Autowired
    private QuestionStorage questionStorage;

    public Map<Integer, List<Question>> queryByPollIds(Set<Integer> pollIds) {
        return questionStorage.queryByPoolIds(pollIds);
    }
}
