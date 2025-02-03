package com.github.muffindud.temptake;

import org.eclipse.paho.client.mqttv3.*;
import com.github.muffindud.temptake.Models.*;

public class App {
    private static final String BROKER =
            "tcp://" + System.getenv("MQTT_BROKER_HOST") + ":" + System.getenv("MQTT_BROKER_PORT");
    private static final String MQTT_TOPIC = "temptake/manager";
    private static final String MQTT_CLIENT_ID = "temptake-consumer-" + System.getenv("HOSTNAME");

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
                byte[] payload = message.getPayload();

                DataPacket dataPacket = DataPacket.fromBinary(payload);
                System.out.println("Received message: " + dataPacket);

                // TODO: Insert into database
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}