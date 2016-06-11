package com.jinyufeili.minas.poll.data;

/**
 * Created by pw on 6/9/16.
 */
public class Question {

    private int id;

    private String content;

    private int pollId;

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

    public int getPollId() {
        return pollId;
    }

    public void setPollId(int pollId) {
        this.pollId = pollId;
    }
}
