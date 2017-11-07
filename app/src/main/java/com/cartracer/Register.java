package com.cartracer;

import android.app.ProgressDialog;
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
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private Button login;
    private EditText email;
    private EditText name;
    private EditText crno;
    private EditText crnm;
    private TextView appmode;
    private DatabaseReference details;
    String mode;
//    private FirebaseAuth firebaseAuth;
//    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        firebaseAuth= FirebaseAuth.getInstance();

        login=(Button)findViewById(R.id.logn);
        email=(EditText)findViewById(R.id.email);
        name=(EditText)findViewById(R.id.pass);
        crnm=(EditText)findViewById(R.id.carnm);
        crno=(EditText)findViewById(R.id.carno);
        appmode=(TextView)findViewById(R.id.textView3);

        login.setOnClickListener(this);
        Intent in=getIntent();
        mode=in.getStringExtra("mode");
        //Toast.makeText(this,mode,Toast.LENGTH_SHORT).show();

        if(mode.equalsIgnoreCase("user"))
            appmode.setText("User Register");
        else
            appmode.setText("Car Register");
        }

    @Override
    public void onClick(View view)
    {
        String mail=email.getText().toString().trim();
        String nme=name.getText().toString().trim();
        String carnm=crnm.getText().toString().trim();
        String carno=crno.getText().toString().trim();
        final SharedPreferences sp1= getSharedPreferences("login", Context.MODE_PRIVATE);
        final SharedPreferences.Editor edit =sp1.edit();

        if(mail.isEmpty()|| nme.isEmpty() || carnm.isEmpty() || carno.isEmpty())
        {
            Toast.makeText(this,"Make Sure All Fields are Non Empty",Toast.LENGTH_SHORT).show();
            return;
        }
//        appmode.setText(mail+"\n"+pass);
//        progressDialog.setMessage("Registering...");
//        progressDialog.show();
//        firebaseAuth.createUserWithEmailAndPassword(mail,pass)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(task.isSuccessful())
//                {
//                    Toast.makeText(Register.this,"Registeration Successfull",Toast.LENGTH_SHORT).show();
//                    Intent i =new Intent(Register.this,MainActivity.class);
//                    edit.putString("login","yes");
//                    edit.apply();
//                    if(mode.equalsIgnoreCase("user"))
//                    {
//                        i.putExtra("mode","user");
//                        startActivity(i);
//                    }
//                    else
//                    {
//                        i.putExtra("mode","car");
//                        startActivity(i);
//                    }
//                }
//                else
//                    Toast.makeText(Register.this,"Try Again "+task.getException(),Toast.LENGTH_SHORT).show();
//            }
//        });
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        details = database.getReference().child("details");
        DatabaseReference newRef = details.child(carno);
        newRef.child("username").setValue(nme);
        newRef.child("email").setValue(mail);
        newRef.child("carname").setValue(carnm);
        newRef.child("carno").setValue(carno);
            Toast.makeText(Register.this,"Registeration Successfull",Toast.LENGTH_SHORT).show();
            Intent i =new Intent(Register.this,MainActivity.class);
            edit.putString("login","yes");
            edit.apply();
            Intent tocar =new Intent(Register.this,MainActivity.class);
            edit.putString("login","yes");
            edit.apply();
        if(mode.equalsIgnoreCase("user"))
            {
                i.putExtra("mode","user");
                startActivity(i);
            }
            else
            {
                i.putExtra("mode","car");
                startActivity(tocar);
            }
        }
    }

