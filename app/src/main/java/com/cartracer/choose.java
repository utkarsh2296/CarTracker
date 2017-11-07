package com.cartracer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class choose extends AppCompatActivity {

    private ImageButton user;
    private ImageButton car;
//    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        final SharedPreferences sp= getSharedPreferences("mode", Context.MODE_PRIVATE);
        final SharedPreferences sp1= getSharedPreferences("login", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edit =sp.edit();
        String mode=sp.getString("mode","");
        String logdin=sp.getString("login","");
//        firebaseAuth=FirebaseAuth.getInstance();
        Toast.makeText(this,mode,Toast.LENGTH_LONG).show();
//        if(firebaseAuth.getCurrentUser()==null)
//        {
//
//        }
        if(mode.equalsIgnoreCase("user"))// && firebaseAuth.getCurrentUser()!=null)//logdin.equalsIgnoreCase("yes"))
        {
            Intent i=new Intent(this,MainActivity.class);
            i.putExtra("mode","user");
            this.startActivity(i);
        }
        else if (mode.equalsIgnoreCase("car"))// && firebaseAuth.getCurrentUser()!=null)//logdin.equalsIgnoreCase("yes"))
        {
            Intent i=new Intent(this,Car.class);
            i.putExtra("mode","car");
            this.startActivity(i);
        }
        else
        {
            user=(ImageButton)findViewById(R.id.usermode);
            car=(ImageButton)findViewById(R.id.carmode);
            user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    edit.putString("mode","user");
                    edit.apply();
                    Intent i=new Intent(choose.this,Register.class);
                    i.putExtra("mode","user");
                    startActivity(i);
                }
            });
            car.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    edit.putString("mode","car");
                    edit.apply();
                    Intent i=new Intent(choose.this,Register.class);
                    i.putExtra("mode","car");
                    startActivity(i);
                }
            });

        }

    }
}
