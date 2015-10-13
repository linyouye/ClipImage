package com.linyouye.clipimage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private ClipImageView mClipImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClipImageView = (ClipImageView) findViewById(R.id.clipImageView);
        mClipImageView.setImageResource(R.drawable.image);
    }


}
