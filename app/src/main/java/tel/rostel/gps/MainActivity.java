package tel.rostel.gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;



public class MainActivity extends AppCompatActivity {
    public int i = 0;
    private Button button;
    private Button button_reset;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public Chronometer chronometer;
    private boolean isRunning = false;
    private long pauseOffset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chronometer = findViewById(R.id.chronometer);
        button = (Button) findViewById(R.id.button);
        button_reset = (Button) findViewById(R.id.resetButton);
        EditText t_lat = (EditText) findViewById(R.id.editTextLatitude);
        EditText t_long = (EditText) findViewById(R.id.editTextLongitude);
        EditText t_alt = (EditText) findViewById(R.id.editTextAltitude);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crono();
            }
        });

        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRun();
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {


                t_alt.setText("\n" + Double.toString(location.getAltitude()));
                t_lat.setText("\n" + Double.toString(location.getLatitude()));
                t_long.setText("\n" + Double.toString(location.getLongitude()));
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            public void onProciderDisabled(String s){
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
            }, 10);
            return;
        }else {
            configureButton();
        }





    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;

        }

    }

    @SuppressLint("MissingPermission")
    private void configureButton() {
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
    }



    public void crono() {
        if (isRunning==false) {
            button.setBackgroundColor(Color.RED);
            button.setText("Pause");

            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            isRunning = true;
        }else {
            button.setText("Continue");
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            isRunning = false;
        }
    }

    public void stopRun() {
        if (isRunning==false) {
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            pauseOffset = 0;
            button.setText("Start");
            button.setBackgroundColor(0xFF6200EE);

        }
    }



}