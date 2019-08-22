package com.example.familypedigree;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Administrator on 2017/6/7.
 */
public class TwoActivity extends Activity{


    HorizontalProgressBar horizontalProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        horizontalProgressBar= (HorizontalProgressBar) findViewById(R.id.hpb_view);
    }
}
