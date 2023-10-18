package com.github.aklakina.edmma.base;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileData extends File {

    private String name;
    private Integer lastLineRead;
    private long lastSize;

    public FileData(String name, Integer lastLineRead, long lastSize) {
        super(Paths.get(Globals.ELITE_LOG_HOME, name).toString());
        this.lastLineRead = lastLineRead;
        this.name = name;
        this.lastSize = lastSize;
    }

    public Integer getLastLineRead() {
        return lastLineRead;
    }

    public void setLastLineRead(Integer lastLineRead) {
        this.lastLineRead = lastLineRead;
    }

    public boolean isChanged() {
        return this.length() != lastSize;
    }

    public void reCalcSize() {
        lastSize = this.length();
    }

}
