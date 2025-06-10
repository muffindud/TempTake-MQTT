package com.github.muffindud.temptake.Mqtt;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.github.muffindud.temptake.Services.MqttService;
import com.github.muffindud.temptake.Models.DataPacket;

public class MqttSubscriber {
    private static final String MQTT_BROKER_HOST =
            System.getenv("MQTT_BROKER_HOST") != null ? System.getenv("MQTT_BROKER_HOST") : "localhost";
    private static final String MQTT_BROKER_PORT =
            System.getenv("MQTT_BROKER_PORT") != null ? System.getenv("MQTT_BROKER_PORT") : "1883";

    private static final String BROKER = "tcp://" + MQTT_BROKER_HOST + ":" + MQTT_BROKER_PORT;

    private static final String[] MQTT_TOPICS = {
        "$share/group/temptake/entry",
        "$share/group/temptake/manager/register",
        "$share/group/temptake/manager/unregister",
        "$share/group/temptake/worker/register",
        "$share/group/temptake/worker/unregister"
    };

    private static final String MQTT_CLIENT_ID = "temptake-consumer-" + System.getenv("HOSTNAME");

    private final MqttService mqttService;

    public MqttSubscriber() {
        this.mqttService = new MqttService();
    }

    public void start() {
        try {
            MqttClient client = new MqttClient(BROKER, MQTT_CLIENT_ID, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();

            options.setKeepAliveInterval(30);
            options.setAutomaticReconnect(true);
            options.setCleanSession(false);
            options.setConnectionTimeout(10);

            ConcurrentMap<String, TopicHandler> topicHandlers = new ConcurrentHashMap<>();
            topicHandlers.put(MQTT_TOPICS[0], this::handleEntry);
            topicHandlers.put(MQTT_TOPICS[1], this::handleManagerRegister);
            topicHandlers.put(MQTT_TOPICS[2], this::handleManagerUnregister);
            topicHandlers.put(MQTT_TOPICS[3], this::handleWorkerRegister);
            topicHandlers.put(MQTT_TOPICS[4], this::handleWorkerUnregister);

            client.setCallback(new MqttCallbackImpl(topicHandlers));

            client.connect(options);
            System.out.println("Connected to MQTT broker as " + MQTT_CLIENT_ID);
            client.subscribe(MQTT_TOPICS, new int[] {0, 0, 0, 0, 0});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private record MqttCallbackImpl(ConcurrentMap<String, TopicHandler> topicHandlers) implements MqttCallback {
        @Override
        public void connectionLost(Throwable throwable) {
            throwable.printStackTrace();
        }

        @Override
        public void messageArrived(String mqttTopic, MqttMessage mqttMessage) {
            byte[] payload = mqttMessage.getPayload();

            if (topicHandlers.containsKey("$share/group/" + mqttTopic)) {
                topicHandlers.get("$share/group/" + mqttTopic).process(payload);
            } else {
                System.out.println("No handler for topic " + "$share/group/" + mqttTopic);
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}
    }

    private void handleEntry(byte[] payload) {
        DataPacket dataPacket = DataPacket.fromBinary(payload);
        if (dataPacket != null) {
            mqttService.insertEntry(dataPacket);
        }
    }

    private void handleManagerRegister(byte[] payload) {
        mqttService.registerManager(payload);
    }

    private void handleManagerUnregister(byte[] payload) {
        mqttService.unregisterManager(payload);
    }

    private void handleWorkerRegister(byte[] payload) {
        mqttService.registerWorker(
            Arrays.copyOfRange(payload, 0, 6),
            Arrays.copyOfRange(payload, 6, 12)
        );
    }

    private void handleWorkerUnregister(byte[] payload) {
        mqttService.unregisterWorker(payload);
    }

    private interface TopicHandler {
        void process(byte[] payload);
    }
}
