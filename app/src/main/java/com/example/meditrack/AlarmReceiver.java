package com.example.meditrack;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    FirebaseAuth mAuth;

    @Override
    public void onReceive(Context context, Intent intent) {

        //For Testing Purposes
        Log.d("ALARM_DEBUG", "Alarm RECEIVED");

        //Get data from Alarm Helper
        String reminderId = intent.getStringExtra("reminderId");
        String name = intent.getStringExtra("name");
        int dosage = intent.getIntExtra("dosage", 0);

        String intervalNoStr = intent.getStringExtra("intervalNo");
        String intervalType = intent.getStringExtra("intervalType");

        String durationNoStr = intent.getStringExtra("durationNo");
        String durationType = intent.getStringExtra("durationType");
        long startTime = intent.getLongExtra("startTime", 0);

        mAuth = FirebaseAuth.getInstance();

        //Validation
        if (mAuth.getCurrentUser() == null) {
            Log.d("ALARM_DEBUG", "User not logged in");
            return;
        }
        String userId = mAuth.getCurrentUser().getUid();

        if (durationNoStr == null || reminderId == null) return;

        int durationNo = Integer.parseInt(durationNoStr);

        //Calculating the last date for reminder
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
            return;
        }

        int intervalNo = 0;
        try {
            intervalNo = Integer.parseInt(intervalNoStr);
        } catch (Exception e) {
            intervalNo = 1;
        }

        createNotificationChannel(context);

        // For Taken Button
        Intent takenIntent = new Intent(context, TakenReceiver.class);
        takenIntent.putExtra("userId", userId);
        takenIntent.putExtra("reminderId", reminderId);
        takenIntent.putExtra("name", name);
        takenIntent.putExtra("dosage", dosage);
        takenIntent.putExtra("interval", intervalNoStr);
        takenIntent.putExtra("duration", durationNoStr);

        PendingIntent takenPendingIntent = PendingIntent.getBroadcast(
                context,
                reminderId.hashCode(),
                takenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Notification Alert
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "med_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Medicine Reminder")
                .setContentText(name + " - " + dosage)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .addAction(android.R.drawable.ic_menu_save, "Taken", takenPendingIntent);

        NotificationManagerCompat.from(context).notify(reminderId.hashCode(), builder.build());

        // Calculate Next Alarm based on Interval
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

        //Don't reschedule after duration ends
        if (calendar.getTimeInMillis() > endTime) {
            return;
        }

        //Set New Alarm
        Intent repeatIntent = new Intent(context, AlarmReceiver.class);
        repeatIntent.putExtra("reminderId", reminderId);
        repeatIntent.putExtra("name", name);
        repeatIntent.putExtra("dosage", dosage);
        repeatIntent.putExtra("intervalNo", intervalNoStr);
        repeatIntent.putExtra("intervalType", intervalType);
        repeatIntent.putExtra("durationNo", durationNoStr);
        repeatIntent.putExtra("durationType", durationType);
        repeatIntent.putExtra("startTime", startTime);

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