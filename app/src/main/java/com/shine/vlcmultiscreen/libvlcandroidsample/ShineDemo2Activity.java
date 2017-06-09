package com.shine.vlcmultiscreen.libvlcandroidsample;

import android.Manifest;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.VideoView;

import com.shine.vlcmultiscreen.R;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.MediaPlayer.Event;
import org.videolan.libvlc.MediaPlayer.EventListener;

import io.reactivex.functions.Consumer;

/**
 * <b>类 名：</b>ShineDemoActivity<br/>
 * <b>类描述：</b><br/>
 * <b>创建人：</b>hebl<br/>
 * <b>创建时间：</b>2016-11-20 上午11:14:47<br/>
 * <b>修改人：</b>hebl<br/>
 * <b>修改时间：</b>2016-11-20 上午11:14:47<br/>
 * <b>修改备注：</b><br/>
 * 可在应用的包名下Shared_pref 文件夹下的shine.xml 配置文件中修改视频源的路径
 *
 * @version 1.0.0<br/>
 */
public class ShineDemo2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shine_demo2);
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.WAKE_LOCK,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE,
                        Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.BIND_ACCESSIBILITY_SERVICE

                )
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        startPlay();
                    }
                });
    }

    private void startPlay() {

        String path1 = "";
        String path2 = "";
        String path3 = "";
        String path4 = "";
        String path5 = "";
        String path6 = "";
        String path7 = "";
        String path8 = "";
        String path9 = "";

        path1 = "rtsp://admin:NVOXBP@172.168.1.100";
        path2 = "rtsp://admin:KYNLHS@172.168.1.103";
        path3 = "rtsp://admin:EQRSQK@172.168.1.101";
        path4 = "rtsp://admin:NVOXBP@172.168.1.100";
        path5 = "http://172.168.1.153:5008";
        path6 = "http://172.168.1.153:5008";
        path7 = "http://172.168.1.153:5008";
        path8 = "http://172.168.1.153:5008";
        path9 = "http://172.168.1.153:5008";

        VideoView video1 = (VideoView) findViewById(R.id.video1);
        VideoView video2 = (VideoView) findViewById(R.id.video2);
//        VideoView video3 = (VideoView) findViewById(R.id.video3);
//        VideoView video4 = (VideoView) findViewById(R.id.video4);
//        VideoView video5 = (VideoView) findViewById(R.id.video5);
//        VideoView video6 = (VideoView) findViewById(R.id.video6);
//        VideoView video7 = (VideoView) findViewById(R.id.video7);
//        VideoView video8 = (VideoView) findViewById(R.id.video8);
//        VideoView video9 = (VideoView) findViewById(R.id.video9);

        showVideo(path1, video1);
        showVideo(path2, video2);
//        showVideo(path3, video3);
//        showVideo(path4, video4);
//        showVideo(path5, video5);
//        showVideo(path6, video6);
//       showVideo(path7, video7);
        //showVideo(path8, video8);
        //showVideo(path9, video9);

    }


    private void showVideo(final String path, VideoView videoView) {
        final LibVLC libVLC = new LibVLC(this);
        Media mMedia = null;
        if (path.startsWith("http")) {
            mMedia = new Media(libVLC, path);
        } else {
            mMedia = new Media(libVLC, path);
        }
        mMedia.setHWDecoderEnabled(true, true);
        final MediaPlayer mMediaPlayer = new MediaPlayer(mMedia);
        IVLCVout vlcVout2 = mMediaPlayer.getVLCVout();
        vlcVout2.setVideoView(videoView);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        float rate = ((float) 1920 / (float) 1080);
        float heitrue = ((float) w_screen) / rate;
        if (h_screen > heitrue) {
            vlcVout2.setWindowSize(w_screen / 2, (int) heitrue);
        } else {
            float wtrue = ((float) h_screen / (float) 2) * rate;
            vlcVout2.setWindowSize((int) wtrue, h_screen);
        }
        vlcVout2.attachViews();
        mMediaPlayer.setVideoTrackEnabled(true);
        mMediaPlayer.play();
        mMediaPlayer.setEventListener(new EventListener() {
            @Override
            public void onEvent(Event event) {
                switch (event.type) {
                    case Event.EncounteredError:
                    case Event.EndReached:
                        mMediaPlayer.stop();
                        Media mMedia;
                        if (path.contains("http")) {
                            Log.d("pathpath", path + "");
                            mMedia = new Media(libVLC, Uri.parse(path));
                        } else if (path.contains("rtsp")) {
                            mMedia = new Media(libVLC, Uri.parse(path));
                        } else {
                            mMedia = new Media(libVLC, path);
                        }
                        mMedia.setHWDecoderEnabled(true, true);
                        mMediaPlayer.setMedia(mMedia);
                        mMedia.release();
                        mMediaPlayer.play();
                        break;
                }
            }
        });
    }
}
