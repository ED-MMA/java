package com.github.aklakina.edmma.database.orms;


import com.github.aklakina.edmma.base.Globals;

import jakarta.persistence.*;

import java.io.File;
import java.nio.file.Paths;

@Entity
@Table(name = "FILE", schema = "MD")
public class FileData {

    public FileData() {
    }

    @Transient
    private File file;
    private String name;
    private Integer lastLineRead;
    private long lastSize;

    public FileData(String name, Integer lastLineRead, long lastSize) {
        this.file = new File(Paths.get(Globals.ELITE_LOG_HOME, name).toString());
        this.lastLineRead = lastLineRead;
        this.name = name;
        this.lastSize = lastSize;
    }

    @Basic
    public Integer getLastLineRead() {
        return lastLineRead;
    }

    public void setLastLineRead(Integer lastLineRead) {
        this.lastLineRead = lastLineRead;
    }

    @Transient
    public boolean getChanged() {
        return file.length() != lastSize;
    }

    private void setChanged(boolean changed) {}

    @Transient
    public void reCalcSize() {
        lastSize = file.length();
    }

    @Id
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.file = new File(Paths.get(Globals.ELITE_LOG_HOME, name).toString());
    }

    @Transient
    public void loadFile() {
        this.file = new File(Paths.get(Globals.ELITE_LOG_HOME, name).toString());
    }
    @Transient
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Basic
    public long getLastSize() {
        return lastSize;
    }

    public void setLastSize(long lastSize) {
        this.lastSize = lastSize;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public boolean equals(Object o) {
        return o instanceof FileData && name.equals(((FileData) o).name);
    }

}
