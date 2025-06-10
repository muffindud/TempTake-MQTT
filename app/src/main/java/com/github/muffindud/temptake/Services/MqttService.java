package com.github.muffindud.temptake.Services;

import com.github.muffindud.temptake.Repository.DataRepository;
import com.github.muffindud.temptake.Models.DataPacket;

public class MqttService {
    private final DataRepository dataRepository;

    public MqttService() {
        this.dataRepository = new DataRepository();
    }

    public void insertEntry(DataPacket payload) {
        dataRepository.insertEntry(payload);
    }

    public void registerManager(byte[] managerMac) {
        dataRepository.addManager(managerMac);
        dataRepository.registerManager(managerMac);
    }

    public void unregisterManager(byte[] managerMac) {
        dataRepository.unregisterManager(managerMac);
    }

    public void registerWorker(byte[] managerMac, byte[] workerMac) {
        dataRepository.addWorker(workerMac);
        dataRepository.registerWorker(managerMac, workerMac);
    }

    public void unregisterWorker(byte[] workerMac) {
        dataRepository.unregisterWorker(workerMac);
    }
}
