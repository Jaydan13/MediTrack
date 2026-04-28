package com.example.meditrack;

public class RemindMed {

    private String id, name, time, interval, duration, intervalNo, intervalType, durationNo, durationType;
    private long startTime;
    private int dosage, hour, minute;

    public RemindMed() {

    }
    public RemindMed(String id, String name, int dosage, String time, String interval, String duration, String intervalNo, String intervalType, String durationNo, String durationType, long startTime, int hour, int minute) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.time = time;
        this.interval = interval;
        this.duration = duration;
        this.intervalNo = intervalNo;
        this.intervalType = intervalType;
        this.durationNo = durationNo;
        this.durationType = durationType;
        this.startTime = startTime;
        this.hour = hour;
        this.minute = minute;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {return id;}
    public String getName() {return name;}
    public int getDosage() {return dosage;}
    public String getTime() {return time;}
    public String getInterval() {return interval;}
    public String getDuration() {return duration;}
    public String getIntervalNo() { return intervalNo; }
    public String getIntervalType() { return intervalType; }
    public String getDurationNo() { return durationNo; }
    public String getDurationType() { return durationType; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) {this.startTime = startTime;}
    public int getHour() { return hour; }
    public int getMinute() { return minute; }
}
