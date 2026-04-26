package com.example.meditrack;

public class RecordPDF {

    private String name, date, time;
    private int dosage;

    // ✅ REQUIRED empty constructor (Firestore safety)
    public RecordPDF() {}

    public RecordPDF(String name, int dosage, String date, String time) {
        this.name = name;
        this.dosage = dosage;
        this.date = date;
        this.time = time;
    }

    public String getName() { return name; }
    public int getDosage() { return dosage; }
    public String getDate() { return date; }
    public String getTime() { return time; }
}