package com.mynote.view;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.mynote.R;

public class SplashActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView tvSplash = findViewById(R.id.tv_splash);

        Typeface lTypeface;

        try{
            lTypeface = Typeface.createFromAsset(this.getAssets(), "fonts/Pacifico.ttf");
        }catch (Exception e){
            e.printStackTrace();
            lTypeface = Typeface.defaultFromStyle(Typeface.ITALIC);
        }

        tvSplash.setTypeface(lTypeface);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent lIntent = new Intent(SplashActivity.this, MainActivity.class);
                lIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(lIntent);
                finish();
            }
        }, 500);

    }
}
