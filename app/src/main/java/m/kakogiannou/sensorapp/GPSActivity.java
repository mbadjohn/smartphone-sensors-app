package m.kakogiannou.sensorapp;

import android.Manifest;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

//notice that the GPS does not require the sensorEventListener
public class GPSActivity extends AppCompatActivity {

    //set instances of widgets and location request code
    private int LOCATION_REQUEST = 1;
    private Button GPSBtn;
    private TextView latText;
    private TextView longText;
    private TextView altitudeText;
    private TextView bearingText;
    private TextView speedText;
    private TextView locationText;

    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        //retrieve widgets
        GPSBtn = findViewById(R.id.GPSBtn);
        latText = findViewById(R.id.latText);
        longText = findViewById(R.id.longText);
        altitudeText = findViewById(R.id.altitudeText);
        bearingText = findViewById(R.id.bearingText);
        speedText = findViewById(R.id.speedText);
        locationText = findViewById(R.id.address);

        session = new SessionManager(getApplicationContext());
        final HashMap<String, String> user = session.getUserDetails();
        final String username = user.get(SessionManager.KEY_username);

        //request permission for GPS access
        ActivityCompat.requestPermissions(GPSActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_REQUEST);

        //set listener for GPS button widget
        GPSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get instance of GPSTracker class in order to ask the user for
                //location access
                GPSTracker GPSTracker = new GPSTracker(getApplicationContext());
                Location location = GPSTracker.getLocation();

                if (location != null) {

                    //get current values for latitude and longitude
                    double latValue = location.getLatitude();
                    double longValue = location.getLongitude();
                    double altitude = location.getAltitude();
                    float bearing = location.getBearing();
                    float speed = location.getSpeed();

                    //display the values retrieved onto the textView widgets
                    latText.setText(getResources().getString(R.string.GPS_lat_value, latValue));
                    longText.setText(getResources().getString(R.string.GPS_long_value, longValue));
                    altitudeText.setText(getResources().getString(R.string.GPS_altitude_value, altitude));
                    bearingText.setText(getResources().getString(R.string.GPS_bearing_value, bearing));
                    speedText.setText(getResources().getString(R.string.GPS_speed_value, speed));


                    JSONObject jsonBody = new JSONObject();

                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latValue, longValue, 1);
                        String fullAddress = addresses.get(0).getAddressLine(0);
                        locationText.setText("Location:\n" +" " + fullAddress);
                        String admin = addresses.get(0).getAdminArea();
                        String subadmin = addresses.get(0).getSubAdminArea();
                        String city = addresses.get(0).getLocality();
                        String area =addresses.get(0).getThoroughfare();
                        String countryName = addresses.get(0).getCountryName();
                        String countryCode = addresses.get(0).getCountryCode();
                        String postalCode = addresses.get(0).getPostalCode();

                        jsonBody.put("fullAddress", fullAddress);
                        jsonBody.put("admin", admin);
                        jsonBody.put("subadmin", subadmin);
                        jsonBody.put("city", city);
                        jsonBody.put("countryName", countryName);
                        jsonBody.put("countryCode", countryCode);
                        jsonBody.put("postalCode", postalCode);
                        Log.d("Address", addresses.toString());
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        jsonBody.put("latitude", latValue);
                        jsonBody.put("longitude", longValue);
                        jsonBody.put("altitude", altitude);
                        jsonBody.put("bearing", bearing);
                        jsonBody.put("speed", speed);
                        jsonBody.put("dataType", "GPS_SENSOR");
                        jsonBody.put("username", username);
                        final String requestBody = jsonBody.toString();
                        Log.i("requestBody: ", requestBody);
                        CreateDataApi.postData(getApplicationContext(), requestBody);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
