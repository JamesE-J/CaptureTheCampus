package com.edmondson_jones.james.capturethecampus;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.edmondson_jones.james.capturethecampus.R.id.map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    List<Circle> circleList;
    User user = null;
    private int mInterval = 2000;
    private Handler mHandler;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        circleList = new ArrayList<Circle>();
        user = new User();
        Intent intent = getIntent();
        user.UserKey = intent.getStringExtra("UserKey");
        user.UserName = intent.getStringExtra("UserName");
        user.Team = intent.getStringExtra("Team");
        progressBar = (ProgressBar)findViewById(R.id.progressBar2);
        TextView mainTextView = (TextView)findViewById((R.id.textView));
        if(user.Team.equals("Red Team")) {
            mainTextView.setTextColor(getResources().getColor(R.color.colorRed));
        }
        else{
            mainTextView.setTextColor(getResources().getColor(R.color.colorBlue));
        }
        new Get().execute();
        mHandler = new Handler();
        updateChecker.run();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng campus = new LatLng(53.7709, -0.3682);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(campus));

        List<LatLng> markerList = new ArrayList<LatLng>();
        markerList.add(new LatLng(53.770115, -0.368518));
        markerList.add(new LatLng(53.770280, -0.367069));
        markerList.add(new LatLng(53.771244, -0.368362));
        markerList.add(new LatLng(53.770819, -0.369371));
        markerList.add(new LatLng(53.771232, -0.367525));
        markerList.add(new LatLng(53.771953, -0.368866));
        markerList.add(new LatLng(53.770723, -0.370068));
        markerList.add(new LatLng(53.771845, -0.366860));

        for(LatLng marker : markerList){
            drawCircle(marker);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(MapsActivity.this, MainActivity.class);
            MapsActivity.this.startActivity(intent);
        }

        try {
            mMap.setMyLocationEnabled(true);
        }catch(SecurityException e){
            e.printStackTrace();
        }

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                int index = 0;
                boolean inCircle = false;
                for(Circle circle : circleList){
                    Location circleLocation = new Location("temp");
                    circleLocation.setLatitude(circle.getCenter().latitude);
                    circleLocation.setLongitude(circle.getCenter().longitude);
                    float f = location.distanceTo(circleLocation);
                    if(f <= 20)
                    {
                        Toast.makeText(getApplicationContext(), "User In circle" + f,
                                Toast.LENGTH_SHORT).show();
                        user.Zone = index;
                        inCircle = true;
                    }
                    index = index + 1;
                }
                if (inCircle == false){
                    user.Zone = 99;
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        LocationManager locationManager =
                (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);
    }

    Runnable updateChecker = new Runnable(){
        @Override
        public void run() {
            try {
                if(user.Zone != 99){
                    new Post().execute();
                }
            } finally {
                mHandler.postDelayed(updateChecker, mInterval);
            }
        }
    };

    private void drawCircle(LatLng point){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(point);
        circleOptions.radius(20);
        circleOptions.strokeColor(Color.BLACK);
        circleOptions.fillColor(0x30ff0000);
        circleOptions.strokeWidth(2);
        Circle circle = mMap.addCircle(circleOptions);
        circleList.add(circle);
    }

    public int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000;
        Green = (Green << 8) & 0x0000FF00;
        Blue = Blue & 0x000000FF;
        return 0x99000000 | Red | Green | Blue;
    }

    public class Get extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            HttpURLConnection client = null;
            String response = null;
            try {
                url = new URL("http://10.0.2.2:56412/api/zone/initialise");
                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("GET");

                URLConnection conn = url.openConnection();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }

                response = sb.toString();
            }catch(Exception e)
            {
                response = e.toString();
            }
            return response;
        }
        @Override
        protected void onPostExecute(String result) {
            int index = 0;
            int gameScore = 0;
            result = result.substring(2,result.length() - 2);
            String[] zoneArray = result.split(",");
            for(Circle circle : circleList) {
                int zoneValue = Integer.parseInt(zoneArray[index]);
                if(zoneValue > 50) {
                    circle.setFillColor(getIntFromColor(100, 100 - zoneValue, 100 - zoneValue));
                }
                else if(zoneValue < 50) {
                    circle.setFillColor(getIntFromColor(zoneValue, zoneValue, 100));
                }
                else{
                    circle.setFillColor(getIntFromColor(100, 100, 100));
                }
                index++;
            }
            for(int i = 0; i < zoneArray.length; i++){
                gameScore = gameScore + Integer.parseInt(zoneArray[i]);
            }
            progressBar.setProgress(100-gameScore/zoneArray.length);
        }
    }
    public class Post extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            HttpURLConnection client = null;
            String response = null;
            try {
                String data = URLEncoder.encode("username", "UTF-8")
                        + "=" + URLEncoder.encode(user.UserName, "UTF-8");

                data += "&" + URLEncoder.encode("team", "UTF-8") + "="
                        + URLEncoder.encode(user.Team, "UTF-8");

                data += "&" + URLEncoder.encode("zone", "UTF-8") + "="
                        + URLEncoder.encode(Integer.toString(user.Zone), "UTF-8");

                data += "&" + URLEncoder.encode("userkey", "UTF-8") + "="
                        + URLEncoder.encode(user.UserKey, "UTF-8");

                url = new URL("http://10.0.2.2:56412/api/zone/update");
                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("POST");

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }

                response = sb.toString();
            }catch(Exception e)
            {
                response = e.toString();
            }
            return response;
        }
        @Override
        protected void onPostExecute(String result) {
            int index = 0;
            int gameScore = 0;
            result = result.substring(2,result.length() - 2);
            String[] zoneArray = result.split(",");
            for(Circle circle : circleList) {
                int zoneValue = Integer.parseInt(zoneArray[index]);
                if(zoneValue > 50) {
                    circle.setFillColor(getIntFromColor(100, 100 - zoneValue, 100 - zoneValue));
                }
                else if(zoneValue < 50) {
                    circle.setFillColor(getIntFromColor(zoneValue, zoneValue, 100));
                }
                else{
                    circle.setFillColor(getIntFromColor(100, 100, 100));
                }
                index++;
            }
            for(int i = 0; i < zoneArray.length; i++){
                gameScore = gameScore + Integer.parseInt(zoneArray[i]);
            }
            progressBar.setProgress(100-gameScore/zoneArray.length);
        }
    }
}
