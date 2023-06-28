package com.ggc.theaterkarten;

public class Time {
    private int hour, minute;

    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    @Override
    public String toString() {
        String sHour = String.valueOf(hour), sMinute = String.valueOf(minute);
        if(hour < 10)
            sHour = "0" +hour;
        if(minute < 10)
            sMinute = "0" + minute;
        return new String(sHour + ":" + sMinute);
    }
}
