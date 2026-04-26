package com.example.meditrack;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String reminderId = intent.getStringExtra("reminderId");
        String name = intent.getStringExtra("name");
        String dosage = intent.getStringExtra("dosage");

        String intervalNoStr = intent.getStringExtra("intervalNo");
        String intervalType = intent.getStringExtra("intervalType");

        String durationNoStr = intent.getStringExtra("durationNo");
        String durationType = intent.getStringExtra("durationType");
        String startTimeStr = intent.getStringExtra("startTime");

        if (durationNoStr == null || startTimeStr == null || reminderId == null) return;

        long startTime = Long.parseLong(startTimeStr);
        int durationNo = Integer.parseInt(durationNoStr);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(startTime);

        if (durationType.equals("Days")) {
            endCalendar.add(Calendar.DAY_OF_MONTH, durationNo);
        }
        else if (durationType.equals("Weeks")) {
            endCalendar.add(Calendar.WEEK_OF_YEAR, durationNo);
        }
        else if (durationType.equals("Months")) {
            endCalendar.add(Calendar.MONTH, durationNo);
        }

        long endTime = endCalendar.getTimeInMillis();

        long now = System.currentTimeMillis();

        if (now >= endTime) {
            // Stop scheduling future alarms
            return;
        }

        int intervalNo = 0;
        try {
            intervalNo = Integer.parseInt(intervalNoStr);
        } catch (Exception e) {
            intervalNo = 1;
        }

        createNotificationChannel(context);

        // ---------------- TAKEN ACTION ----------------
        Intent takenIntent = new Intent(context, TakenReceiver.class);
        takenIntent.putExtra("reminderId", reminderId);
        takenIntent.putExtra("name", name);
        takenIntent.putExtra("dosage", dosage);

        PendingIntent takenPendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId.hashCode(),
                takenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // ---------------- NOTIFICATION ----------------
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "med_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Medicine Reminder")
                .setContentText(name + " - " + dosage)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(android.R.drawable.ic_menu_save, "Taken", takenPendingIntent);

        NotificationManagerCompat.from(context).notify(reminderId.hashCode(), builder.build());

        // ---------------- NEXT ALARM (REPEAT) ----------------
        Calendar calendar = Calendar.getInstance();

        if (intervalType != null) {
            if (intervalType.equals("Hours")) {
                calendar.add(Calendar.HOUR_OF_DAY, intervalNo);
            } else if (intervalType.equals("Days")) {
                calendar.add(Calendar.DAY_OF_MONTH, intervalNo);
            } else if (intervalType.equals("Weeks")) {
                calendar.add(Calendar.WEEK_OF_YEAR, intervalNo);
            }
        }

        if (calendar.getTimeInMillis() > endTime) {
            return; // ❌ don’t schedule beyond duration
        }

        Intent repeatIntent = new Intent(context, AlarmReceiver.class);
        repeatIntent.putExtra("reminderId", reminderId);
        repeatIntent.putExtra("name", name);
        repeatIntent.putExtra("dosage", dosage);
        repeatIntent.putExtra("intervalNo", intervalNoStr);
        repeatIntent.putExtra("intervalType", intervalType);
        repeatIntent.putExtra("durationNo", durationNoStr);
        repeatIntent.putExtra("durationType", durationType);
        repeatIntent.putExtra("startTime", startTimeStr);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId.hashCode(),
                repeatIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }

    private void createNotificationChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    "med_channel",
                    "MediTrack Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}