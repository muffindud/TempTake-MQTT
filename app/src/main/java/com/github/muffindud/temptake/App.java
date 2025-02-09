package com.github.muffindud.temptake;

import com.github.muffindud.temptake.Mqtt.MqttSubscriber;

public class App {
    public static void main(String[] args) {
        MqttSubscriber subscriber = new MqttSubscriber();
        subscriber.start();
    }
}