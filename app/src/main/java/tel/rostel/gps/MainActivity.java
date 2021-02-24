package tel.rostel.gps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public int i = 0;
    private TextView t_lat, t_long, t_alt;
    private Button button;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        t_lat = (TextView) findViewById(R.id.editTextLatitude);
        t_long = (TextView) findViewById(R.id.editTextLongitude);
        t_alt = (TextView) findViewById(R.id.editTextAltitude);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                t_alt.setText("\n" + location.getAltitude());
                t_lat.setText("\n" + location.getLatitude());
                t_long.setText("\n" + location.getLongitude());
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
//        onRequestPermissionsResult();







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

    private void configureButton() {
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
    }


//    public void click (View v){
//        t_lat= (TextView) findViewById(R.id.editTextLattitude);
//        t_long= (TextView) findViewById(R.id.editTextLongtitude);
//        t_alt= (TextView) findViewById(R.id.editTextAltitude);
//        t_lat.append("\n" + i);
//        t_long.append("\n" + i);
//        t_alt.append("\n" + i);


//        i = i+1;

//    }



}