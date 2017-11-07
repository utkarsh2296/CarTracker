package com.cartracer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.config.LocationParams;

import static android.location.Location.distanceBetween;

public class MainActivity extends AppCompatActivity implements SensorEventListener, OnLocationUpdatedListener {

    private TextView xAxisValue;
    private TextView yAxisValue;
    private TextView disp;
    private TextView zAxisValue;
    private TextView latitudeValue;
    private TextView longitudeValue;
    private TextView safelabel;
    private TextView dist;
    private TextView unsafe;
    private TextView car_cond;
    private Button maps;

    private double curr_lat;
    private double curr_long;
    private double Current_dist;
//    MediaPlayer alertSound;
    private double last_car_lat;
    private double last_car_long;
    private double car_to_car_dist;
    private Long status;
    private String state_of_car;
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private boolean isSensorAvailable = false;

    private DatabaseReference accelerometerRef;
    private DatabaseReference locationRef;
    private DatabaseReference car_locRef;

    private boolean isLocationUpdateStarted = false;
    public static final int LOCATION_REQUEST_CODE = 179;
    //private static int points=5000;
    private Queue X;
    private Queue Y;
    private Queue Z;
    private DatabaseReference phoneRef;
    private DatabaseReference carRef;
    private static final int SENSOR_DELAY = 0;
    private  int frequency = 8000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xAxisValue = findViewById(R.id.x_axis_value);
        yAxisValue = findViewById(R.id.y_axis_value);
        zAxisValue = findViewById(R.id.z_axis_value);
        latitudeValue = findViewById(R.id.latitude_value);
        longitudeValue = findViewById(R.id.longitude_value);
        disp=findViewById(R.id.disp);
        dist=findViewById(R.id.distance);
        safelabel=findViewById(R.id.stats);
        unsafe=findViewById(R.id.unsafe);
        car_cond=findViewById(R.id.condition);
        maps = findViewById(R.id.maps);
        Intent old=getIntent();
        String mode=old.getStringExtra("mode");
        if(mode.equalsIgnoreCase("user"))
            disp.setText("User Application");
        else
            disp.setText("Car Application");

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGoogleMaps();
            }
        });
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometerSensor!=null) {
            isSensorAvailable = true;

        } else {
            Toast.makeText(this, "Accelerometer not available", Toast.LENGTH_SHORT).show();
        }
        X=new LinkedList();
        Y=new LinkedList();
        Z=new LinkedList();
        final MediaPlayer alertSound=MediaPlayer.create(getApplicationContext(),R.raw.high_alert);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        phoneRef = database.getReference().child(mode);
        accelerometerRef = phoneRef.child("accelerometer");
        locationRef=phoneRef.child("location");
        Log.d("key",accelerometerRef.toString());
        DatabaseReference stat=phoneRef.child("status");
        DatabaseReference car_state=database.getReference().child("car").child("state");
        Log.d("status path",stat.toString());
        stat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                status=dataSnapshot.getValue(Long.class);
                Log.d("status",""+status);
                if(status!=3 && status!=5)
                {
                    unsafe.setText(null);
                    car_cond.setText("Everything is FINE");
                    car_cond.setTextColor(getResources().getColor(R.color.safe));
                    safelabel.setText("Your Vehicle is SAFE");
                }
                else if(status ==3 || status==5)
                {
                    safelabel.setText(null);
                    Car_unsafe(status);
                }

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                status=dataSnapshot.getValue(Long.class);
                Log.d("status",""+status);
                if(status!=3 && status!=5)
                {
                    dist.setText(null);
                    unsafe.setText(null);
                    car_cond.setText("Everything is FINE");
                    car_cond.setTextColor(getResources().getColor(R.color.safe));
                    safelabel.setText("Your Vehicle is SAFE :"+status);
                }
                else if(status ==3 || status==5)
                {
                    safelabel.setText(null);
                    Car_unsafe(status);

                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        car_state.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                state_of_car=dataSnapshot.getValue(String.class);
                Log.d("state",state_of_car);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                state_of_car=dataSnapshot.getValue(String.class);
                Log.d("state",state_of_car);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void openGoogleMaps() {
        final String BASE_URI = "https://www.google.com/maps";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(BASE_URI));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(getPackageManager()) != null) startActivity(intent);
    }
    private void Car_unsafe(Long s)
    {

        FirebaseDatabase databse = FirebaseDatabase.getInstance();
        unsafe.setText("Your Vehicle is NOT SAFE :"+s);
        carRef = databse.getReference().child("car");
        car_locRef = carRef.child("location");
        Log.d("car path",car_locRef.toString());
        car_locRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                float[]results=new float[1];
                float[]car_res=new float[1];

                Loc loc=dataSnapshot.getValue(Loc.class);
                Log.d("CAR loc Obj",""+dataSnapshot.toString() + " long:" + loc.longitude);
                double lat=loc.latitude;
                double lng=loc.longitude;
//                if(last_car_long==0) {
//                    last_car_lat = lat;
//                    last_car_long = lng;
//                }
//                else
//                {
//                    distanceBetween(last_car_lat,last_car_long,lat,lng,car_res);
//                    car_to_car_dist =car_res[0];
//                }
                Log.d("car loc","lat "+lat+" longitude "+lng);
                if(lat>0 && lng>0 && (status==3 || status==5))
                {
                    Log.d("user current loc","lat "+curr_lat+" longitude"+curr_long);
                    Log.d("car loc","lat "+lat+" longitude "+lng);
                    distanceBetween(curr_lat,curr_long,lat,lng,results);
                    double distance=results[0]/1000;

//                Location startPoint=new Location("locationA");
//                startPoint.setLatitude(curr_lat);
//                startPoint.setLongitude(curr_long);
//
//                Location endPoint=new Location("locationB");
//                endPoint.setLatitude(lat);
//                endPoint.setLongitude(lng);
//
//                double distance=startPoint.distanceTo(endPoint);

                    Log.d("dist",""+distance);
                    if(Current_dist==0)
                    Current_dist=distance;
                    else if(Current_dist!=distance && distance>1)
                    {
                        Current_dist = distance;
                        Log.d("old loc", "" + Current_dist + " new " + distance);
                        if (state_of_car.equals("ON")){
                            car_cond.setText("CAR IS BEING STOLEN");
                            car_cond.setTextColor(getResources().getColor(R.color.unsafe));
                        }
                        else if(state_of_car.equals("OFF")) {
                            car_cond.setText(("CAR IS BEING TOED"));
                            car_cond.setTextColor(getResources().getColor(R.color.unsafe));
                        }
                        final MediaPlayer alertSound=MediaPlayer.create(getApplicationContext(),R.raw.high_alert);
                        alertSound.start();
                        alertSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                           @Override
                           public void onCompletion(MediaPlayer mediaPlayer) {
                               alertSound.release();
                           }
                       });
                        dist.setText("Car is moving Distance from You is"+Math.round(Current_dist)+" m");
//                        Toast.makeText(getApplicationContext(),"Car is moving Distance from You is"+Current_dist,Toast.LENGTH_SHORT).show();
                    }
                }
                return;
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("Changed: CAR loc Obj",""+dataSnapshot.toString());
                float[]results=new float[1];
                Loc loc=dataSnapshot.getValue(Loc.class);
                Log.d("CAR loc Obj",""+dataSnapshot.toString() + " long:" + loc.longitude);
                double lat=loc.latitude;
                double lng=loc.longitude;
                Log.d("car loc","lat "+lat+" longitude "+lng);
                if(lat>0 && lng>0 & (status==3 || status==5))
                {
                    Log.d("user current loc","lat "+curr_lat+" longitude"+curr_long);
                    Log.d("car loc","lat "+lat+" longitude "+lng);
                    distanceBetween(curr_lat,curr_long,lat,lng,results);
                    double distance=results[0]/1000;

//                Location startPoint=new Location("locationA");
//                startPoint.setLatitude(curr_lat);
//                startPoint.setLongitude(curr_long);
//
//                Location endPoint=new Location("locationB");
//                endPoint.setLatitude(lat);
//                endPoint.setLongitude(lng);
//
//                double distance=startPoint.distanceTo(endPoint);

                    Log.d("dist",""+distance);
                    if(Current_dist==0)
                        Current_dist=distance;
                    else if(Current_dist!=distance && distance>1) {

                        Log.d("old loc", "" + Current_dist + " new " + distance);
                        Current_dist = distance;

                        if (state_of_car.equals("ON"))
                        {

                            car_cond.setText("CAR IS BEING STOLEN");
                            car_cond.setTextColor(getResources().getColor(R.color.unsafe));
                        }
                        else if (state_of_car.equals("OFF"))
                        {
                            car_cond.setText(("CAR IS BEING TOED"));
                            car_cond.setTextColor(getResources().getColor(R.color.unsafe));
                        }
                        final MediaPlayer alertSound=MediaPlayer.create(getApplicationContext(),R.raw.high_alert);
                        alertSound.start();
                        alertSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                alertSound.release();
                            }
                        });
                        dist.setText("Car is moving Distance from You is"+Math.round(Current_dist)+" m");
//                        Toast.makeText(getApplicationContext(),"Car is moving Distance from You is"+Current_dist,Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume", "onResume");
        if (isSensorAvailable) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (arePermissionsGranted()) {
            if (!SmartLocation.with(this).location().state().isGpsAvailable()) {
                Toast.makeText(this, "Please turn on the GPS", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startLocationUpdates();
                    }
                }, 2000);
            } else {
                SmartLocation.with(this).location().config(LocationParams.NAVIGATION).start(this);
                isLocationUpdateStarted = true;
            }
        } else {
            requestPermissions();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (isLocationUpdateStarted) {
            SmartLocation.with(this).location().stop();
        }
    }

    long lastTimeSensorValueReceived = System.currentTimeMillis();

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        long currentTimeInMillis = System.currentTimeMillis();
        if (currentTimeInMillis - lastTimeSensorValueReceived < SENSOR_DELAY) {
            return;
        }
            lastTimeSensorValueReceived = currentTimeInMillis;
            xAxisValue.setText(String.valueOf(sensorEvent.values[0]));
            yAxisValue.setText(String.valueOf(sensorEvent.values[1]));
            zAxisValue.setText(String.valueOf(sensorEvent.values[2]));

            if(X.size()>=500 && Y.size()>=500 && Z.size()>=500)
            {
                    phoneRef.child("accelerometer").setValue(null);
                    upload();
//                while(!X.isEmpty() || !Y.isEmpty() || !Z.isEmpty())
//                {
//                    if(X.size()==100)
//                        phoneRef.child("accelerometer").setValue(null);
//                    newRef.child("x").setValue(X.remove());
//                    newRef.child("y").setValue(Y.remove());
//                    newRef.child("z").setValue(Z.remove());
//                }
            }
            else
            {
                Log.d("size of key"," "+X.size());
                X.add(sensorEvent.values[0]);
                Y.add(sensorEvent.values[1]);
                Z.add(sensorEvent.values[2]);
            }
    }

    public  void upload()
    {
        //Log.d("key",newrf.toString());
        while(X.size()>0 && Y.size()>0 && Z.size()>0)
                {
                    DatabaseReference newrf = accelerometerRef.child(accelerometerRef.push().getKey());
                    Log.d("path",newrf.toString());
                    newrf.child("x").setValue(X.remove());
                    newrf.child("y").setValue(Y.remove());
                    newrf.child("z").setValue(Z.remove());
                }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onLocationUpdated(Location location) {
        latitudeValue.setText(String.valueOf(location.getLatitude()));
        longitudeValue.setText(String.valueOf(location.getLongitude()));
        DatabaseReference newRef = locationRef.child(locationRef.push().getKey());
        curr_lat=location.getLatitude();
        curr_long=location.getLongitude();
        newRef.child("latitude").setValue(curr_lat);
        newRef.child("longitude").setValue(curr_long);
    }

    private void requestPermissions() {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    private boolean arePermissionsGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

    }
}
class Loc
{
    double latitude;
    double longitude;
    void Loc()
    {
        latitude=0;
        longitude=0;
    }
    void Loc(double latitude,double longitude)
    {
        this.latitude=latitude;
        this.longitude=longitude;

    }
}
