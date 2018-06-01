package in.yefindia.yefadmin.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import in.yefindia.yefadmin.R;

public class SplashScreenActivity extends AppCompatActivity {

    public static final int SPLASH_SCREEN_TIME=3;   // Specifies time of splash screen in seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, SignInActivity.class));
                finish();
            }
        }, SPLASH_SCREEN_TIME * 1000);
    }
}
