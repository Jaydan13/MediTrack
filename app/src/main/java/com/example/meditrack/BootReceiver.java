package com.example.meditrack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() == null) return;

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (auth.getCurrentUser() == null) return;

            String userId = auth.getCurrentUser().getUid();

            db.collection("users").document(userId).collection("reminder").get().addOnSuccessListener(querySnapshot -> {
                for (var doc : querySnapshot) {
                    Map<String, Object> data = doc.getData();
                    String id = doc.getId();
                    String name = (String) data.get("name");
                    String dosage = (String) data.get("dosage");
                    String time = (String) data.get("time");
                    String interval = (String) data.get("interval");

                    if (time == null || !time.contains(":")) continue;

                    String[] split = time.split(":");
                    int hour = Integer.parseInt(split[0]);
                    int minute = Integer.parseInt(split[1]);

                    String[] intervalSplit = interval.split(" ");
                    String intervalNo = intervalSplit[0];
                    String intervalType = intervalSplit[1];

                    //Send data to Alarm Receiver
                    Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                    alarmIntent.putExtra("reminderId", id);
                    alarmIntent.putExtra("name", name);
                    alarmIntent.putExtra("dosage", dosage);
                    alarmIntent.putExtra("intervalNo", intervalNo);
                    alarmIntent.putExtra("intervalType", intervalType);

                    int requestCode = id.hashCode();

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            context,
                            requestCode,
                            alarmIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                    );

                    java.util.Calendar calendar = java.util.Calendar.getInstance();
                    calendar.set(java.util.Calendar.HOUR_OF_DAY, hour);
                    calendar.set(java.util.Calendar.MINUTE, minute);
                    calendar.set(java.util.Calendar.SECOND, 0);

                    if (calendar.before(java.util.Calendar.getInstance())) {
                        calendar.add(java.util.Calendar.DATE, 1);
                    }

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                    if (alarmManager != null) {
                        alarmManager.cancel(pendingIntent);
                        alarmManager.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(),
                                pendingIntent
                        );
                    }
                }
            });
        }
    }
}