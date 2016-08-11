package com.jinyufeili.minas.crm.data;

/**
 * Created by pw on 6/9/16.
 */
public class Resident {

    private int id;

    private String name;

    private String mobilePhone;

    private int roomId;

    private int userId;

    private boolean verified;

    private int voteId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int getVoteId() {
        return voteId;
    }

    public void setVoteId(int voteId) {
        this.voteId = voteId;
    }

    @Override
    public String toString() {
        return "Resident{" + "id=" + id + ", name='" + name + '\'' + ", mobilePhone='" + mobilePhone + '\'' +
                ", roomId=" + roomId + ", userId=" + userId + ", verified=" + verified + ", voteId=" + voteId + '}';
    }
}
