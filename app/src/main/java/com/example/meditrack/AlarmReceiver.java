package com.example.meditrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.media.MediaPlayer;
import android.os.Build;
import android.app.PendingIntent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.example.meditrack.ALARM_ACTION".equals(intent.getAction())) {
            String planName = intent.getStringExtra("planName");
            String planId = intent.getStringExtra("planId");
            String time = intent.getStringExtra("time");
            String alarmTone = intent.getStringExtra("alarmTone");

            // Intent for notification to open activity with options
            Intent notifyIntent = new Intent(context, Today.class);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Ensure the PendingIntent is created with the right flags
            int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
            PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                    context, planId.hashCode(), notifyIntent, flags);

            // Build notification
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = "plan_channel_id";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        channelId, "Plan Reminders", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.alarm)
                    .setContentTitle("Plan Reminder: " + planName)
                    .setContentText("Time for your plan at " + time)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSound(alarmSound)
                    .setContentIntent(notifyPendingIntent)
                    .setAutoCancel(true);

            // Notify
            notificationManager.notify(0, builder.build());
            int alarmToneResId = getAlarmToneResId(alarmTone);
            if (alarmToneResId != 0) {
                MediaPlayer mediaPlayer = MediaPlayer.create(context, alarmToneResId);
                mediaPlayer.start();
            }

        }
    }

    private int getAlarmToneResId(String alarmToneName) {
        switch (alarmToneName) {
            case "Wake up!":
                return R.raw.wake_up;
            case "Alarm":
                return R.raw.alarm;
            case "Warning":
                return R.raw.warning;
            case "Banana":
                return R.raw.banana_song;
            case "Rooster":
                return R.raw.rooster_alarm;
            case "Bird Chirping":
                return R.raw.bird_chirping;

            default:
                return 0;
        }
    }
}
