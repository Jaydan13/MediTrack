package com.example.meditrack;

public class Medication {
    public String medname, dosage, intervalType, durationType;
    public int hour, minute, interval, duration;

    public Medication() {
        //Required for Firebase
    }

    public Medication(String medname, String dosage, int hour, int minute, int interval, String intervalType, int duration, String durationType) {
        this.medname = medname;
        this.dosage = dosage;
        this.hour = hour;
        this.minute = minute;
        this.interval = interval;
        this.intervalType = intervalType;
        this.duration = duration;
        this.durationType = durationType;
    }
}
