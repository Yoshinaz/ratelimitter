package com.agoda.rate.constant;

public enum RateConstants {
    Default("default"),
    HotelControl("hotels"),;
    public enum Hotel {
        City("city"),
        Room("room");;
        private String toString;
        Hotel(String toString) {
            this.toString = toString;
        }
        public String toString(){
            return toString;
        }
    }

    private String toString;
    RateConstants(String toString) {
        this.toString = toString;
    }
    public String toString(){
        return toString;
    }
}
