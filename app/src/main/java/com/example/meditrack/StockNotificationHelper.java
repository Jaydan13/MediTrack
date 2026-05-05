package com.example.meditrack;

import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class StockNotificationHelper {

    public static void sendLowStockNotification(Context context, String message) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, "low_stock_channel").setSmallIcon(R.drawable.warning)
                        .setContentTitle("Low Stock Alert").setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setPriority(NotificationCompat.PRIORITY_HIGH).setAutoCancel(true);

        NotificationManagerCompat.from(context).notify(1, builder.build());
    }
}
