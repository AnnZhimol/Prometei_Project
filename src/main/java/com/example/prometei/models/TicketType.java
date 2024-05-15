package com.example.prometei.models;

public enum TicketType {
    BUSINESS,
    ECONOMIC;

    public String getTicketType(){
        return this.name();
    }
}
