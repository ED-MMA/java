package com.github.aklakina.edmma.databaseInterface;

import com.github.aklakina.edmma.base.*;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

@Singleton
public class Getter {

    private final Connector dbInstance = SingletonFactory.getSingleton(Connector.class);
    private final Inserter inserter = SingletonFactory.getSingleton(Inserter.class);

    public FileData getFile(String name) {
        String sql = "Select * from metadata where fileName = ?";
        ResultSet temp;
        try(PreparedStatement pstmt = dbInstance.getConnection().prepareStatement(sql)) {
            pstmt.setString(0, name);
            temp = pstmt.executeQuery();
            if (!temp.next()) {
                return new FileData(name, 0, 0);
            }
            return new FileData(temp.getString(0), temp.getInt(1), temp.getInt(2));
        } catch (SQLException e) {
            System.out.println("Error occurred during read from db");
            System.out.println(e.getMessage());
            return null;
        }
    }
    Integer getIDbyName(tableName table, String name) {
        String sql = "SELECT id FROM " + table + " WHERE name = ?";
        try (PreparedStatement pstmt = dbInstance.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                return inserter.createByName(table, name);
            } else {
                try {
                    return rs.getInt("id");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    return -1;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }

    }
}
