package com.fysx.game2d;

import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
 
/**
 * Android Accelerometer Sensor Manager Archetype
 * @author antoine vianey
 * 
 */
public class AccelerometerManager {
 
    /** Accuracy configuration */
    private static float threshold     = 0.2f;
    private static int interval     = 1000;
 
    private static Sensor sensor;
    private static SensorManager sensorManager;
    // you could use an OrientationListener array instead
    // if you plans to use more than one listener
    private static AccelerometerListener listener;
    /** поддерживает ли устройство */
    private static Boolean supported;
    /** показывает, запущен ли акселерометр */
    private static boolean running = false;
 
    /**
     * возвращае тру если слушаем акселерометр
     */
    public static boolean isListening() {
        return running;
    }
 
    /**
     * заершаем прослушку
     */
    public static void stopListening() {
        running = false;
        try {
            if (sensorManager != null && sensorEventListener != null) {
                sensorManager.unregisterListener(sensorEventListener);
            }
        } catch (Exception e) {}
    }
 
    /**
     * возвращаем тру если поддерживается акс.
     */
    public static boolean isSupported() {
        if (supported == null) {
            if (AndGame2DActivity.getContext() != null) {
                sensorManager = (SensorManager) AndGame2DActivity.getContext().
                        getSystemService(Context.SENSOR_SERVICE);
                List<Sensor> sensors = sensorManager.getSensorList(
                        Sensor.TYPE_ACCELEROMETER);
                supported = new Boolean(sensors.size() > 0);
            } else {
                supported = Boolean.FALSE;
            }
        }
        return supported;
    }
 
    /**
     * конфигурируем класс для рекистрации шейка
     * @param threshold
     *             минимальное значение ускорения 
     * @param interval
     *             минимальный интервал между шейками
     */
    public static void configure(int threshold, int interval) {
        AccelerometerManager.threshold = threshold;
        AccelerometerManager.interval = interval;
    }
 
    /**
     * регистрируем слухачи и запускаем
     * @param accelerometerListener
     *             callback for accelerometer events
     */
    public static void startListening(
            AccelerometerListener accelerometerListener) {
        sensorManager = (SensorManager) AndGame2DActivity.getContext().
                getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(
                Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            sensor = sensors.get(0);
            running = sensorManager.registerListener(
                    sensorEventListener, sensor, 
                    SensorManager.SENSOR_DELAY_GAME);
            listener = accelerometerListener;
        }
    }
 
    /**
     * Конфигурируем threshold и interval
     * регистрируем слушатель и слушаем
     * @param accelerometerListener
     *             callback for accelerometer events
     * @param threshold
     *             минимальное значение ускорения
     * @param interval
     *              минимальный интервал между шейками
     */
    public static void startListening(
            AccelerometerListener accelerometerListener, 
            int threshold, int interval) {
        configure(threshold, interval);
        startListening(accelerometerListener);
    }
 
    /**
     * The listener that listen to events from the accelerometer listener
     */
    private static SensorEventListener sensorEventListener = 
        new SensorEventListener() {
 
        private long now = 0;
        private long timeDiff = 0;
        private long lastUpdate = 0;
        private long lastShake = 0;
 
        private float x = 0;
        private float y = 0;
        private float z = 0;
        private float lastX1 = 0;
        private float lastY1 = 0;
        private float lastZ1 = 0;
        private float lastX2 = 0;
        private float lastY2 = 0;
        private float lastZ2 = 0;
        private float force = 0;
 
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
 
        public void onSensorChanged(SensorEvent event) {
            // use the event timestamp as reference
            // so the manager precision won't depends 
            // on the AccelerometerListener implementation
            // processing time
            now = event.timestamp;
            //if(lastX1 !=0 && lastX2!=0)
            //{
            	lastX2=lastZ1;
            	lastY2=lastZ1;
            	lastZ2=lastZ1;
            	lastX1=x;
            	lastY1=y;
            	lastZ1=z;
            //}
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
           /* if(x==lastX1){x+=lastX1-lastX2;}
            if(y==lastY1){y+=lastY1-lastY2;}*/
            listener.onAccelerationChanged(x, y, z);
        }
 
    };
 
}