package com.github.aklakina.edmma.base;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileData implements Comparable<FileData> {

    public class MD5 implements Comparable<MD5> {
        private byte[] data;

        public MD5(byte[] md5) {
            this.data = md5;
        }

        @Override
        public int compareTo(MD5 o) {
            // Compare based on the decimal value of the md5
            for (int i = 0; i < data.length; i++) {
                if (data[i] != o.data[i]) {
                    return data[i] - o.data[i];
                }
            }
            return 0;
        }

    }

    private MD5 hash;
    private String name;
    private Integer lastLineRead;
    private Path path;

    public FileData(String name, byte[] md5, Integer lastLineRead) {
        this.hash = new MD5(md5);
        this.lastLineRead = lastLineRead;
        this.name = name;
        this.path = Paths.get(Globals.ELITE_LOG_HOME, name);
    }

    public MD5 getMD5() {
        return hash;
    }

    @Override
    public int compareTo(FileData o) {
        // Compare based on the name of the file
        return this.name.compareTo(o.name);
    }

    private MD5 calculateHash() {
        byte[] digest;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            try (InputStream is = Files.newInputStream(path);
                 DigestInputStream dis = new DigestInputStream(is, md)) {
            } catch (IOException e) {
                System.out.println("Error reading file");
                e.printStackTrace();
                return null;
            }
            digest = md.digest();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("MD5 algorithm not found");
            e.printStackTrace();
            return null;
        }
        return new MD5(digest);
    }

    public boolean checkIfChanged() {
        MD5 newHash = calculateHash();
        if (newHash == null) {
            return false;
        }
        if (hash.compareTo(newHash) != 0) {
            hash = newHash;
            return true;
        }
        return false;
    }

}
