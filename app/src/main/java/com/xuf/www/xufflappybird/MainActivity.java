package com.xuf.www.xufflappybird;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by Xuf on 2015/8/28.
 */
public class MainActivity extends Activity{

    public static final String EXTRA_IS_SPEED_MODE = "is_speed_mode";

    private ProgressBar mBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBar = (ProgressBar)findViewById(R.id.pb_loading);

        findViewById(R.id.btn_normal_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra(EXTRA_IS_SPEED_MODE, false);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_speed_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra(EXTRA_IS_SPEED_MODE, true);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onPause();
        mBar.setVisibility(View.INVISIBLE);
    }
}
