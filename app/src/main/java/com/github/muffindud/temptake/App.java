package com.github.muffindud.temptake;

import org.eclipse.paho.client.mqttv3.*;
import com.github.muffindud.temptake.Models.DataPacket;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class App {
    private static final String BROKER = "tcp://" + System.getenv("MQTT_BROKER_HOST") + ":" + System.getenv("MQTT_BROKER_PORT");
    private static final String MQTT_TOPIC = "$share/group/temptake/manager";
    private static final String MQTT_CLIENT_ID = "temptake-consumer-" + System.getenv("HOSTNAME");

    public static void main(String[] args) {
        try {
            MqttClient client = new MqttClient(BROKER, MQTT_CLIENT_ID, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();

            options.setKeepAliveInterval(30);
            options.setAutomaticReconnect(true);
            options.setCleanSession(false);
            options.setConnectionTimeout(10);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    byte[] payload = message.getPayload();

                    DataPacket dataPacket = DataPacket.fromBinary(payload);
                    System.out.println("Received data packet: " + dataPacket);
                    System.out.println("Temperature: " + dataPacket.rawData.temperature);
                    System.out.println("Humidity: " + dataPacket.rawData.humidity);
                    System.out.println("Pressure: " + dataPacket.rawData.pressure);
                    System.out.println("PPM: " + dataPacket.rawData.ppm);

                    // TODO: Insert into database
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("Delivery complete");
                }
            });

            client.connect(options);
            System.out.println("Connected to MQTT broker as " + MQTT_CLIENT_ID);

            client.subscribe(MQTT_TOPIC, 1);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}