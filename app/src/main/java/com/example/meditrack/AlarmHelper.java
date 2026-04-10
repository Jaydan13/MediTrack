package com.example.meditrack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmHelper {
    public static void setAlarm (Context context, Medication med, String id) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("name,", med.medname);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, id.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, med.hour);
        calendar.set(Calendar.MINUTE, med.minute);

        long intervalMillis;

        if (med.intervalType.equals("Hours")) {
            intervalMillis = med.interval * 60 * 60 * 1000;
        } else {
            intervalMillis = med.interval * 24 * 60 * 60 * 1000;
        }

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), intervalMillis, pendingIntent
        );
    }
}