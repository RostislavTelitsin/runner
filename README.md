# Running tracker

Tracker for running shows distance, time and heartbeat rate  
This app still in progress. It uses different trads for every function  

It's working with location service, checking if previous data exist or not to calculate distance:

~~~
if (isLocationEnabled(getBaseContext())) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @SuppressLint(value = "SetTextI18n")
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    t_alt.setText(Double.toString(location.getAltitude()));
                    t_lat.setText(Double.toString(location.getLatitude()));
                    t_long.setText(Double.toString(location.getLongitude()));
                    if (isRunning) {
                        if (prevCoordinates.latitude==0 && prevCoordinates.longitude==0){
                            prevCoordinates = currentCoordinates;
                            currentCoordinates.setAll(location.getLatitude(), location.getLongitude(), location.getAltitude());

                        } else {
                            prevCoordinates = currentCoordinates;
                            currentCoordinates.setAll(location.getLatitude(), location.getLongitude(), location.getAltitude());

                            distance = distance + distanceCalc(prevCoordinates, currentCoordinates);
                            distanceText.setText(String.format("%.4f", distance). toString() + "км");
                        }
                    }else {
                        prevCoordinates = currentCoordinates;
                        currentCoordinates.setAll(location.getLatitude(), location.getLongitude(), location.getAltitude());
                    }
                }
~~~               

This method is used to calculate passed distance (altitude is not used yet. It will be used to calculate the passed distance more precisely):

~~~
public double distanceCalc (Coordinates firstPoint, Coordinates secondPoint) {
        double zemR=6371;
        double sin_lat = Math.sin(Math.toRadians((secondPoint.latitude - firstPoint.latitude)/2));
        double sin_longt = Math.sin(Math.toRadians((secondPoint.longitude - firstPoint.longitude)/2));
        double alt = secondPoint.altitude - firstPoint.altitude;
        double rez_temp = sin_lat * sin_lat + (sin_longt * sin_longt *Math.cos(Math.toRadians(firstPoint.latitude)) * Math.cos(Math.toRadians(secondPoint.latitude)));
        double rez = zemR * 2 * Math.atan2(Math.sqrt(rez_temp), Math.sqrt(1- rez_temp));
        if (rez<0.001) {rez = 0;}
        return rez;
    }
~~~

This class is used for separeted **thread** for chronometer implementation. **"mainHandler.post(new Runnable()..."** is to get access for MainActivity components:

~~~
class ExtThreadChrono extends Thread {

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
                            distance = 0;
                            distanceText.setText(String.format("%.2f", distance). toString() + "км");
                            progressBar.setVisibility(View.INVISIBLE);
                            ifReset = true;//workout can be reset
                        }
                    });
                }}
            }
        }
~~~
