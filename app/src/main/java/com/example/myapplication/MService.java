package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class MService extends Service {
    private static MediaPlayer mediaPlayer = null;
    private String filePath = "/storage/9E83-0C66/My_Music/";
    private int curId = 0;
    private volatile boolean isRunning = false;
    private File[] files = null;
    private static Thread thread = null;
    private List<Integer> randomArray = new ArrayList<>();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            File file = null;
            if(intent.getBooleanExtra("next", false)){
                if(files == null){
                    Toast.makeText(this, "Cac", Toast.LENGTH_SHORT).show();
                    return START_STICKY;
                }
            }
            else{
                randomArray.clear();
                filePath = "/storage/9E83-0C66/My_Music/";
                curId=0;
                if(intent.getBooleanExtra("bolero", false)){
                    filePath += "bolero/";
                }
                else if(intent.getBooleanExtra("soviet", false)){
                    filePath += "soviet/";
                }
                file = new File(filePath);
                files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    randomArray.add(i);
                }
                Collections.shuffle(randomArray);
            }
            if(thread != null){
                isRunning = false;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread = new Thread() {
                @Override
                public void run() {
                    isRunning = true;
                    while(isRunning){
                        if (curId >= files.length){
                            curId = 0;
                        }
                        Uri uri = Uri.parse(files[randomArray.get(curId)].toString());
                        mediaPlayer = MediaPlayer.create(MService.this, uri);
                        mediaPlayer.start();
                        while(mediaPlayer.isPlaying() && isRunning){};
                        stopPlaying();
                        curId++;
                    }
                }
            };
            thread.start();
        }
        return START_STICKY;
    }


    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
