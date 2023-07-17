package com.example.dangerbehiviordetect.ui.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.FrameLayout;

import com.example.dangerbehiviordetect.R;

import cn.nodemedia.NodePlayer;

public class MonitorActivity extends AppCompatActivity{
    private NodePlayer np;
    private FrameLayout vv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_monitor);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        vv = findViewById(R.id.video_view);
        np = new NodePlayer(this, "");
        np.attachView(vv);

        np.setBufferTime(100);
        np.start(url);
//        Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
//        Log.d(TAG, String.format("MonitorActivity: %s", url));
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(np != null){
            np.detachView();
            np.stop();
        }

    }


}