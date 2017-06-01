package com.shine.vlcmultiscreen.libvlcandroidsample;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.shine.vlcmultiscreen.R;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

public class VerticalVideoActivity extends Activity {
    MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_video);
        SharedPreferences sp = getSharedPreferences("shine", MODE_PRIVATE);
        // 第一次把默认路径保存进去
        if (sp.getBoolean("flag", true)) {
            sp.edit()
                    .putString("path", "http://10.0.1.87:8080/111")
                    .putBoolean("flag", false).commit();

        }

//		String path="/storage/extsd/231971.mpg";
//		String path = sp.getString("path", "http://172.168.100.100:5000");
        String path = sp.getString("path", "http://10.0.1.87:8080/111");
        StapleGLSurfaceView surfaceView = (StapleGLSurfaceView) findViewById(R.id.surfaceView);
//		String path="http://172.168.1.153:5022";

//		Media mMedia=new Media(new LibVLC(this),Uri.parse(path));
        Media mMedia = null;
        if (path.startsWith("http")) {
            mMedia = new Media(new LibVLC(this), Uri.parse(path));
        } else {
            mMedia = new Media(new LibVLC(this), path);
        }
        mMedia.setHWDecoderEnabled(true, true);
//		mMedia.setHttpoptimize();
        mMediaPlayer = new MediaPlayer(mMedia);
        surfaceView.init(mMediaPlayer, false);
        mMediaPlayer.setVideoTrackEnabled(true);
        mMediaPlayer.play();

    }


}
