package sis.pewpew.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import sis.pewpew.R;
import sis.pewpew.tutorial.TutorialActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
        finish();
    }
}
