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

public class pressureActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private TextView pressureSensorText;
    SessionManager session;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        session = new SessionManager(getApplicationContext());
        final HashMap<String, String> user = session.getUserDetails();
        final String username = user.get(SessionManager.KEY_username);

        //retrieve the current value of the pressure sensor
        float currentValue = sensorEvent.values[0];

        //display the retrieved value onto the textView
        pressureSensorText.setText(getResources().getString(R.string.pressure_text, currentValue));

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("pressure", currentValue);
            jsonBody.put("dataType", "PRESSURE_SENSOR");
            jsonBody.put("username", username);
            final String requestBody = jsonBody.toString();

            CreateDataApi.postData(getApplicationContext(), requestBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

        //pressure sensor does not report accuracy changes
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pressure);

        //retrieve widget
        pressureSensorText = findViewById(R.id.pressureSensorText);

        //define instances
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
    }

    //register the listener once the activity starts
    @Override
    protected void onStart() {
        super.onStart();

        if (pressureSensor != null) {

            sensorManager.registerListener(this, pressureSensor, sensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    //stop the sensor when the activity stops to reduce battery usage
    @Override
    protected void onStop() {
        super.onStop();

        sensorManager.unregisterListener(this);
    }
}
