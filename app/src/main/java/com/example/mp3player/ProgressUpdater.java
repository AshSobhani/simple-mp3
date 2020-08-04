package com.example.mp3player;

import android.util.Log;
import android.widget.SeekBar;

public class ProgressUpdater extends Thread implements Runnable {
    public boolean playing = true;

    SeekBar songProgress;
    PlayerService.MyBinder mp3Player;

    public ProgressUpdater(PlayerService.MyBinder mp3Player, SeekBar songProgress) {
        this.mp3Player = mp3Player;
        this.songProgress = songProgress;
        this.start();
    }

    public void run() {
        int songProg;

        while(this.playing) {
            try {Thread.sleep(250);} catch(Exception e) {return;}

            //Update seek bar
            songProg = (int)(((float) mp3Player.getProgess() / (float) mp3Player.getDuration()) * 10000);
            songProgress.setProgress(songProg);

//            Log.d("g53mdp", "Song Progress (Maths): " + songProg);
//            Log.d("g53mdp", "Song Progress: " + mp3Player.getProgess());
//            Log.d("g53mdp", "Song Duration: " + mp3Player.getDuration());
        }
        Log.d("g53mdp", "Progress updater thread exiting");
    }
}
