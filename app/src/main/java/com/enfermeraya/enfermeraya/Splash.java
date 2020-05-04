package com.enfermeraya.enfermeraya;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.enfermeraya.enfermeraya.views.MainActivity;


public class Splash extends Activity {

    private ImageView logo;
    private static int splashTimeOut=3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        logo=(ImageView)findViewById(R.id.logo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        },splashTimeOut);

        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.splashanimation);
        logo.startAnimation(myanim);
    }
}
