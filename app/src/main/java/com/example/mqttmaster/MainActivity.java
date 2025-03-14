package com.example.mqttmaster;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;

import com.example.anew.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private MqttAndroidClient mqttClient;
    private TextView logTextView;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view);

        logTextView = findViewById(R.id.logTextView);
        mapView = findViewById(R.id.mapView);

        // Configurar OSMDroid
        mapView.setMultiTouchControls(true);
        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(40.4168, -3.7038); // Ejemplo: Madrid
        mapController.setCenter(startPoint);

        // Configurar MQTT
        String serverUri = "tcp://broker.hivemq.com:1883";
        String clientId = MqttClient.generateClientId();
        mqttClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);

        try {
            mqttClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT", "Conexión exitosa");
                    subscribeToTopic("tu/tema/mqtt");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MQTT", "Fallo en la conexión: " + exception.getMessage());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Log.e("MQTT", "Conexión perdida", cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                final String msg = new String(message.getPayload());
                // Obtener el tiempo actual en formato HH:mm:ss
                final String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Mostrar en el log el mensaje recibido junto con la hora
                        logTextView.append("[" + currentTime + "] " + msg + "\n");
                        // Se espera un mensaje en JSON con latitud y longitud, ejemplo: {"lat":40.4168,"lon":-3.7038}
                        Pair<Double, Double> location = parseLocation(msg);
                        if (location != null) {
                            addMarkerToMap(location.first, location.second, currentTime);
                        }
                    }
                });
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // No se utiliza en este ejemplo
            }
        });
    }

    private void subscribeToTopic(String topic) {
        try {
            mqttClient.subscribe(topic, 1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT", "Suscrito a " + topic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("MQTT", "Error al suscribir: " + exception.getMessage());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // Método para parsear el JSON y extraer la latitud y longitud
    private Pair<Double, Double> parseLocation(String message) {
        try {
            JSONObject json = new JSONObject(message);
            double lat = json.getDouble("lat");
            double lon = json.getDouble("lon");
            return new Pair<>(lat, lon);
        } catch (JSONException e) {
            return null;
        }
    }

    // Método para añadir un marcador al mapa, incluyendo la hora de actualización en el snippet
    private void addMarkerToMap(double lat, double lon, String time) {
        Marker marker = new Marker(mapView);
        marker.setPosition(new GeoPoint(lat, lon));
        marker.setTitle("Dispositivo");
        marker.setSnippet("Actualizado a las: " + time);
        mapView.getOverlays().add(marker);
        mapView.invalidate();
    }
}
