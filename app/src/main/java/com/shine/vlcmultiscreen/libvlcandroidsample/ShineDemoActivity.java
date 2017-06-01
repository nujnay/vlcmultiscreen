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
public class ShineDemoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shine_demo);
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

//        path1 = "rtsp://admin:ab123456@172.168.1.14/surfix";
//        path1 = "rtsp://admin:ab123456@172.168.1.14/surfix";
        path1 = "shine_net://tcp@10.0.1.11:5554";
        path2 = "http://10.0.1.87:8080/111";
        path3 = "http://10.0.1.87:8080/111";
        path4 = "shine_net://tcp@10.0.1.11:5554";
        path5 = "http://10.0.1.153:5022";
        path6 = "http://10.0.1.153:5022";
        //rtsp://10.0.1.87:8554/111

        VideoView video1 = (VideoView) findViewById(R.id.video1);
        VideoView video2 = (VideoView) findViewById(R.id.video2);
        VideoView video3 = (VideoView) findViewById(R.id.video3);
        VideoView video4 = (VideoView) findViewById(R.id.video4);
//		VideoView video5 = (VideoView) findViewById(R.id.video5);
//		VideoView video6 = (VideoView) findViewById(R.id.video6);

        showVideoHard(path1, video1);
//        showVideoHard(path2, video2);
//        showVideoSoft(path3, video3);
//        showVideoSoft(path4, video4);
//		showVideo(path5, video5);
//		showVideo(path6, video6);

    }

    /**
     * showVideo1(这里用一句话描述这个方法的作用) void
     *
     * @throws
     * @since 1.0.0
     */
    private void showVideo(final String path, VideoView videoView) {
        final LibVLC libVLC = new LibVLC(this);
        Media mMedia = null;
        if (path.startsWith("http")) {
            mMedia = new Media(libVLC, path);
            Log.d("fileeeeee111", path);
        } else {
            mMedia = new  Media(libVLC, path);
            Log.d("fileeeeee222", path);
//            mMedia = new Media(libVLC, Uri.parse(path));
        }
        mMedia.setHWDecoderEnabled(false, false);
//		mMedia.setHttpoptimize();
        final MediaPlayer mMediaPlayer = new MediaPlayer(mMedia);

        IVLCVout vlcVout2 = mMediaPlayer.getVLCVout();
        vlcVout2.setVideoView(videoView);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        Log.i("dddddddd", "屏幕尺寸2：宽度 = " + w_screen + "高度 = " + h_screen + "密度 = " + dm.densityDpi);
//        vlcVout2.setWindowSize(w_screen / 2, h_screen / 2);
        float rate = ((float) 1920 / (float) 1080);
        float heitrue = ((float) w_screen / (float) 2) / rate;
        Log.i("dddddddd", "aaaaa " + rate + "ssss " + heitrue);
        if (h_screen > heitrue) {
            vlcVout2.setWindowSize(w_screen / 2, (int) heitrue);
        } else {
            float wtrue = ((float) h_screen / (float) 2) * rate;
            vlcVout2.setWindowSize((int) wtrue, h_screen / 2);
        }
        vlcVout2.attachViews();

        mMediaPlayer.setVideoTrackEnabled(true);
        mMediaPlayer.play();
        mMediaPlayer.setEventListener(new EventListener() {
            @Override
            public void onEvent(Event event) {
                switch (event.type) {

                    case MediaPlayer.Event.EncounteredError:
                    case MediaPlayer.Event.EndReached:
                        mMediaPlayer.stop();
                        Media mMedia;
                        if (path.startsWith("http")) {
                            mMedia = new Media(libVLC, Uri.parse(path));
                        } else {
                            mMedia = new Media(libVLC, path);
                        }

                         mMedia.setHWDecoderEnabled(true,true);
                        // mMedia.setHttpoptimize();

                        mMediaPlayer.setMedia(mMedia);
                        mMedia.release();
                        mMediaPlayer.play();
                        break;
                }

            }
        });
    }


    private void showVideoHard(final String path, VideoView videoView) {
        final LibVLC libVLC = new LibVLC(this);
        Media mMedia = null;
        if (path.startsWith("http")) {
            mMedia = new Media(libVLC, path);
            Log.d("fileeeeee111", path);
        } else {
            mMedia = new  Media(libVLC, path);
            Log.d("fileeeeee222", path);
//            mMedia = new Media(libVLC, Uri.parse(path));
        }
        mMedia.setHWDecoderEnabled(false, false);
//		mMedia.setHttpoptimize();
        final MediaPlayer mMediaPlayer = new MediaPlayer(mMedia);

        IVLCVout vlcVout2 = mMediaPlayer.getVLCVout();
        vlcVout2.setVideoView(videoView);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        Log.i("dddddddd", "屏幕尺寸2：宽度 = " + w_screen + "高度 = " + h_screen + "密度 = " + dm.densityDpi);
//        vlcVout2.setWindowSize(w_screen / 2, h_screen / 2);
        float rate = ((float) 1920 / (float) 1080);
        float heitrue = ((float) w_screen / (float) 2) / rate;
        Log.i("dddddddd", "aaaaa " + rate + "ssss " + heitrue);
        if (h_screen > heitrue) {
            vlcVout2.setWindowSize(w_screen / 2, (int) heitrue);
        } else {
            float wtrue = ((float) h_screen / (float) 2) * rate;
            vlcVout2.setWindowSize((int) wtrue, h_screen / 2);
        }
        vlcVout2.attachViews();

        mMediaPlayer.setVideoTrackEnabled(true);
        mMediaPlayer.play();
        mMediaPlayer.setEventListener(new EventListener() {
            @Override
            public void onEvent(Event event) {
                switch (event.type) {

                    case MediaPlayer.Event.EncounteredError:
                    case MediaPlayer.Event.EndReached:
                        mMediaPlayer.stop();
                        Media mMedia;
                        if (path.startsWith("http")) {
                            mMedia = new Media(libVLC, Uri.parse(path));
                        } else {
                            mMedia = new Media(libVLC, path);
                        }

                        mMedia.setHWDecoderEnabled(true,true);
                        // mMedia.setHttpoptimize();

                        mMediaPlayer.setMedia(mMedia);
                        mMedia.release();
                        mMediaPlayer.play();
                        break;
                }

            }
        });
    }

    private void showVideoSoft(final String path, VideoView videoView) {
        final LibVLC libVLC = new LibVLC(this);
        Media mMedia = null;
        if (path.startsWith("http")) {
            mMedia = new Media(libVLC, path);
            Log.d("fileeeeee111", path);
        } else {
            mMedia = new  Media(libVLC, path);
            Log.d("fileeeeee222", path);
//            mMedia = new Media(libVLC, Uri.parse(path));
        }
        mMedia.setHWDecoderEnabled(false, true);
//		mMedia.setHttpoptimize();
        final MediaPlayer mMediaPlayer = new MediaPlayer(mMedia);

        IVLCVout vlcVout2 = mMediaPlayer.getVLCVout();
        vlcVout2.setVideoView(videoView);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int w_screen = dm.widthPixels;
        int h_screen = dm.heightPixels;
        Log.i("dddddddd", "屏幕尺寸2：宽度 = " + w_screen + "高度 = " + h_screen + "密度 = " + dm.densityDpi);
//        vlcVout2.setWindowSize(w_screen / 2, h_screen / 2);
        float rate = ((float) 1920 / (float) 1080);
        float heitrue = ((float) w_screen / (float) 2) / rate;
        Log.i("dddddddd", "aaaaa " + rate + "ssss " + heitrue);
        if (h_screen > heitrue) {
            vlcVout2.setWindowSize(w_screen / 2, (int) heitrue);
        } else {
            float wtrue = ((float) h_screen / (float) 2) * rate;
            vlcVout2.setWindowSize((int) wtrue, h_screen / 2);
        }
        vlcVout2.attachViews();

        mMediaPlayer.setVideoTrackEnabled(true);
        mMediaPlayer.play();
        mMediaPlayer.setEventListener(new EventListener() {
            @Override
            public void onEvent(Event event) {
                switch (event.type) {

                    case MediaPlayer.Event.EncounteredError:
                    case MediaPlayer.Event.EndReached:
                        mMediaPlayer.stop();
                        Media mMedia;
                        if (path.startsWith("http")) {
                            mMedia = new Media(libVLC, Uri.parse(path));
                        } else {
                            mMedia = new Media(libVLC, path);
                        }

                        mMedia.setHWDecoderEnabled(true,true);
                        // mMedia.setHttpoptimize();

                        mMediaPlayer.setMedia(mMedia);
                        mMedia.release();
                        mMediaPlayer.play();
                        break;
                }

            }
        });
    }

    private void showVideo1(final Uri path, VideoView videoView) {
        final LibVLC libVLC = new LibVLC(this);
        Media mMedia = new Media(libVLC, path);
        // mMedia.setHWDecoderEnabled(true,true);
//		mMedia.setHttpoptimize();
        final MediaPlayer mMediaPlayer = new MediaPlayer(mMedia);

        IVLCVout vlcVout2 = mMediaPlayer.getVLCVout();
        vlcVout2.setVideoView(videoView);
        vlcVout2.attachViews();
        mMediaPlayer.setVideoTrackEnabled(true);
        mMediaPlayer.play();

        mMediaPlayer.setEventListener(new EventListener() {
            @Override
            public void onEvent(Event event) {
                switch (event.type) {

                    case MediaPlayer.Event.EncounteredError:
                    case MediaPlayer.Event.EndReached:
                        mMediaPlayer.stop();
                        Media mMedia = new Media(libVLC, path);
                        // mMedia.setHWDecoderEnabled(true,true);
                        // mMedia.setHttpoptimize();

                        mMediaPlayer.setMedia(mMedia);
                        mMedia.release();
                        mMediaPlayer.play();
                        break;
                }
            }
        });
    }
}
