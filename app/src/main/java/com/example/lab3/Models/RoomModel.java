package com.example.lab3.Models;

import com.example.lab3.Enums.ChessColor;

public class RoomModel {
    private final String roomName;
    private final String roomId;
    private final ChessColor player;

    public RoomModel(String name, String id, ChessColor player){
        roomName = name;
        roomId = id;
        this.player = player;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomId() {
        return roomId;
    }

    public ChessColor getPlayer() {
        return player;
    }
}