package com.dcg.letturasensori;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.ToggleButton;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;

/**
 * The legibility is not important, is the only thing that matters.
 */
// Implements SensorEventListener is to use the OnChangeEvents linked to the sensors
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // is the android object that is aimed to access the sensors
    private SensorManager sensorManager;
    // just a textview where to write the results
    private TextView textView;
    // in this list all the sensors
    private List<Sensor> listAllSensors;
    // in this hashtable all the string to print
    private Hashtable<Integer, String> tableSensorValue;




    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        textView.setText("");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // fill this list with all the sensors
        listAllSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        tableSensorValue = new Hashtable<Integer, String>();

        for (Sensor e : listAllSensors)// for all the existing sensors, taken as "e"
        {
            //register this sensor, so if it changes onChangeSensor is called
            sensorManager.registerListener(this, e, sensorManager.SENSOR_DELAY_UI);
        }

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(200);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Enumeration keys = tableSensorValue.keys();
                                textView.setText("");
                                while (keys.hasMoreElements()) {
                                    Object key = keys.nextElement();
                                    String value = tableSensorValue.get(key);
                                    textView.append(value + "\n");
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

    }



    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.

    }

    @Override
    public final void onSensorChanged(SensorEvent event) {//when some (at least 1) sensor changes
        synchronized (this) {
            String v = "";
                // for all the values of the sensor (sometimes sensors have 3 value, sometimes only 1)
            for(int i = 0; i < event.values.length; i++)
            {
                DecimalFormat decimalFormat = new DecimalFormat("#.###");
                v += decimalFormat.format(event.values[i]) + " ; ";//print them separated by semicolon
            }
            //create a string to show the name and the value of the sensor
            String toPrint = "I'm : " + event.sensor.getName() + "\n" + String.valueOf(v);
            tableSensorValue.put(event.sensor.getType(), toPrint);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (Sensor e : listAllSensors)
        {
            sensorManager.registerListener(this, e, sensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
