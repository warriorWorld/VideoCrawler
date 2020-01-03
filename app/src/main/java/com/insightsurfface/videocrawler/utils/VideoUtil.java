package com.insightsurfface.videocrawler.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

public class VideoUtil {
    public static Bitmap getVideoThumbnail(Context context,Uri uri) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(context, uri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            int seconds = Integer.valueOf(time) / 1000;
            int timeS = Integer.valueOf(time) / 10;
            for (int i = 1; i <= 10; i++) {
                bitmap = retriever.getFrameAtTime(i * timeS * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }
}
