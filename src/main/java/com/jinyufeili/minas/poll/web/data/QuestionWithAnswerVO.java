/**
 * @(#)${FILE_NAME}.java, 6/30/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.poll.web.data;

import com.jinyufeili.minas.poll.data.AnswerResult;

/**
 * @author pw
 */
public class QuestionWithAnswerVO {

    private int id;

    private String content;

    private AnswerResult result;

    private int pollId;

    private int voteSheetId;

    private int answerId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public AnswerResult getResult() {
        return result;
    }

    public void setResult(AnswerResult result) {
        this.result = result;
    }

    public int getPollId() {
        return pollId;
    }

    public void setPollId(int pollId) {
        this.pollId = pollId;
    }

    public int getVoteSheetId() {
        return voteSheetId;
    }

    public void setVoteSheetId(int voteSheetId) {
        this.voteSheetId = voteSheetId;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }
}
