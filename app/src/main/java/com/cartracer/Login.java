package com.cartracer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import org.w3c.dom.Text;

public class Login extends AppCompatActivity implements View.OnClickListener {

    String mode;
    private EditText mail;
    private EditText pass;
    private TextView show;
    private TextView reg;
    private Button login;

//    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        Intent in=getIntent();
        mode=in.getStringExtra("mode");
        mail = (EditText) findViewById(R.id.mail);
        pass = (EditText) findViewById(R.id.pswrd);
        show=(TextView)findViewById(R.id.textView5);
        reg=(TextView)findViewById(R.id.reg);
        show.setText(mode.toUpperCase()+" LOGIN");
        login=(Button)findViewById(R.id.login);
//        firebaseAuth=FirebaseAuth.getInstance();
        login.setOnClickListener(this);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent snd=new Intent(Login.this,Register.class);
                if(mode.equalsIgnoreCase("user"))
                {
                    snd.putExtra("mode","user");
                    startActivity(snd);
                }
                else
                {
                    snd.putExtra("mode","car");
                    startActivity(snd);
                }
            }
        });
    }

    @Override
    public void onClick(View view)
    {
        final SharedPreferences sp1= getSharedPreferences("login", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edit =sp1.edit();
        String email = mail.getText().toString().trim();
        String paswrd = mail.getText().toString().trim();
        if (email.isEmpty() || paswrd.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Make Sure All Fields are Non Empty",Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
//            firebaseAuth.signInWithEmailAndPassword(email, paswrd)
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task)
//                        {
//                            if(task.isSuccessful())
//                            {
//                                Intent i =new Intent(Login.this,MainActivity.class);
//                                edit.putString("login","yes");
//                                edit.apply();
//                                if(mode.equalsIgnoreCase("user"))
//                                {
//                                    i.putExtra("mode","user");
//                                    startActivity(i);
//                                }
//                                else
//                                {
//                                    i.putExtra("mode","car");
//                                    startActivity(i);
//                                }
//                            }
//                            else
//                            {
//                                Toast.makeText(getApplicationContext(),"Credentials are Wrong, Try Again !!",Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });

        }
    }
    }

