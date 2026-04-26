package com.example.meditrack;

public class RecordItem {

    private String name;
    private int dosage;
    private String date;
    private String time;
    private long timestamp;

    public RecordItem() {

    }
    public RecordItem(String name, int dosage, String date, String time, long timestamp) {
        this.name = name;
        this.dosage = dosage;
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
    }
    public String getName() {return name;}
    public int getDosage() {return dosage;}
    public String getDate() {return date;}
    public String getTime() {return time;}
    public long getTimestamp() {return timestamp;}
}
