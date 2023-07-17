package com.example.dangerbehiviordetect.ui.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.dangerbehiviordetect.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.StyledPlayerView;

public class PlaybackActivity extends AppCompatActivity {


    private ExoPlayer mPlayer;
    private StyledPlayerView mPlayerView;
    private Button mBtnZoom;
    private boolean isFullscreen = false;

    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        mPlayerView = findViewById(R.id.player_view);
        mBtnZoom = findViewById(R.id.btn_zoom);
//         创建媒体播放器
        mPlayer = new ExoPlayer.Builder(this).build();
        mPlayerView.setPlayer(mPlayer);
//        mPlayerView.hideController();
//        mPlayerView.showController();
//        mPlayerView.setControllerShowTimeoutMs(1000);
//        PlayerView playerView;
//        mPlayerView.setShowRewindButton(true);
//        mPlayerView.show
//        准备视频源
        MediaItem mediaItem = MediaItem.fromUri(url);
        mPlayer.setMediaItem(mediaItem);
        mPlayer.prepare();
        mPlayer.play();




//        按钮放大缩小
        mBtnZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFullscreen();
            }
        });


//        双击视频放大缩小
        mPlayerView.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(PlaybackActivity.this,
                    new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onDoubleTap(MotionEvent e) {
                            toggleFullscreen();
                            return super.onDoubleTap(e);
                        }
                    });

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }


    //    视频放大缩小方法
    private void toggleFullscreen() {
        if (isFullscreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            getSupportActionBar().show();
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            getSupportActionBar().hide();
        }
        isFullscreen = !isFullscreen;
    }


    //    以下是生命周期
    @Override
    protected void onStart() {
        super.onStart();
        mPlayerView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPlayerView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.release();
    }


    //    注意：在使用ExoP注意：在使用ExoPlayer时，需要在Activity中保持屏幕常亮以避免视频播放过程中屏幕自动关闭。layer时，需要在Activity中保持屏幕常亮以避免视频播放过程中屏幕自动关闭。
    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}