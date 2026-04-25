package com.example.meditrack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String name = intent.getStringExtra("name");
        String dosage = intent.getStringExtra("dosage");

        String intervalNoStr = intent.getStringExtra("intervalNo");
        String intervalType = intent.getStringExtra("intervalType");

        int intervalNo = Integer.parseInt(intervalNoStr);

        // ---------------- NOTIFICATION ----------------
        Intent takenIntent = new Intent(context, TakenReceiver.class);
        takenIntent.putExtra("name", name);
        takenIntent.putExtra("dosage", dosage);

        PendingIntent takenPendingIntent = PendingIntent.getBroadcast(
                context,
                (int) System.currentTimeMillis(),
                takenIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "med_channel")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Medicine Reminder")
                .setContentText(name + " - " + dosage)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(android.R.drawable.ic_menu_save, "Taken", takenPendingIntent);

        NotificationManagerCompat.from(context).notify((int) System.currentTimeMillis(), builder.build());

        // ---------------- REPEAT LOGIC ----------------
        Calendar calendar = Calendar.getInstance();

        if (intervalType.equals("Hours")) {
            calendar.add(Calendar.HOUR_OF_DAY, intervalNo);
        } else if (intervalType.equals("Days")) {
            calendar.add(Calendar.DAY_OF_MONTH, intervalNo);
        } else if (intervalType.equals("Weeks")) {
            calendar.add(Calendar.WEEK_OF_YEAR, intervalNo);
        }

        Intent repeatIntent = new Intent(context, AlarmReceiver.class);
        repeatIntent.putExtra("name", name);
        repeatIntent.putExtra("dosage", dosage);
        repeatIntent.putExtra("intervalNo", intervalNoStr);
        repeatIntent.putExtra("intervalType", intervalType);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) System.currentTimeMillis(),
                repeatIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
        }
    }
}