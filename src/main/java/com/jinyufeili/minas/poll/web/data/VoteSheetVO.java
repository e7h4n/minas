/**
 * @(#)${FILE_NAME}.java, 6/30/16.
 * <p/>
 * Copyright 2016 fenbi.com. All rights reserved.
 * FENBI.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.jinyufeili.minas.poll.web.data;

import com.jinyufeili.minas.account.web.data.UserVO;
import com.jinyufeili.minas.crm.data.Resident;
import com.jinyufeili.minas.crm.data.Room;
import com.jinyufeili.minas.poll.data.Poll;

import java.util.List;

/**
 * @author pw
 */
public class VoteSheetVO {

    private int id;

    private Poll poll;

    private UserVO user;

    private Resident resident;

    private Room room;

    private List<QuestionWithAnswerVO> questions;

    private boolean voted;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public UserVO getUser() {
        return user;
    }

    public void setUser(UserVO user) {
        this.user = user;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public List<QuestionWithAnswerVO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionWithAnswerVO> questions) {
        this.questions = questions;
    }

    public boolean isVoted() {
        return voted;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }
}
