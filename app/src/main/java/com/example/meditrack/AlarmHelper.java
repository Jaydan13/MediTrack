package com.example.meditrack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

public class AlarmHelper {

    // =========================
    // SET ALARM
    // =========================
    public static void setAlarm(
            Context context,
            String reminderId,
            int hour,
            int minute,
            String name,
            int dosage,
            String intervalNo,
            String intervalType,
            String durationNo,
            String durationType,
            long startTime
    ) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("reminderId", reminderId);
        intent.putExtra("name", name);
        intent.putExtra("dosage", dosage);
        intent.putExtra("intervalNo", intervalNo);
        intent.putExtra("intervalType", intervalType);
        intent.putExtra("durationNo", durationNo);
        intent.putExtra("durationType", durationType);
        intent.putExtra("startTime", startTime);

        int requestCode = reminderId.hashCode();

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                return;
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                pendingIntent
        );
    }

    // =========================
    // CANCEL ALARM
    // =========================
    public static void cancelAlarm(Context context, String reminderId) {

        Intent intent = new Intent(context, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}