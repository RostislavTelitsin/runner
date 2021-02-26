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
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ProgressBar;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener, GestureDetector.OnGestureListener {
    public int i = 0;
    private Button button;
    private Button button_reset;
    private LocationManager locationManager;
    private LocationListener locationListener;
    public Chronometer chronometer;
    private boolean isRunning = false;
    private long pauseOffset;
    private ProgressBar progressBar;
    private GestureDetector gestureDetector;
    private volatile boolean stopthread = false;
    private boolean ifResset = false;
    private Handler mainHandler = new Handler();

    @SuppressLint("ClickableViewAccessibility")
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
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        gestureDetector = new GestureDetector(this, this);
        button_reset.setOnTouchListener(this);
        progressBar.setVisibility(View.INVISIBLE);

        button_reset.setVisibility(View.INVISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                crono();
            }
        });



        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationChanged(@NonNull Location location) {
                t_alt.setText(Double.toString(location.getAltitude()));
                t_lat.setText(Double.toString(location.getLatitude()));
                t_long.setText(Double.toString(location.getLongitude()));
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
            button_reset.setVisibility(View.INVISIBLE);
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            isRunning = true;

        }else {
            button.setText("Continue");
            button_reset.setVisibility(View.VISIBLE);
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            isRunning = false;

        }
    }

    public void stopRun() {
        progressBar.setVisibility(View.VISIBLE);
        ExtThread extThread = new ExtThread();
        extThread.start();


        }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            stopthread = false;
            progressBar.setVisibility(View.VISIBLE);
            stopRun();



        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            progressBar.setVisibility(View.INVISIBLE);
            stopthread = true;
            if (ifResset) {


                isRunning = false;
                button_reset.setVisibility(View.INVISIBLE);
                ifResset = false;
            }

        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {


    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }


    class ExtThread extends Thread {

        @Override
        public void run(){
            for (int x =0; x<10; x++) {
                if (stopthread) {
                    stopthread=false;
                    return;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (x==9) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            chronometer.stop();
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            pauseOffset = 0;
                            button.setText("Start");
                            button.setBackgroundColor(0xFF6200EE);
                            progressBar.setVisibility(View.INVISIBLE);
                            ifResset = true;
                        }
                    });




                }}
            }
        }

}