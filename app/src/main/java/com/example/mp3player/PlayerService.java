package com.example.mp3player;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;


public class PlayerService extends Service {
    NotificationManager notificationManager;
    private final IBinder binder = new MyBinder();
    MP3Player mp3 = new MP3Player();

    private final String CHANNEL_ID = "100";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("g53mdp", "Service Binded");

        //Return binder
        return binder;
    }

    public class MyBinder extends Binder {
        public void play() {
            mp3.play();
            Log.d("g53mdp", "Song Playing");
        }

        public void pause() {
            mp3.pause();
            Log.d("g53mdp", "Song Paused");
        }

        public void stop() {
            mp3.stop();
            mp3.filePath = null;
            Log.d("g53mdp", "Song Unloaded");
        }
        public void load(String filePath) {
            mp3.load(filePath);
            Log.d("g53mdp", "Song Loaded");
        }

        public int getProgess () {
            return mp3.getProgress();
        }

        public int getDuration () {
            return mp3.getDuration();
        }

        public String getFilePath() {
            return mp3.getFilePath();
        }
    }

    public void createNotification () {
        int NOTIFICATION_ID = 001;
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel name";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }

        //Create new intent
        Intent intent = new Intent(PlayerService.this, MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,
                CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("MP3 Player")
                .setContentText("Your are currently online, tap to open the player.")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}

