package com.github.muffindud.temptake.Services;

import com.github.muffindud.temptake.Repository.DataRepository;
import com.github.muffindud.temptake.Models.DataPacket;

public class MqttService {
    private final DataRepository dataRepository;

    public MqttService() {
        this.dataRepository = new DataRepository();
    }

    public void insertEntry(DataPacket payload, byte[] workerMac) {
        dataRepository.insertEntry(payload, workerMac);
        // System.out.println("Inserted entry");
    }

    public void registerManager(byte[] managerMac) {
        dataRepository.addManager(managerMac);
        // System.out.println("Added manager");
        dataRepository.registerManager(managerMac);
        // System.out.println("Registered manager");
    }

    public void unregisterManager(byte[] managerMac) {
        dataRepository.unregisterManager(managerMac);
        // System.out.println("Unregistered manager");
    }

    public void registerWorker(byte[] managerMac, byte[] workerMac) {
        dataRepository.addWorker(workerMac);
        // System.out.println("Added worker");
        dataRepository.registerWorker(managerMac, workerMac);
        // System.out.println("Registered worker");
    }

    public void unregisterWorker(byte[] workerMac) {
        dataRepository.unregisterWorker(workerMac);
        // System.out.println("Unregistered worker");
    }
}
