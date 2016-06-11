package com.jinyufeili.minas.poll.data;

/**
 * Created by pw on 6/9/16.
 */
public class Answer {

    private int id;

    private AnswerResult result;

    private int questionId;

    private int voteSheetId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AnswerResult getResult() {
        return result;
    }

    public void setResult(AnswerResult result) {
        this.result = result;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getVoteSheetId() {
        return voteSheetId;
    }

    public void setVoteSheetId(int voteSheetId) {
        this.voteSheetId = voteSheetId;
    }
}
