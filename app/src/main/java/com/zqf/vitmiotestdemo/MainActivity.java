package com.zqf.vitmiotestdemo;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;

/**
 * class from 主界面
 * Created by zqf
 * Time 2017/7/25 15:43
 */
public class MainActivity extends Activity implements MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {

    private String video_path = "http://baobab.wdjcdn.com/145076769089714.mp4";
    private Uri mUri;
    private ProgressBar pb;
    private TextView downloadRateView, loadRateView;
    private CustomMediaController mCustomMediaController;
    private VideoView mVideoView;
    public static long mCurrent_position = 0;//当前播放的位置
    public static final int VIDEO_LAYOUT_ORIGIN = 0;//缩放参数，原始画面大小0。
    public static final int VIDEO_LAYOUT_SCALE = 1;//缩放参数，画面全屏1。

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = MainActivity.this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        mUri = Uri.parse(video_path);//将地址转化为Uri
        mVideoView.setVideoURI(mUri);//设置播放视频的地址
        mCustomMediaController.show(5000);//设置显示时间差
        mVideoView.setMediaController(mCustomMediaController);//设置媒体控制器。
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);//设置画质
        mVideoView.requestFocus();//获取焦点
        mVideoView.setBufferSize(512 * 1024);//设置缓冲大小（单位Byte）
        /**
         * 监听在有警告或错误信息时调用。例如：开始缓冲、缓冲结束、下载速度变化。
         */
        mVideoView.setOnInfoListener(this);
        /**
         * 监听在网络视频流缓冲变化时调用。
         */
        mVideoView.setOnBufferingUpdateListener(this);
        /**
         * 视频播放完成后调用。
         */
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                /**
                 * 在视频预处理完成后调用。在视频预处理完成后被调用。
                 * 此时视频的宽度、高度、宽高比信息已经获取到，
                 * 此时可调用seekTo让视频从指定位置开始播放。
                 */
                mp.setPlaybackSpeed(1.0f);
            }
        });
    }

    private void initView() {
        mVideoView = (VideoView) findViewById(R.id.vitamio_video);
        mCustomMediaController = new CustomMediaController(this, mVideoView, this);
        mCustomMediaController.setVideoName("此处可以设置视频名称");
        pb = (ProgressBar) findViewById(R.id.probar);
        downloadRateView = (TextView) findViewById(R.id.download_rate);
        loadRateView = (TextView) findViewById(R.id.load_rate);
    }


    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                //开始缓存事，执行暂停播放、加载、下载、缓存控件可见
                if (mVideoView.isPlaying()) {
                    Log.e("Tag", what + "---缓存flag----");
                    mVideoView.pause();
                    pb.setVisibility(View.VISIBLE);
                    downloadRateView.setVisibility(View.VISIBLE);
                    loadRateView.setVisibility(View.VISIBLE);
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                //缓存完成，执行继续播放；加载、下载、缓存控件不可见
                mVideoView.start();
                pb.setVisibility(View.GONE);
                downloadRateView.setVisibility(View.GONE);
                loadRateView.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                //缓存时显示下载速度
                //此时下载速度应该实时获取手机的网速。这以kb/s代替
                Log.e("Tag", what + "----下载flag---" + extra);
                downloadRateView.setText(extra + "kb/s" + "");
                break;
        }
        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        loadRateView.setText("缓冲" + percent + "%");
        Log.e("Tag", "+++++++" + percent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Tag", "onResume");
        if (mCurrent_position != 0) {
            mVideoView.seekTo(mCurrent_position);
            mVideoView.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Tag", "onPause");
        mCurrent_position = mVideoView.getCurrentPosition();
        Log.e("Tag", mCurrent_position + "");
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Tag", "onStop");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e("Tag", "onRestoreInstanceState");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("Tag", "onSaveInstanceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Tag", "onDestroy");
        //停止视频播放，并释放资源。
        mVideoView.stopPlayback();
        mCurrent_position = 0;
    }

    /**
     * 视频播放完成后调用。
     *
     * @param mp the MediaPlayer that reached the end of the file
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        mCurrent_position = 0;
    }

    /**
     * getRequestedOrientation获取横竖屏标志
     * -1 || 1--->竖屏
     * 0 --->横屏
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.e("Tag", "返回键。。。。" + getRequestedOrientation());
            int orient = getRequestedOrientation();
            if (orient == -1 || orient == 1) {
                finish();
            } else if (orient == 0) {
                //切换为竖屏
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 屏幕切换时
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.e("Tag", newConfig.orientation + "");
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏-->显示原始画面
            if (mVideoView != null) {
                mVideoView.setVideoLayout(VIDEO_LAYOUT_ORIGIN, 0);
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏-->显示全屏画面
            if (mVideoView != null) {
                mVideoView.setVideoLayout(VIDEO_LAYOUT_SCALE, 0);
            }
        }
        super.onConfigurationChanged(newConfig);
    }
}
