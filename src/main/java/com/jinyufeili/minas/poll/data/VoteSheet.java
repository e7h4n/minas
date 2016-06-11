package com.jinyufeili.minas.poll.data;

/**
 * Created by pw on 6/9/16.
 */
public class VoteSheet {

    private int id;

    private long votedTime;

    private long createdTime;

    private boolean voted;

    private int pollId;

    private int residentId;

    private int roomId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getVotedTime() {
        return votedTime;
    }

    public void setVotedTime(long votedTime) {
        this.votedTime = votedTime;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public boolean isVoted() {
        return voted;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }

    public int getPollId() {
        return pollId;
    }

    public void setPollId(int pollId) {
        this.pollId = pollId;
    }

    public int getResidentId() {
        return residentId;
    }

    public void setResidentId(int residentId) {
        this.residentId = residentId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
}
