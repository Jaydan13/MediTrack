package com.example.meditrack;

public class RemindMed {

    private String id;
    private String name;
    private String dosage;
    private String time;
    private String interval;
    private String duration;

    public RemindMed() {

    }
    public RemindMed(String id, String name, String dosage, String time, String interval, String duration) {
        this.id = id;
        this.name = name;
        this.dosage = dosage;
        this.time = time;
        this.interval = interval;
        this.duration = duration;
    }

    public String getId() {return id;}
    public String getName() {return name;}
    public String getDosage() {return dosage;}
    public String getTime() {return time;}
    public String getInterval() {return interval;}
    public String getDuration() {return duration;}
}
