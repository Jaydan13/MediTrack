public class Medication {
    public String medname;
    public String dosage;
    public int hour;
    public int minute;
    public int interval;
    public String intervalType;

    public Medication() {
        //Required for Firebase
    }

    public Medication(String medname, String dosage, int hour, int minute, int interval, String intervalType) {
        this.medname = medname;
        this.dosage = dosage;
        this.hour = hour;
        this.minute = minute;
        this.interval = interval;
        this.intervalType = intervalType;
    }
}
