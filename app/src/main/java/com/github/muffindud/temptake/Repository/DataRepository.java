package com.github.muffindud.temptake.Repository;

import com.github.muffindud.temptake.Config.DatabaseConfig;
import com.github.muffindud.temptake.Models.DataPacket;

import java.sql.*;

public class DataRepository {
    public void insertEntry(DataPacket dataPacket, byte[] workerMac) {
        // TODO: Insert into database
    }

    public void registerManager(byte[] managerMac) {
        // TODO: Create manager
    }

    public void registerWorker(byte[] managerMac, byte[] workerMac) {
        // TODO: Create worker
    }

    public void unregisterManager(byte[] managerMac) {
        // TODO: Delete manager and linked workers
    }

    public void unregisterWorker(byte[] workerMac) {
        // TODO: Unlink worker from manager
    }

    public void unregisterAllWorkers(byte[] managerMac) {
        // TODO: Unlink all workers from manager
    }

    public boolean isWorkerRegitered(byte[] workerMac) {
        // TODO: Check if worker is registered

        return false;
    }

    public int getManagerId(byte[] managerMac) {
        String query = "SELECT Id FROM Manager WHERE MAC = ?";

        try {
            PreparedStatement statement = DatabaseConfig.getConnection().prepareStatement(query);
            statement.setBytes(1, managerMac);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("Id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int getWorkerId(byte[] workerMac) {
        String query = "SELECT Id FROM Worker WHERE MAC = ?";

        try {
            PreparedStatement statement = DatabaseConfig.getConnection().prepareStatement(query);
            statement.setBytes(1, workerMac);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("Id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}