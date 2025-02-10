package com.github.muffindud.temptake.Repository;

import com.github.muffindud.temptake.Config.DatabaseConfig;
import com.github.muffindud.temptake.Models.DataPacket;

import java.sql.*;

public class DataRepository {
    private String byteToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));
        }

        return hexString.toString();
    }

    public void insertEntry(DataPacket dataPacket, byte[] workerMac) {
        int workerId = getWorkerId(workerMac);

        if (workerId != -1) {
            String query = "INSERT INTO \"Entries\" (\"Temperature\", \"Humidity\", \"Pressure\", \"Ppm\", \"WorkerId\", \"CreatedAt\") VALUES (?, ?, ?, ?, ?, NOW())";

            try {
                PreparedStatement statement = DatabaseConfig.getConnection().prepareStatement(query);
                statement.setFloat(1, dataPacket.rawData.temperature);
                statement.setFloat(2, dataPacket.rawData.humidity);
                statement.setFloat(3, dataPacket.rawData.pressure);
                statement.setFloat(4, dataPacket.rawData.ppm);
                statement.setInt(5, workerId);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int addManager(byte[] managerMac) {
        int managerId = getManagerId(managerMac);

        if (managerId != -1) {
            return managerId;
        }

        String query = "INSERT INTO \"Managers\" (\"MAC\", \"CreatedAt\") VALUES (?, NOW())";

        try {
            PreparedStatement statement = DatabaseConfig.getConnection().prepareStatement(query);
            statement.setString(1, byteToHex(managerMac));
            statement.executeUpdate();

            return getManagerId(managerMac);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int addWorker(byte[] workerMac) {
        int workerId = getWorkerId(workerMac);

        if (workerId != -1) {
            return workerId;
        }

        String query = "INSERT INTO \"Workers\" (\"MAC\", \"CreatedAt\") VALUES (?, NOW())";

        try {
            PreparedStatement statement = DatabaseConfig.getConnection().prepareStatement(query);
            statement.setString(1, byteToHex(workerMac));
            statement.executeUpdate();

            return getWorkerId(workerMac);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void registerManager(byte[] managerMac) {
        // TODO: Will implement when user accounts are added to link managers to users
    }

    public void registerWorker(byte[] managerMac, byte[] workerMac) {
        int managerId = getManagerId(managerMac);

        if (managerId == -1) {
            return;
        }

        unregisterWorker(workerMac);

        int workerId = getWorkerId(workerMac);

        if (workerId == -1) {
            workerId = addWorker(workerMac);
        }

        String query = "INSERT INTO \"ManagerWorkers\" (\"ManagerId\", \"WorkerId\", \"CreatedAt\") VALUES (?, ?, NOW())";

        try {
            PreparedStatement statement = DatabaseConfig.getConnection().prepareStatement(query);
            statement.setInt(1, managerId);
            statement.setInt(2, workerId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unregisterManager(byte[] managerMac) {
        unregisterAllWorkers(managerMac);

        int managerId = getManagerId(managerMac);

        // TODO: Unlink manager from user account when user accounts are added
    }

    public void unregisterWorker(byte[] workerMac) {
        int workerId = getWorkerId(workerMac);

        if (workerId == -1) {
            return;
        }

        String query = "UPDATE \"ManagerWorkers\" SET \"DeletedAt\" = NOW() WHERE \"WorkerId\" = ? AND \"DeletedAt\" IS NULL";

        try {
            PreparedStatement statement = DatabaseConfig.getConnection().prepareStatement(query);
            statement.setInt(1, workerId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unregisterAllWorkers(byte[] managerMac) {
        int managerId = getManagerId(managerMac);

        if (managerId == -1) {
            return;
        }

        String query = "UPDATE \"ManagerWorkers\" SET \"DeletedAt\" = NOW() WHERE \"ManagerId\" = ? AND \"DeletedAt\" IS NULL";

        try {
            PreparedStatement statement = DatabaseConfig.getConnection().prepareStatement(query);
            statement.setInt(1, managerId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getManagerId(byte[] managerMac) {
        String query = "SELECT \"Id\" FROM \"Managers\" WHERE \"MAC\" = ?";

        try {
            PreparedStatement statement = DatabaseConfig.getConnection().prepareStatement(query);
            statement.setString(1, byteToHex(managerMac));
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
        String query = "SELECT \"Id\" FROM \"Workers\" WHERE \"MAC\" = ?";

        try {
            PreparedStatement statement = DatabaseConfig.getConnection().prepareStatement(query);
            statement.setString(1, byteToHex(workerMac));
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