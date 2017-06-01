package com.shine.vlcmultiscreen.libvlcandroidsample;


import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.VideoView;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.MediaPlayer.Event;
import org.videolan.libvlc.MediaPlayer.EventListener;

//import org.videolan.libvlc.IVLCVout;
//import org.videolan.libvlc.LibVLC;
//import org.videolan.libvlc.Media;
//import org.videolan.libvlc.MediaPlayer;


public class VideoActivity extends Activity {
    public final static String TAG = "LibVLCAndroidSample/VideoActivity";
    public final static String LOCATION = "com.compdigitec.libvlcandroidsample.VideoActivity.location";

    MediaPlayer mMediaPlayer;
    MediaPlayer mMediaPlayer2;
    TextureView textureView2;
    Media mMedia;
    Media mMedia2;
    SurfaceHolder v2SurfaceHolder;
    VideoView mMyGLSurfaceView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//		String video="/sdcard/SONG/index.mpg";
//		String video="/storage/extsd/231971.mpg";
//        String video = "shine_net://tcp@10.0.1.153:5020";
        String video = "shine_net://tcp@10.0.1.153:5020";


        //Uri video=Uri.parse("http:");
        Media mMedia = new Media(new LibVLC(this), video);
        mMedia.setHWDecoderEnabled(true, true);
//        mMedia.setHttpoptimize();
        mMediaPlayer = new MediaPlayer(mMedia);

        mMyGLSurfaceView = new VideoView(this);
        IVLCVout vlcVout2 = mMediaPlayer.getVLCVout();
        vlcVout2.setVideoView(mMyGLSurfaceView);
        vlcVout2.attachViews();

        ViewGroup.LayoutParams framelayout_params =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                        ViewGroup.LayoutParams.FILL_PARENT);
        FrameLayout mFrameLayout = new FrameLayout(this);
        mFrameLayout.setLayoutParams(framelayout_params);

        FrameLayout.LayoutParams cameraFL = new FrameLayout.LayoutParams(1920, 1080, Gravity.TOP);
        cameraFL.setMargins(0, 0, 0, 0);
        mMyGLSurfaceView.setLayoutParams(cameraFL);
        mFrameLayout.addView(mMyGLSurfaceView);

        this.addContentView(mFrameLayout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mMediaPlayer.setEventListener(new EventListener() {
            @Override
            public void onEvent(Event event) {

            }
        });
        mMediaPlayer.setVideoTrackEnabled(true);
        mMediaPlayer.play();
    }


}
