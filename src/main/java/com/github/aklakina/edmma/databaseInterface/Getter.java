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

    public TreeSet<FileData> getMetadata() {
        String sql = "Select * from metadata";
        ResultSet temp;
        try(PreparedStatement pstmt = dbInstance.getConnection().prepareStatement(sql)) {
            temp = pstmt.executeQuery();
        } catch (SQLException e) {
            System.out.println("Error occurred during read from db");
            System.out.println(e.getMessage());
            return null;
        }
        TreeSet<FileData> res=new TreeSet<>();
        try {
            while (temp.next()) {
                res.add(new FileData(temp.getString(0), blobToByte(temp.getBlob(1)), temp.getInt(2)));
            }
        } catch (SQLException e) {
            System.out.println("Error occurred during parsing data");
            System.out.println(e.getMessage());
            return null;
        }
        return res;
    }

    public String getFile(String name) {
        String sql = "Select * from metadata where fileName = ?";
        ResultSet temp;
        try(PreparedStatement pstmt = dbInstance.getConnection().prepareStatement(sql)) {
            pstmt.setString(0, name);
            temp = pstmt.executeQuery();
            return temp.getString(0);
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
    private byte[] blobToByte(Blob blob) {
        try {
            int blobLength = (int) blob.length();
            return blob.getBytes(1, blobLength);
        } catch (SQLException e) {
            return null;
        }
    }


}
