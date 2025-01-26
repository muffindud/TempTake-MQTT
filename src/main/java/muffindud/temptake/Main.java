package muffindud.temptake;

import org.eclipse.paho.client.mqttv3.*;

public class Main {
    public static void main(String[] args) {
        String mqtt_host = System.getenv("MQTT_BROKER_HOST");
        String mqtt_port = System.getenv("MQTT_BROKER_PORT");

        String broker = "tcp://" + mqtt_host + ":" + mqtt_port;
        String clientId = "temptake";
        String topic = "temptake";


        try {
            MqttClient client = new MqttClient(broker, clientId);
            MqttConnectOptions options = new MqttConnectOptions();

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println("Message arrived: " + new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("Delivery complete");
                }
            });

            client.connect(options);
            client.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}