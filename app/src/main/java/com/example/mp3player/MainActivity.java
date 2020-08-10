package com.example.mp3player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private PlayerService.MyBinder playerService = null;
    Intent intent;

    //Initialise values
    String currentSong;
    int songProgressTime;
    int songDurationTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Bind the PlayerService
        Log.d("g53mdp", "Service About To Create");

        intent = new Intent(MainActivity.this, PlayerService.class);

        startService(intent);
        this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        //Show list of songs
        showMusicList();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("g53mdp", "Service Connected");
            playerService = (PlayerService.MyBinder) service;

            SeekBar musicProgress = findViewById(R.id.songProgressBar);
            musicProgress.setEnabled(false);
            musicProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    updateSongTime();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            ProgressUpdater progressUpdater = new ProgressUpdater(playerService, musicProgress);

            updateScreen();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("g53mdp", "Service Disconnected");
            playerService = null;
        }
    };

    protected void showMusicList () {
        final ListView lv = (ListView) findViewById(R.id.musicList);
        File musicDir = new File(Environment.getExternalStorageDirectory().getPath()+ "/Music/");
        File[] list = musicDir.listFiles();


        if (list != null) {
            lv.setAdapter(new ArrayAdapter<File>(this, android.R.layout.simple_list_item_1, list));
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                File selectedFromList =(File) (lv.getItemAtPosition(myItemInt));
                Log.d("g53mdp", selectedFromList.getAbsolutePath());

                //Stop other songs and load new song
                playerService.stop();
                playerService.load(selectedFromList.getAbsolutePath());

                //Save current song and set text
                currentSong = selectedFromList.getAbsolutePath();
                updateScreen();
            }
        });
    }

    public void playButton (View v){
        playerService.play();
    }

    public void pauseButton (View v){
        playerService.pause();
    }

    public void stopButton (View v){
        playerService.stop();
        updateScreen();
    }

    public int getProgress() {
        return  playerService.getProgess();
    }

    public int getDuration() {
        return  playerService.getDuration();
    }

    public void updateScreen() {
        updateSongName();
        updateSongTime();
    }

    public void updateSongName() {
        //Set Current Song Text
        currentSong = playerService.getFilePath();

        //Set it to no song selected if null
        if (currentSong == null) {
            currentSong = "No Song Selected";
        }

        final TextView currentSongText = findViewById(R.id.currentSong);
        currentSongText.setText(currentSong);
    }

    public void updateSongTime() {
        songProgressTime = 0;
        songDurationTime = 0;

        //Set it to no song selected if null
        if (currentSong != null) {
            songProgressTime = getProgress() / 1000;
            songDurationTime = getDuration() / 1000;
        }

        TextView songTimes = findViewById(R.id.songTimes);
        songTimes.setText(songProgressTime + "s | " + songDurationTime + "s");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serviceConnection != null) {
            unbindService(serviceConnection);
            serviceConnection = null;

            Log.d("g53mdp", "Activity Destroyed");
        }
    }
}



