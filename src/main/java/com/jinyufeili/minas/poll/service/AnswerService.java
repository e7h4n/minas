/**
 * @(#)${FILE_NAME}.java, 6/30/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.poll.service;

import com.jinyufeili.minas.poll.data.Answer;
import com.jinyufeili.minas.poll.storage.AnswerStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author pw
 */
@Service
public class AnswerService {

    @Autowired
    private AnswerStorage answerStorage;

    public Map<Integer, List<Answer>> queryByVoteSheetIds(Collection<Integer> voteSheetIds) {
        return answerStorage.queryByVoteSheetIds(voteSheetIds);
    }
}
