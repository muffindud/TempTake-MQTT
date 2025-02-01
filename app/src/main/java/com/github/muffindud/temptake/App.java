package com.github.muffindud.temptake;

import org.eclipse.paho.client.mqttv3.*;

public class App {
    private static final String BROKER = "tcp://" + System.getenv("MQTT_BROKER") + ":" + System.getenv("MQTT_PORT");
    private static final String MQTT_TOPIC = "temptake/manager";
    private static final String MQTT_CLIENT_ID = "temptake-consumer";

    public static void main(String[] args) {
        try {
            MqttClient client = new MqttClient(BROKER, MQTT_CLIENT_ID);
            MqttConnectOptions options = new MqttConnectOptions();

            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);

            client.connect(options);
            System.out.println("Connected to MQTT broker");

            client.subscribe(MQTT_TOPIC, (topic, message) -> {
                // print raw payload in hex
                byte[] payload = message.getPayload();
                StringBuilder hexPayload = new StringBuilder();
                for (byte b : payload) {
                    hexPayload.append(String.format("%02X ", b));
                }
                System.out.println("Received message: " + hexPayload.toString());

                // TODO: Insert into database
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}