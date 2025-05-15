package com.github.muffindud.temptake.Repository;

import com.github.muffindud.temptake.Config.DatabaseConfig;
import com.github.muffindud.temptake.Models.DataPacket;

import java.sql.*;

public class DataRepository {
    private enum ModuleType {
        WORKER,
        MANAGER
    }

    private String byteToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));
        }

        return hexString.toString();
    }

    public void insertEntry(DataPacket dataPacket, byte[] workerMac, byte[] managerMac) {
        int managerWorkerId = getManagerWorkerId(managerMac, workerMac);

        if (managerWorkerId != -1) {
            String query = "INSERT INTO entry (temperature_c, humidity_perc, pressure_mmhg, ppm, manager_worker_id, created_at) VALUES (?, ?, ?, ?, ?, NOW());";

            try (Connection connection = DatabaseConfig.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setFloat(1, dataPacket.rawData.temperature);
                statement.setFloat(2, dataPacket.rawData.humidity);
                statement.setFloat(3, dataPacket.rawData.pressure);
                statement.setFloat(4, dataPacket.rawData.ppm);
                statement.setInt(5, managerWorkerId);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int addManager(byte[] managerMac) {
        int managerId = getModuleId(managerMac, ModuleType.MANAGER);

        if (managerId != -1) {
            return managerId;
        }

        String query = "INSERT INTO manager (mac, created_at) VALUES (?, NOW());";

        try (Connection connection = DatabaseConfig.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, byteToHex(managerMac));
            statement.executeUpdate();

            return getModuleId(managerMac, ModuleType.MANAGER);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int addWorker(byte[] workerMac) {
        int workerId = getModuleId(workerMac, ModuleType.WORKER);

        if (workerId != -1) {
            return workerId;
        }

        String query = "INSERT INTO worker (mac, created_at) VALUES (?, NOW());";

        try (Connection connection = DatabaseConfig.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, byteToHex(workerMac));
            statement.executeUpdate();

            return getModuleId(workerMac, ModuleType.WORKER);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void registerManager(byte[] managerMac) {
        // TODO: Will implement when user accounts are added to link managers to users
    }

    public void registerWorker(byte[] managerMac, byte[] workerMac) {
        int managerId = getModuleId(managerMac, ModuleType.MANAGER);

        if (managerId == -1 || getPairedManagerId(workerMac) == managerId) {
            return;
        }

        unregisterWorker(workerMac);

        int workerId = getModuleId(workerMac, ModuleType.WORKER);

        if (workerId == -1) {
            workerId = addWorker(workerMac);
        }

        String query = "INSERT INTO manager_worker (manager_id, worker_id, created_at) VALUES (?, ?, NOW());";

        try (Connection connection = DatabaseConfig.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, managerId);
            statement.setInt(2, workerId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unregisterManager(byte[] managerMac) {
        unregisterAllWorkers(managerMac);

        int managerId = getModuleId(managerMac, ModuleType.MANAGER);

        // TODO: Unlink manager from user account when user accounts are added
    }

    public void unregisterWorker(byte[] workerMac) {
        int workerId = getModuleId(workerMac, ModuleType.WORKER);

        if (workerId == -1) {
            return;
        }

        String query = "UPDATE manager_worker SET deleted_at = NOW() WHERE worker_id = ? AND deleted_at IS NULL;";

        try (Connection connection = DatabaseConfig.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, workerId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unregisterAllWorkers(byte[] managerMac) {
        int managerId = getModuleId(managerMac, ModuleType.MANAGER);

        if (managerId == -1) {
            return;
        }

        String query = "UPDATE manager_worker SET deleted_at = NOW() WHERE manager_id = ? AND deleted_at IS NULL;";

        try (Connection connection = DatabaseConfig.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, managerId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getManagerWorkerId(byte[] managerMac, byte[] workerMac) {
        int managerId = getModuleId(managerMac, ModuleType.MANAGER);
        int workerId = getModuleId(workerMac, ModuleType.WORKER);
        int managerWorkerId = -1;

        String query = "SELECT id FROM manager_worker WHERE manager_id = ? AND worker_id = ? AND deleted_at IS NULL;";

        try (Connection connection = DatabaseConfig.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, managerId);
            statement.setInt(2, workerId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                managerWorkerId = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return managerWorkerId;
    }

    private int getModuleId(byte[] moduleMac, ModuleType moduleType) {
        String module = ModuleType.MANAGER == moduleType ? "manager" : "worker";
        String query = "SELECT id FROM " + module + " WHERE mac = ?";
        int moduleId = -1;

        try (Connection connection = DatabaseConfig.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, byteToHex(moduleMac));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                moduleId = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return moduleId;
    }

    public int getPairedManagerId(byte[] workerMac) {
        String query = "SELECT manager_id FROM manager_worker WHERE worker_id = ? AND deleted_at IS NULL;";
        int workerId = -1;

        try (Connection connection = DatabaseConfig.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, getModuleId(workerMac, ModuleType.WORKER));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                workerId = resultSet.getInt("manager_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return workerId;
    }
}