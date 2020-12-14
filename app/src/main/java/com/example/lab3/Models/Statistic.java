package com.example.lab3.Models;

public class Statistic {
    private String roomId, roomName, rivalId, myScore, rivalScore;
    private boolean winner;

    public Statistic(){}

    public Statistic(String roomId, String roomName, boolean winner, String rivalId, String myScore, String rivalScore){
        this.roomId = roomId;
        this.roomName = roomName;
        this.winner = winner;
        this.rivalId = rivalId;
        this.myScore = myScore;
        this.rivalScore = rivalScore;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRivalId() {
        return rivalId;
    }

    public void setRivalId(String rivalId) {
        this.rivalId = rivalId;
    }

    public String getMyScore() {
        return myScore;
    }

    public void setMyScore(String myScore) {
        this.myScore = myScore;
    }

    public String getRivalScore() {
        return rivalScore;
    }

    public void setRivalScore(String rivalScore) {
        this.rivalScore = rivalScore;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }
}
