package com.cartracer;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Launch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run()
            {
                Intent i=new Intent(Launch.this,choose.class);
                startActivity(i);
                finish();
            }
        }, 3000);
    }
}
