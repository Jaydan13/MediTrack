package com.example.meditrack;

public class RecordItem {

    private String name;
    private String dosage;
    private String date;
    private String time;

    public RecordItem() {

    }
    public RecordItem(String name, String dosage, String date, String time) {
        this.name = name;
        this.dosage = dosage;
        this.date = date;
        this.time = time;
    }
    public String getName() {
        return name;
    }
    public String getDosage() {
        return dosage;
    }
    public String getDate() {
        return date;
    }
    public String getTime() {
        return time;
    }
}
