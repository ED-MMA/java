package com.github.aklakina.edmma.databaseInterface;

import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.base.SingletonFactory;

import java.sql.*;
import java.time.Clock;
import java.time.Instant;

@Singleton
public class Inserter {

    private final Connector dbInstance = SingletonFactory.getSingleton(Connector.class);

    private final Getter getter = SingletonFactory.getSingleton(Getter.class);

    Integer createByName(tableName table, String name) {
        String sql = "INSERT INTO " + table + "(name) VALUES(?)";
        try (PreparedStatement pstmt = dbInstance.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
        sql = "SELECT top 1 id FROM " + table + " order by id desc";
        try (PreparedStatement pstmt = dbInstance.getConnection().prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt("id");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void linkSystemToCluster(String systemName, String targetSystemName) {
        String sql = "INSERT INTO clusters(sourceSystemID, targetSystemID) VALUES(?,?)";
        try (PreparedStatement pstmt = dbInstance.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, getter.getIDbyName(tableName.SOURCESYSTEMS, systemName));
            pstmt.setInt(2, getter.getIDbyName(tableName.TARGETSYSTEMS, targetSystemName));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void linkStationToSystem(String stationName, String systemName) {
        String sql = "UPDATE stations SET systemID = ? WHERE ID = ?";
        try (PreparedStatement pstmt = dbInstance.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, getter.getIDbyName(tableName.SOURCESYSTEMS, systemName));
            pstmt.setInt(2, getter.getIDbyName(tableName.STATIONS, stationName));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void linkFactionToStation(String stationName, String factionName) {
        String sql = "INSERT INTO stationFaction(stationID, factionID) VALUES(?, ?)";
        try (PreparedStatement pstmt = dbInstance.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, getter.getIDbyName(tableName.STATIONS, stationName));
            pstmt.setInt(2, getter.getIDbyName(tableName.FACTIONS, factionName));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean addFile(String name, byte[] md5, Integer lastLineRead) {
        if (getter.getFile(name) == null) {
            String sql = "INSERT INTO metadata (fileName, md5, lastLine) values (?, ?, ?)";
            try (PreparedStatement pstmt = dbInstance.getConnection().prepareStatement(sql)) {
                pstmt.setString(0,name);
                pstmt.setBlob(1,new javax.sql.rowset.serial.SerialBlob(md5));
                pstmt.setInt(2, lastLineRead);
                return true;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        } else {
            // update the last line and md5 of file
            String sql = "update metadata set lastLine = ?, md5 = ? where fileName = ?";
            try (PreparedStatement pstmt = dbInstance.getConnection().prepareStatement(sql)) {
                pstmt.setString(2, name);
                pstmt.setBlob(1, new javax.sql.rowset.serial.SerialBlob(md5));
                pstmt.setInt(0, lastLineRead);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean addMission(
            Integer missionID,
            String sourceFactionName,
            String targetFactionName,
            String sourceSystemName,
            String targetSystemName,
            String sourceStationName,
            Integer reward,
            Integer killsNeeded,
            Boolean winged,
            String ExpiresAt
    ) {
        String sql = "INSERT INTO missions(" +
                "missionID" +
                ",sourceFactionID" +
                ",targetFactionID" +
                ",sourceSystemID" +
                ",targetSystemID" +
                ",stationID" +
                ",reward" +
                ",killsNeeded" +
                ",winged" +
                ",acceptanceTime" +
                ",expiresAt" +
                ") values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = dbInstance.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, missionID);
            pstmt.setInt(2, getter.getIDbyName(tableName.FACTIONS, sourceFactionName));
            pstmt.setInt(3, getter.getIDbyName(tableName.FACTIONS, targetFactionName));
            pstmt.setInt(4, getter.getIDbyName(tableName.SOURCESYSTEMS, sourceSystemName));
            pstmt.setInt(5, getter.getIDbyName(tableName.TARGETSYSTEMS, targetSystemName));
            pstmt.setInt(6, getter.getIDbyName(tableName.STATIONS, sourceStationName));
            pstmt.setInt(7, reward);
            pstmt.setInt(8, killsNeeded);
            pstmt.setBoolean(9, winged);
            Clock clock = Clock.systemDefaultZone();
            Instant now = clock.instant();
            pstmt.setString(10, now.toString());
            pstmt.setString(11, ExpiresAt);
            int res = pstmt.executeUpdate();
            if (res == 0) {
                return false;
            }
            linkFactionToStation(sourceStationName, sourceFactionName);
            linkStationToSystem(sourceStationName, sourceSystemName);
            linkSystemToCluster(sourceSystemName, targetSystemName);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }


        return true;
    }

}
