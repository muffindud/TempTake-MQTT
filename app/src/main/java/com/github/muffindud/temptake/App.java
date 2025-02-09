package com.github.muffindud.temptake;

import org.eclipse.paho.client.mqttv3.*;
import com.github.muffindud.temptake.Models.DataPacket;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.sql.*;

public class App {
    private static final String MQTT_BROKER_HOST = System.getenv("MQTT_BROKER_HOST") != null ? System.getenv("MQTT_BROKER_HOST") : "localhost";
    private static final String MQTT_BROKER_PORT = System.getenv("MQTT_BROKER_PORT") != null ? System.getenv("MQTT_BROKER_PORT") : "1883";

    private static final String BROKER = "tcp://" + MQTT_BROKER_HOST + ":" + MQTT_BROKER_PORT;
    private static final String MQTT_TOPIC = "$share/group/temptake/manager";
    private static final String MQTT_CLIENT_ID = "temptake-consumer-" + System.getenv("HOSTNAME");

    private static final String DB_HOST = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : "localhost";
    private static final String DB_PORT = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : "5432";
    private static final String DB_NAME = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : "temptake";
    private static final String DB_USER = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "postgres";
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "password";

    private static final String DB_URL = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;

    private static Connection connection;

    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

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
                    insertEntry(dataPacket, dataPacket.metaData.worker_mac);
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

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    connection.close();
                    client.disconnect();
                    System.out.println("Database and MQTT connection closed");
                } catch (SQLException | MqttException e) {
                    e.printStackTrace();
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertEntry(DataPacket dataPacket, byte[] workerMAC) throws SQLException {
        // String getWorkerIdQuery = "SELECT TOP 1 Id FROM Worker Where MAC = ?";
        // String insertEntryQuery = "INSERT INTO Entry (Temperature, Humidity, Pressure, Ppm, WorkerId) VALUES (?, ?, ?, ?, ?)";
        // int workerId = -1;

        // try (PreparedStatement workerIdStatement = connection.prepareStatement(getWorkerIdQuery)) {
        //     workerIdStatement.setString(1, workerMAC.toString());
        //     ResultSet workerIdResult = workerIdStatement.executeQuery();
        //     workerId = workerIdResult.getInt("Id");

        //     if (workerId == -1) {
        //         throw new SQLException("Worker not found");
        //     }
        // } catch (SQLException e) {
        //     e.printStackTrace();
        // }

        // try (PreparedStatement insertEntryStatement = connection.prepareStatement(insertEntryQuery)) {
        //     insertEntryStatement.setFloat(1, dataPacket.rawData.temperature);
        //     insertEntryStatement.setFloat(2, dataPacket.rawData.humidity);
        //     insertEntryStatement.setFloat(3, dataPacket.rawData.pressure);
        //     insertEntryStatement.setFloat(4, dataPacket.rawData.ppm);
        //     insertEntryStatement.setInt(5, workerId);
        //     insertEntryStatement.executeUpdate();
        // } catch (SQLException e) {
        //     e.printStackTrace();
        // }

        // TODO: Insert data packet into database

        System.out.println("Received data packet: " + dataPacket);
        System.out.println("Temperature: " + dataPacket.rawData.temperature);
        System.out.println("Humidity: " + dataPacket.rawData.humidity);
        System.out.println("Pressure: " + dataPacket.rawData.pressure);
        System.out.println("PPM: " + dataPacket.rawData.ppm);
    }
}