package m.kakogiannou.sensorapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class lightActivity extends AppCompatActivity implements SensorEventListener {

    //set instances for the sensorManager, light sensor, and textViews
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private TextView lightSensorText;
    SessionManager session;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        session = new SessionManager(getApplicationContext());
        final HashMap<String, String> user = session.getUserDetails();
        final String username = user.get(SessionManager.KEY_username);

        //retrieve the current value of the light sensor
        float currentValue = sensorEvent.values[0];

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("light", currentValue);
            jsonBody.put("dataType", "LIGHT_SENSOR");
            jsonBody.put("username", username);
            final String requestBody = jsonBody.toString();

            CreateDataApi.postData(getApplicationContext(), requestBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //display the retrieved values onto the textView
        lightSensorText.setText(getResources().getString(R.string.light_text, currentValue));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

        //ambient light sensor does not report accuracy changes
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        //retrieve widgets
        lightSensorText = findViewById(R.id.lightSensorText);

        //define instances
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    //register the listener once the activity starts
    @Override
    protected void onStart() {
        super.onStart();

        if(lightSensor != null) {

            sensorManager.registerListener(this, lightSensor, sensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    //stop the sensor when the activity stops to reduce battery usage
    @Override
    protected void onStop() {
        super.onStop();

        sensorManager.unregisterListener(this);
    }
}
