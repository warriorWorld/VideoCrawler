package com.insightsurfface.videocrawler;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    private VideoView crawlerVv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        String url = "https://pptv.com-h-pptv.com/share/0a66e17d3ccde15bdecb253e7e293294";
        crawlerVv.setVideoURI(Uri.parse(url));
        MediaController mediaController = new MediaController(this);
        mediaController.setMediaPlayer(crawlerVv);
        crawlerVv.setMediaController(mediaController);
    }

    private void initUI() {
        crawlerVv = findViewById(R.id.crawler_vv);
    }
}
